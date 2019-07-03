package org.jumpmind.pos.core.model;

import org.jumpmind.pos.core.ui.validator.GreaterThanZeroValidator;
import org.jumpmind.pos.core.ui.validator.IValidatorSpec;
import org.jumpmind.pos.core.ui.validator.MinValueValidator;

/**
 * Use IValidator and its subclasses instead
 * @deprecated
 */
@Deprecated
public final class Validator {
    /** @deprecated
     * Enforce that a field must have a value greater than 0. 
     */
    public static final IValidatorSpec GT_0 = new GreaterThanZeroValidator();
    /** 
     * @deprecated
     */
    public static final IValidatorSpec GTE_0 = new MinValueValidator("GTE_0", 0);
}
