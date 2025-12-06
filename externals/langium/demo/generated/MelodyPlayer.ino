
//Wiring code generated from an ArduinoML model
// Application name: MelodyPlayer

long debounce = 200;
enum STATE { off, chorus, verse };

STATE currentState = off;


bool button1BounceGuard = false;
long button1LastDebounceTime = 0;


bool button2BounceGuard = false;
long button2LastDebounceTime = 0;


void setup() {
    pinMode(12, OUTPUT); // buzzer [Actuator]
    pinMode(9, INPUT); // button1 [Sensor]
    pinMode(10, INPUT); // button2 [Sensor]
}

void loop() {
    switch(currentState) {

		
        case off:
            digitalWrite(12, LOW);
            if (digitalRead(9) == HIGH) {
                currentState = chorus;
            }
        
            if (digitalRead(10) == HIGH) {
                currentState = verse;
            }
        
            break;
		
        case chorus:
            tone(12, 262, 250);
            delay(325);
            noTone(12);
        
            tone(12, 262, 250);
            delay(325);
            noTone(12);
        
            tone(12, 262, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 330, 500);
            delay(650);
            noTone(12);
        
            tone(12, 294, 500);
            delay(650);
            noTone(12);
        
            tone(12, 262, 250);
            delay(325);
            noTone(12);
        
            tone(12, 330, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 262, 500);
            delay(650);
            noTone(12);
        
            if (digitalRead(9) == HIGH || digitalRead(9) == HIGH) {
                currentState = off;
            }
        
            break;
		
        case verse:
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 220, 500);
            delay(650);
            noTone(12);
        
            tone(12, 220, 500);
            delay(650);
            noTone(12);
        
            tone(12, 294, 250);
            delay(325);
            noTone(12);
        
            tone(12, 262, 250);
            delay(325);
            noTone(12);
        
            tone(12, 247, 250);
            delay(325);
            noTone(12);
        
            tone(12, 220, 250);
            delay(325);
            noTone(12);
        
            tone(12, 196, 500);
            delay(650);
            noTone(12);
        
            if (digitalRead(10) == HIGH || digitalRead(10) == HIGH) {
                currentState = off;
            }
        
            break;
    }
}

