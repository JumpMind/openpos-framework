package org.jumpmind.pos.util.model;

public class PrintMessage extends Message {
    String html;
    String printerId;

    public PrintMessage() {
        super("Print");
    }

    public PrintMessage(String html, String printerId) {
        super("Print");
        this.html = html;
        this.printerId = printerId;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterId() {
        return printerId;
    }
}
