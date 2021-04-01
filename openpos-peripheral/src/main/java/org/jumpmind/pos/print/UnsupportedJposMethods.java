package org.jumpmind.pos.print;

import jpos.JposException;

public  class UnsupportedJposMethods {

    public PrintNotSupportedException unsupported() {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }
    public boolean getCapCompareFirmwareVersion() throws JposException {
        throw unsupported();
    }
    public boolean getCapConcurrentPageMode() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecPageMode() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpPageMode() throws JposException {
        throw unsupported();
    }
    public boolean getCapUpdateFirmware() throws JposException {
        throw unsupported();
    }
    public String getPageModeArea() throws JposException {
        throw unsupported();
    }
    public int getPageModeDescriptor() throws JposException {
        throw unsupported();
    }
    public int getPageModeHorizontalPosition() throws JposException {
        throw unsupported();
    }
    public void setPageModeHorizontalPosition(int position) throws JposException {
        throw unsupported();
    }
    public String getPageModePrintArea() throws JposException {
        throw unsupported();
    }
    public void setPageModePrintArea(String area) throws JposException {
        throw unsupported();
    }
    public int getPageModePrintDirection() throws JposException {
        throw unsupported();
    }
    public void setPageModePrintDirection(int direction) throws JposException {
        throw unsupported();
    }
    public int getPageModeStation() throws JposException {
        throw unsupported();
    }
    public void setPageModeStation(int station) throws JposException {
        throw unsupported();
    }
    public int getPageModeVerticalPosition() throws JposException {
        throw unsupported();
    }
    public void setPageModeVerticalPosition(int position) throws JposException {
        throw unsupported();
    }
    public void clearPrintArea() throws JposException {
        throw unsupported();
    }
    public void compareFirmwareVersion(String firmwareFileName, int[] result) throws JposException {
        throw unsupported();
    }
    public void pageModePrint(int control) throws JposException {
        throw unsupported();
    }
    public void updateFirmware(String firmwareFileName) throws JposException {
        throw unsupported();
    }
    public boolean getCapStatisticsReporting() throws JposException {
        throw unsupported();
    }
    public boolean getCapUpdateStatistics() throws JposException {
        throw unsupported();
    }
    public void resetStatistics(String statisticsBuffer) throws JposException {
        throw unsupported();
    }
    public void retrieveStatistics(String[] statisticsBuffer) throws JposException {
        throw unsupported();
    }
    public void updateStatistics(String statisticsBuffer) throws JposException {
        throw unsupported();
    }
    public boolean getCapMapCharacterSet() throws JposException {
        throw unsupported();
    }
    public boolean getMapCharacterSet() throws JposException {
        throw unsupported();
    }
    public void setMapCharacterSet(boolean mapCharacterSet) throws JposException {
        throw unsupported();
    }
    public String getRecBitmapRotationList() throws JposException {
        throw unsupported();
    }
    public String getSlpBitmapRotationList() throws JposException {
        throw unsupported();
    }
    public int getCapJrnCartridgeSensor() throws JposException {
        throw unsupported();
    }
    public int getCapJrnColor() throws JposException {
        throw unsupported();
    }
    public int getCapRecCartridgeSensor() throws JposException {
        throw unsupported();
    }
    public int getCapRecColor() throws JposException {
        throw unsupported();
    }
    public int getCapRecMarkFeed() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpBothSidesPrint() throws JposException {
        throw unsupported();
    }
    public int getCapSlpCartridgeSensor() throws JposException {
        throw unsupported();
    }
    public int getCapSlpColor() throws JposException {
        throw unsupported();
    }
    public int getCartridgeNotify() throws JposException {
        throw unsupported();
    }
    public void setCartridgeNotify(int notify) throws JposException {
        throw unsupported();
    }
    public int getJrnCartridgeState() throws JposException {
        throw unsupported();
    }
    public int getJrnCurrentCartridge() throws JposException {
        throw unsupported();
    }
    public void setJrnCurrentCartridge(int cartridge) throws JposException {
        throw unsupported();
    }
    public int getRecCartridgeState() throws JposException {
        throw unsupported();
    }
    public int getRecCurrentCartridge() throws JposException {
        throw unsupported();
    }
    public void setRecCurrentCartridge(int cartridge) throws JposException {
        throw unsupported();
    }
    public int getSlpCartridgeState() throws JposException {
        throw unsupported();
    }
    public int getSlpCurrentCartridge() throws JposException {
        throw unsupported();
    }
    public void setSlpCurrentCartridge(int cartridge) throws JposException {
        throw unsupported();
    }
    public int getSlpPrintSide() throws JposException {
        throw unsupported();
    }
    public void changePrintSide(int side) throws JposException {
        throw unsupported();
    }
    public void markFeed(int type) throws JposException {
        throw unsupported();
    }
    public void deleteInstance() throws JposException {
        throw unsupported();
    }
    public int getCapPowerReporting() throws JposException {
        throw unsupported();
    }
    public int getPowerNotify() throws JposException {
        throw unsupported();
    }
    public void setPowerNotify(int powerNotify) throws JposException {
        throw unsupported();
    }
    public int getPowerState() throws JposException {
        throw unsupported();
    }
    public int getCapCharacterSet() throws JposException {
        throw unsupported();
    }
    public boolean getCapConcurrentJrnRec() throws JposException {
        throw unsupported();
    }
    public boolean getCapConcurrentJrnSlp() throws JposException {
        throw unsupported();
    }
    public boolean getCapConcurrentRecSlp() throws JposException {
        throw unsupported();
    }
    public boolean getCapCoverSensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrn2Color() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnBold() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnDwide() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnDwideDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnEmptySensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnItalic() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnNearEndSensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnPresent() throws JposException {
        throw unsupported();
    }
    public boolean getCapJrnUnderline() throws JposException {
        throw unsupported();
    }
    public boolean getCapRec2Color() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecBarCode() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecBitmap() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecBold() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecDwide() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecDwideDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecEmptySensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecItalic() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecLeft90() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecNearEndSensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecPapercut() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecPresent() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecRight90() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecRotate180() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecStamp() throws JposException {
        throw unsupported();
    }
    public boolean getCapRecUnderline() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlp2Color() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpBarCode() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpBitmap() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpBold() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpDwide() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpDwideDhigh() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpEmptySensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpFullslip() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpItalic() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpLeft90() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpNearEndSensor() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpPresent() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpRight90() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpRotate180() throws JposException {
        throw unsupported();
    }
    public boolean getCapSlpUnderline() throws JposException {
        throw unsupported();
    }
    public boolean getCapTransaction() throws JposException {
        throw unsupported();
    }
    public boolean getAsyncMode() throws JposException {
        throw unsupported();
    }
    public void setAsyncMode(boolean asyncMode) throws JposException {
        throw unsupported();
    }
    public int getCharacterSet() throws JposException {
        throw unsupported();
    }
    public void setCharacterSet(int characterSet) throws JposException {
        throw unsupported();
    }
    public String getCharacterSetList() throws JposException {
        throw unsupported();
    }
    public int getErrorLevel() throws JposException {
        throw unsupported();
    }
    public int getErrorStation() throws JposException {
        throw unsupported();
    }
    public String getErrorString() throws JposException {
        throw unsupported();
    }
    public boolean getFlagWhenIdle() throws JposException {
        throw unsupported();
    }
    public void setFlagWhenIdle(boolean flagWhenIdle) throws JposException {
        throw unsupported();
    }
    public String getFontTypefaceList() throws JposException {
        throw unsupported();
    }
    public boolean getJrnLetterQuality() throws JposException {
        throw unsupported();
    }
    public void setJrnLetterQuality(boolean jrnLetterQuality) throws JposException {
        throw unsupported();
    }
    public int getJrnLineChars() throws JposException {
        throw unsupported();
    }
    public void setJrnLineChars(int jrnLineChars) throws JposException {
        throw unsupported();
    }
    public String getJrnLineCharsList() throws JposException {
        throw unsupported();
    }
    public int getJrnLineHeight() throws JposException {
        throw unsupported();
    }
    public void setJrnLineHeight(int jrnLineHeight) throws JposException {
        throw unsupported();
    }
    public int getJrnLineSpacing() throws JposException {
        throw unsupported();
    }
    public void setJrnLineSpacing(int jrnLineSpacing) throws JposException {
        throw unsupported();
    }
    public int getJrnLineWidth() throws JposException {
        throw unsupported();
    }
    public boolean getJrnNearEnd() throws JposException {
        throw unsupported();
    }
    public int getMapMode() throws JposException {
        throw unsupported();
    }
    public void setMapMode(int mapMode) throws JposException {
        throw unsupported();
    }
    public int getOutputID() throws JposException {
        throw unsupported();
    }
    public String getRecBarCodeRotationList() throws JposException {
        throw unsupported();
    }
    public boolean getRecLetterQuality() throws JposException {
        throw unsupported();
    }
    public void setRecLetterQuality(boolean recLetterQuality) throws JposException {
        throw unsupported();
    }
    public int getRecLineChars() throws JposException {
        throw unsupported();
    }
    public void setRecLineChars(int recLineChars) throws JposException {
        throw unsupported();
    }
    public String getRecLineCharsList() throws JposException {
        throw unsupported();
    }
    public int getRecLineHeight() throws JposException {
        throw unsupported();
    }
    public void setRecLineHeight(int recLineHeight) throws JposException {
        throw unsupported();
    }
    public int getRecLinesToPaperCut() throws JposException {
        throw unsupported();
    }
    public int getRecLineWidth() throws JposException {
        // TODO this would be nice to support.
        throw unsupported();
    }
    public int getRecSidewaysMaxChars() throws JposException {
        throw unsupported();
    }
    public int getRecSidewaysMaxLines() throws JposException {
        throw unsupported();
    }
    public int getRotateSpecial() throws JposException {
        throw unsupported();
    }
    public void setRotateSpecial(int rotateSpecial) throws JposException {
        throw unsupported();
    }
    public String getSlpBarCodeRotationList() throws JposException {
        throw unsupported();
    }
    public boolean getSlpEmpty() throws JposException {
        throw unsupported();
    }
    public boolean getSlpLetterQuality() throws JposException {
        throw unsupported();
    }
    public void setSlpLetterQuality(boolean recLetterQuality) throws JposException {
        throw unsupported();
    }
    public int getSlpLineChars() throws JposException {
        throw unsupported();
    }
    public void setSlpLineChars(int recLineChars) throws JposException {
        throw unsupported();
    }
    public String getSlpLineCharsList() throws JposException {
        throw unsupported();
    }
    public int getSlpLineHeight() throws JposException {
        throw unsupported();
    }
    public void setSlpLineHeight(int recLineHeight) throws JposException {
        throw unsupported();
    }
    public int getSlpLinesNearEndToEnd() throws JposException {
        throw unsupported();
    }
    public int getSlpLineSpacing() throws JposException {
        throw unsupported();
    }
    public void setSlpLineSpacing(int recLineSpacing) throws JposException {
        throw unsupported();
    }
    public int getSlpLineWidth() throws JposException {
        throw unsupported();
    }
    public int getSlpMaxLines() throws JposException {
        throw unsupported();
    }
    public boolean getSlpNearEnd() throws JposException {
        throw unsupported();
    }
    public int getSlpSidewaysMaxChars() throws JposException {
        throw unsupported();
    }
    public int getSlpSidewaysMaxLines() throws JposException {
        throw unsupported();
    }
    public void beginInsertion(int timeout) throws JposException {
        throw unsupported();
    }
    public void clearOutput() throws JposException {
        throw unsupported();
    }
    public void endInsertion() throws JposException {
        throw unsupported();
    }
    public void printImmediate(int station, String data) throws JposException {
        throw unsupported();
    }
    public void printTwoNormal(int stations, String data1, String data2) throws JposException {
        // fiscal printer?
        throw unsupported();
    }
    public void rotatePrint(int station, int rotation) throws JposException {
        throw unsupported();
    }
    public void setBitmap(int bitmapNumber, int station, String fileName, int width, int alignment) throws JposException {
        throw unsupported();
    }
    public void setLogo(int location, String data) throws JposException {
        throw unsupported();
    }
    public void transactionPrint(int station, int control) throws JposException {
        throw unsupported();
    }
    public void validateData(int station, String data) throws JposException {
        throw unsupported();
    }
    public String getCheckHealthText() throws JposException {
        throw unsupported();
    }
    public boolean getClaimed() throws JposException {
        throw unsupported();
    }
    public String getDeviceServiceDescription() throws JposException {
        throw unsupported();
    }
    public boolean getFreezeEvents() throws JposException {
        throw unsupported();
    }
    public void setFreezeEvents(boolean freezeEvents) throws JposException {
        throw unsupported();
    }
    public String getPhysicalDeviceDescription() throws JposException {
        throw unsupported();
    }
    public String getPhysicalDeviceName() throws JposException {
        throw unsupported();
    }
    public int getState() throws JposException {
        throw unsupported();
    }
    public void checkHealth(int level) throws JposException {
        throw unsupported();
    }
    public void directIO(int command, int[] data, Object object) throws JposException {
        throw unsupported();
    }
}
