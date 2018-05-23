package org.jumpmind.pos.core.javapos;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.POSPrinterService19;

public class SimulatedPOSPrinterService extends AbstractSimulatedService implements POSPrinterService19 {
    static final Log logger = LogFactory.getLog(SimulatedPOSPrinterService.class);
    protected Logger printerLogger = Logger.getLogger(SimulatedPOSPrinterService.class);

    protected boolean asyncMode = true;
    protected int slpLineChars;
    protected int slpLineHeight;
    protected int slpLineSpacing;
    protected int characterSet;
    protected boolean flagWhenIdle;
    protected boolean jrnLetterQuality;
    protected int jrnLineChars;
    protected int jrnLineHeight;
    protected int jrnLineSpacing;
    protected int mapMode;
    protected boolean recLetterQuality;
    protected int recLineChars;
    protected int recLineHeight;
    protected int recLineSpacing;
    protected int rotateSpecial;
    protected boolean slpLetterQuality;
    protected Map<Integer, InMemoryBitmap> inMemoryBitmaps;
    
    protected StringBuilder receipt = new StringBuilder();

    public SimulatedPOSPrinterService() {
        logger.trace(String.format("%s created", this.getClass()));
    }
    @Override
    public void reset() {
    }

    public void appendText(final int type, final String newText) {
        receipt.append(newText);        
    }

    public void printBarCode(int i, String s, int j, int k, int l, int i1, int j1) throws JposException {
        appendText(i, s);
    }

    public void printBitmap(final int i, final String image, int j, int k) throws JposException {

    }

    public void printImmediate(int i, String s) throws JposException {
        printNormalWithEscapeSequences(i, s);
    }

    public void printNormal(int i, String s) throws JposException {
        printNormalWithEscapeSequences(i, s);
    }

    public void printMemoryBitmap(int i, byte[] abyte0, int j, int k, int l) throws JposException {
    }

    public void cutPaper(int i) throws JposException {
        appendText(i, "\n\n--------------- cut here ---------------\n\n");
        printerLogger.info("\n" + receipt);
        receipt.setLength(0);
    }

    public void clearPrintArea() throws JposException {
    }

    public void compareFirmwareVersion(String s, int[] ai) throws JposException {
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    public boolean getCapConcurrentPageMode() throws JposException {
        return false;
    }

    public boolean getCapRecPageMode() throws JposException {
        return false;
    }

    public boolean getCapSlpPageMode() throws JposException {
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    public String getPageModeArea() throws JposException {
        return null;
    }

    public int getPageModeDescriptor() throws JposException {
        return 0;
    }

    public int getPageModeHorizontalPosition() throws JposException {
        return 0;
    }

    public String getPageModePrintArea() throws JposException {
        return null;
    }

    public int getPageModePrintDirection() throws JposException {
        return 0;
    }

    public int getPageModeStation() throws JposException {
        return 0;
    }

    public int getPageModeVerticalPosition() throws JposException {
        return 0;
    }

    public void pageModePrint(int i) throws JposException {
    }

    public void setPageModeHorizontalPosition(int i) throws JposException {
    }

    public void setPageModePrintArea(String s) throws JposException {
    }

    public void setPageModePrintDirection(int i) throws JposException {
    }

    public void setPageModeStation(int i) throws JposException {
    }

    public void setPageModeVerticalPosition(int i) throws JposException {
    }

    public void updateFirmware(String s) throws JposException {
    }

    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public void resetStatistics(String s) throws JposException {
    }

    public void retrieveStatistics(String[] as) throws JposException {
    }

    public void updateStatistics(String s) throws JposException {
    }

    public boolean getCapMapCharacterSet() throws JposException {
        return false;
    }

    public boolean getMapCharacterSet() throws JposException {
        return false;
    }

    public String getRecBitmapRotationList() throws JposException {
        return null;
    }

    public String getSlpBitmapRotationList() throws JposException {
        return null;
    }

    public void setMapCharacterSet(boolean flag) throws JposException {
    }

    public void changePrintSide(int i) throws JposException {
    }

    public int getCapJrnCartridgeSensor() throws JposException {
        return 0;
    }

    public int getCapJrnColor() throws JposException {
        return 0;
    }

    public int getCapRecCartridgeSensor() throws JposException {
        return 0;
    }

    public int getCapRecColor() throws JposException {
        return 0;
    }

    public int getCapRecMarkFeed() throws JposException {
        return 0;
    }

    public boolean getCapSlpBothSidesPrint() throws JposException {
        return false;
    }

    public int getCapSlpCartridgeSensor() throws JposException {
        return 0;
    }

    public int getCapSlpColor() throws JposException {
        return 0;
    }

    public int getCartridgeNotify() throws JposException {
        return 0;
    }

    public int getJrnCartridgeState() throws JposException {
        return 0;
    }

    public int getJrnCurrentCartridge() throws JposException {
        return 0;
    }

    public int getRecCartridgeState() throws JposException {
        return 0;
    }

    public int getRecCurrentCartridge() throws JposException {
        return 0;
    }

    public int getSlpCartridgeState() throws JposException {
        return 0;
    }

    public int getSlpCurrentCartridge() throws JposException {
        return 0;
    }

    public int getSlpPrintSide() throws JposException {
        return 0;
    }

    public void markFeed(int i) throws JposException {
    }

    public void setCartridgeNotify(int i) throws JposException {
    }

    public void setJrnCurrentCartridge(int i) throws JposException {
    }

    public void setRecCurrentCartridge(int i) throws JposException {
    }

    public void setSlpCurrentCartridge(int i) throws JposException {
    }

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerState() throws JposException {

        return 0;
    }

    public void beginInsertion(int i) throws JposException {
        appendText(i, "\n\n-------------- begin insertion --------------\n\n");
    }

    public void beginRemoval(int i) throws JposException {
        appendText(i, "\n\n-------------- begin removal --------------\n\n");
    }

    public void clearOutput() throws JposException {
    }

    public void endInsertion() throws JposException {
        appendText(POSPrinterConst.PTR_S_RECEIPT, "\n\n-------------- end insertion --------------\n\n");
    }

    public void endRemoval() throws JposException {
        appendText(POSPrinterConst.PTR_S_RECEIPT, "\n\n-------------- end removal --------------\n\n");
    }

    public boolean getAsyncMode() throws JposException {
        return this.asyncMode;
    }

    public int getCapCharacterSet() throws JposException {
        return 0;
    }

    public boolean getCapConcurrentJrnRec() throws JposException {
        return false;
    }

    public boolean getCapConcurrentJrnSlp() throws JposException {
        return false;
    }

    public boolean getCapConcurrentRecSlp() throws JposException {
        return false;
    }

    public boolean getCapCoverSensor() throws JposException {
        return false;
    }

    public boolean getCapJrn2Color() throws JposException {
        return false;
    }

    public boolean getCapJrnBold() throws JposException {
        return false;
    }

    public boolean getCapJrnDhigh() throws JposException {
        return false;
    }

    public boolean getCapJrnDwide() throws JposException {
        return false;
    }

    public boolean getCapJrnDwideDhigh() throws JposException {
        return false;
    }

    public boolean getCapJrnEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapJrnItalic() throws JposException {
        return false;
    }

    public boolean getCapJrnNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapJrnPresent() throws JposException {
        return false;
    }

    public boolean getCapJrnUnderline() throws JposException {
        return false;
    }

    public boolean getCapRec2Color() throws JposException {
        return false;
    }

    public boolean getCapRecBarCode() throws JposException {
        return false;
    }

    public boolean getCapRecBitmap() throws JposException {
        return false;
    }

    public boolean getCapRecBold() throws JposException {
        return false;
    }

    public boolean getCapRecDhigh() throws JposException {
        return false;
    }

    public boolean getCapRecDwide() throws JposException {
        return false;
    }

    public boolean getCapRecDwideDhigh() throws JposException {
        return false;
    }

    public boolean getCapRecEmptySensor() throws JposException {

        return false;
    }

    public boolean getCapRecItalic() throws JposException {

        return false;
    }

    public boolean getCapRecLeft90() throws JposException {

        return false;
    }

    public boolean getCapRecNearEndSensor() throws JposException {

        return false;
    }

    public boolean getCapRecPapercut() throws JposException {

        return false;
    }

    public boolean getCapRecPresent() throws JposException {

        return false;
    }

    public boolean getCapRecRight90() throws JposException {

        return false;
    }

    public boolean getCapRecRotate180() throws JposException {

        return false;
    }

    public boolean getCapRecStamp() throws JposException {

        return false;
    }

    public boolean getCapRecUnderline() throws JposException {

        return false;
    }

    public boolean getCapSlp2Color() throws JposException {

        return false;
    }

    public boolean getCapSlpBarCode() throws JposException {

        return false;
    }

    public boolean getCapSlpBitmap() throws JposException {

        return false;
    }

    public boolean getCapSlpBold() throws JposException {

        return false;
    }

    public boolean getCapSlpDhigh() throws JposException {

        return false;
    }

    public boolean getCapSlpDwide() throws JposException {

        return false;
    }

    public boolean getCapSlpDwideDhigh() throws JposException {

        return false;
    }

    public boolean getCapSlpEmptySensor() throws JposException {

        return false;
    }

    public boolean getCapSlpFullslip() throws JposException {

        return false;
    }

    public boolean getCapSlpItalic() throws JposException {

        return false;
    }

    public boolean getCapSlpLeft90() throws JposException {

        return false;
    }

    public boolean getCapSlpNearEndSensor() throws JposException {

        return false;
    }

    public boolean getCapSlpPresent() throws JposException {

        return false;
    }

    public boolean getCapSlpRight90() throws JposException {

        return false;
    }

    public boolean getCapSlpRotate180() throws JposException {

        return false;
    }

    public boolean getCapSlpUnderline() throws JposException {

        return false;
    }

    public boolean getCapTransaction() throws JposException {

        return false;
    }

    public int getCharacterSet() throws JposException {

        return 0;
    }

    public String getCharacterSetList() throws JposException {

        return "";
    }

    public boolean getCoverOpen() throws JposException {

        return false;
    }

    public int getErrorLevel() throws JposException {

        return 0;
    }

    public int getErrorStation() throws JposException {

        return 0;
    }

    public String getErrorString() throws JposException {

        return null;
    }

    public boolean getFlagWhenIdle() throws JposException {

        return false;
    }

    public String getFontTypefaceList() throws JposException {

        return null;
    }

    public boolean getJrnEmpty() throws JposException {

        return false;
    }

    public boolean getJrnLetterQuality() throws JposException {

        return false;
    }

    public int getJrnLineChars() throws JposException {

        return 0;
    }

    public String getJrnLineCharsList() throws JposException {

        return null;
    }

    public int getJrnLineHeight() throws JposException {

        return 0;
    }

    public int getJrnLineSpacing() throws JposException {

        return 0;
    }

    public int getJrnLineWidth() throws JposException {

        return 0;
    }

    public boolean getJrnNearEnd() throws JposException {

        return false;
    }

    public int getMapMode() throws JposException {

        return 0;
    }

    public int getOutputID() throws JposException {

        return 0;
    }

    public String getRecBarCodeRotationList() throws JposException {

        return null;
    }

    public boolean getRecEmpty() throws JposException {

        return false;
    }

    public boolean getRecLetterQuality() throws JposException {

        return false;
    }

    public int getRecLineChars() throws JposException {

        return 44;
    }

    public String getRecLineCharsList() throws JposException {

        return null;
    }

    public int getRecLineHeight() throws JposException {

        return 0;
    }

    public int getRecLineSpacing() throws JposException {

        return 0;
    }

    public int getRecLineWidth() throws JposException {

        return 0;
    }

    public int getRecLinesToPaperCut() throws JposException {

        return 0;
    }

    public boolean getRecNearEnd() throws JposException {

        return false;
    }

    public int getRecSidewaysMaxChars() throws JposException {

        return 0;
    }

    public int getRecSidewaysMaxLines() throws JposException {

        return 0;
    }

    public int getRotateSpecial() throws JposException {

        return 0;
    }

    public String getSlpBarCodeRotationList() throws JposException {

        return null;
    }

    public boolean getSlpEmpty() throws JposException {

        return false;
    }

    public boolean getSlpLetterQuality() throws JposException {
        return this.slpLetterQuality;
    }

    public int getSlpLineChars() throws JposException {
        return this.slpLineChars;
    }

    public String getSlpLineCharsList() throws JposException {

        return null;
    }

    public int getSlpLineHeight() throws JposException {
        return this.slpLineHeight;
    }

    public int getSlpLineSpacing() throws JposException {
        return this.slpLineSpacing;
    }

    public int getSlpLineWidth() throws JposException {

        return 0;
    }

    public int getSlpLinesNearEndToEnd() throws JposException {

        return 0;
    }

    public int getSlpMaxLines() throws JposException {

        return 0;
    }

    public boolean getSlpNearEnd() throws JposException {

        return false;
    }

    public int getSlpSidewaysMaxChars() throws JposException {

        return 0;
    }

    public int getSlpSidewaysMaxLines() throws JposException {

        return 0;
    }

    public void printTwoNormal(int i, String s, String s1) throws JposException {
    }

    public void rotatePrint(int i, int j) throws JposException {

    }

    public void setAsyncMode(boolean flag) throws JposException {
        this.asyncMode = flag;
    }

    public void setBitmap(int i, int j, String s, int k, int l) throws JposException {
    }

    public void setCharacterSet(int i) throws JposException {
        this.characterSet = i;
    }

    public void setFlagWhenIdle(boolean flag) throws JposException {
        this.flagWhenIdle = flag;
    }

    public void setJrnLetterQuality(boolean flag) throws JposException {
        this.jrnLetterQuality = flag;
    }

    public void setJrnLineChars(int i) throws JposException {
        this.jrnLineChars = i;
    }

    public void setJrnLineHeight(int i) throws JposException {
        this.jrnLineHeight = i;
    }

    public void setJrnLineSpacing(int i) throws JposException {
        this.jrnLineSpacing = i;
    }

    public void setLogo(int i, String s) throws JposException {

    }

    public void setMapMode(int i) throws JposException {
        this.mapMode = i;
    }

    public void setRecLetterQuality(boolean flag) throws JposException {
        this.recLetterQuality = flag;
    }

    public void setRecLineChars(int i) throws JposException {
        this.recLineChars = i;
    }

    public void setRecLineHeight(int i) throws JposException {
        this.recLineHeight = i;
    }

    public void setRecLineSpacing(int i) throws JposException {
        this.recLineSpacing = i;
    }

    public void setRotateSpecial(int i) throws JposException {
        this.rotateSpecial = i;
    }

    public void setSlpLetterQuality(boolean flag) throws JposException {
        this.slpLetterQuality = flag;
    }

    public void setSlpLineChars(int i) throws JposException {
        this.slpLineChars = i;
    }

    public void setSlpLineHeight(int i) throws JposException {
        this.slpLineHeight = i;
    }

    public void setSlpLineSpacing(int i) throws JposException {
        this.slpLineSpacing = i;
    }

    public void transactionPrint(int i, int j) throws JposException {

    }

    public void validateData(int i, String s) throws JposException {

    }

    public void checkHealth(int i) throws JposException {

    }

    public void directIO(int i, int[] ai, Object obj) throws JposException {

    }

    public String getCheckHealthText() throws JposException {
        return null;
    }

    public String getDeviceServiceDescription() throws JposException {
        return null;
    }

    public String getPhysicalDeviceDescription() throws JposException {
        return null;
    }

    public String getPhysicalDeviceName() throws JposException {
        return null;
    }

    @Override
    public void deleteInstance() throws JposException {
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }
    
    public Map<Integer, InMemoryBitmap> getInMemoryBitmaps() {
        return inMemoryBitmaps;
    }

    public void printNormalWithEscapeSequences(int station, String data) throws JposException {
        // Add additional escape sequence classes here to be filtered on.
        IEscapeSequence[] checkSequences = new IEscapeSequence[] { 
                new PrintBitmapEscapeSequence(),
                new PrintLineFeedEscapeSequence()};
        
        // Capture the length - 1 so that all comparisons are against 0 based index
        int indexedLength = data.length() - 1;
        
        int nextFilterPosition = indexedLength;
        int previousPrintPosition = 0;
        
        do {
            nextFilterPosition = indexedLength;
            IEscapeSequence seqToPrint = null;
            
            // Find the position of next escape sequence
            for (int i = 0; i < checkSequences.length; i++) {
                int currentFilterEndPosition = checkSequences[i].findNext(data, previousPrintPosition);
                
                
                if (currentFilterEndPosition >= 0 && currentFilterEndPosition <= nextFilterPosition) {
                    // We found a sequence match that occurs sooner than what we had before
                    nextFilterPosition = currentFilterEndPosition;
                    seqToPrint = checkSequences[i];
                }
            }
            if (nextFilterPosition <= indexedLength) {
                int endPrintPosition = seqToPrint == null ? data.length() - 1 : nextFilterPosition - (seqToPrint.getSequenceLength() - 1);
                
                // print normal text 
                if (previousPrintPosition >= 0 && endPrintPosition >= previousPrintPosition) {
                    // Check if we are at last piece of text, if so just print remaining.
                    String text = endPrintPosition == indexedLength 
                                        ? data.substring(previousPrintPosition) 
                                        : data.substring(previousPrintPosition, endPrintPosition);
                    
                    String[] replacedChars = {"|N", "|bC", "|rA"};
                    for (int i = 0; i < replacedChars.length ; i++) {
                        text = StringUtils.replace(text, replacedChars[i], "");
                    }
                    appendText(station, text);
                }
                
                if (seqToPrint != null) {
                    // print the escape
                    seqToPrint.print(this, station);
                }
            }           
            previousPrintPosition = nextFilterPosition + 1;
        } while (nextFilterPosition  < indexedLength);
    }

    
    public interface IEscapeSequence {
        public void print(SimulatedPOSPrinterService service, int station) throws JposException;
        
        public int findNext(String data, int startPosition);
        
        public int getSequenceLength();
        
        public String getPrefix();
        
        public String getSuffix();
    }

    public abstract class AbstractEscapeSequence implements IEscapeSequence {
        private String prefix = Character.toString('\u001B') + "x";

        public String getPrefix() {
            return prefix;
        }
        
    }    
    public class PrintBitmapEscapeSequence extends AbstractEscapeSequence {
        private int bitmapNumber;
            
        public void print(SimulatedPOSPrinterService printerService, int station) throws JposException {
            if (printerService.getInMemoryBitmaps() == null || 
                    !printerService.getInMemoryBitmaps().containsKey(new Integer(bitmapNumber))) {
                
                logger.error("Unable to print bitmap from memory, it was not found.  " +
                        "Ensure setBitmap() was called to load it.");
            }
            else {
                InMemoryBitmap memoryBMap = printerService.getInMemoryBitmaps().get(new Integer(bitmapNumber));
                printerService.printBitmap(memoryBMap.getStation(), memoryBMap.getFileName(), 
                        memoryBMap.getWidth(), memoryBMap.getAlignment());
            }
        }

        public int findNext(String data, int startPosition) {
            int current = data.indexOf(getPrefix(), startPosition);
            if (current >= 0) {
                int beginSeqPosition = current + getPrefix().length();
                int endSeqPosition = data.indexOf(getSuffix(), beginSeqPosition);
                if (endSeqPosition > 0) {
                    bitmapNumber = new Integer(data.substring(beginSeqPosition, endSeqPosition)).intValue();
                } 
                else {
                    current = -1;
                }
                current = endSeqPosition;
            }
            return current;
        }   
        
        public int getSequenceLength() {
            int length = 0;
            if (bitmapNumber > 0) {
                length = new String(getPrefix() + (new Integer(bitmapNumber).toString()) + getSuffix()).length();
            }
            return length;
        }
        
        public String getSuffix() {
            return "B";
        }
    }
    
    public class PrintLineFeedEscapeSequence extends AbstractEscapeSequence {
        private int lineFeedNumber;
        
        public int findNext(String data, int startPosition) {
            int current = data.indexOf(getPrefix(), startPosition);
            if (current >= 0) {
                int beginSeqPosition = current + getPrefix().length();
                int endSeqPosition = data.indexOf(getSuffix(), beginSeqPosition);
                if (endSeqPosition > 0) {
                    lineFeedNumber = new Integer(data.substring(beginSeqPosition, endSeqPosition)).intValue();
                    current = endSeqPosition + getSuffix().length() - 1;
                } 
                else {
                    current = -1;
                }
                
            }
            return current;
        }

        public int getSequenceLength() {
            int length = 0;
            if (lineFeedNumber > 0) {
                length = new String(getPrefix() + (new Integer(lineFeedNumber).toString()) + getSuffix()).length();
            }
            return length;
        }

        public String getSuffix() {
            return "lF";
        }

        public void print(SimulatedPOSPrinterService service, int station) throws JposException {
            for (int i = 0; i < lineFeedNumber; i++) {
                service.printNormal(station, "\n");
            }
        }
        

    }    
    
    public class InMemoryBitmap {
        private int station;
        private String fileName;
        private int width;
        private int alignment;
        
        public InMemoryBitmap(int station, String fileName, int width, int alignment) {
            this.station = station;
            this.fileName = fileName;
            this.width = width;
            this.alignment = alignment;         
        }
        public int getStation() {
            return station;
        }
        public void setStation(int station) {
            this.station = station;
        }
        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        public int getWidth() {
            return width;
        }
        public void setWidth(int width) {
            this.width = width;
        }
        public int getAlignment() {
            return alignment;
        }
        public void setAlignment(int alignment) {
            this.alignment = alignment;
        }
    }
    
}
