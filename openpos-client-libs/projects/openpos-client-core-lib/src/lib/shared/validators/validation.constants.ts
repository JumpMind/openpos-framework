import { GreaterThanEqZeroValidator } from './greater-than-eq-zero-validator';
import { GreaterThanZeroValidator } from './greater-than-zero-validator';
import { MinValueValidator } from './min-value-validator';
import { MaxValueValidator } from './max-value-validator';

export const ValidationConstants = {

    /**
     * Defines the list of validators that can be dynamically created by
     * specification on the server.  name must match the name of the
     * corresponding server side validator.
     */
    validators: [
        { name: 'MinValueValidator', validatorClass: MinValueValidator },
        { name: 'MaxValueValidator', validatorClass: MaxValueValidator },
        { name: 'GT_0',              validatorClass: GreaterThanZeroValidator },
        { name: 'GTE_0',             validatorClass: GreaterThanEqZeroValidator },
    ],

};
