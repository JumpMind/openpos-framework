package org.jumpmind.pos.core.screen;

public interface IPromptScreen {
    public static final String TYPE_ALPHANUMERICTEXT = "ALPHANUMERICTEXT";
    public static final String TYPE_ALPHANUMERICPASSWORD = "ALPHANUMERICPASSWORD";
    public static final String TYPE_PHONE = "PHONE";
    public static final String TYPE_CURRENCYTEXT = "CURRENCYTEXT";
    public static final String TYPE_NUMERICTEXT = "NUMERICTEXT";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_NO_YEAR_DATE = "NOYEARDATE";
    public static final String TYPE_ONOFF = "ONOFF";
    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_MONEY = "MONEY";

    public Integer getMinLength();
    public void setMinLength(Integer minLength);

    public Integer getMaxLength();
    public void setMaxLength(Integer maxLength);

    public String getPromptIcon();
    public void setPromptIcon(String promptIcon);

    public String getPlaceholderText();
    public void setPlaceholderText(String placeholderText);

    public String getResponseText();
    public void setResponseText(String responseText);

    public boolean isEditable();
    public void setEditable(boolean editable);
    
    public String getResponseType();
    public void setResponseType(String responseType);
    
    public String getText();
    public void setText(String promptText);
    
}
