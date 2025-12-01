package io.github.mosser.arduinoml.samples

import io.github.mosser.arduinoml.dsl.ArduinoML

object MultiStateAlarm extends App with ArduinoML {

  this hasForName "MultiStateAlarm!"

  val button = declare.aSensor()    named "button" boundToPin 8
  val led    = declare.anActuator() named "led"    boundToPin 12
  val buzzer = declare.anActuator() named "buzzer" boundToPin 11

  val off = state named "off" executing (
    led --> low,
    buzzer --> low
  )

  val buzzing = state named "buzzing" executing (
    buzzer --> high,
    led --> low
  )

  val ledOn = state named "ledOn" executing (
    led --> high,
    buzzer --> low
  )

  off.isInitial

  transitions {
    off     -> buzzing when (button is high)
    buzzing -> ledOn   when (button is high)
    ledOn   -> off     when (button is high)
  }

  exportToWiring

}
