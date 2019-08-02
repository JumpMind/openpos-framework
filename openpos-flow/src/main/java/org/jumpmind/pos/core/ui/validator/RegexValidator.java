package org.jumpmind.pos.core.ui.validator;

/**
 * A client side validator specification used to pass a javacript
 * regular expression to the client which can be used for validating field contents.
 */
public class RegexValidator implements IValidatorSpec {

    private static final long serialVersionUID = 1L;
    
    private String javascriptRegex;
    private String flags;

    RegexValidator() {
    }
    
    public RegexValidator(String javascriptRegex) {
        this.javascriptRegex= javascriptRegex;
    }

    public RegexValidator(String javascriptRegex, String flags) {
        this.javascriptRegex= javascriptRegex;
        this.flags = flags;
    }
    
    public String getJavascriptRegex() {
        return javascriptRegex;
    }

    public void setJavascriptRegex(String javascriptRegex) {
        this.javascriptRegex = javascriptRegex;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
    
    

}
