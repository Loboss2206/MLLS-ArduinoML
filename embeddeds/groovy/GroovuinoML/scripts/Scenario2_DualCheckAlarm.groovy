// Scenario 2: Dual-Check Alarm (button + light sensor)
// Buzzer activates if and only if BOTH button is pressed AND light sensor detects light
// Releasing either one stops the buzzer

sensor "button" pin 8
sensor "lightSensor" pin 9
buzzer "buzzer" pin 11

state "off" means "buzzer" becomes "low"
state "on" means "buzzer" becomes "high"

initial "off"

from "off" to "on" when "button" becomes "high" and "lightSensor" becomes "high"
from "on" to "off" when "button" becomes "low" or "lightSensor" becomes "low"

export "DualCheckAlarm"
