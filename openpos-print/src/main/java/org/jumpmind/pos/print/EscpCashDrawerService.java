package org.jumpmind.pos.print;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jpos.JposConst;
import jpos.JposException;
import jpos.services.CashDrawerService19;
import jpos.services.EventCallbacks;

public class EscpCashDrawerService implements CashDrawerService19 {

	private IOpenposPrinter printer;
	public static String printerHostname = "";
	private String printerPort = "9100";
	
    public final static int DEVICE_VERSION = 1009000;
    
    protected final Log logger = LogFactory.getLog(getClass());

    protected EventCallbacks callbacks;

    private int state = JposConst.JPOS_S_CLOSED;
    private boolean open;
    private boolean claimed;
    private boolean enabled;
    private boolean freezeEvents;
    private int powerNotify;
	
	public EscpCashDrawerService() {
		System.out.print(true);
	}

	public void setPrinter(IOpenposPrinter printer) {
		this.printer = printer;
	}

	public IOpenposPrinter getPrinter() {
		return printer;
	}

	public static void setPrinterHostname(String printerHostname) {
		//no op
	}

	public void setPrinterPort(String printerPort) {
		this.printerPort = printerPort;
	}
	
	public void init() throws JposException {
		printer = new EscpPOSPrinter();
		Map<String, Object> settings = new HashMap<>();
		settings.put("hostName", printerHostname);
		settings.put("port", printerPort);
		settings.put("connectionClass", "org.jumpmind.pos.print.SocketConnectionFactory");
		settings.put("printerCommandLocations", "esc_p.properties,epson.properties");
		this.printer.init(settings);
	}

	protected void openPrinter() throws JposException {
		this.printer.open("drawerPrinter", null);
	}

	protected void closePrinter() throws JposException {
		this.printer.close();
	}

	@Override
	public boolean getDrawerOpened() throws JposException {
		String returnCode = "1";
		if (printer != null) {
			try {
				openPrinter();
				returnCode = printer.getDrawerStatus("");
				closePrinter();
			} catch (JposException e) {
				logger.error("Could not check drawer", e);
			}
		} else {
			logger.error("Could not check drawer. Printer not found");
		}
		return Integer.parseInt(returnCode) == 0;
	}

	public void waitForDrawerClose(int arg0, int arg1, int arg2, int arg3) throws JposException {
		try {
			while (getDrawerOpened()) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void openDrawer() throws JposException {
		init();
		if (printer != null) {
			try {
				openPrinter();
				printer.openCashDrawer("");
				closePrinter();
			} catch (JposException e) {
				logger.error("Could not open drawer", e);
			}
		} else {
			logger.error("Could not open drawer. Printer not found");
		}
	}
	
    public int getDeviceServiceVersion() throws JposException {
        return DEVICE_VERSION;
    }

    protected void invoke(Runnable runnable) throws JposException {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeAndWait(runnable);
            }
        } catch (Exception e) {
            throw new JposException(JposConst.JPOS_E_FAILURE);
        }
    }

    protected void checkIfOpen() throws JposException {
        if (!open)
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open.");
    }

    protected void checkIfClaimed() throws JposException {
        if (!claimed)
            throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed.");
    }

    public void deleteInstance() throws JposException {
        checkIfOpen();
    }

    public void open(String s, EventCallbacks eventcallbacks) throws JposException {
        if (open) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Service is already open.");
        }
        this.open = true;
        this.state = JposConst.JPOS_S_IDLE;
        this.callbacks = eventcallbacks;
        logger.info("The device was opened");
    }

    public void close() throws JposException {
        checkIfOpen();
        this.open = false;
        this.state = JposConst.JPOS_S_CLOSED;

        // Also need to reset all the member variables
        callbacks = null;
        enabled = false;
        freezeEvents = false;
        claimed = false;
        logger.info("The device was closed");
    }

    public void claim(int arg0) throws JposException {
        checkIfOpen();
        claimed = true;
        logger.info("The device was claimed");
    }

    public void release() throws JposException {
        checkIfOpen();
        checkIfClaimed();
        this.claimed = false;
        this.enabled = false;
        this.state = JposConst.JPOS_S_IDLE;
        logger.info("The device was released");
    }

    public int getState() throws JposException {
        return this.state;
    }

    public boolean getClaimed() throws JposException {
        return claimed;
    }

    public boolean getDeviceEnabled() throws JposException {
        return enabled;
    }

    public void setDeviceEnabled(boolean enabled) throws JposException {
        checkIfOpen();
        checkIfClaimed();
        this.enabled = enabled;
    }

    public void setFreezeEvents(boolean freezeEvents) throws JposException {
        checkIfOpen();
        this.freezeEvents = freezeEvents;
    }

    public boolean getFreezeEvents() throws JposException {
        return freezeEvents;
    }

    public int getPowerNotify() throws JposException {
        return powerNotify;
    }

    public void setPowerNotify(int powerNotify) {
        this.powerNotify = powerNotify;
    }

    public EventCallbacks getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(EventCallbacks callbacks) {
        this.callbacks = callbacks;
    }

	@Override
	public boolean getCapStatisticsReporting() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getCapUpdateStatistics() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrieveStatistics(String[] statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getCapStatusMultiDrawerDetect() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCapPowerReporting() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPowerState() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getCapStatus() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCheckHealthText() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceServiceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhysicalDeviceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhysicalDeviceName() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkHealth(int level) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void directIO(int command, int[] data, Object object) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getCapCompareFirmwareVersion() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getCapUpdateFirmware() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void compareFirmwareVersion(String firmwareFileName, int[] result) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFirmware(String firmwareFileName) throws JposException {
		// TODO Auto-generated method stub

	}
}
