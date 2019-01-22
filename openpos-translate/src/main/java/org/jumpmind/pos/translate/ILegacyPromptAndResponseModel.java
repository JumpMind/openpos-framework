package org.jumpmind.pos.translate;

public interface ILegacyPromptAndResponseModel {
    public void setResponseText(String value);
    public String[] getArguments();
    public String getMinLength();
    public String getMaxLength();
    public String getResponseText();
    public String getPromptText();
    public void setScanned(boolean value);
    public boolean getResponseEnabled();
}
