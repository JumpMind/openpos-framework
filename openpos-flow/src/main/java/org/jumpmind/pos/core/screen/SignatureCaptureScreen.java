package org.jumpmind.pos.core.screen;

public class SignatureCaptureScreen extends Screen {
    private static final long serialVersionUID = 1L;

    private String title;
    private String text;
    private String textIcon;
    private String signatureData;
    private String signatureMediaType;
    private ActionItem saveAction;
    

    public SignatureCaptureScreen() {
        setScreenType(ScreenType.SignatureCapture);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSignatureData() {
        return signatureData;
    }

    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }
    
    public String getTextIcon() {
        return textIcon;
    }

    public void setTextIcon(String textIcon) {
        this.textIcon = textIcon;
    }

    public String getSignatureMediaType() {
        return signatureMediaType;
    }

    public void setSignatureMediaType(String signatureMediaType) {
        this.signatureMediaType = signatureMediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ActionItem getSaveAction() {
        return saveAction;
    }

    public void setSaveAction(ActionItem saveAction) {
        this.saveAction = saveAction;
    }
    
}
