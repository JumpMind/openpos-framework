package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.EventCallbacks;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;

public class LogPOSPrinter implements IOpenposPrinter {

    private PrinterCommands printerCommands = new PrinterCommandPlaceholders();
    private static final Logger log = Logger.getLogger(LogPOSPrinter.class);

    private StringBuilder buff = new StringBuilder(128);

    @Override
    public PrinterCommands getPrinterCommands() {
        return printerCommands;
    }

    @Override
    public void setPrinterCommands(PrinterCommands printerCommands) {

    }

    @Override
    public void setOutputStream(OutputStream outputStream) {

    }

    @Override
    public void printImage(InputStream image) {

    }

    @Override
    public String getCommand(String commandName) {
        return printerCommands.get(commandName);
    }

    @Override
    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    @Override
    public boolean getCapConcurrentPageMode() throws JposException {
        return false;
    }

    @Override
    public int getPrintWidth() {
        return 48;
    }

    @Override
    public boolean getCapRecPageMode() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpPageMode() throws JposException {
        return false;
    }

    @Override
    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    @Override
    public String getPageModeArea() throws JposException {
        return null;
    }

    @Override
    public int getPageModeDescriptor() throws JposException {
        return 0;
    }

    @Override
    public int getPageModeHorizontalPosition() throws JposException {
        return 0;
    }

    @Override
    public void setPageModeHorizontalPosition(int position) throws JposException {

    }

    @Override
    public String getPageModePrintArea() throws JposException {
        return null;
    }

    @Override
    public void setPageModePrintArea(String area) throws JposException {

    }

    @Override
    public int getPageModePrintDirection() throws JposException {
        return 0;
    }

    @Override
    public void setPageModePrintDirection(int direction) throws JposException {

    }

    @Override
    public int getPageModeStation() throws JposException {
        return 0;
    }

    @Override
    public void setPageModeStation(int station) throws JposException {

    }

    @Override
    public int getPageModeVerticalPosition() throws JposException {
        return 0;
    }

    @Override
    public void setPageModeVerticalPosition(int position) throws JposException {

    }

    @Override
    public void clearPrintArea() throws JposException {

    }

    @Override
    public void compareFirmwareVersion(String firmwareFileName, int[] result) throws JposException {

    }

    @Override
    public void pageModePrint(int control) throws JposException {

    }

    @Override
    public void updateFirmware(String firmwareFileName) throws JposException {

    }

    @Override
    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    @Override
    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    @Override
    public void resetStatistics(String statisticsBuffer) throws JposException {

    }

    @Override
    public void retrieveStatistics(String[] statisticsBuffer) throws JposException {

    }

    @Override
    public void updateStatistics(String statisticsBuffer) throws JposException {

    }

    @Override
    public boolean getCapMapCharacterSet() throws JposException {
        return false;
    }

    @Override
    public boolean getMapCharacterSet() throws JposException {
        return false;
    }

    @Override
    public void setMapCharacterSet(boolean mapCharacterSet) throws JposException {

    }

    @Override
    public String getRecBitmapRotationList() throws JposException {
        return null;
    }

    @Override
    public String getSlpBitmapRotationList() throws JposException {
        return null;
    }

    @Override
    public int getCapJrnCartridgeSensor() throws JposException {
        return 0;
    }

    @Override
    public int getCapJrnColor() throws JposException {
        return 0;
    }

    @Override
    public int getCapRecCartridgeSensor() throws JposException {
        return 0;
    }

    @Override
    public int getCapRecColor() throws JposException {
        return 0;
    }

    @Override
    public int getCapRecMarkFeed() throws JposException {
        return 0;
    }

    @Override
    public boolean getCapSlpBothSidesPrint() throws JposException {
        return false;
    }

    @Override
    public int getCapSlpCartridgeSensor() throws JposException {
        return 0;
    }

    @Override
    public int getCapSlpColor() throws JposException {
        return 0;
    }

    @Override
    public int getCartridgeNotify() throws JposException {
        return 0;
    }

    @Override
    public void setCartridgeNotify(int notify) throws JposException {

    }

    @Override
    public int getJrnCartridgeState() throws JposException {
        return 0;
    }

    @Override
    public int getJrnCurrentCartridge() throws JposException {
        return 0;
    }

    @Override
    public void setJrnCurrentCartridge(int cartridge) throws JposException {

    }

    @Override
    public int getRecCartridgeState() throws JposException {
        return 0;
    }

    @Override
    public int getRecCurrentCartridge() throws JposException {
        return 0;
    }

    @Override
    public void setRecCurrentCartridge(int cartridge) throws JposException {

    }

    @Override
    public int getSlpCartridgeState() throws JposException {
        return 0;
    }

    @Override
    public int getSlpCurrentCartridge() throws JposException {
        return 0;
    }

    @Override
    public void setSlpCurrentCartridge(int cartridge) throws JposException {

    }

    @Override
    public int getSlpPrintSide() throws JposException {
        return 0;
    }

    @Override
    public void changePrintSide(int side) throws JposException {

    }

    @Override
    public void markFeed(int type) throws JposException {

    }

    @Override
    public void deleteInstance() throws JposException {

    }

    @Override
    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    @Override
    public int getPowerNotify() throws JposException {
        return 0;
    }

    @Override
    public void setPowerNotify(int powerNotify) throws JposException {

    }

    @Override
    public int getPowerState() throws JposException {
        return 0;
    }

    @Override
    public int getCapCharacterSet() throws JposException {
        return 0;
    }

    @Override
    public boolean getCapConcurrentJrnRec() throws JposException {
        return false;
    }

    @Override
    public boolean getCapConcurrentJrnSlp() throws JposException {
        return false;
    }

    @Override
    public boolean getCapConcurrentRecSlp() throws JposException {
        return false;
    }

    @Override
    public boolean getCapCoverSensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrn2Color() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnBold() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnDwide() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnDwideDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnEmptySensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnItalic() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnNearEndSensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnPresent() throws JposException {
        return false;
    }

    @Override
    public boolean getCapJrnUnderline() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRec2Color() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecBarCode() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecBitmap() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecBold() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecDwide() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecDwideDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecEmptySensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecItalic() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecLeft90() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecNearEndSensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecPapercut() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecPresent() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecRight90() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecRotate180() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecStamp() throws JposException {
        return false;
    }

    @Override
    public boolean getCapRecUnderline() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlp2Color() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpBarCode() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpBitmap() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpBold() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpDwide() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpDwideDhigh() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpEmptySensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpFullslip() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpItalic() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpLeft90() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpNearEndSensor() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpPresent() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpRight90() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpRotate180() throws JposException {
        return false;
    }

    @Override
    public boolean getCapSlpUnderline() throws JposException {
        return false;
    }

    @Override
    public boolean getCapTransaction() throws JposException {
        return false;
    }

    @Override
    public boolean getAsyncMode() throws JposException {
        return false;
    }

    @Override
    public void setAsyncMode(boolean asyncMode) throws JposException {

    }

    @Override
    public int getCharacterSet() throws JposException {
        return 0;
    }

    @Override
    public void setCharacterSet(int characterSet) throws JposException {

    }

    @Override
    public String getCharacterSetList() throws JposException {
        return null;
    }

    @Override
    public boolean getCoverOpen() throws JposException {
        return false;
    }

    @Override
    public int getErrorLevel() throws JposException {
        return 0;
    }

    @Override
    public int getErrorStation() throws JposException {
        return 0;
    }

    @Override
    public String getErrorString() throws JposException {
        return null;
    }

    @Override
    public boolean getFlagWhenIdle() throws JposException {
        return false;
    }

    @Override
    public void setFlagWhenIdle(boolean flagWhenIdle) throws JposException {

    }

    @Override
    public String getFontTypefaceList() throws JposException {
        return null;
    }

    @Override
    public boolean getJrnEmpty() throws JposException {
        return false;
    }

    @Override
    public boolean getJrnLetterQuality() throws JposException {
        return false;
    }

    @Override
    public void setJrnLetterQuality(boolean jrnLetterQuality) throws JposException {

    }

    @Override
    public int getJrnLineChars() throws JposException {
        return 0;
    }

    @Override
    public void setJrnLineChars(int jrnLineChars) throws JposException {

    }

    @Override
    public String getJrnLineCharsList() throws JposException {
        return null;
    }

    @Override
    public int getJrnLineHeight() throws JposException {
        return 0;
    }

    @Override
    public void setJrnLineHeight(int jrnLineHeight) throws JposException {

    }

    @Override
    public int getJrnLineSpacing() throws JposException {
        return 0;
    }

    @Override
    public void setJrnLineSpacing(int jrnLineSpacing) throws JposException {

    }

    @Override
    public int getJrnLineWidth() throws JposException {
        return 0;
    }

    @Override
    public boolean getJrnNearEnd() throws JposException {
        return false;
    }

    @Override
    public int getMapMode() throws JposException {
        return 0;
    }

    @Override
    public void setMapMode(int mapMode) throws JposException {

    }

    @Override
    public int getOutputID() throws JposException {
        return 0;
    }

    @Override
    public String getRecBarCodeRotationList() throws JposException {
        return null;
    }

    @Override
    public boolean getRecEmpty() throws JposException {
        return false;
    }

    @Override
    public boolean getRecLetterQuality() throws JposException {
        return false;
    }

    @Override
    public void setRecLetterQuality(boolean recLetterQuality) throws JposException {

    }

    @Override
    public int getRecLineChars() throws JposException {
        return 0;
    }

    @Override
    public void setRecLineChars(int recLineChars) throws JposException {

    }

    @Override
    public String getRecLineCharsList() throws JposException {
        return null;
    }

    @Override
    public int getRecLineHeight() throws JposException {
        return 0;
    }

    @Override
    public void setRecLineHeight(int recLineHeight) throws JposException {

    }

    @Override
    public int getRecLineSpacing() throws JposException {
        return 0;
    }

    @Override
    public void setRecLineSpacing(int recLineSpacing) throws JposException {

    }

    @Override
    public int getRecLinesToPaperCut() throws JposException {
        return 0;
    }

    @Override
    public int getRecLineWidth() throws JposException {
        return 0;
    }

    @Override
    public boolean getRecNearEnd() throws JposException {
        return false;
    }

    @Override
    public int getRecSidewaysMaxChars() throws JposException {
        return 0;
    }

    @Override
    public int getRecSidewaysMaxLines() throws JposException {
        return 0;
    }

    @Override
    public int getRotateSpecial() throws JposException {
        return 0;
    }

    @Override
    public void setRotateSpecial(int rotateSpecial) throws JposException {

    }

    @Override
    public String getSlpBarCodeRotationList() throws JposException {
        return null;
    }

    @Override
    public boolean getSlpEmpty() throws JposException {
        return false;
    }

    @Override
    public boolean getSlpLetterQuality() throws JposException {
        return false;
    }

    @Override
    public void setSlpLetterQuality(boolean recLetterQuality) throws JposException {

    }

    @Override
    public int getSlpLineChars() throws JposException {
        return 0;
    }

    @Override
    public void setSlpLineChars(int recLineChars) throws JposException {

    }

    @Override
    public String getSlpLineCharsList() throws JposException {
        return null;
    }

    @Override
    public int getSlpLineHeight() throws JposException {
        return 0;
    }

    @Override
    public void setSlpLineHeight(int recLineHeight) throws JposException {

    }

    @Override
    public int getSlpLinesNearEndToEnd() throws JposException {
        return 0;
    }

    @Override
    public int getSlpLineSpacing() throws JposException {
        return 0;
    }

    @Override
    public void setSlpLineSpacing(int recLineSpacing) throws JposException {

    }

    @Override
    public int getSlpLineWidth() throws JposException {
        return 0;
    }

    @Override
    public int getSlpMaxLines() throws JposException {
        return 0;
    }

    @Override
    public boolean getSlpNearEnd() throws JposException {
        return false;
    }

    @Override
    public int getSlpSidewaysMaxChars() throws JposException {
        return 0;
    }

    @Override
    public int getSlpSidewaysMaxLines() throws JposException {
        return 0;
    }

    @Override
    public void beginInsertion(int timeout) throws JposException {

    }

    @Override
    public void beginRemoval(int timeout) throws JposException {

    }

    @Override
    public void clearOutput() throws JposException {

    }

    @Override
    public void cutPaper(int percentage) throws JposException {
        log.info("\n" + buff.toString());
        buff.setLength(0);
    }

    @Override
    public void endInsertion() throws JposException {

    }

    @Override
    public void endRemoval() throws JposException {

    }

    @Override
    public void printBarCode(int station, String data, int symbology, int height, int width, int alignment, int textPosition) throws JposException {
        switch (symbology) {
            case POSPrinterConst.PTR_BCS_Code128:
                buff.append("<BARCODE_CODE128>" + data + "</BARCODE_CODE128>\n");
                break;
            case POSPrinterConst.PTR_BCS_Code39:
                buff.append("<BARCODE_CODE39>" + data + "</BARCODE_CODE39>\n");
                break;
            default:
                buff.append("<BARCODE_" + symbology + "/>" + data + "<BARCODE_" + symbology + "/>\n");
                break;
        }
    }

    @Override
    public void printBitmap(int station, String fileName, int width, int alignment) throws JposException {

    }

    @Override
    public void printImmediate(int station, String data) throws JposException {

    }

    @Override
    public void printNormal(int station, String data) throws JposException {
        buff.append(data);
    }

    @Override
    public void printTwoNormal(int stations, String data1, String data2) throws JposException {

    }

    @Override
    public void rotatePrint(int station, int rotation) throws JposException {

    }

    @Override
    public void setBitmap(int bitmapNumber, int station, String fileName, int width, int alignment) throws JposException {

    }

    @Override
    public void setLogo(int location, String data) throws JposException {

    }

    @Override
    public void transactionPrint(int station, int control) throws JposException {

    }

    @Override
    public void validateData(int station, String data) throws JposException {

    }

    @Override
    public String getCheckHealthText() throws JposException {
        return null;
    }

    @Override
    public boolean getClaimed() throws JposException {
        return false;
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        return false;
    }

    @Override
    public void setDeviceEnabled(boolean deviceEnabled) throws JposException {

    }

    @Override
    public String getDeviceServiceDescription() throws JposException {
        return null;
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        final int deviceVersion19  = 1009000; // 1.9.0
        return deviceVersion19;
    }

    @Override
    public boolean getFreezeEvents() throws JposException {
        return false;
    }

    @Override
    public void setFreezeEvents(boolean freezeEvents) throws JposException {

    }

    @Override
    public String getPhysicalDeviceDescription() throws JposException {
        return null;
    }

    @Override
    public String getPhysicalDeviceName() throws JposException {
        return null;
    }

    @Override
    public int getState() throws JposException {
        return 0;
    }

    @Override
    public void claim(int timeout) throws JposException {

    }

    @Override
    public void close() throws JposException {

    }

    @Override
    public void checkHealth(int level) throws JposException {

    }

    @Override
    public void directIO(int command, int[] data, Object object) throws JposException {

    }

    @Override
    public void open(String logicalName, EventCallbacks cb) throws JposException {

    }

    @Override
    public void release() throws JposException {

    }
}
