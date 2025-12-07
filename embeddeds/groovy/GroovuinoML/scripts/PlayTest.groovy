sensor "button" pin 8
actuator "buzzer" pin 11

note "C" frequency 262 duration 500

state "off" means "buzzer" becomes "low"
state "on" means play "C" on "buzzer"

initial "off"

from "off" to "on" when "button" becomes "high"
from "on" to "off" when "button" becomes "low"

export "PlayTest"
