/**
 *  Person Status Device Handler
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
    definition (name: "HubConnect Person Status", namespace: "shackrat", author: "Steve White") {
        capability "Actuator"
        capability "Presence Sensor"
        capability "Refresh"
        capability "Sensor"
        capability "Sleep Sensor"

        attribute "state", "enum", ["home", "away", "sleep"]
        attribute "version", "string"
        
        command "awake"
        command "asleep"
        command "arrived"
        command "departed"
        command "sync"
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "state", type: "generic", width: 6, height: 4, canChangeBackground: true) {
            tileAttribute ("device.state", key: "PRIMARY_CONTROL") {
                attributeState "home", label: 'Home', icon:"st.nest.nest-home", backgroundColor:"#00A0DC"
                attributeState "away", label: 'Away', icon:"st.nest.nest-away", backgroundColor:"#ffffff"
                attributeState "sleep", label: 'Sleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#ffffff"
            }
        }
        standardTile("home", "device.isHome", width: 2, height: 2, canChangeIcon: true) {
            state "home", label:"Home", icon: "st.nest.nest-home", action: "awake", backgroundColor:"#ffffff", nextState:"toHome"
            state "toHome", label:"Updating", icon: "st.nest.nest-home", backgroundColor:"#00A0DC"
        }
        standardTile("away", "device.isAway", width: 2, height: 2, canChangeIcon: true) {
            state "away", label:"Away", icon: "st.nest.nest-away", action: "departed", backgroundColor:"#ffffff", nextState:"toAway"
            state "toAway", label:"Updating", icon:"st.nest.nest-away", backgroundColor:"#00A0DC"
        }
        standardTile("sleep", "device.isSleep", width: 2, height: 2, canChangeIcon: true) {
            state "sleep", label:"Sleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toSleep"
            state "toSleep", label:"Updating", icon:"st.Bedroom.bedroom2", backgroundColor:"#00A0DC"
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
        details(["state", "home", "away", "sleep", "refresh", "sync", "version"])
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

def awake() {
    parent.sendDeviceEvent(device.deviceNetworkId, "awake")
}

def asleep() {
    parent.sendDeviceEvent(device.deviceNetworkId, "asleep")
}

def arrived() {
    parent.sendDeviceEvent(device.deviceNetworkId, "arrived")
}

def departed() {
    parent.sendDeviceEvent(device.deviceNetworkId, "departed")
}

def sync() {
    parent.syncDevice(device.deviceNetworkId, "omnipurpose")
    sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}

def getDriverVersion() {
    [platform: "Hubitat", major: 1, minor: 0, build: 0]
}