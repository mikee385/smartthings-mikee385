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
    definition (name: "HubConnect Appliance Status", namespace: "shackrat", author: "Steve White") {
        capability "Actuator"
        capability "Refresh"
        capability "Sensor"
        capability "Switch"

        attribute "state", "enum", ["running", "finished", "unstarted"]
        attribute "stateColor", "enum", ["running-blue", "running-orange", "running-gray", "finished-blue", "finished-orange", "finished-gray", "unstarted-blue", "unstarted-orange", "unstarted-gray"]

        attribute "running", "boolean"
        attribute "finished", "boolean"
        attribute "unstarted", "boolean"
        attribute "version", "string"

        command "start"
        command "finish"
        command "reset"
        command "sync"
    }

    tiles(scale: 2) {    
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true, canChangeIcon: true) {
            tileAttribute ("device.stateColor", key: "PRIMARY_CONTROL") {
                attributeState "running-blue", label: 'Running', backgroundColor:"#00A0DC"
                attributeState "running-orange", label: 'Running', backgroundColor:"#e86d13"
                attributeState "running-gray", label: 'Running', backgroundColor:"#ffffff"
                attributeState "finished-blue", label: 'Finished', backgroundColor:"#00A0DC"
                attributeState "finished-orange", label: 'Finished', backgroundColor:"#e86d13"
                attributeState "finished-gray", label: 'Finished', backgroundColor:"#ffffff"
                attributeState "unstarted-blue", label: 'Unstarted', backgroundColor:"#00A0DC"
                attributeState "unstarted-orange", label: 'Unstarted', backgroundColor:"#e86d13"
                attributeState "unstarted-gray", label: 'Unstarted', backgroundColor:"#ffffff"
            }
        }
        standardTile("start", "device.running", width: 2, height: 2) {
            state "running", label:"Start", action: "start", backgroundColor:"#008000", nextState:"toStart"
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
        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
        }
        valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label: '${currentValue}'
        }
        main (["state"])
        details(["state", "start", "finish", "reset", "refresh", "sync", "version"])
    }
    
    preferences {
        input name: "stateColorRunning", type: "enum", title: "What color should be shown for 'Running'?", options: ["Blue", "Orange", "Gray"], defaultValue: "Blue", required: true, displayDuringSetup: false
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
    refresh()
}

def refresh () {
    parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
}

def on() {
    parent.sendDeviceEvent(device.deviceNetworkId, "on")
}

def off() {
    parent.sendDeviceEvent(device.deviceNetworkId, "off")
}

def start() {
    parent.sendDeviceEvent(device.deviceNetworkId, "start")
}

def finish() {
    parent.sendDeviceEvent(device.deviceNetworkId, "finish")
}

def reset() {
    parent.sendDeviceEvent(device.deviceNetworkId, "reset")
}

def sync()
{
    parent.syncDevice(device.deviceNetworkId, "omnipurpose")
    sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}

def getDriverVersion() {[platform: "Hubitat", major: 1, minor: 0, build: 0]}
