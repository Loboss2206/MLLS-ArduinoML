
//Wiring code generated from an ArduinoML model
// Application name: MelodyPlayer

long debounce = 200;
enum STATE {off, chorus, verse};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

            

	void setup(){
		pinMode(11, OUTPUT); // buzzer [Actuator]
		pinMode(12, INPUT); // button [Sensor]
	}
	void loop() {
			switch(currentState){

				case off:digitalWrite(11, LOW);
        	if(digitalRead(12) == HIGH) {
         	   currentState = verse;
        }
    	
				break;
				case chorus:
				tone(11, 262, 250);
				delay(325);
				noTone(11);
			
				tone(11, 262, 250);
				delay(325);
				noTone(11);
			
				tone(11, 262, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 330, 500);
				delay(650);
				noTone(11);
			
				tone(11, 294, 500);
				delay(650);
				noTone(11);
			
				tone(11, 262, 250);
				delay(325);
				noTone(11);
			
				tone(11, 330, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 262, 500);
				delay(650);
				noTone(11);
			
        	if(digitalRead(12) == LOW) {
         	   currentState = off;
        }
    	
				break;
				case verse:
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 220, 500);
				delay(650);
				noTone(11);
			
				tone(11, 220, 500);
				delay(650);
				noTone(11);
			
				tone(11, 294, 250);
				delay(325);
				noTone(11);
			
				tone(11, 262, 250);
				delay(325);
				noTone(11);
			
				tone(11, 247, 250);
				delay(325);
				noTone(11);
			
				tone(11, 220, 250);
				delay(325);
				noTone(11);
			
				tone(11, 196, 500);
				delay(650);
				noTone(11);
			
        	if(digitalRead(12) == LOW) {
         	   currentState = off;
        }
    	
				break;
		}
	}
	
