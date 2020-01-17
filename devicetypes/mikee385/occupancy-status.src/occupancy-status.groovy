/**
 *  Occupancy Status Device Handler
 *
 *  Copyright 2019 Michael Pierce
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
    definition (name: "Occupancy Status", namespace: "mikee385", author: "Michael Pierce") {
        capability "Actuator"
        capability "Occupancy Sensor"
        capability "Sensor"

        attribute "state", "enum", ["occupied", "vacant", "checking", "blind"]
        
        attribute "occupied", "boolean"
        attribute "vacant", "boolean"
        attribute "checking", "boolean"
        attribute "blind", "boolean"

        command "occupied"
        command "vacant"
        command "checking"
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true, canChangeIcon: true) {
            tileAttribute ("device.state", key: "PRIMARY_CONTROL") {
                attributeState "occupied", label: 'Occupied', backgroundColor:"#00A0DC"
                attributeState "vacant", label: 'Vacant', backgroundColor:"#ffffff"
                attributeState "checking", label: 'Checking', backgroundColor:"#e86d13"
                attributeState "blind", label: 'Blind', backgroundColor:"#ff0000"
            }
        }
        standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true) {
            state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
            state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#00A0DC"
        }
        standardTile("checking", "device.checking", width: 2, height: 2, canChangeIcon: true) {
            state "checking", label:"Checking", icon: "st.Health & Wellness.health9", action: "checking", backgroundColor:"#ffffff", nextState:"toChecking"
            state "toChecking", label:"Updating", icon: "st.Health & Wellness.health9", backgroundColor:"#00A0DC"
        }
        standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true) {
            state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
            state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "occupied", "checking", "vacant"])
    }
    
    preferences {
        input "checkingPeriod", "number", title: "Checking Period in Seconds\nHow long (in seconds) should zone stay in the 'checking' state (including the 'blind' period) before transitioning to the 'vacant' state?", range: "0..*", defaultValue: 240, required: true, displayDuringSetup: false
        input "blindPeriod", "number", title: "Blind Period in Seconds\nHow long (in seconds) at the beginning of the 'checking' state should zone ignore certain events?", range: "0..*", defaultValue: 0, required: true, displayDuringSetup: false
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def initialize() {
    if (!device.currentValue("state")) {
        vacant()
    }
}

def occupied() {
    setStateToOccupied()
}

def vacant() {
    setStateToVacant()
}

def checking() {
    if (blindPeriod > 0) {
        setStateToBlind()
        runIn(blindPeriod, resumeFromBlind)
    } else if (checkingPeriod > 0) {
        setStateToChecking()
        runIn(checkingPeriod, resumeFromChecking)
    } else {
        setStateToVacant()
    }
}

def resumeFromBlind() {
    def remainingTime = checkingPeriod - blindPeriod
    if (remainingTime > 0) {
        setStateToChecking()
        runIn(remainingTime, resumeFromChecking)
    } else {
        setStateToVacant()
    }
}

def resumeFromChecking() {
    setStateToVacant()
}

private def setStateToOccupied() {
    sendEvent(name: "state", value: "occupied", descriptionText: "$device.displayName changed to occupied", displayed: true)
    
    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: false, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)
    
    sendEvent(name: "occupancy", value: 'occupied', displayed: false)
    
    unschedule()
}

private def setStateToVacant() {
    sendEvent(name: "state", value: "vacant", descriptionText: "$device.displayName changed to vacant", displayed: true)
    
    sendEvent(name: "occupied", value: false, displayed: false)
    sendEvent(name: "vacant", value: true, displayed: false)
    sendEvent(name: "checking", value: false, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)
    
    sendEvent(name: "occupancy", value: 'unoccupied', displayed: false)
    
    unschedule()
}

private def setStateToChecking() {
    sendEvent(name: "state", value: "checking", descriptionText: "$device.displayName changed to checking", displayed: true)

    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: true, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)

    sendEvent(name: "occupancy", value: 'occupied', displayed: false)
}

private def setStateToBlind() {
    sendEvent(name: "state", value: "blind", descriptionText: "$device.displayName changed to blind", displayed: true)

    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: true, displayed: false)
    sendEvent(name: "blind", value: true, displayed: false)

    sendEvent(name: "occupancy", value: 'occupied', displayed: false)
}