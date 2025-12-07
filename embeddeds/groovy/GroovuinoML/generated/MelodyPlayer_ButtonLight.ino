// Wiring code generated from an ArduinoML model
// Application name: MelodyPlayer

long debounce = 200;

enum STATE {off, chorus, verse};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean lightSensorBounceGuard = false;
long lightSensorLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(10, INPUT);  // lightSensor [Sensor]
  pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case off:
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			lightSensorBounceGuard = millis() - lightSensorLastDebounceTime > debounce;
			if( (digitalRead(9) == HIGH && buttonBounceGuard) ) {
				buttonLastDebounceTime = millis();
				currentState = chorus;
			}
			if( (digitalRead(10) == HIGH && lightSensorBounceGuard) ) {
				lightSensorLastDebounceTime = millis();
				currentState = verse;
			}
		break;
		case chorus:
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 330, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			tone(12, 330, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if( (digitalRead(9) == LOW && buttonBounceGuard) ) {
				buttonLastDebounceTime = millis();
				currentState = off;
			}
			if( (digitalRead(9) == HIGH && buttonBounceGuard) ) {
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
		case verse:
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 220, 500);
			delay(500);
			noTone(12);
			tone(12, 220, 500);
			delay(500);
			noTone(12);
			tone(12, 294, 500);
			delay(500);
			noTone(12);
			tone(12, 262, 500);
			delay(500);
			noTone(12);
			tone(12, 247, 500);
			delay(500);
			noTone(12);
			tone(12, 220, 500);
			delay(500);
			noTone(12);
			tone(12, 196, 500);
			delay(500);
			noTone(12);
			lightSensorBounceGuard = millis() - lightSensorLastDebounceTime > debounce;
			if( (digitalRead(10) == LOW && lightSensorBounceGuard) ) {
				lightSensorLastDebounceTime = millis();
				currentState = off;
			}
			if( (digitalRead(10) == HIGH && lightSensorBounceGuard) ) {
				lightSensorLastDebounceTime = millis();
				currentState = off;
			}
		break;
	}
}
