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
		transition+=Transition
		(transition+=Transition)*
	'}';


Action :
	actuator=[Actuator:ID] '<=' value=(Signal | Note);

Predicate:
    sensor=[Sensor:ID] 'is' value=Signal;

BinaryExpression:
    op=('and' | 'or') condition=BooleanExpression;

BooleanExpression:
    expression=(Predicate | BinaryExpression)+;

Transition:
    condition=BooleanExpression '=>' next=[State:ID];

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
