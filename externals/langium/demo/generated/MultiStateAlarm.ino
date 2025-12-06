
//Wiring code generated from an ArduinoML model
// Application name: MultiStateAlarm

long debounce = 200;
enum STATE { ready, noise, light };

STATE currentState = ready;


bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;


void setup() {
    pinMode(11, OUTPUT); // buzzer [Actuator]
    pinMode(12, OUTPUT); // led [Actuator]
    pinMode(8, INPUT); // button [Sensor]
}

void loop() {
    switch(currentState) {

		
        case ready:
            digitalWrite(11, LOW);
            digitalWrite(12, LOW);
            if (digitalRead(8) == HIGH) {
                currentState = noise;
            }
        
            break;
		
        case noise:
            digitalWrite(11, HIGH);
            digitalWrite(12, LOW);
            if (digitalRead(8) == HIGH) {
                currentState = light;
            }
        
            break;
		
        case light:
            digitalWrite(11, LOW);
            digitalWrite(12, HIGH);
            if (digitalRead(8) == HIGH) {
                currentState = ready;
            }
        
            break;
    }
}

