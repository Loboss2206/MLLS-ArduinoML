import { ValidationAcceptor, ValidationChecks } from 'langium';
import { ArduinoMlAstType, App, Action } from './generated/ast';
import type { ArduinoMlServices } from './arduino-ml-module';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: ArduinoMlServices) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.ArduinoMlValidator;
    const checks: ValidationChecks<ArduinoMlAstType> = {
        App: validator.checkNothing,
        Action: validator.checkAction,
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class ArduinoMlValidator {

    checkNothing(app: App, accept: ValidationAcceptor): void {
        if (app.name) {
            const firstChar = app.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept('warning', 'App name should start with a capital.', { node: app, property: 'name' });
            }
        }
    }

    checkAction(action: Action, accept: ValidationAcceptor): void {
        if ('pitch' in action.value) {
            if (action.actuator.ref?.$type !== 'Buzzer') {
                accept('error', 'Only a Buzzer can play a Note', { node: action });
            }
        }
    }

}
