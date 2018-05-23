package org.jumpmind.pos.core.screen;

public class PrintPreviewScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private String printText = "";

    	public PrintPreviewScreen() {
    		this.setType(ScreenType.PrintPreview);
    	}
    
	public String getPrintText() {
		return printText;
	}

	public void setPrintText(String printText) {
		this.printText = printText;
	}
}
