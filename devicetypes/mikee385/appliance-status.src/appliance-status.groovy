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
        capability "Sensor"
        capability "Switch"

        attribute "state", "enum", ["started", "finished", "unstarted"]
        attribute "stateColor", "enum", ["started-blue", "started-orange", "started-gray", "finished-blue", "finished-orange", "finished-gray", "unstarted-blue", "unstarted-orange", "unstarted-gray"]

        attribute "started", "boolean"
        attribute "finished", "boolean"
        attribute "unstarted", "boolean"

        command "start"
        command "finish"
        command "reset"
    }

    tiles(scale: 2) {    
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true, canChangeIcon: true) {
            tileAttribute ("device.stateColor", key: "PRIMARY_CONTROL") {
                attributeState "started-blue", label: 'Running', backgroundColor:"#00A0DC"
                attributeState "started-orange", label: 'Running', backgroundColor:"#e86d13"
                attributeState "started-gray", label: 'Running', backgroundColor:"#ffffff"
                attributeState "finished-blue", label: 'Finished', backgroundColor:"#00A0DC"
                attributeState "finished-orange", label: 'Finished', backgroundColor:"#e86d13"
                attributeState "finished-gray", label: 'Finished', backgroundColor:"#ffffff"
                attributeState "unstarted-blue", label: 'Unstarted', backgroundColor:"#00A0DC"
                attributeState "unstarted-orange", label: 'Unstarted', backgroundColor:"#e86d13"
                attributeState "unstarted-gray", label: 'Unstarted', backgroundColor:"#ffffff"
            }
        }
        standardTile("start", "device.started", width: 2, height: 2) {
            state "started", label:"Start", action: "start", backgroundColor:"#008000", nextState:"toStart"
            state "toStart", label:"Updating", backgroundColor:"#00A0DC"
        }
        standardTile("finish", "device.finished", width: 2, height: 2) {
            state "finished", label:"Finish", action: "finish", backgroundColor:"#ff0000", nextState:"toFinish"
            state "toFinish", label:"Updating", backgroundColor:"#00A0DC"
        }
        standardTile("reset", "device.unstarted", width: 2, height: 2) {
            state "unstarted", label:"Reset", icon: "st.secondary.refresh-icon", action: "reset", backgroundColor:"#ffffff", nextState:"toReset"
            state "toReset", label:"Updating", icon: "st.secondary.refresh-icon", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "start", "finish", "reset"])
    }
    
    preferences {
        input name: "stateColorStarted", type: "enum", title: "What color should be shown for 'Started'?", options: ["Blue", "Orange", "Gray"], defaultValue: "Blue", required: true, displayDuringSetup: false
        input name: "stateColorFinished", type: "enum", title: "What color should be shown for 'Finished'?", options: ["Blue", "Orange", "Gray"], defaultValue: "Orange", required: true, displayDuringSetup: false
        input name: "stateColorUnstarted", type: "enum", title: "What color should be shown for 'Unstarted'?", options: ["Blue", "Orange", "Gray"], defaultValue: "Gray", required: true, displayDuringSetup: false
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
        reset()
    }
}

def on() {
    start()
}

def off() {
    finish()
}

def start() {
    sendEvent(name: "state", value: "started", descriptionText: "$device.displayName changed to started", displayed: true)
    
    sendEvent(name: "started", value: true, displayed: false)
    sendEvent(name: "finished", value: false, displayed: false)
    sendEvent(name: "unstarted", value: false, displayed: false)
    
    sendEvent(name: "switch", value: "on", displayed: false)
    
    if (stateColorStarted == "Blue") {
        sendEvent(name: "stateColor", value: "started-blue", displayed: false)
    } else if (stateColorStarted == "Orange") {
        sendEvent(name: "stateColor", value: "started-orange", displayed: false)
    } else if (stateColorStarted == "Gray") {
        sendEvent(name: "stateColor", value: "started-gray", displayed: false)
    } else {
        sendEvent(name: "stateColor", value: "started-blue", displayed: false)
    }
}

def finish() {
    sendEvent(name: "state", value: "finished", descriptionText: "$device.displayName changed to finished", displayed: true)
    
    sendEvent(name: "started", value: false, displayed: false)
    sendEvent(name: "finished", value: true, displayed: false)
    sendEvent(name: "unstarted", value: false, displayed: false)
    
    sendEvent(name: "switch", value: "off", displayed: false)
    
    if (stateColorFinished == "Blue") {
        sendEvent(name: "stateColor", value: "finished-blue", displayed: false)
    } else if (stateColorFinished == "Orange") {
        sendEvent(name: "stateColor", value: "finished-orange", displayed: false)
    } else if (stateColorFinished == "Gray") {
        sendEvent(name: "stateColor", value: "finished-gray", displayed: false)
    } else {
        sendEvent(name: "stateColor", value: "finished-orange", displayed: false)
    }
}

def reset() {
    sendEvent(name: "state", value: "unstarted", descriptionText: "$device.displayName changed to unstarted", displayed: true)
    
    sendEvent(name: "started", value: false, displayed: false)
    sendEvent(name: "finished", value: false, displayed: false)
    sendEvent(name: "unstarted", value: true, displayed: false)
    
    sendEvent(name: "switch", value: "off", displayed: false)
    
    if (stateColorUnstarted == "Blue") {
        sendEvent(name: "stateColor", value: "unstarted-blue", displayed: false)
    } else if (stateColorUnstarted == "Orange") {
        sendEvent(name: "stateColor", value: "unstarted-orange", displayed: false)
    } else if (stateColorUnstarted == "Gray") {
        sendEvent(name: "stateColor", value: "unstarted-gray", displayed: false)
    } else {
        sendEvent(name: "stateColor", value: "unstarted-gray", displayed: false)
    }
}