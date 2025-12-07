// Wiring code generated from an ArduinoML model
// Application name: DualCheckAlarm

long debounce = 200;

enum STATE {off, on};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean lightSensorBounceGuard = false;
long lightSensorLastDebounceTime = 0;

void setup(){
  pinMode(8, INPUT);  // button [Sensor]
  pinMode(9, INPUT);  // lightSensor [Sensor]
  pinMode(11, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case off:
			digitalWrite(11,LOW);
			lightSensorBounceGuard = millis() - lightSensorLastDebounceTime > debounce;
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( ((digitalRead(8) == HIGH && buttonBounceGuard) && (digitalRead(9) == HIGH && lightSensorBounceGuard)) ) {
				lightSensorLastDebounceTime = millis();
				buttonLastDebounceTime = millis();
				currentState = on;
			}
		break;
		case on:
			digitalWrite(11,HIGH);
			lightSensorBounceGuard = millis() - lightSensorLastDebounceTime > debounce;
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( ((digitalRead(8) == LOW && buttonBounceGuard) || (digitalRead(9) == LOW && lightSensorBounceGuard)) ) {
				lightSensorLastDebounceTime = millis();
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
	}
}
