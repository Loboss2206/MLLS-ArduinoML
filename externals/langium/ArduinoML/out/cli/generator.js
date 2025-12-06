"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateInoFile = void 0;
const fs_1 = __importDefault(require("fs"));
const langium_1 = require("langium");
const path_1 = __importDefault(require("path"));
const cli_util_1 = require("./cli-util");
function generateInoFile(app, filePath, destination) {
    const data = (0, cli_util_1.extractDestinationAndName)(filePath, destination);
    const generatedFilePath = `${path_1.default.join(data.destination, data.name)}.ino`;
    const fileNode = new langium_1.CompositeGeneratorNode();
    compile(app, fileNode);
    if (!fs_1.default.existsSync(data.destination)) {
        fs_1.default.mkdirSync(data.destination, { recursive: true });
    }
    fs_1.default.writeFileSync(generatedFilePath, (0, langium_1.toString)(fileNode));
    return generatedFilePath;
}
exports.generateInoFile = generateInoFile;
function compile(app, fileNode) {
    var _a;
    fileNode.append(`
//Wiring code generated from an ArduinoML model
// Application name: ${app.name}

long debounce = 200;
enum STATE { ${app.states.map(s => s.name).join(', ')} };

STATE currentState = ${(_a = app.initial.ref) === null || _a === void 0 ? void 0 : _a.name};
`, langium_1.NL);
    for (const brick of app.bricks) {
        if ('inputPin' in brick) {
            fileNode.append(`
bool ${brick.name}BounceGuard = false;
long ${brick.name}LastDebounceTime = 0;
`, langium_1.NL);
        }
    }
    fileNode.append(`
void setup() {`);
    for (const brick of app.bricks) {
        if ('inputPin' in brick) {
            compileSensor(brick, fileNode);
        }
        else {
            compileActuator(brick, fileNode);
        }
    }
    fileNode.append(`
}

void loop() {
    switch(currentState) {`, langium_1.NL);
    for (const state of app.states) {
        compileState(state, fileNode);
    }
    fileNode.append(`
    }
}
`, langium_1.NL);
}
function compileActuator(actuator, fileNode) {
    fileNode.append(`
    pinMode(${actuator.outputPin}, OUTPUT); // ${actuator.name} [Actuator]`);
}
function compileSensor(sensor, fileNode) {
    fileNode.append(`
    pinMode(${sensor.inputPin}, INPUT); // ${sensor.name} [Sensor]`);
}
function compileState(state, fileNode) {
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
function compileAction(action, fileNode) {
    var _a, _b, _c;
    if ('pitch' in action.value) {
        const note = action.value;
        const duration = Math.round(1000 / note.duration);
        fileNode.append(`
            tone(${(_a = action.actuator.ref) === null || _a === void 0 ? void 0 : _a.outputPin}, ${pitchToFrequency(note.pitch)}, ${duration});
            delay(${duration * 1.30});
            noTone(${(_b = action.actuator.ref) === null || _b === void 0 ? void 0 : _b.outputPin});
        `);
    }
    else {
        fileNode.append(`
            digitalWrite(${(_c = action.actuator.ref) === null || _c === void 0 ? void 0 : _c.outputPin}, ${action.value.value});`);
    }
}
function compilePredicate(cmp) {
    const sensor = cmp.sensor.ref;
    return `digitalRead(${sensor.inputPin}) == ${cmp.value.value}`;
}
function compileBooleanExpression(cond) {
    let leftCond = cond;
    while (leftCond.expression.$type === 'BinaryExpression') {
        leftCond = leftCond.expression.condition;
    }
    let expr = compilePredicate(leftCond.expression);
    function testForOperator(c) {
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
function compileTransition(t, fileNode) {
    const conditionCode = compileBooleanExpression(t.condition);
    const next = t.next.ref.name;
    fileNode.append(`
            if (${conditionCode}) {
                currentState = ${next};
            }
        `);
}
/* Music notes */
function pitchToFrequency(pitch) {
    const noteBase = { 'C': -9, 'D': -7, 'E': -5, 'F': -4, 'G': -2, 'A': 0, 'B': 2 };
    const match = pitch.match(/^([A-G])([#b]?)([0-8]?)$/);
    if (!match)
        return 440;
    let [, base, accidental, octaveStr] = match;
    let octave = octaveStr ? parseInt(octaveStr) : 4;
    let n = noteBase[base];
    if (accidental === '#')
        n += 1;
    else if (accidental === 'b')
        n -= 1;
    n += (octave - 4) * 12;
    return Math.round(440 * Math.pow(2, n / 12));
}
//# sourceMappingURL=generator.js.map