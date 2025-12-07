// MelodyPlayer: Play musical sequences with button + light sensor controls
// Based on Langium MelodyPlayer
// Button triggers chorus, light sensor triggers verse

sensor "button" pin 9
sensor "lightSensor" pin 10
buzzer "buzzer" pin 12

// Define notes (NOTE duration: 1=whole, 2=half, 4=quarter, 8=eighth)
// Using 500ms for quarter note as base
note "C4" frequency 262 duration 500
note "D4" frequency 294 duration 500
note "E4" frequency 330 duration 500
note "G3" frequency 196 duration 500
note "A3" frequency 220 duration 500
note "B3" frequency 247 duration 500

// Off state
state "off" means "buzzer" becomes "low"

// Chorus: C C C D E D C E D D C (simplified from Langium)
state("chorus").means(
    play("C4").on("buzzer")
).and(
    play("C4").on("buzzer")
).and(
    play("C4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("E4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("C4").on("buzzer")
).and(
    play("E4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("C4").on("buzzer")
)

// Verse: D D D D A A D C B A G (simplified from Langium)
state("verse").means(
    play("D4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("A3").on("buzzer")
).and(
    play("A3").on("buzzer")
).and(
    play("D4").on("buzzer")
).and(
    play("C4").on("buzzer")
).and(
    play("B3").on("buzzer")
).and(
    play("A3").on("buzzer")
).and(
    play("G3").on("buzzer")
)

initial "off"

from "off" to "chorus" when "button" becomes "high"
from "off" to "verse" when "lightSensor" becomes "high"
from "chorus" to "off" when "button" becomes "low"
from "chorus" to "off" when "button" becomes "high"
from "verse" to "off" when "lightSensor" becomes "low"
from "verse" to "off" when "lightSensor" becomes "high"

export "MelodyPlayer"
