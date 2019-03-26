/**
 *  Appliance Status Device Handler
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
    definition (name: "Appliance Status", namespace: "mikee385", author: "Michael Pierce") {
        capability "Actuator"
        capability "Button"
        capability "Sensor"
        capability "Switch"

        attribute "state", "enum", ["started", "finished", "not started"]
        attribute "started", "boolean"
        attribute "finished", "boolean"
        attribute "not started", "boolean"

        command "start"
        command "finish"
        command "reset"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        standardTile("state", "device.state", width: 6, height: 4, canChangeBackground: true, canChangeIcon: true) {
            state "started", label: 'Running', backgroundColor:"#00A0DC"
            state "finished", label: 'Finished', backgroundColor:"#cccccc"
            state "not started", label: 'Not Started', backgroundColor:"#e86d13"
        }
        standardTile("start", "device.started", width: 2, height: 2, canChangeIcon: true) {
            state "started", label:"Start", icon: "st.sonos.play-icon", action: "start", backgroundColor:"#ffffff", nextState:"toStart"
            state "toStart", label:"Updating", icon:"st.sonos.play-icon", backgroundColor:"#00A0DC"
        }
        standardTile("finish", "device.finished", width: 2, height: 2, canChangeIcon: true) {
            state "finished", label:"Finish", icon: "st.sonos.stop-icon", action: "finish", backgroundColor:"#ffffff", nextState:"toFinish"
            state "toFinish", label:"Updating", icon: "st.sonos.stop-icon", backgroundColor:"#00A0DC"
        }
        standardTile("reset", "device.not started", width: 2, height: 2, canChangeIcon: true) {
            state "not started", label:"Reset", icon: "st.secondary.refresh-icon", action: "reset", backgroundColor:"#ffffff", nextState:"toReset"
            state "toReset", label:"Updating", icon: "st.secondary.refresh-icon", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "start", "finish", "reset"])
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
    
    sendEvent(name: "numberOfButtons", value: 3, displayed: false)
    sendEvent(name: "supportedButtonValues", value: ["pushed"], displayed: false)
    
    if (!device.currentValue("state")) {
        reset()
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands
def on() {
    log.debug "Executing 'on'"
    
    start()
}

def off() {
    log.debug "Executing 'off'"
    
    finish()
}

def push(buttonNumber) {
    log.debug "Executing 'push' with button '$buttonNumber'"
        
    if (button == 1) {
        start()
    } else if (button == 2) {
        finish()
    } else if (button == 3) {
        reset()
    } else {
        sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buttonNumber"], descriptionText: "$device.displayName button $buttonNumber was pushed", isStateChange: true, displayed: true)
    }
}

def start() {
    log.debug "Executing 'start'"    
    
    setStateToStarted()
}

def finish() {
    log.debug "Executing 'finish'"
    
    setStateToFinished()
}

def reset() {
    log.debug "Executing 'reset'"
    
    setStateToNotStarted()
}

private def setStateToStarted() {
    log.debug "Executing 'setStateToStarted'"
    
    sendEvent(name: "state", value: "started", descriptionText: "$device.displayName changed to started", displayed: true)
    
    sendEvent(name: "started", value: true, displayed: false)
    sendEvent(name: "finished", value: false, displayed: false)
    sendEvent(name: "not started", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'on', displayed: false)
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], isStateChange: true, displayed: false)
}

private def setStateToFinished() {
    log.debug "Executing 'setStateToFinished'"
    
    sendEvent(name: "state", value: "finished", descriptionText: "$device.displayName changed to finished", displayed: true)
    
    sendEvent(name: "started", value: false, displayed: false)
    sendEvent(name: "finished", value: true, displayed: false)
    sendEvent(name: "not started", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'off', displayed: false)
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 2], isStateChange: true, displayed: false)
}

private def setStateToNotStarted() {
    log.debug "Executing 'setStateToReset'"
    
    sendEvent(name: "state", value: "not started", descriptionText: "$device.displayName changed to not started", displayed: true)
    
    sendEvent(name: "started", value: false, displayed: false)
    sendEvent(name: "finished", value: false, displayed: false)
    sendEvent(name: "not started", value: true, displayed: false)
    
    sendEvent(name: "switch", value: 'off', displayed: false)
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 3], isStateChange: true, displayed: false)
}