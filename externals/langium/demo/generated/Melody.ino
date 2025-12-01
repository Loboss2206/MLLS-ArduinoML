
//Wiring code generated from an ArduinoML model
// Application name: MelodyPlayer

long debounce = 200;
enum STATE {off, chorus, verse};

STATE currentState = off;

bool button1BounceGuard = false;
long button1LastDebounceTime = 0;

            

bool button2BounceGuard = false;
long button2LastDebounceTime = 0;

            

	void setup(){
		pinMode(10, OUTPUT); // led [Actuator]
		pinMode(11, OUTPUT); // buzzer [Actuator]
		pinMode(8, INPUT); // button1 [Sensor]
		pinMode(9, INPUT); // button2 [Sensor]
	}
	void loop() {
			switch(currentState){

				case off:
					digitalWrite(10,LOW);
					digitalWrite(11,LOW);
            if (digitalRead(8) == HIGH) {
                currentState = chorus;
            }
        
            if (digitalRead(9) == HIGH) {
                currentState = verse;
            }
        
				break;
				case chorus:
					digitalWrite(10,HIGH);
            if (digitalRead(8) == HIGH && digitalRead(9) == LOW) {
                currentState = chorus;
            }
        
            if (digitalRead(8) == LOW && digitalRead(9) == HIGH) {
                currentState = verse;
            }
        
            if (digitalRead(8) == HIGH && digitalRead(9) == HIGH) {
                currentState = off;
            }
        
            if (digitalRead(8) == LOW && digitalRead(9) == LOW) {
                currentState = off;
            }
        
				break;
				case verse:
					digitalWrite(11,HIGH);
            if (digitalRead(8) == HIGH) {
                currentState = chorus;
            }
        
            if (digitalRead(9) == HIGH) {
                currentState = verse;
            }
        
				break;
		}
	}
	
