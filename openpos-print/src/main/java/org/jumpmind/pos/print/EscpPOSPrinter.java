package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.EventCallbacks;
import org.apache.commons.lang3.text.StrSubstitutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EscpPOSPrinter implements IOpenposPrinter {

    PrinterCommands printerCommands = new PrinterCommandPlaceholders();
    int receiptLineSpacing;
    PrintWriter writer;
    PrinterConnection printerConnection;
    Map<String, Object> settings;
    EscpImagePrinter imagePrinter;
    IConnectionFactory connectionFactory;
    boolean deviceEnabled  = true;
    private String printerName;

    static final int STATUS_RECEIPT_PAPER_LOW             = 0b00000001;
    static final int STATUS_COVER_OPEN                    = 0b00000010;
    static final int STATUS_RECEIPT_PAPER_OUT             = 0b00000100;
    static final int STATUS_JAM                           = 0b00001000;
    static final int SLIP_LEADING_EDGE_SENSOR_COVERED     = 0b00100000;
    static final int SLIP_TRAILING_EDGE_SENSOR_COVERED    = 0b01000000;
    static final int THERMAL_HEAD_OR_VOLTAGE_OUT_OF_RANGE = 0b10000000;

    int currentPrintStation = POSPrinterConst.PTR_S_RECEIPT;

    public EscpPOSPrinter() {

    }

    public EscpPOSPrinter(Map<String,Object> settings) {
        this.settings = settings;
    }

    @Override
    public void claim(int timeout) throws JposException {
    }


    @Override
    public void open(String logicalName, EventCallbacks cb) {
        this.printerName = logicalName;
        this.printerConnection = connectionFactory.open(this.settings);
        this.writer = new PrintWriter(printerConnection.getOut());
        imagePrinter = new EscpImagePrinter(printerCommands.get(PrinterCommands.IMAGE_START_BYTE)); // TODO parameterize the image byte
        initializePrinter();
    }

    protected void initializePrinter() {
        printNormal(0, getCommand(PrinterCommands.ESC_P_MODE));
        printNormal(0, getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
        printNormal(0, getCommand(PrinterCommands.FORMAT_NORMAL));
        printNormal(0, getCommand(PrinterCommands.ALIGN_LEFT));
        printNormal(0, getCommand(PrinterCommands.LINE_SPACING_SINGLE));
    }

    @Override
    public void close() throws JposException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        this.connectionFactory.close();
    }

    @Override
    public void release() throws JposException {

    }

    @Override
    public void printNormal(int station, String data) {
        try {
            setPrintStation(station);

            if (data != null && data.length() > 0) {
                if (writer == null) {
                    throw new PrintException("The output stream for the printer driver cannot be null " +
                            "at this point. It probably was not initialized properly. (Hint: you may need to call open()");
                }
                writer.print(data);
                writer.flush();
            }
        } catch (Exception ex) {
            if (ex instanceof PrintException) {
                throw (PrintException)ex;
            } else {
                throw new PrintException("Failed to print " + data, ex);
            }
        }
    }

    private void setPrintStation(int station) throws Exception {
        if (station != currentPrintStation) {
            writer.flush();
            switch (station) {
                case POSPrinterConst.PTR_S_SLIP:
                    printerConnection.getOut().write((byte)0x1C); // select slip station.
                    break;
                case POSPrinterConst.PTR_S_RECEIPT:
                default:
                    printerConnection.getOut().write((byte)0x10); // clear printer/receipt to receipt mode.
                    break;
            }
         }
        currentPrintStation = station;
    }

    @Override
    public void cutPaper(int percentage) {
        printNormal(0, printerCommands.get(PrinterCommands.CUT_PAPER));
    }

    @Override
    public void openCashDrawer(String cashDrawerId) {
        printNormal(0, printerCommands.get(PrinterCommands.CASH_DRAWER_OPEN));
    }

    @Override
    public void printBarCode(int station, String data, int symbology, int height, int width, int alignment, int textPosition) {
        String printBarcodeCommand = buildBarcodeCommand(station, data, symbology, height, width, alignment, textPosition);
        printNormal(0, printerCommands.get(PrinterCommands.ALIGN_CENTER));
        printNormal(0, printBarcodeCommand);
        printNormal(0, printerCommands.get(PrinterCommands.BARCODE_FEED));  // epson will cut through barcode without some feed
        printNormal(0, printerCommands.get(PrinterCommands.ALIGN_LEFT));
    }

    public String buildBarcodeCommand(int station, String data, int symbology, int height, int width, int alignment, int textPosition) {
        String printBarcodeCommand = printerCommands.get(PrinterCommands.PRINT_BARCODE);

        String barcodeData = data;

        Map<String, String> substitutions = new HashMap<>();

        switch (symbology) {
            case POSPrinterConst.PTR_BCS_Code128:
                substitutions.put("barcodeType", printerCommands.get(PrinterCommands.BARCODE_TYPE_CODE_128));
                barcodeData = printerCommands.get(PrinterCommands.BARCODE_TYPE_CODE_128_CODEA) + data;
                break;
            default:
                throw new PrintException("Unsupported barcode symbology: " + symbology);
        }

        substitutions.put("barcodeLength", String.valueOf((char)barcodeData.length()));

        substitutions.put("barcodeData", barcodeData);

        StrSubstitutor substitutor = new StrSubstitutor(substitutions);

        printBarcodeCommand = substitutor.replace(printBarcodeCommand);
        return printBarcodeCommand;
    }

    @Override
    public void printBitmap(int station, String fileName, int width, int alignment) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                printImage(new FileInputStream(new File(fileName)));
            } catch (Exception ex) {
                throw new PrintException("Failed to open image file to print: '" + fileName + "'", ex);
            }

        } else {
            throw new PrintException("Cannot print image because can't find file: '" + fileName + "'");
        }
    }

    @Override
    public void printImage(InputStream image) {
        try {
            if (image == null) {
                throw new PrintException("Image input stream cannot be null.");
            }
            if (imagePrinter == null) {
                throw new PrintException("imagePrinter cannot be null here. This printer driver was not initialized properly.");
            }
            if (printerConnection == null) {
                throw new PrintException("printerConnection cannot be null here. This printer driver was not initialized properly.");
            }
            BufferedImage bufferedImage = ImageIO.read(image);
            imagePrinter.printImage(printerConnection.getOut(), bufferedImage);
            printNormal(0, getCommand(PrinterCommands.LINE_SPACING_SINGLE));
        } catch (Exception ex) {
            throw new PrintException("Failed to read and print buffered image", ex);
        }
    }

    @Override
    public int getRecLineSpacing() throws JposException {
        return receiptLineSpacing;
    }

    @Override
    public void setRecLineSpacing(int receiptLineSpacing) throws JposException {
        this.receiptLineSpacing = receiptLineSpacing;
    }

    public String getCommand(String commandName) {
        return printerCommands.get(commandName);
    }

    public void init(Map<String, Object> settings) {
        this.settings = settings;
        this.refreshConnectionFactoryFromSettings();
        this.refreshPrinterCommandsFromSettings();
    }

    @Override
    public String getPrinterName() {
        return printerName;
    }

    @Override
    public PrinterConnection getPrinterConnection() {
        return printerConnection;
    }

    private void refreshConnectionFactoryFromSettings() {
        try {
            this.connectionFactory = (IConnectionFactory)Class.forName((String)this.settings.get("connectionClass")).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new PrintException("Failed to create the connection factory for " + getClass().getName());
        }
    }

    private void refreshPrinterCommandsFromSettings() {
        this.printerCommands = new PrinterCommands();
        String printerCommandLocations = (String)settings.get("printerCommandLocations");
        String[] locationsSplit = printerCommandLocations.split(",");
        for (String printerCommandLocation : locationsSplit) {
            printerCommands.load(Thread.currentThread().getContextClassLoader().getResource(printerCommandLocation.trim()));
        }
    }

    @Override
    public int getPrintWidth() {
        Integer printWidth = (Integer)settings.get("printWidth");
        if (printWidth == null) {
            printWidth = 48;
        }
        return printWidth;
    }

    public int readPrinterStatus() {
        try {
            getPrinterConnection().getOut().write(new byte[] {0x1B, 0x76}); // request status.
            getPrinterConnection().getOut().flush();
            int statusByte = getPrinterConnection().getIn().read();
            return statusByte;
        } catch (Exception ex) {
            throw new PrintException("getCoverOpen() failed ", ex);
        }
    }

    public void printSlip(String text, int timeoutInMillis) {
        // wait for paper to appear in the slip printer (check franking/endorsement).
        long start = System.currentTimeMillis();

        while ((readPrinterStatus() & SLIP_LEADING_EDGE_SENSOR_COVERED) == 0
            && (System.currentTimeMillis()-start) < timeoutInMillis) {
            try {
                System.out.println("NO PAPER YET, WAITING.");
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
        }

        if ((readPrinterStatus() & SLIP_LEADING_EDGE_SENSOR_COVERED) == 0) {
            PrintException ex = new PrintException("Timed out while waiting for printer in the slip station after " +
                    timeoutInMillis + "ms.");
            ex.setErrorCode(PrintException.PRINT_ERROR_SLIP_INSERT_TIMEOUT);
            throw ex;
        }

        System.out.println("Printnig to slip printer");
        printNormal(POSPrinterConst.PTR_S_SLIP, text);

        try {
            System.out.println("ok now removing.");
            beginRemoval(timeoutInMillis);
        } catch (Exception ex) {
            throw new PrintException("Failed to print to slip station " + text, ex);
        }

    }

    @Override
    public boolean getCoverOpen() throws JposException {
        return (readPrinterStatus() & STATUS_COVER_OPEN) > 0;
    }

    @Override
    public boolean getJrnEmpty() throws JposException {
        return (readPrinterStatus() & SLIP_LEADING_EDGE_SENSOR_COVERED) == 0;
    }

    @Override
    public boolean getRecNearEnd() throws JposException {
        return (readPrinterStatus() & STATUS_RECEIPT_PAPER_LOW) > 0;
    }

    @Override
    public boolean getRecEmpty() throws JposException {
        return (readPrinterStatus() & STATUS_RECEIPT_PAPER_OUT) > 0;
    }

    @Override
    public void beginRemoval(int timeout) throws JposException {
        // not really clear on what the semantics of "beginRemoval" should be.
        long start = System.currentTimeMillis();

        final String FEED_10_LINES = "\n\n\n\n\n\n\n\n\n\n";

        do {
            writer.write(FEED_10_LINES);
            writer.flush();
        } while ((readPrinterStatus() & SLIP_TRAILING_EDGE_SENSOR_COVERED) > 0
            && (System.currentTimeMillis()-start) < timeout);
    }

    @Override
    public void endRemoval() throws JposException {
        // not really clear on what the semantics of "endRemoval" should be.
    }

    @Override
    public boolean getCapCompareFirmwareVersion() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapConcurrentPageMode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecPageMode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpPageMode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapUpdateFirmware() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getPageModeArea() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPageModeDescriptor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPageModeHorizontalPosition() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPageModeHorizontalPosition(int position) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getPageModePrintArea() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPageModePrintArea(String area) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPageModePrintDirection() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPageModePrintDirection(int direction) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPageModeStation() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPageModeStation(int station) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPageModeVerticalPosition() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPageModeVerticalPosition(int position) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void clearPrintArea() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void compareFirmwareVersion(String firmwareFileName, int[] result) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void pageModePrint(int control) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void updateFirmware(String firmwareFileName) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapStatisticsReporting() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapUpdateStatistics() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void resetStatistics(String statisticsBuffer) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void retrieveStatistics(String[] statisticsBuffer) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void updateStatistics(String statisticsBuffer) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapMapCharacterSet() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getMapCharacterSet() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setMapCharacterSet(boolean mapCharacterSet) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getRecBitmapRotationList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getSlpBitmapRotationList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapJrnCartridgeSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapJrnColor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapRecCartridgeSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapRecColor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapRecMarkFeed() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpBothSidesPrint() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapSlpCartridgeSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapSlpColor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCartridgeNotify() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setCartridgeNotify(int notify) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnCartridgeState() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnCurrentCartridge() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setJrnCurrentCartridge(int cartridge) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecCartridgeState() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecCurrentCartridge() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setRecCurrentCartridge(int cartridge) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpCartridgeState() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpCurrentCartridge() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setSlpCurrentCartridge(int cartridge) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpPrintSide() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void changePrintSide(int side) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void markFeed(int type) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void deleteInstance() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapPowerReporting() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPowerNotify() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setPowerNotify(int powerNotify) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getPowerState() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCapCharacterSet() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapConcurrentJrnRec() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapConcurrentJrnSlp() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapConcurrentRecSlp() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapCoverSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrn2Color() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnBold() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnDwide() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnDwideDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnEmptySensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnItalic() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnNearEndSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnPresent() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapJrnUnderline() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRec2Color() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecBarCode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecBitmap() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecBold() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecDwide() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecDwideDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecEmptySensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecItalic() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecLeft90() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecNearEndSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecPapercut() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecPresent() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecRight90() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecRotate180() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecStamp() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapRecUnderline() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlp2Color() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpBarCode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpBitmap() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpBold() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpDwide() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpDwideDhigh() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpEmptySensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpFullslip() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpItalic() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpLeft90() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpNearEndSensor() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpPresent() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpRight90() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpRotate180() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapSlpUnderline() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getCapTransaction() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getAsyncMode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setAsyncMode(boolean asyncMode) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getCharacterSet() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setCharacterSet(int characterSet) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getCharacterSetList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getErrorLevel() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getErrorStation() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getErrorString() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getFlagWhenIdle() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setFlagWhenIdle(boolean flagWhenIdle) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getFontTypefaceList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getJrnLetterQuality() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setJrnLetterQuality(boolean jrnLetterQuality) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnLineChars() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setJrnLineChars(int jrnLineChars) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getJrnLineCharsList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnLineHeight() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setJrnLineHeight(int jrnLineHeight) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnLineSpacing() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setJrnLineSpacing(int jrnLineSpacing) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getJrnLineWidth() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getJrnNearEnd() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getMapMode() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setMapMode(int mapMode) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getOutputID() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getRecBarCodeRotationList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getRecLetterQuality() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setRecLetterQuality(boolean recLetterQuality) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecLineChars() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setRecLineChars(int recLineChars) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getRecLineCharsList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecLineHeight() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setRecLineHeight(int recLineHeight) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecLinesToPaperCut() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecLineWidth() throws JposException {
        // TODO this would be nice to support.
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecSidewaysMaxChars() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRecSidewaysMaxLines() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getRotateSpecial() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setRotateSpecial(int rotateSpecial) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getSlpBarCodeRotationList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getSlpEmpty() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getSlpLetterQuality() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setSlpLetterQuality(boolean recLetterQuality) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpLineChars() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setSlpLineChars(int recLineChars) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getSlpLineCharsList() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpLineHeight() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setSlpLineHeight(int recLineHeight) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpLinesNearEndToEnd() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpLineSpacing() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setSlpLineSpacing(int recLineSpacing) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpLineWidth() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpMaxLines() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getSlpNearEnd() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpSidewaysMaxChars() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getSlpSidewaysMaxLines() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void beginInsertion(int timeout) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void clearOutput() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void endInsertion() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void printImmediate(int station, String data) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void printTwoNormal(int stations, String data1, String data2) throws JposException {
        // fiscal printer?
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void rotatePrint(int station, int rotation) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setBitmap(int bitmapNumber, int station, String fileName, int width, int alignment) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setLogo(int location, String data) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void transactionPrint(int station, int control) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void validateData(int station, String data) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getCheckHealthText() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getClaimed() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        return deviceEnabled;
    }

    @Override
    public void setDeviceEnabled(boolean deviceEnabled) throws JposException {
        this.deviceEnabled = deviceEnabled;
    }

    @Override
    public String getDeviceServiceDescription() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        final int deviceVersion19  = 1009000; // 1.9.0
        return deviceVersion19;
    }

    @Override
    public boolean getFreezeEvents() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void setFreezeEvents(boolean freezeEvents) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getPhysicalDeviceDescription() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public String getPhysicalDeviceName() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public int getState() throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void checkHealth(int level) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }

    @Override
    public void directIO(int command, int[] data, Object object) throws JposException {
        throw new PrintNotSupportedException("Method not supported on this driver.");
    }
}
