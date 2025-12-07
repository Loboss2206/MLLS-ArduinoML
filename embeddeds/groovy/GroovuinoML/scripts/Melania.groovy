sensor "B1" pin 8
sensor "B2" pin 9
actuator "buzzer" pin 11

note "C" frequency 262 duration 500
note "D" frequency 294 duration 500
note "E" frequency 330 duration 500
note "F" frequency 349 duration 500
note "G" frequency 392 duration 500
note "A" frequency 440 duration 500
note "B" frequency 494 duration 500
note "C_high" frequency 523 duration 500

state "idle" means "buzzer" becomes "low"
state("melody1").means(play("C").on("buzzer"))
state("melody2").means(play("E").on("buzzer"))
state("melody3").means(play("G").on("buzzer"))
state("finale").means(play("C_high").on("buzzer"))

initial "idle"

from "idle" to "melody1" when "B1" becomes "high"
from "idle" to "melody2" when "B2" becomes "high"

from "melody1" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "melody1" to "melody3" when "B2" becomes "high"

from "melody2" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "melody2" to "finale" when "B1" becomes "high"

from "melody3" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "finale" to "idle" when "B1" becomes "low" and "B2" becomes "low"

export "Melania_MelodyPlayer"
