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
        capability "Sensor"
        capability "Switch"

        attribute "state", "enum", ["in", "out"]
        attribute "in", "boolean"
        attribute "out", "boolean"

        command "bringIn"
        command "takeOut"
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true) {
            tileAttribute ("device.state", key: "PRIMARY_CONTROL") {
                attributeState "in", label: 'In', icon:"st.Office.office10", backgroundColor:"#00A0DC"
                attributeState "out", label: 'Out', icon:"st.Office.office10", backgroundColor:"#e86d13"
            }
        }
        standardTile("out", "device.out", width: 2, height: 2, canChangeIcon: true) {
            state "out", label:"Take Out", icon: "st.thermostat.thermostat-up", action: "takeOut", backgroundColor:"#ffffff", nextState:"toOut"
            state "toOut", label:"Updating", icon: "st.thermostat.thermostat-up", backgroundColor:"#00A0DC"
        }
        standardTile("in", "device.in", width: 2, height: 2, canChangeIcon: true) {
            state "in", label:"Bring In", icon: "st.thermostat.thermostat-down", action: "bringIn", backgroundColor:"#ffffff", nextState:"toIn"
            state "toIn", label:"Updating", icon:"st.thermostat.thermostat-down", backgroundColor:"#00A0DC"
        }
        main (["state"])
        details(["state", "out", "in"])
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
        bringIn()
    }
}

def on() {
    takeOut()
}

def off() {
    bringIn()
}

def bringIn() {
    setStateToIn()
}

def takeOut() {
    setStateToOut()
}

private def setStateToIn() {
    sendEvent(name: "state", value: "in", descriptionText: "$device.displayName changed to in", displayed: true)
    
    sendEvent(name: "in", value: true, displayed: false)
    sendEvent(name: "out", value: false, displayed: false)
    
    sendEvent(name: "switch", value: 'off', displayed: false)
}

private def setStateToOut() {
    sendEvent(name: "state", value: "out", descriptionText: "$device.displayName changed to out", displayed: true)
    
    sendEvent(name: "in", value: false, displayed: false)
    sendEvent(name: "out", value: true, displayed: false)
    
    sendEvent(name: "switch", value: 'on', displayed: false)
}