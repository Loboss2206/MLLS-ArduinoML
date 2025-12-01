grammar ArduinoMl

entry App:
	'app' name=ID 'initial' 'state' initial=[State:ID]
	'{'
		'bricks'
		bricks+=Brick
		(bricks+=Brick)* 
		
		'states'
		states+=State
		( states+=State)* 		
	'}';

Brick :
	(Actuator | Sensor);

Actuator:
	{infer Actuator}
	'Actuator' name=ID ':' outputPin=INT;

Sensor :
	{infer Sensor}
	'Sensor'  name=ID ':' inputPin=INT;
	
State :
	name=ID '{'
		actions+=Action
		(actions+=Action)* 
		transition=Transition
	'}';


Action :
	actuator=[Actuator:ID] '<=' value=(Signal | Note);

Comparison:
    sensor=[Sensor:ID] 'is' value=Signal;

Operator:
    op=('and' | 'or');

Condition:
    left=Comparison (ops+=Operator rights+=Comparison)* '=>' next=[State:ID];

Transition:
    conditions+=Condition*;

Signal:
    value=(HIGH | LOW);

Note:
    'NOTE' '(' pitch=PITCH ',' duration=INT ')';

terminal HIGH : 'HIGH';
terminal LOW : 'LOW';
terminal PITCH: /[A-G](b|#)?[0-8]/;

hidden terminal WS: /\s+/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal INT returns number: /[0-9]+/;
terminal STRING: /"[^"]*"|'[^']*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
