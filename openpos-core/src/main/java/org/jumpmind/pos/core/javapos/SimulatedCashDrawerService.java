package org.jumpmind.pos.core.javapos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jpos.JposException;
import jpos.services.CashDrawerService19;

public class SimulatedCashDrawerService extends AbstractSimulatedService implements CashDrawerService19 {
    protected final Log logger = LogFactory.getLog(getClass());

    private long cashDrawerOpened = 1;

    @Override
    public int getDeviceServiceVersion() throws JposException {
        return 1007000;
    }

    @Override
    public void reset() {
        this.cashDrawerOpened = 1;
    }



    public boolean getDrawerOpened() throws JposException {
        boolean open = System.currentTimeMillis() - this.cashDrawerOpened < 2000;
        logger.info("The cash drawer was open? " + open);
        return open;
    }

    public void openDrawer() throws JposException {
        logger.info("The cash drawer was opened");
        this.cashDrawerOpened = System.currentTimeMillis();
    }

    public void waitForDrawerClose(int arg0, int arg1, int arg2, int arg3) throws JposException {
    	try {
			Thread.sleep(2000);
			logger.info("The simulated cash drawer was closed");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void compareFirmwareVersion(String arg0, int[] arg1) throws JposException {
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    public void updateFirmware(String arg0) throws JposException {
    }

    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public void resetStatistics(String arg0) throws JposException {
    }

    public void retrieveStatistics(String[] arg0) throws JposException {
    }

    public void updateStatistics(String arg0) throws JposException {
    }

    public boolean getCapStatusMultiDrawerDetect() throws JposException {
        return false;
    }

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerState() throws JposException {
        return 0;
    }

    public boolean getCapStatus() throws JposException {
        return false;
    }

    public void checkHealth(int arg0) throws JposException {
    }

    public void directIO(int arg0, int[] arg1, Object arg2) throws JposException {
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

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }

}
