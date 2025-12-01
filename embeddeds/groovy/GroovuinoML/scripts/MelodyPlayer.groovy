actuator "buzzer" pin 12
sensor "button1" pin 9
sensor "button2" pin 10


state "off" means "buzzer" becomes "low"

state "chorus" means "buzzer" becomes "C4_4" and 
      "buzzer" becomes "C4_4" and
      "buzzer" becomes "C4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "E4_2" and
      "buzzer" becomes "D4_2" and
      "buzzer" becomes "C4_4" and
      "buzzer" becomes "E4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "C4_2"

state "verse" means "buzzer" becomes "D4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "A3_2" and
      "buzzer" becomes "A3_2" and
      "buzzer" becomes "D4_4" and
      "buzzer" becomes "C4_4" and
      "buzzer" becomes "B3_4" and
      "buzzer" becomes "A3_4" and
      "buzzer" becomes "G3_2"

initial "off"

from "off" to "chorus" when "button1" becomes "high"
from "off" to "verse"  when "button2" becomes "high"

from "chorus" to "off" when "button1" becomes "low" or "button1" becomes "high"
from "verse"  to "off" when "button2" becomes "low" or "button2" becomes "high"

export "MelodyPlayer"