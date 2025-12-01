import fs from 'fs';
import { CompositeGeneratorNode, NL, toString } from 'langium';
import path from 'path';
import { Action, Actuator, App, BooleanExpression, Note, Predicate, Sensor, Signal, State, Transition } from '../language-server/generated/ast';
import { extractDestinationAndName } from './cli-util';


export function generateInoFile(app: App, filePath: string, destination: string | undefined): string {
	const data = extractDestinationAndName(filePath, destination);
	const generatedFilePath = `${path.join(data.destination, data.name)}.ino`;

	const fileNode = new CompositeGeneratorNode();
	compile(app, fileNode)


	if (!fs.existsSync(data.destination)) {
		fs.mkdirSync(data.destination, { recursive: true });
	}
	fs.writeFileSync(generatedFilePath, toString(fileNode));
	return generatedFilePath;
}


function compile(app: App, fileNode: CompositeGeneratorNode) {
	fileNode.append(
		`
//Wiring code generated from an ArduinoML model
// Application name: `+ app.name + `

long debounce = 200;
enum STATE {`+ app.states.map(s => s.name).join(', ') + `};

STATE currentState = `+ app.initial.ref?.name + `;`
		, NL);

	for (const brick of app.bricks) {
		if ("inputPin" in brick) {
			fileNode.append(`
bool `+ brick.name + `BounceGuard = false;
long `+ brick.name + `LastDebounceTime = 0;

            `, NL);
		}
	}
	fileNode.append(`
	void setup(){`);
	for (const brick of app.bricks) {
		if ("inputPin" in brick) {
			compileSensor(brick, fileNode);
		} else {
			compileActuator(brick, fileNode);
		}
	}


	fileNode.append(`
	}
	void loop() {
			switch(currentState){`, NL)
	for (const state of app.states) {
		compileState(state, fileNode)
	}
	fileNode.append(`
		}
	}
	`, NL);
}


function compileActuator(actuator: Actuator, fileNode: CompositeGeneratorNode) {
	fileNode.append(`
		pinMode(`+ actuator.outputPin + `, OUTPUT); // ` + actuator.name + ` [Actuator]`)
}


function compileSensor(sensor: Sensor, fileNode: CompositeGeneratorNode) {
	fileNode.append(`
		pinMode(`+ sensor.inputPin + `, INPUT); // ` + sensor.name + ` [Sensor]`)
}

function compileState(state: State, fileNode: CompositeGeneratorNode) {
	fileNode.append(`
                case ${state.name}:`);

	for (const action of state.actions) {
		compileAction(action, fileNode);
	}

	for (const tr of state.transition) {
		compileTransition(tr, fileNode);
	}

	fileNode.append(`
                break;`);
}

function compileAction(action: Action, fileNode: CompositeGeneratorNode) {
	if ('pitch' in action.value) {
		const note: Note = action.value as Note;
		const duration: number = Math.round(1000 / note.duration);
		fileNode.append(`
			tone(${action.actuator.ref?.outputPin}, ${pitchToFrequency(note.pitch)}, ${duration});
			delay(${duration * 1.30});
			noTone(${action.actuator.ref?.outputPin});
		`);
	}
	else {
		fileNode.append(`digitalWrite(${action.actuator.ref?.outputPin}, ${(action.value as Signal).value});`);
	}
}

function compilePredicate(cmp: Predicate): string {
	const sensor = cmp.sensor.ref!;
	return `digitalRead(${sensor.inputPin}) == ${cmp.value.value}`;
}

function compileBooleanExpression(cond: BooleanExpression): string {
	let leftCond = cond;
	while (leftCond.expression.$type === 'BinaryExpression') {
		leftCond = leftCond.expression.condition;
	}

	let expr = compilePredicate(leftCond.expression);

	function testForOperator(c: BooleanExpression): string {
		if (c.expression.$type === 'BinaryExpression') {
			const op = c.expression.op === 'and' ? '&&' : '||';
			return `${op} ${testForOperator(c.expression.condition)}`;
		}
		return compilePredicate(c.expression);
	}

	const suffix = testForOperator(cond);
	return (suffix.startsWith('&&') || suffix.startsWith('||'))
		? `${expr} ${suffix}`
		: expr;
}

function compileTransition(t: Transition, fileNode: CompositeGeneratorNode) {
	const conditionCode = compileBooleanExpression(t.condition);
	const next = t.next.ref!.name;

	fileNode.append(`
        if (${conditionCode}) {
            currentState = ${next};
        }
    `);
}

/* Music notes */

function pitchToFrequency(pitch: string): number {
	const noteBase: Record<string, number> = { 'C': -9, 'D': -7, 'E': -5, 'F': -4, 'G': -2, 'A': 0, 'B': 2 };

	const match = pitch.match(/^([A-G])([#b]?)([0-8]?)$/);
	if (!match) return 440;

	let [, base, accidental, octaveStr] = match;
	let octave = octaveStr ? parseInt(octaveStr) : 4;

	let n = noteBase[base];
	if (accidental === '#') n += 1;
	else if (accidental === 'b') n -= 1;
	n += (octave - 4) * 12;

	return Math.round(440 * Math.pow(2, n / 12));
}