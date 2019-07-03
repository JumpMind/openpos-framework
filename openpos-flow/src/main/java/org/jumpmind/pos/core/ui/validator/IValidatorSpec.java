package org.jumpmind.pos.core.ui.validator;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * Provides a mechanism to specify validators on the server
 * side that can be applied to a given {@link org.jumpmind.pos.core.model.FormField}.
 * The name you assign to your implementation of the IValidatorSpec
 * should match the name of a corresponding IValidator implementation in the 
 * client application.  Your implementation could also specify custom fields
 * that are set on the server side and passed over to the client.
 */
public interface IValidatorSpec extends Serializable {

    @JsonGetter
    default public String getName() {
        return this.getClass().getSimpleName();
    }
}
