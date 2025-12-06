"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ArduinoMlValidator = exports.registerValidationChecks = void 0;
/**
 * Register custom validation checks.
 */
function registerValidationChecks(services) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.ArduinoMlValidator;
    const checks = {
        App: validator.checkNothing,
        Action: validator.checkAction,
    };
    registry.register(checks, validator);
}
exports.registerValidationChecks = registerValidationChecks;
/**
 * Implementation of custom validations.
 */
class ArduinoMlValidator {
    checkNothing(app, accept) {
        if (app.name) {
            const firstChar = app.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept('warning', 'App name should start with a capital.', { node: app, property: 'name' });
            }
        }
    }
    checkAction(action, accept) {
        var _a;
        if ('pitch' in action.value) {
            if (((_a = action.actuator.ref) === null || _a === void 0 ? void 0 : _a.$type) !== 'Buzzer') {
                accept('error', 'Only a Buzzer can play a Note', { node: action });
            }
        }
    }
}
exports.ArduinoMlValidator = ArduinoMlValidator;
//# sourceMappingURL=arduino-ml-validator.js.map