// Scenario 1: Simple Alarm (1 button)
// Press button -> LED and buzzer ON
// Release button -> LED and buzzer OFF

sensor "button" pin 8
led "led" pin 12
buzzer "buzzer" pin 11

state "off" means "led" becomes "low" and "buzzer" becomes "low"
state "on" means "led" becomes "high" and "buzzer" becomes "high"

initial "off"

from "off" to "on" when "button" becomes "high"
from "on" to "off" when "button" becomes "low"

export "SimpleAlarm"
