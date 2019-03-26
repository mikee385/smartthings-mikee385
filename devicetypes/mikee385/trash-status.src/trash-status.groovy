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
    definition (name: "Trash Status", namespace: "mikee385", author: "Michael Pierce") {
        capability "Actuator"
        capability "Button"
        capability "Sensor"
        capability "Switch"

        attribute "state", "enum", ["in", "out"]
        attribute "in", "boolean"
        attribute "out", "boolean"

        command "in"
        command "out"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true) {
            tileAttribute ("device.state", key: "PRIMARY_CONTROL") {
                attributeState "in", label: 'In', icon:"st.secondary.remove", backgroundColor:"#00A0DC", 
                attributeState "out", label: 'Out', icon:"st.secondary.remove", backgroundColor:"#e86d13"
            }
        }
        standardTile("in", "device.in", width: 2, height: 2, canChangeIcon: true) {
            state "in", label:"In", icon: "st.thermostat.thermostat-down", action: "in", backgroundColor:"#ffffff", nextState:"toIn"
            state "toIn", label:"Updating", icon:"st.thermostat.thermostat-down", backgroundColor:"#00A0DC"
        }
        standardTile("out", "device.out", width: 2, height: 2, canChangeIcon: true) {
            state "out", label:"Out", icon: "st.thermostat.thermostat-up", action: "out", backgroundColor:"#ffffff", nextState:"toOut"
            state "toOut", label:"Updating", icon: "st.thermostat.thermostat-up", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "in", "out"])
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
    
    sendEvent(name: "numberOfButtons", value: 2, displayed: false)
    sendEvent(name: "supportedButtonValues", value: ["pushed"], displayed: false)
    
    if (!device.currentValue("state")) {
        in()
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands
def on() {
    log.debug "Executing 'on'"
    
    in()
}

def off() {
    log.debug "Executing 'off'"
    
    out()
}

def push(buttonNumber) {
    log.debug "Executing 'push' with button '$buttonNumber'"
        
    if (button == 1) {
        in()
    } else if (button == 2) {
        out()
    } else {
        sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buttonNumber"], descriptionText: "$device.displayName button $buttonNumber was pushed", isStateChange: true, displayed: true)
    }
}

def in() {
    log.debug "Executing 'in'"    
    
    setStateToIn()
}

def out() {
    log.debug "Executing 'out'"
    
    setStateToOut()
}

private def setStateToIn() {
    log.debug "Executing 'setStateToIn'"
    
    sendEvent(name: "state", value: "in", descriptionText: "$device.displayName changed to in", displayed: true)
    
    sendEvent(name: "in", value: true, displayed: false)
    sendEvent(name: "out", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'off', displayed: false)    
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], isStateChange: true, displayed: false)
}

private def setStateToOut() {
    log.debug "Executing 'setStateToOut'"
    
    sendEvent(name: "state", value: "out", descriptionText: "$device.displayName changed to out", displayed: true)
    
    sendEvent(name: "in", value: false, displayed: false)
    sendEvent(name: "out", value: true, displayed: false)
    
    sendEvent(name: "switch", value: 'on', displayed: false)
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 2], isStateChange: true, displayed: false)
}