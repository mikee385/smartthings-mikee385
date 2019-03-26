/**
 *  Occupancy Virtual Device Handler
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
capability "Button"
capability "Occupancy Sensor"
capability "Presence Sensor"
capability "Sensor"
capability "Switch"

attribute "state", "enum", ["occupied", "vacant", "checking", "blind"]
        attribute "occupied", "boolean"
attribute "vacant", "boolean"
attribute "checking", "boolean"
attribute "blind", "boolean"

command "occupied"
command "vacant"
command "checking"
}

simulator {
// TODO: define status and reply messages here
}

tiles(scale: 2) {
standardTile("state", "device.state", width: 6, height: 4, canChangeBackground: true, canChangeIcon: true) {
state "occupied", label: 'Occupied', backgroundColor:"#00A0DC"
            state "vacant", label: 'Vacant', backgroundColor:"#cccccc"
state "checking", label: 'Checking', backgroundColor:"#e86d13"
state "blind", label: 'Blind', backgroundColor:"#ff0000"
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
    log.debug "Executing 'installed'"
    
initialize()
}

def updated() {
    log.debug "Executing 'updated'"
    
unschedule()
    initialize()
}

def initialize() {
    log.debug "Executing 'initialize'"
    
sendEvent(name: "numberOfButtons", value: 3, displayed: false)
sendEvent(name: "supportedButtonValues", value: ["pushed"], displayed: false)
    
    if (!device.currentValue("state")) {
        vacant()
    }
}

// parse events into attributes
def parse(String description) {
log.debug "Parsing '${description}'"
}

// handle commands
def on() {
log.debug "Executing 'on'"
    
occupied()
}

def off() {
log.debug "Executing 'off'"
    
vacant()
}

def push(buttonNumber) {
log.debug "Executing 'push' with button '$buttonNumber'"
        
    if (button == 1) {
    occupied()
    } else if (button == 2) {
    checking()
    } else if (button == 3) {
    vacant()
    } else {
sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buttonNumber"], descriptionText: "$device.displayName button $buttonNumber was pushed", isStateChange: true, displayed: true)
    }
}

def occupied() {
log.debug "Executing 'occupied'"    
    
    setStateToOccupied()
}

def vacant() {
log.debug "Executing 'vacant'"
    
    setStateToVacant()
}

def checking() {
log.debug "Executing 'checking'"
    
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
    log.debug "Executing 'resumeFromBlind'"

    def remainingTime = checkingPeriod - blindPeriod
    if (remainingTime > 0) {
    setStateToChecking()
        runIn(remainingTime, resumeFromChecking)
    } else {
    setStateToVacant()
    }
}

def resumeFromChecking() {
    log.debug "Executing 'resumeFromChecking'"
    
    setStateToVacant()
}

private def setStateToOccupied() {
    log.debug "Executing 'setStateToOccupied'"
    
sendEvent(name: "state", value: "occupied", descriptionText: "$device.displayName changed to occupied", displayed: true)
    
    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: false, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'on', displayed: false)
    sendEvent(name: "presence", value: 'present', displayed: false)
    sendEvent(name: "occupancy", value: 'occupied', displayed: false)
    
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], isStateChange: true, displayed: false)
    
    unschedule()
}

private def setStateToVacant() {
    log.debug "Executing 'setStateToVacant'"
    
sendEvent(name: "state", value: "vacant", descriptionText: "$device.displayName changed to vacant", displayed: true)
    
    sendEvent(name: "occupied", value: false, displayed: false)
    sendEvent(name: "vacant", value: true, displayed: false)
    sendEvent(name: "checking", value: false, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'off', displayed: false)
    sendEvent(name: "presence", value: 'not present', displayed: false)
    sendEvent(name: "occupancy", value: 'unoccupied', displayed: false)
    
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 2], isStateChange: true, displayed: false)
    
    unschedule()
}

private def setStateToChecking() {
    log.debug "Executing 'setStateToChecking'"
    
    sendEvent(name: "state", value: "checking", descriptionText: "$device.displayName changed to checking", displayed: true)

    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: true, displayed: false)
    sendEvent(name: "blind", value: false, displayed: false)

    sendEvent(name: "switch", value: 'on', displayed: false)
    sendEvent(name: "presence", value: 'present', displayed: false)
    sendEvent(name: "occupancy", value: 'occupied', displayed: false)

    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 3], isStateChange: true, displayed: false)
}

private def setStateToBlind() {
    log.debug "Executing 'setStateToBlind'"
    
    sendEvent(name: "state", value: "blind", descriptionText: "$device.displayName changed to blind", displayed: true)

    sendEvent(name: "occupied", value: true, displayed: false)
    sendEvent(name: "vacant", value: false, displayed: false)
    sendEvent(name: "checking", value: true, displayed: false)
    sendEvent(name: "blind", value: true, displayed: false)

    sendEvent(name: "switch", value: 'on', displayed: false)
    sendEvent(name: "presence", value: 'present', displayed: false)
    sendEvent(name: "occupancy", value: 'occupied', displayed: false)

    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 4], isStateChange: true, displayed: false)
}