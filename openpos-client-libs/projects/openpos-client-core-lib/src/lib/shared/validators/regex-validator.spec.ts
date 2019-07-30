import { RegexValidator, RegexValidatorSpec } from './regex-validator';
import { FormControl, ValidationErrors } from '@angular/forms';

describe('RegexValidator Test', () => {
    let validator: RegexValidator;
    let validatorSpec: RegexValidatorSpec;
    let formControl: FormControl;

    beforeEach(() => {
        validatorSpec = {name: 'RegexValidator', javascriptRegex: '', flags: null};
        validator = new RegexValidator(validatorSpec);
        formControl = new FormControl();
    });

    it('#ensure control with value that doesnt match pattern fails validation', () => {
        // Pattern to fail validation if value has a number in it
        validatorSpec.javascriptRegex = '^[^0-9]*$';
        validatorSpec.flags = 'g';
        formControl.setValue('abcdefgh1');
        const errors: ValidationErrors = validator.validationFunc(formControl);
        expect(errors.pattern.valid).toBe(false);
    });

    it('#ensure control with value that matches pattern passes validation', () => {
        // Pattern to fail validation if value has a number in it
        validatorSpec.javascriptRegex = '^[^0-9]*$';
        validatorSpec.flags = 'g';
        formControl.setValue('abcdefgh');
        const errors: ValidationErrors = validator.validationFunc(formControl);
        expect(errors).toBe(null);
    });

});
