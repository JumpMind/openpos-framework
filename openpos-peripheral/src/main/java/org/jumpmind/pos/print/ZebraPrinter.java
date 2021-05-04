package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.services.EventCallbacks;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.status.Status;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.jumpmind.pos.print.ZebraCommands.*;

public class ZebraPrinter implements IOpenposPrinter {

    boolean deviceEnabled = true;

    PeripheralConnection peripheralConnection;
    Map<String, Object> settings;
    IConnectionFactory connectionFactory;
    ZebraImagePrinter imagePrinter;
    PrintWriter writer;
    private String printerName;
    private IPrinterStatusReporter printerStatusReporter;

    @Delegate
    UnsupportedJposMethods unsupportedJposMethods = new UnsupportedJposMethods();

    public ZebraPrinter() {
        this.settings = new HashMap<>();
    }

    public ZebraPrinter(Map<String, Object> settings) {
        this.settings = settings;
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
    public int getDeviceServiceVersion() throws JposException {
        final int deviceVersion19 = 1009000; // 1.9.0
        return deviceVersion19;
    }

    @Override
    public void claim(int timeout) throws JposException {

    }

    @Override
    public void open(String logicalName, EventCallbacks cb) throws JposException {
        this.printerName = logicalName;
        this.peripheralConnection = connectionFactory.open(this.settings);
        this.writer = new PrintWriter(peripheralConnection.getOut());
        imagePrinter = new ZebraImagePrinter();
        writer.print(COMMAND_ENABLE_LINE_PRINT);
        writer.flush();
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
    public void printBarCode(int station, String data, int symbology, int height, int width, int alignment, int textPosition) throws JposException {
        writer.println(COMMAND_ENABLE_TEXT_UNDER_BARCODE);
        writer.println(String.format(COMMAND_PRINT_BARCODE, data));
        writer.println(COMMAND_DISABLE_TEXT_UNDER_BARCODE);
        writer.println("\n\n\n\n\n\n\n");
    }

    @Override
    public void printNormal(int station, String data) {
        if (data.endsWith("\n")) {
            data = data.substring(0, data.length()-1);
        }
        writer.print(COMMAND_LINE_PRINT);
        if (data.contains(CENTER_HINT)) {
            data = data.replace(CENTER_HINT, "");

            String text = removePrinterCommands(data);
            int whitespace = getPrintWidth() - text.length();
            int leftPadding = whitespace/2;

            data = StringUtils.leftPad("", leftPadding) + data;
        }
        writer.print(data);
        writer.print("\r\n");
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
            String imageCommand = imagePrinter.getZebraGraphicsCommand(bufferedImage);
            writer.println(String.format(COMMAND_CPCL_MODE_Y, bufferedImage.getHeight()));
            writer.println(imageCommand);
            writer.println(COMMAND_PRINT);
            writer.flush();
        } catch (Exception ex) {
            throw new PrintException("Failed to read and print buffered image", ex);
        }
    }

    @Override
    public boolean supportsPrintDivider() {
        return true;
    }

    @Override
    public void printDivider() {
        writer.println(String.format(COMMAND_CPCL_MODE_Y, 5));
        writer.println("LINE 0 0 575 0 1");
        writer.println(COMMAND_PRINT);
    }

    @Override
    public void openCashDrawer(String cashDrawerId) {
    }

    @Override
    public String getCommand(String commandName) {
        switch (commandName) {
            case PrinterCommands.FORMAT_BOLD:
                return COMMAND_BOLD;
            case PrinterCommands.FORMAT_NORMAL:
                return COMMAND_NORMAL;
            case PrinterCommands.ALIGN_CENTER:
                return CENTER_HINT;
            case PrinterCommands.FONT_SIZE_MEDIUM:
                return COMMAND_FONT_SIZE_MEDIUM;
            case PrinterCommands.FONT_SIZE_LARGE:
                return COMMAND_FONT_SIZE_LARGE;
        }
        return "";
    }

    private String removePrinterCommands(String value) {
        value = value.replace(COMMAND_BOLD, "");
        value = value.replace(COMMAND_NORMAL, "");
        value = value.replace(COMMAND_FONT_SIZE_MEDIUM, "");
        value = value.replace(COMMAND_FONT_SIZE_LARGE, "");
        return value;
    }

    @Override
    public int getPrintWidth() {
        Integer printWidth = (Integer) settings.get("printWidth");
        if (printWidth == null) {
            printWidth = 46;
        }
        return printWidth;
    }

    @Override
    public void init(Map<String, Object> settings, IPrinterStatusReporter printerStatusReporter) {
        this.printerStatusReporter = printerStatusReporter;
        this.settings = settings;
        this.refreshConnectionFactoryFromSettings();
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

    @Override
    public String getPrinterName() {
        return null;
    }

    @Override
    public PeripheralConnection getPeripheralConnection() {
        return peripheralConnection;
    }

    @Override
    public int readPrinterStatus() {
        return 0;
    }

    @Override
    public boolean isDrawerOpen(String cashDrawerId) {
        return false;
    }

    @Override
    public int waitForDrawerClose(String cashDrawerId, long timeout) {
        return 0;
    }

    @Override
    public void beginSlipMode() {

    }

    @Override
    public void endSlipMode() {

    }

    @Override
    public void printSlip(String text, int timeoutInMillis) {

    }

    @Override
    public String readMicr() {
        return null;
    }

    @Override
    public boolean getCoverOpen() throws JposException {
        return false;
    }

    @Override
    public boolean getJrnEmpty() throws JposException {
        return false;
    }

    @Override
    public boolean getRecEmpty() throws JposException {
        return false;
    }

    @Override
    public int getRecLineSpacing() throws JposException {
        return 0;
    }

    @Override
    public void setRecLineSpacing(int recLineSpacing) throws JposException {

    }

    @Override
    public boolean getRecNearEnd() throws JposException {
        return false;
    }

    @Override
    public void beginRemoval(int timeout) throws JposException {

    }

    @Override
    public void cutPaper(int percentage) throws JposException {
        writer.print("\n\n");
    }

    @Override
    public void endRemoval() throws JposException {
    }

    @Override
    public void printBitmap(int station, String fileName, int width, int alignment) throws JposException {
    }
}
