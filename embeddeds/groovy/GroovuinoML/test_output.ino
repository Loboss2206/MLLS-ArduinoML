// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean button2BounceGuard = false;
long button2LastDebounceTime = 0;

void setup(){
  pinMode(8, INPUT);  // button [Sensor]
  pinMode(9, INPUT);  // button2 [Sensor]
  pinMode(11, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			if( ((digitalRead(8) == LOW && buttonBounceGuard) || (digitalRead(9) == LOW && button2BounceGuard)) ) {
				currentState = off;
			}
		break;
		case off:
			digitalWrite(11,LOW);
			if( ((digitalRead(8) == HIGH && buttonBounceGuard) && (digitalRead(9) == HIGH && button2BounceGuard)) ) {
				currentState = on;
			}
		break;
	}
}
