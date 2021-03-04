package org.jumpmind.pos.util.model;

public class PrintMessage extends Message {
    String html;

    public PrintMessage() {
        super("Print");
    }

    public PrintMessage(String html) {
        super("Print");
        this.html = html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }
}
