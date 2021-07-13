package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.EventCallbacks;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.BoolUtils;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.status.Status;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EscpPOSPrinter implements IOpenposPrinter {

    PrinterCommands printerCommands = new PrinterCommandPlaceholders();
    int receiptLineSpacing;
    PrintWriter writer;
    PeripheralConnection peripheralConnection;
    Map<String, Object> settings;
    EscpImagePrinter imagePrinter;
    IConnectionFactory connectionFactory;
    boolean deviceEnabled = true;
    private String printerName;

    @Delegate
    UnsupportedJposMethods unsupportedJposMethods = new UnsupportedJposMethods();

    // certain configurations have to be configured for "unsolicited status" to work properly, which means
    // status bytes come back after commands where we are not really interested in them.
    // 11/25/2020 = This was added specifically to support reading the cash drawer status over USB on the Toshiba
    // TcX (Suremark) printer.
    boolean unsolicitedStatusMode = false;

    static final int STATUS_RECEIPT_PAPER_LOW = 0b00000001;
    static final int STATUS_COVER_OPEN = 0b00000010;
    static final int STATUS_RECEIPT_PAPER_OUT = 0b00000100;
    static final int STATUS_JAM = 0b00001000;
    static final int SLIP_LEADING_EDGE_SENSOR_COVERED = 0b00100000;
    static final int SLIP_TRAILING_EDGE_SENSOR_COVERED = 0b01000000;
    static final int THERMAL_HEAD_OR_VOLTAGE_OUT_OF_RANGE = 0b10000000;

    int currentPrintStation = POSPrinterConst.PTR_S_RECEIPT;
    protected IPrinterStatusReporter printerStatusReporter;

    public EscpPOSPrinter() {
        this.settings = new HashMap<>();
    }

    public EscpPOSPrinter(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public void claim(int timeout) throws JposException {
    }


    @Override
    public void open(String logicalName, EventCallbacks cb) {
        this.printerName = logicalName;
        this.peripheralConnection = connectionFactory.open(this.settings);
        this.writer = new PrintWriter(peripheralConnection.getOut());
        imagePrinter = new EscpImagePrinter(printerCommands.get(PrinterCommands.IMAGE_START_BYTE)); // TODO parameterize the image byte

        unsolicitedStatusMode = BoolUtils.toBoolean(settings.get("unsolicitedStatusMode"));

        initializePrinter();
    }

    protected void initializePrinter() {
        printNormal(0, getCommand(PrinterCommands.ESC_P_MODE));
        printNormal(0, getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
        printNormal(0, getCommand(PrinterCommands.FORMAT_NORMAL));
        printNormal(0, getCommand(PrinterCommands.ALIGN_LEFT));
        printNormal(0, getCommand(PrinterCommands.LINE_SPACING_SINGLE));
        printNormal(0, getCommand(PrinterCommands.ESC_P_RESET));

        // Petco format: ESC w p T '/ A '/ C '/ S <CR>
        // String resetFormat = new String(new char[] { 0x1B, 0x77, 0x70, 0x54, 0x27, 0x2f, 0x41, 0x27, 0x2f, 0x43, 0x27, 0x2f, 0x53, 0x0D });
        // Default format: ESC w p <CR>
//        String resetFormat = new String(new char[]{0x1B, 0x77, 0x70, 0x0D});

        // Set the MICR read format
        printNormal(0, getCommand(PrinterCommands.MICR_RESET_TO_DEFAULT_SETTINGS));
    }

    @Override
    public void close() throws JposException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        this.connectionFactory.close(this.peripheralConnection);
    }

    @Override
    public void release() throws JposException {

    }

    @Override
    public void printNormal(int station, String data) {
        try {
            if (data != null && data.length() > 0) {
                if (writer == null) {
                    throw new PrintException("The output stream for the printer driver cannot be null " +
                            "at this point. It probably was not initialized properly. (Hint: you may need to call open()");
                }
                writer.print(data);
                writer.flush();
                if (printerStatusReporter != null) {
                    printerStatusReporter.reportStatus(Status.Online, "Ok.");
                }

            }
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Error, "Failed to print. ");
            }
            if (ex instanceof PrintException) {
                throw (PrintException) ex;
            } else {
                throw new PrintException("Failed to print " + data, ex);
            }
        }
    }

    @Override
    public void cutPaper(int percentage) {
        printNormal(0, printerCommands.get(PrinterCommands.CUT_FEED));  // epson will cut through barcode without some feed
        printNormal(0, printerCommands.get(PrinterCommands.CUT_PAPER));
    }

    @Override
    public void openCashDrawer(String cashDrawerId) {
        printNormal(0, printerCommands.get(PrinterCommands.CASH_DRAWER_OPEN));
    }

    @Override
    public int waitForDrawerClose(String cashDrawerId, long timeout) {
        long startTime = System.currentTimeMillis();
        int drawerState = DRAWER_OPEN;
        try {
            while (drawerState != DRAWER_CLOSED && System.currentTimeMillis() - startTime < timeout) {
                    Thread.sleep(1000);
                    drawerState = isDrawerOpen(cashDrawerId) ? DRAWER_OPEN : DRAWER_CLOSED;
            }
        } catch (Exception e) {
            String msg = String.format("Failure to read the status of the drawer: %s", cashDrawerId);
            throw new PrintException(msg, e);
        }
        return drawerState;
    }

    public boolean isDrawerOpen(String cashDrawerId) {
        try {
            PeripheralConnection connection = getPeripheralConnection();
            connection.resetInput();
            connection.getOut().write(getCommand(PrinterCommands.CASH_DRAWER_STATE).getBytes());
            connection.getOut().flush();
            AppUtils.sleep(500);

            if (unsolicitedStatusMode) {
                int statusByteThrowaway1 = connection.getIn().read();
                int statusByteThrowaway2 = connection.getIn().read();
            }

            int cashDrawerState = connection.getIn().read();
            log.debug("cash drawer state was: {}", cashDrawerState);
            return cashDrawerState == DRAWER_OPEN ? true : false;
        } catch (Exception e) {
            String msg = String.format("Failure to read the status of the drawer: %s", cashDrawerId);
            throw new PrintException(msg, e);
        }
    }

    @Override
    public void printBarCode(int station, String data, int symbology, int height, int width, int alignment, int textPosition) {
        try {
            printNormal(0, printerCommands.get(PrinterCommands.ALIGN_CENTER));
            String printBarcodeCommand = buildBarcodeCommand(station, data, symbology, height, width, alignment, textPosition);
            printNormal(0, printBarcodeCommand);
            printNormal(0, printerCommands.get(PrinterCommands.ALIGN_LEFT));
        } catch (Exception ex) {
            throw new PrintException("Failed to print barcode: " + data + " symbology: " + symbology, ex);
        }
    }

    public String buildBarcodeCommand(int station, String data, int symbology, int height, int width, int alignment, int textPosition) {
        String printBarcodeCommand = printerCommands.get(PrinterCommands.PRINT_BARCODE);

        String barcodeData = data;

        Map<String, String> substitutions = new HashMap<>();

        switch (symbology) {
            case POSPrinterConst.PTR_BCS_Code128:
                substitutions.put("barcodeType", printerCommands.get(PrinterCommands.BARCODE_TYPE_CODE_128));

                if (StringUtils.isNumeric(data)) {
                    List<Byte> barcodeCommand = new ArrayList<>();
                    for (byte b : getCommand(PrinterCommands.BARCODE_TYPE_CODE_128_CODEC).getBytes()) {
                        barcodeCommand.add(b);
                    }
                    for (int i = 0; i < data.length(); i++) {
                        if (i <= data.length() - 2) {
                            byte number = (byte) Integer.parseInt(data.substring(i, i + 2));
                            barcodeCommand.add(number);
                            i++;
                        } else {
                            // switch to code B just for the last (ODD) number.  Code C only support an even number of digits.
                            for (byte b : getCommand(PrinterCommands.BARCODE_TYPE_CODE_128_CODEB).getBytes()) {
                                barcodeCommand.add(b);
                            }
                            barcodeCommand.add(data.substring(i, i + 1).getBytes()[0]);
                        }
                    }

                    barcodeData = new String(toBytes(barcodeCommand));
                } else {
                    barcodeData = getCommand(PrinterCommands.BARCODE_TYPE_CODE_128_CODEB) + data;

                }
                substitutions.put("barcodeLength", new String(new byte[] {(byte)barcodeData.length()}));
                substitutions.put("barcodeData", barcodeData);

                break;
            default:
                throw new PrintException("Unsupported barcode symbology: " + symbology);
        }


        StrSubstitutor substitutor = new StrSubstitutor(substitutions);

        printBarcodeCommand = substitutor.replace(printBarcodeCommand);
        return printBarcodeCommand;
    }

    private byte[] toBytes(List<Byte> barcodeCommand) {
        byte[] bytes = new byte[barcodeCommand.size()];
        for (int i = 0; i < barcodeCommand.size(); i++) {
            bytes[i] = barcodeCommand.get(i);
        }
        return bytes;
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
            if (peripheralConnection == null) {
                throw new PrintException("printerConnection cannot be null here. This printer driver was not initialized properly.");
            }
            BufferedImage bufferedImage = ImageIO.read(image);
            imagePrinter.printImage(peripheralConnection.getOut(), bufferedImage);
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

    public void init(Map<String, Object> settings, IPrinterStatusReporter printerStatusReporter) {
        this.printerStatusReporter = printerStatusReporter;
        this.settings = settings;
        this.refreshConnectionFactoryFromSettings();
        this.refreshPrinterCommandsFromSettings();
    }

    @Override
    public String getPrinterName() {
        return printerName;
    }

    @Override
    public PeripheralConnection getPeripheralConnection() {
        return peripheralConnection;
    }

    private void refreshConnectionFactoryFromSettings() {
        try {
            this.connectionFactory = (IConnectionFactory) ClassUtils.loadClass((String) this.settings.get("connectionClass")).newInstance();
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Offline, ex.getMessage());
            }
            throw new PrintException("Failed to create the connection factory for " + getClass().getName(), ex);
        }
    }

    private void refreshPrinterCommandsFromSettings() {
        this.printerCommands = new PrinterCommands();
        String printerCommandLocations = (String) settings.get("printerCommandLocations");
        String[] locationsSplit = printerCommandLocations.split(",");
        for (String printerCommandLocation : locationsSplit) {
            printerCommands.load(Thread.currentThread().getContextClassLoader().getResource(printerCommandLocation.trim()));
        }
    }

    @Override
    public int getPrintWidth() {
        Integer printWidth = (Integer) settings.get("printWidth");
        if (printWidth == null) {
            printWidth = 48;
        }
        return printWidth;
    }

    @Override
    public int readPrinterStatus() {
        try {
            getPeripheralConnection().getOut().flush();
            // TODO this needs work on NCR. Calling this more than a few times puts the printer in a bad state.
            getPeripheralConnection().getOut().write(new byte[]{0x1B, 0x76}); // request status.

            getPeripheralConnection().getOut().flush();
            if (getPeripheralConnection().getIn() != null) {
                Thread.sleep(500);
                int statusByte = getPeripheralConnection().getIn().read();
                if (statusByte == -1) {
                    throw new PrinterException("Can't read printer status.");
                }
                return statusByte;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Error, ex.getMessage());
            }
            throw new PrintException("readPrinterStatus() failed ", ex);
        }
    }

    @Override
    public void printSlip(String text, int timeoutInMillis) {
        try {
            beginSlipMode();
            printNormal(POSPrinterConst.PTR_S_SLIP, text);
            endSlipMode();
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Error, ex.getMessage());
            }
            throw new PrintException("Failed to print to slip station " + text, ex);
        }
    }

    @Override
    public String readMicr() {
        StringBuilder buff = new StringBuilder(32);

        byte status = -1;

        getPeripheralConnection().resetInput();

        try {
            getPeripheralConnection().getOut().write(new byte[]{0x1B, 0x77, 0x01});
            getPeripheralConnection().getOut().flush();
            long micrWaitTime = Long.valueOf(settings.getOrDefault("micrWaitTime", "2000").toString());
            long micrTimeout = Long.valueOf(settings.getOrDefault("micrTimeout", "20000").toString());

            Thread.sleep(micrWaitTime);

            int available = 0, lastAvailable = 0;

            long start = System.currentTimeMillis();

            while ((System.currentTimeMillis() - start) < micrTimeout)  {
                available = getPeripheralConnection().getIn().available();
                if (available > 0 && available == lastAvailable) { // looking for available bytes to stabilize.
                    break;
                }
                lastAvailable = available;
                Thread.sleep(500);
            }

            if (available == 0) {
                throw new PrintException("Could not read micr data.");
            }

            byte[] micrBytes = new byte[available];

            int bytesRead = getPeripheralConnection().getIn().read(micrBytes);

            status = micrBytes[0];

            for (int i = 1; i < bytesRead; i++) {
                if (micrBytes[i] == '\r') { // MICR read is spec'd to end in carriage return
                    break;
                }
                buff.append((char) micrBytes[i]);
            }

            endSlipMode(); // kick the slip out no matter what happenens.

            if (status != 0) {
                throw new PrintException("MICR read failed with error code: " + status);
            }

            return buff.toString();
        } catch (Exception ex) {
            throw new PrintException("Failed to read MICR. status=" + status + " data read: '" + buff + "'", ex);
        }
    }

    @Override
    public void beginSlipMode() {
        try {
            getPeripheralConnection().getOut().write(new byte[]{0x1B, 0x63, 0x30, 4}); // select slip
//            getPeripheralConnection().getOut().write(new byte[] {0x1B, 0x66, 1, 2}); // wait for one minute for a slip, and start printing .2 seconds after slip detected.
            getPeripheralConnection().getOut().flush();
        } catch (Exception ex) {
            printerStatusReporter.reportStatus(Status.Error, ex.getMessage());
            throw new PrintException("Failed to beginSlipMode", ex);
        }
    }

    @Override
    public void endSlipMode() {
        try {
            // beginRemoval(-1);
            getPeripheralConnection().getOut().write(new byte[]{0x0C}); // print and eject slip
            getPeripheralConnection().getOut().flush();
            getPeripheralConnection().getOut().write(new byte[]{0x1B, 0x63, 0x30, 1}); // select receipt
            getPeripheralConnection().getOut().flush();
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Error, ex.getMessage());
            }
            throw new PrintException("Failed to endSlipMode", ex);
        }
    }

    @Override
    public boolean getCoverOpen() throws JposException {
        return (readPrinterStatus() & STATUS_COVER_OPEN) > 0;
    }

    @Override
    public boolean getJrnEmpty() throws JposException {
        int printerStatus = readPrinterStatus();
        return (printerStatus & SLIP_LEADING_EDGE_SENSOR_COVERED) == 0;
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
        final String FEED_100_LINES = StringUtils.repeat("\n", 100);
        writer.write(FEED_100_LINES);
        writer.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void endRemoval() throws JposException {
        // not really clear on what the semantics of "endRemoval" should be.
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        final int deviceVersion19 = 1009000; // 1.9.0
        return deviceVersion19;
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        return deviceEnabled;
    }

    @Override
    public void setDeviceEnabled(boolean deviceEnabled) throws JposException {
        this.deviceEnabled = deviceEnabled;
    }


}
