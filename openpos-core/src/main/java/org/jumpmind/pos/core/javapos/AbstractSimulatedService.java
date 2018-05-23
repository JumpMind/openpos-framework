package org.jumpmind.pos.core.javapos;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jpos.JposConst;
import jpos.JposException;
import jpos.services.EventCallbacks;

public abstract class AbstractSimulatedService {
    
    public final static int DEVICE_VERSION = 1009000;
    
    protected final Log logger = LogFactory.getLog(getClass());

    protected EventCallbacks callbacks;

    private int state = JposConst.JPOS_S_CLOSED;
    private boolean open;
    private boolean claimed;
    private boolean enabled;
    private boolean freezeEvents;
    private int powerNotify;

    public abstract void reset();

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
        reset();
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

    public void addToGridBag(int x, int y, int colspan, Component comp, GridBagConstraints c,
            Container container) {
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = colspan;

        container.add(comp, c);
    }

}
