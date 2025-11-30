sensor "button" pin 8
sensor "button2" pin 9
actuator "buzzer" pin 11

state "on" means "buzzer" becomes "high"
state "off" means "buzzer" becomes "low"

initial "off"

from "on" to "off" when "button" becomes "low" or "button2" becomes "low"
from "off" to "on" when "button" becomes "high" and "button2" becomes "high"

export "Switch!"