/**
 *  Trash Status Device Handler
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
    definition (name: "Person Status", namespace: "mikee385", author: "Michael Pierce") {
        capability "Actuator"
        capability "Presence Sensor"
		capability "Sensor"
        capability "Sleep Sensor"

        attribute "state", "enum", ["awake", "asleep", "away"]
		
        command "awake"
        command "asleep"
        command "away"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true) {
            tileAttribute ("device.state", key: "PRIMARY_CONTROL") {
                attributeState "awake", label: 'Awake', icon:"st.nest.nest-away", backgroundColor:"#00A0DC"
                attributeState "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#ffffff"
                attributeState "away", label: 'Away', icon:"st.Office.office5", backgroundColor:"#ffffff"
            }
        }
        standardTile("awake", "device.isAwake", width: 2, height: 2, canChangeIcon: true) {
            state "awake", label:"Awake", icon: "st.nest.nest-away", action: "awake", backgroundColor:"#ffffff", nextState:"toAwake"
            state "toAwake", label:"Updating", icon: "st.nest.nest-away", backgroundColor:"#00A0DC"
        }
        standardTile("asleep", "device.isAsleep", width: 2, height: 2, canChangeIcon: true) {
            state "asleep", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toAsleep"
            state "toAsleep", label:"Updating", icon:"st.Bedroom.bedroom2", backgroundColor:"#00A0DC"
        }
        standardTile("away", "device.isAway", width: 2, height: 2, canChangeIcon: true) {
            state "away", label:"Away", icon: "st.Office.office5", action: "away", backgroundColor:"#ffffff", nextState:"toAway"
            state "toAway", label:"Updating", icon:"st.Office.office5", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "awake", "asleep", "away"])
    }
    
    preferences {
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
    
    if (!device.currentValue("state")) {
        awake()
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands
def awake() {
    log.debug "Executing 'awake'"
    
    sendEvent(name: "state", value: "awake", descriptionText: "$device.displayName changed to awake", displayed: true)
    
    sendEvent(name: "presence", value: "present", displayed: false)
    sendEvent(name: "sleeping", value: "not sleeping", displayed: false)
}

def asleep() {
    log.debug "Executing 'asleep'"
    
    sendEvent(name: "state", value: "asleep", descriptionText: "$device.displayName changed to asleep", displayed: true)
    
    sendEvent(name: "presence", value: "present", displayed: false)
    sendEvent(name: "sleeping", value: "sleeping", displayed: false)
}

def away() {
    log.debug "Executing 'away'"
    
    sendEvent(name: "state", value: "away", descriptionText: "$device.displayName changed to away", displayed: true)
    
    sendEvent(name: "presence", value: "not present", displayed: false)
    sendEvent(name: "sleeping", value: "not sleeping", displayed: false)
}
