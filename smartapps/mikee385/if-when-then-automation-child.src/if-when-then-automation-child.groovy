/**
 *  If-When-Then Automation
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
definition(
    name: "If-When-Then Automation - Child",
    namespace: "mikee385",
    author: "Michael Pierce",
    parent: "mikee385:If-When-Then Automation",
    description: "Child app for If-When-Then Automation, which provides a more sophisticated automation system.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/lighting-wizard.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/lighting-wizard@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/lighting-wizard@2x.png")
    
def getTriggerType_DeviceChanges() { "Device Changes" }
def getTriggerType_ModeChanges() { "Mode Changes" }
def getTriggerType_RoutineExectutes() { "Routine Exectutes" }
def getTriggerType_AtSunrise() { "At Sunrise" }
def getTriggerType_AtSunset() { "At Sunset" }
def getTriggerType_AtSpecificTime() { "At a Specific Time" }

def getConditionType_DeviceStatus() { "Device Status" }
def getConditionType_Mode() { "Mode" }
def getConditionType_Time() { "Time" }
def getConditionType_Day() { "Days of the Week" }

def getConditionCombine_All() { "All" }
def getConditionCombine_Any() { "Any" }

def getActionType_ControlDevice() { "Control Device" }
def getActionType_ChangeMode() { "Change Mode" }
def getActionType_ExecuteRoutine() { "Execute Routine" }
def getActionType_SendNotification() { "Send Notification" }

def getBool_True() { "true" }
def getBool_False() { "false" }

def getComparison_Is() { "is" }
def getComparison_IsNot() { "is not" }

def getComparison_EqualTo() { "is equal to" }
def getComparison_NotEqualTo() { "is not equal to" }
def getComparison_GreaterThan() { "is greater than" }
def getComparison_LessThan() { "is less than" }
def getComparison_GreaterThanOrEqualTo() { "is greater than or equal to" }
def getComparison_LessThanOrEqualTo() { "is less than or equal to" }

def getNotificationType_Push() { "Push Notification" }
def getNotificationType_Text() { "Text Message" }
def getNotificationType_Log() { "Notification Log" }

preferences {
    page(name: "settings")
}

def settings() {
    dynamicPage(name: "settings", title: "", install: true, uninstall: true) {
        def triggers = readTriggers()
        def numTriggers = triggers.size()
        if (triggerMore == true) {
            numTriggers += 1
            app.updateSetting("triggerMore", false)
        }
        
        def conditions = readConditions()
        def numConditions = conditions.size()
        if (conditionMore == true) {
            numConditions += 1
            app.updateSetting("conditionMore", false)
        }
        
        def actions = readActions()
        def numActions = actions.size()
        if (actionMore == true) {
            numActions += 1
            app.updateSetting("actionMore", false)
        }
        
        if (enabled == null) {
            app.updateSetting("enabled", true)
        }
        section() {
            input "enabled", "bool", title: "Enabled?", defaultValue: true, required: true
        }
        
        def showTriggerMore = false
        for (int index = 1; index <= Math.max(numTriggers, 1); index++) {
            def triggerId = -1            
            if (index <= triggers.size()) {
                triggerId = triggers[index-1].id
            } else if (triggers.size() > 0) {
                triggerId = triggers[-1].id + 1
            } else {
                triggerId = 1
            }
            def sectionTitle = ""
            if (index == 1) {
                sectionTitle = "If this happens:"
            } else {
                sectionTitle = "OR if this happens:"
            }            
            section(sectionTitle) {
                input "triggerType_${triggerId}", "enum", title: "Trigger", options: [triggerType_DeviceChanges, triggerType_ModeChanges, triggerType_RoutineExectutes, triggerType_AtSpecificTime, triggerType_AtSunrise, triggerType_AtSunset], required: true, submitOnChange: true
                
                if (index <= triggers.size()) {
                    def triggerType = triggers[index-1].type
                    if (triggerType != null && triggerType != "") {
                        if (triggerType == triggerType_DeviceChanges) {
                            input "triggerDevice_${triggerId}", "capability.sensor", title: "Device", required: true, submitOnChange: true

                            def triggerDevice = triggers[index-1].device
                            if (triggerDevice != null && triggerDevice != "") {
                                def attributes = triggerDevice.supportedAttributes
                                input "triggerAttributeName_${triggerId}", "enum", title: "Attribute", options: attributes*.name.unique().sort(), required: true, submitOnChange: true

                                def triggerAttributeName = triggers[index-1].attributeName
                                if (triggerAttributeName != null && triggerAttributeName != "") {
                                    def attribute = attributes.find{element -> element.name == triggerAttributeName}
                                    if (attribute != null) {
                                        if (attribute.dataType == "ENUM") {
                                            input "triggerComparison_${triggerId}", "enum", title: "Comparison", options: [comparison_Is, comparison_IsNot], required: true, submitOnChange: true

                                            def triggerComparison = triggers[index-1].comparison
                                            if (triggerComparison != null && triggerComparison != "") {
                                                if (attribute.values) {
                                                    input "triggerValue_${triggerId}", "enum", title: "Value", options: attribute.values, required: true
                                                } else {
                                                    input "triggerValue_${triggerId}", "string", title: "Value", required: true
                                                }
                                            }
                                            
                                        } else if (attribute.dataType == "NUMBER") {
                                            input "triggerComparison_${triggerId}", "enum", title: "Comparison", options: [comparison_EqualTo, comparison_NotEqualTo, comparison_GreaterThan, comparison_LessThan, comparison_GreaterThanOrEqualTo, comparison_LessThanOrEqualTo], required: true, submitOnChange: true

                                            def triggerComparison = triggers[index-1].comparison
                                            if (triggerComparison != null && triggerComparison != "") {
                                                input "triggerValue_${triggerId}", "number", title: "Value", range: "*..*", required: true
                                            }
                                            
                                        } else if (attribute.dataType == "BOOLEAN") {
                                            input "triggerValue_${triggerId}", "enum", title: "Value", options: [bool_True, bool_False], required: true
                                        } else {
                                            input "triggerComparison_${triggerId}", "enum", title: "Comparison", options: [comparison_Is, comparison_IsNot], required: true, submitOnChange: true

                                            def triggerComparison = triggers[index-1].comparison
                                            if (triggerComparison != null && triggerComparison != "") {
                                                input "triggerValue_${triggerId}", "string", title: "Value", required: true
                                            }
                                        }
                                        
                                    } else {
                                        app.updateSetting("triggerAttributeName", "")
                                    }
                                }
                            }
                        } else if (triggerType == triggerType_ModeChanges) {
                            def modes = location.modes*.name.unique().sort()
                            input "triggerMode_${triggerId}", "enum", title: "Mode(s)", options: modes, multiple: true, required: true
                            
                        } else if (triggerType == triggerType_RoutineExectutes) {
                            def routines = location.helloHome?.getPhrases()*.label.unique().sort()
                            input "triggerRoutine_${triggerId}", "enum", title: "Routine(s)", options: routines, multiple: true, required: true
                            
                        } else if (triggerType == triggerType_AtSpecificTime) {
                            input "triggerTime_${triggerId}", "time", title: "Time", required: true
                            
                        } else if (triggerType == triggerType_AtSunrise) {
                            input "triggerOffset_${triggerId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                            
                        } else if (triggerType == triggerType_AtSunset) {
                            input "triggerOffset_${triggerId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                        }
                
                        showTriggerMore = true
                    } else {
                        showTriggerMore = false
                    }
                } else {
                    showTriggerMore = false
                }
            }
        }
        if (showTriggerMore) {
            section() {
                input "triggerMore", "bool", title: "Add More Triggers?", required: true, submitOnChange: true
            }
        }
        
        if (conditionCombine == null) {
            app.updateSetting("conditionCombine", conditionCombine_All)
        }
        if (numConditions > 1) {
            section() {
                input "conditionCombine", "enum", title: "Include which conditions?", options: [conditionCombine_All, conditionCombine_Any], defaultValue: conditionCombine_All, required: true, submitOnChange: true
            }
        }
        
        def showConditionMore = false
        for (int index = 1; index <= Math.max(numConditions, 1); index++) {
            def conditionId = -1            
            if (index <= conditions.size()) {
                conditionId = conditions[index-1].id
            } else if (conditions.size() > 0) {
                conditionId = conditions[-1].id + 1
            } else {
                conditionId = 1
            }
            def sectionTitle = ""
            if (index == 1) {
                sectionTitle = "When this is true:"
            } else if (conditionCombine == conditionCombine_Any) {
                sectionTitle = "OR when this is true:"
            } else {
                sectionTitle = "AND when this is true:"
            }
            section(sectionTitle) {
                input "conditionType_${conditionId}", "enum", title: "Condition", options: [conditionType_DeviceStatus, conditionType_Mode, conditionType_Time, conditionType_Day], required: false, submitOnChange: true
                
                if (index <= conditions.size()) {
                    def conditionType = conditions[index-1].type
                    if (conditionType != null && conditionType != "") {
                        if (conditionType == conditionType_DeviceStatus) {
                            input "conditionDevice_${conditionId}", "capability.sensor", title: "Device", required: true, submitOnChange: true
                            
                            def conditionDevice = conditions[index-1].device
                            if (conditionDevice != null && conditionDevice != "") {
                                def attributes = conditionDevice.supportedAttributes
                                input "conditionAttributeName_${conditionId}", "enum", title: "Attribute", options: attributes*.name.unique().sort(), required: true, submitOnChange: true

                                def conditionAttributeName = conditions[index-1].attributeName
                                if (conditionAttributeName != null && conditionAttributeName != "") {
                                    def attribute = attributes.find{element -> element.name == conditionAttributeName}
                                    if (attribute != null) {
                                        if (attribute.dataType == "ENUM") {
                                            input "conditionComparison_${conditionId}", "enum", title: "Comparison", options: [comparison_Is, comparison_IsNot], required: true, submitOnChange: true

                                            def conditionComparison = conditions[index-1].comparison
                                            if (conditionComparison != null && conditionComparison != "") {
                                                if (attribute.values) {
                                                    input "conditionValue_${conditionId}", "enum", title: "Value", options: attribute.values, required: true
                                                } else {
                                                    input "conditionValue_${conditionId}", "string", title: "Value", required: true
                                                }
                                            }
                                            
                                        } else if (attribute.dataType == "NUMBER") {
                                            input "conditionComparison_${conditionId}", "enum", title: "Comparison", options: [comparison_EqualTo, comparison_NotEqualTo, comparison_GreaterThan, comparison_LessThan, comparison_GreaterThanOrEqualTo, comparison_LessThanOrEqualTo], required: true, submitOnChange: true

                                            def conditionComparison = conditions[index-1].comparison
                                            if (conditionComparison != null && conditionComparison != "") {
                                                input "conditionValue_${conditionId}", "number", title: "Value", range: "*..*", required: true
                                            }
                                            
                                        } else if (attribute.dataType == "BOOLEAN") {
                                            input "conditionValue_${conditionId}", "enum", title: "Value", options: [bool_True, bool_False], required: true
                                            
                                        } else {
                                            input "conditionComparison_${conditionId}", "enum", title: "Comparison", options: [comparison_Is, comparison_IsNot], required: true, submitOnChange: true

                                            def conditionComparison = conditions[index-1].comparison
                                            if (conditionComparison != null && conditionComparison != "") {
                                                input "conditionValue_${conditionId}", "string", title: "Value", required: true
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (conditionType == conditionType_Mode) {
                            def modes = location.modes*.name.unique().sort()
                            input "conditionMode_${conditionId}", "enum", title: "Mode(s)", options: modes, multiple: true, required: true
                            
                        } else if (conditionType == conditionType_Time) {
                            input "conditionStartType_${conditionId}", "enum", title: "Starting at", options: [triggerType_AtSpecificTime, triggerType_AtSunrise, triggerType_AtSunset], required: true, submitOnChange: true
                            
                            def conditionStartType = conditions[index-1].startType
                            if (conditionStartType != null && conditionStartType != "") {
                                if (conditionStartType == triggerType_AtSpecificTime) {
                                    input "conditionStartTime_${conditionId}", "time", title: "Start Time", required: true
                                } else if (conditionStartType == triggerType_AtSunrise) {
                                    input "conditionStartOffset_${conditionId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                                } else if (conditionStartType == triggerType_AtSunset) {
                                    input "conditionStartOffset_${conditionId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                                }
                            }

                            input "conditionEndType_${conditionId}", "enum", title: "Ending at", options: [triggerType_AtSpecificTime, triggerType_AtSunrise, triggerType_AtSunset], required: true, submitOnChange: true
                            
                            def conditionEndType = conditions[index-1].endType
                            if (conditionEndType != null && conditionEndType != "") {
                                if (conditionEndType == triggerType_AtSpecificTime) {
                                    input "conditionEndTime_${conditionId}", "time", title: "End Time", required: true
                                } else if (conditionEndType == triggerType_AtSunrise) {
                                    input "conditionEndOffset_${conditionId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                                } else if (conditionEndType == triggerType_AtSunset) {
                                    input "conditionEndOffset_${conditionId}", "number", title: "Offset in minutes (+/-)", range: "*..*", required: false
                                }
                            }
                            
                        } else if (conditionType == conditionType_Day) {
                            input "conditionDay_${conditionId}", "enum", title: "Day(s) of the Week", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], multiple: true, required: true
                        }
                
                        showConditionMore = true
                    } else {
                        showConditionMore = false
                    }
                } else {
                    showConditionMore = false
                }
            }
        }
        if (showConditionMore) {
            section() {
                input "conditionMore", "bool", title: "Add More Conditions?", required: true, submitOnChange: true
            }
        }
        
        def showActionMore = false
        for (int index = 1; index <= Math.max(numActions, 1); index++) {
            def actionId = -1            
            if (index <= actions.size()) {
                actionId = actions[index-1].id
            } else if (actions.size() > 0) {
                actionId = actions[-1].id + 1
            } else {
                actionId = 1
            }
            def sectionTitle = ""
            if (index == 1) {
                sectionTitle = "Then do this:"
            } else {
                sectionTitle = "AND then do this:"
            }
            section(sectionTitle) {
                input "actionType_${actionId}", "enum", title: "Action", options: [actionType_ControlDevice, actionType_ChangeMode, actionType_ExecuteRoutine, actionType_SendNotification], required: true, submitOnChange: true
                
                if (index <= actions.size()) {
                    def actionType = actions[index-1].type
                    if (actionType != null && actionType != "") {
                        if (actionType == actionType_ControlDevice) {
                            input "actionDevice_${actionId}", "capability.actuator", title: "Device", required: true, submitOnChange: true

                            def actionDevice = actions[index-1].device
                            if (actionDevice != null) {
                                def commands = actionDevice.supportedCommands
                                input "actionCommandName_${actionId}", "enum", title: "Command", options: commands*.name.unique().sort(), required: true
                            }
                            
                        } else if (actionType == actionType_ChangeMode) {
                            def modes = location.modes*.name.unique().sort()
                            input "actionMode_${actionId}", "enum", title: "Mode", options: modes, required: true
                            
                        } else if (actionType == actionType_ExecuteRoutine) {
                            def routines = location.helloHome?.getPhrases()*.label.unique().sort()
                            input "actionRoutine_${actionId}", "enum", title: "Routine(s)", options: routines, multiple: true, required: true
                            
                        } else if (actionType == actionType_SendNotification) {
                            input "actionNotificationType_${actionId}", "enum", title: "Type(s)", options: [notificationType_Push, notificationType_Text, notificationType_Log], multiple: true, required: true, submitOnChange: true
                            
                            def actionNotificationType = actions[index-1].notificationType
                            if (actionNotificationType != null) {
                                if (actionNotificationType.contains(notificationType_Text)) {
                                    input "actionNotificationPhone_${actionId}", "phone", title: "Phone Number", required: true
                                }
                                
                                input "actionNotificationMessage_${actionId}", "text", title: "Message", required: true
                            }
                        }
                        
                        showActionMore = true
                    } else {
                        showActionMore = false
                    }
                } else {
                    showActionMore = false
                }
            }
        }
        if (showActionMore) {
            section() {
                input "actionMore", "bool", title: "Add More Actions?", required: true, submitOnChange: true
            }
        }
        
        section() {
            label title: "Assign a name", required: true
        }
    }
}

def readTriggers() {    
    def triggers = []
    def triggerTypes = settings.findAll{key, value -> key.startsWith("triggerType_") && value != null && value != ""}
    for (triggerType in triggerTypes) {
        def split = triggerType.key.split("_")
        if (split.size() >= 2) {
            def idString = split[-1]
            if (idString.isInteger()) {
                def triggerId = idString.toInteger()

                triggers << [
                    id: triggerId,
                    type: triggerType.value, 
                    device: settings["triggerDevice_${triggerId}"], 
                    attributeName: settings["triggerAttributeName_${triggerId}"],
                    comparison: settings["triggerComparison_${triggerId}"],
                    value: settings["triggerValue_${triggerId}"],
                    mode: settings["triggerMode_${triggerId}"],
                    routine: settings["triggerRoutine_${triggerId}"],
                    time: settings["triggerTime_${triggerId}"],
                    offset: settings["triggerOffset_${triggerId}"]]
            }
        }
    }
    return triggers.sort{ it.id }
}

def readConditions() {    
    def conditions = []
    def conditionTypes = settings.findAll{key, value -> key.startsWith("conditionType") && value != null && value != ""}
    for (conditionType in conditionTypes) {
        def split = conditionType.key.split("_")
        if (split.size() >= 2) {
            def idString = split[-1]
            if (idString.isInteger()) {
                def conditionId = idString.toInteger()

                conditions << [
                    id: conditionId,
                    type: conditionType.value, 
                    device: settings["conditionDevice_${conditionId}"], 
                    attributeName: settings["conditionAttributeName_${conditionId}"], 
                    comparison: settings["conditionComparison_${conditionId}"], 
                    value: settings["conditionValue_${conditionId}"],
                    mode: settings["conditionMode_${conditionId}"],
                    startType: settings["conditionStartType_${conditionId}"],
                    startTime: settings["conditionStartTime_${conditionId}"],
                    startOffset: settings["conditionStartOffset_${conditionId}"],
                    endType: settings["conditionEndType_${conditionId}"],
                    endTime: settings["conditionEndTime_${conditionId}"],
                    endOffset: settings["conditionEndOffset_${conditionId}"],
                    day: settings["conditionDay_${conditionId}"]]
            }
        }
    }
    return conditions.sort{ it.id }
}

def readActions() {
    def actions = []
    def actionTypes = settings.findAll{key, value -> key.startsWith("actionType") && value != null && value != ""}
    for (actionType in actionTypes) {
        def split = actionType.key.split("_")
        if (split.size() >= 2) {
            def idString = split[-1]
            if (idString.isInteger()) {
                def actionId = idString.toInteger()

                actions << [
                    id: actionId,
                    type: actionType.value, 
                    device: settings["actionDevice_${actionId}"], 
                    commandName: settings["actionCommandName_${actionId}"], 
                    mode: settings["actionMode_${actionId}"], 
                    routine: settings["actionRoutine_${actionId}"],
                    notificationType: settings["actionNotificationType_${actionId}"],
                    notificationPhone: settings["actionNotificationPhone_${actionId}"],
                    notificationMessage: settings["actionNotificationMessage_${actionId}"]]
            }
        }
    }
    return actions.sort{ it.id }
}

def installed() {
    try {
        log.debug "Installed with settings: ${settings}"

        initialize()
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def updated() {
    try {
        log.debug "Updated with settings: ${settings}"

        unsubscribe()
        unschedule()
        initialize()
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def initialize() {
    if (enabled == false) {
        log.debug "Automation Disabled"
        return
    }

    def triggers = readTriggers()
    for (trigger in triggers) {
        if (trigger.type == triggerType_DeviceChanges) {
            log.debug "Subscribing to ${trigger.device}.${trigger.attributeName}"
            
            subscribe(trigger.device, trigger.attributeName, deviceHandler)
            
        } else if (trigger.type == triggerType_ModeChanges) {
            log.debug "Subscribing to mode ${trigger.mode}"
            
            subscribe(location, "mode", modeHandler)
            
        } else if (trigger.type == triggerType_RoutineExectutes) {
            log.debug "Subscribing to routine ${trigger.routine}"
            
            subscribe(location, "routineExecuted", routineHandler)
            
        } else if (trigger.type == triggerType_AtSunrise) {
            log.debug "Subscribing to sunrise"
            
            subscribe(location, "sunriseTime", sunriseTimeHandler)
            scheduleSunrise(location.currentValue("sunriseTime"), trigger.offset)
            
        } else if (trigger.type == triggerType_AtSunset) {
            log.debug "Subscribing to sunset"
            
            subscribe(location, "sunsetTime", sunsetTimeHandler)
            scheduleSunset(location.currentValue("sunsetTime"), trigger.offset)
            
        } else if (trigger.type == triggerType_AtSpecificTime) {
            log.debug "Subscribing to time ${trigger.time}"
            
            schedule(trigger.time, timeHandler)
            
        } else {
            log.debug "UNKNOWN TRIGGER: ${trigger}"
            
        }
    }
}

def sunriseTimeHandler(evt) {
    try {
        log.debug "Received sunrise time event"

        def triggers = readTriggers()
        for (trigger in triggers) {
            if (trigger.type == triggerType_AtSunrise) {
                scheduleSunrise(evt.value, trigger.offset)
            }
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def sunsetTimeHandler(evt) {
    try {
        log.debug "Received sunset time event"

        def triggers = readTriggers()
        for (trigger in triggers) {
            if (trigger.type == triggerType_AtSunset) {
                scheduleSunset(evt.value, trigger.offset)
            }
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def scheduleSunrise(sunriseString, offset) {
    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)
    def sunriseTimeWithOffset = new Date(sunriseTime.time + (offset * 60 * 1000))

    log.debug "Scheduling for: $sunriseTimeWithOffset (sunrise is $sunriseTime)"

    runOnce(sunriseTimeWithOffset, sunriseHandler, [overwrite: false])    
}

def scheduleSunset(sunsetString, offset) {
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
    def sunsetTimeWithOffset = new Date(sunsetTime.time + (offset * 60 * 1000))

    log.debug "Scheduling for: $sunsetTimeWithOffset (sunset is $sunsetTime)"

    runOnce(sunsetTimeWithOffset, sunsetHandler, [overwrite: false])
}

def deviceHandler(evt) {
    try {
        log.debug "Received device event: ${evt.device}.${evt.name} is ${evt.value} at ${evt.date}"

        def result = false
        def triggers = readTriggers()
        for (trigger in triggers) {
            if (trigger.type == triggerType_DeviceChanges) {
                if (trigger.device.id == evt.device.id && trigger.attributeName == evt.name) {
    log.debug "Checking trigger: ${trigger}"
                    
                    if (checkDeviceStatus(trigger.device, trigger.attributeName, trigger.comparison, trigger.value, evt.value)) {
                        result = true
                        break;
                    }
                }
            }
        }

        if (result) {
        log.debug "Triggers satisfied"
            result = checkConditions()
            if (result) {
            log.debug "Conditions satisfied"
                performActions()
            } else {
            log.debug "Conditions not satisfied"
            }
        } else {
        log.debug "Triggers not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def modeHandler(evt) {
    try {
        log.debug "Received mode event: ${evt.descriptionText} at ${evt.date}"

        def result = false
        def triggers = readTriggers()
        for (trigger in triggers) {
            if (trigger.type == triggerType_ModeChanges) {
    log.debug "Checking trigger: ${trigger}"
                
                if (checkMode(trigger.mode, evt.value)) {
                    result = true
                    break;
                }
            }
        }

        if (result) {
        log.debug "Triggers satisfied"
            result = checkConditions()
            if (result) {
            log.debug "Conditions satisfied"
                performActions()
            } else {
            log.debug "Conditions not satisfied"
            }
        } else {
        log.debug "Triggers not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def routineHandler(evt) {
    try {
        log.debug "Received routine event: ${evt.displayName} at ${evt.date}"

        def result = false
        def triggers = readTriggers()
        for (trigger in triggers) {
            if (trigger.type == triggerType_RoutineExectutes) {
    log.debug "Checking trigger: ${trigger}"
                
                if (checkRoutine(trigger.routine, evt.displayName)) {
                    result = true
                    break;
                }
            }
        }

        if (result) {
        log.debug "Triggers satisfied"
            result = checkConditions()
            if (result) {
            log.debug "Conditions satisfied"
                performActions()
            } else {
            log.debug "Conditions not satisfied"
            }
        } else {
        log.debug "Triggers not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def sunriseHandler(evt) {
    try {
        log.debug "Received sunrise event"

        def result = checkConditions()
        if (result) {
            log.debug "Conditions satisfied"
            performActions()
        } else {
            log.debug "Conditions not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def sunsetHandler(evt) {
    try {
        log.debug "Received sunset event"

        def result = checkConditions()
        if (result) {
            log.debug "Conditions satisfied"
            performActions()
        } else {
            log.debug "Conditions not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def timeHandler(evt) {
    try {
        log.debug "Received time event"

        def result = checkConditions()
        if (result) {
            log.debug "Conditions satisfied"
            performActions()
        } else {
            log.debug "Conditions not satisfied"
        }
        
    } catch (e) {
        sendPush("${e}")
        throw e
    }
}

def checkConditions() {
    def conditions = readConditions()
    for (condition in conditions) {
    log.debug "Checking condition: ${condition}"
    
        def result = true    
        if (condition.type == conditionType_DeviceStatus) {
            def deviceValue = condition.device.currentValue(condition.attributeName)
            result = checkDeviceStatus(condition.device, condition.attributeName, condition.comparison, condition.value, deviceValue)
            
        } else if (condition.type == conditionType_Mode) {
            result = checkMode(condition.mode, location.mode)
            
        } else if (condition.type == conditionType_Time) {
            result = checkTimeRange(condition.startType, condition.startTime, condition.startOffset, condition.endType, condition.endTime, condition.endOffset)
            
        } else if (condition.type == conditionType_Day) {
            result = checkDays(condition.day)
            
        } else {
            log.debug "UNKNOWN CONDITION: ${condition}"
            return false
        }
        
        if (conditionCombine == conditionCombine == conditionCombine_Any) {
            if (result == true) {
                return true
            }
        } else {
            if (result == false) {
                return false
            }
        }
    }
    
    if (conditionCombine == conditionCombine == conditionCombine_Any) {
        return false
    } else {
        return true
    }
}

def checkDeviceStatus(device, attributeName, comparison, expectedValue, actualValue) {
    def attributes = device.supportedAttributes
    def attribute = attributes.find{element -> element.name == attributeName}    
    if (attribute != null) {
        if (attribute.dataType == "NUMBER") {
            if (comparison == comparison_EqualTo) {
            log.debug "Comparing ${actualValue} == ${expectedValue}"
                return actualValue == expectedValue

            } else if (comparison == comparison_NotEqualTo) {
            log.debug "Comparing ${actualValue} != ${expectedValue}"
                return actualValue != expectedValue

            } else if (comparison == comparison_GreaterThan) {
            log.debug "Comparing ${actualValue} > ${expectedValue}"
                return actualValue > expectedValue

            } else if (comparison == comparison_LessThan) {
            log.debug "Comparing ${actualValue} < ${expectedValue}"
                return actualValue < expectedValue

            } else if (comparison == comparison_GreaterThanOrEqualTo) {
            log.debug "Comparing ${actualValue} >= ${expectedValue}"
                return actualValue >= expectedValue

            } else if (comparison == comparison_LessThanOrEqualTo) {
            log.debug "Comparing ${actualValue} <= ${expectedValue}"
                return actualValue <= expectedValue

            } else {
                log.debug "UNKNOWN COMPARISON: ${comparison}"

            }
        } else if (attribute.dataType == "BOOLEAN") {
            log.debug "Comparing ${actualValue} == ${expectedValue}"
        return actualValue == expectedValue
            
        } else {
            if (comparison == comparison_Is) {
                log.debug "Comparing ${actualValue} == ${expectedValue}"
                return actualValue == expectedValue

            } else if (comparison == comparison_IsNot) {
                log.debug "Comparing ${actualValue} != ${expectedValue}"
                return actualValue != expectedValue

            } else {
                log.debug "UNKNOWN COMPARISON: ${comparison}"

            }
        }
    }
    return false
}

def checkMode(modeList, mode) {
    return modeList.contains(mode)
}

def checkRoutine(routineList, routine) {
    return routineList.contains(routine)
}

def checkTimeRange(startType, startTime, startOffset, endType, endTime, endOffset) {
    def fromTime
    if (startType == triggerType_AtSunrise) {
        def sunriseString = location.currentValue("sunriseTime")
        def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)
        if (startOffset != null) {
            fromTime = new Date(sunriseTime.time + (startOffset * 60 * 1000))
        } else {
            fromTime = sunriseTime 
        }
        
    } else if (startType == triggerType_AtSunset) {
        def sunsetString = location.currentValue("sunsetTime")
        def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
        if (startOffset != null) {
            fromTime = new Date(sunsetTime.time + (startOffset * 60 * 1000))
        } else {
            fromTime = sunsetTime 
        }
        
    } else if (startType == triggerType_AtSpecificTime) {
        fromTime = startTime
        
    } else {
        log.debug "UNKNOWN START TYPE: ${startType}"
        return false
    }    
    
    def toTime
    if (endType == triggerType_AtSunrise) {
        def sunriseString = location.currentValue("sunriseTime")
        def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)
        if (endOffset != null) {
            toTime = new Date(sunriseTime.time + (endOffset * 60 * 1000))
        } else {
            toTime = sunriseTime 
        }
        
    } else if (endType == triggerType_AtSunset) {
        def sunsetString = location.currentValue("sunsetTime")
        def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
        if (endOffset != null) {
            toTime = new Date(sunsetTime.time + (endOffset * 60 * 1000))
        } else {
            toTime = sunsetTime 
        }
        
    } else if (endType == triggerType_AtSpecificTime) {
        toTime = endTime
        
    } else {
        log.debug "UNKNOWN END TYPE: ${endType}"
        return false
    }
    
    return timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
}

def checkDays(dayList) {
    def df = new java.text.SimpleDateFormat("EEEE")
    df.setTimeZone(location.timeZone)
    
    def day = df.format(new Date())
    return dayList.contains(day)
}

def performActions() {
    def actions = readActions()
    for (action in actions) {
        if (action.type == actionType_ControlDevice) {
            action.device."${action.commandName}"()
            
        } else if (action.type == actionType_ChangeMode) {
            location.setMode(action.mode)
            
        } else if (action.type == actionType_ExecuteRoutine) {
            for (routine in action.routine) {
                location.helloHome?.execute(routine)
            }
            
        } else if (action.type == actionType_SendNotification) {
            if (action.notificationType.contains(notificationType_Push)) {
                sendPushMessage(action.notificationMessage)
            }        
            if (action.notificationType.contains(notificationType_Text)) {
                sendSmsMessage(action.notificationPhone, action.notificationMessage)
            }
            if (action.notificationType.contains(notificationType_Log)) {
                sendNotificationEvent(action.notificationMessage)
            }
            
        } else {
            log.debug "UNKNOWN ACTION: ${action}"
        }
    }
}