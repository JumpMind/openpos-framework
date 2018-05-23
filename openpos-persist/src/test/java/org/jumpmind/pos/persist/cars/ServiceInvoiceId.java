package org.jumpmind.pos.persist.cars;

import java.util.Date;

import org.jumpmind.pos.persist.EntityId;

public class ServiceInvoiceId implements EntityId {
    
    private Date invoiceDate;
    private long invoiceLocation;
    private long invoiceNumber;
    
    public Date getInvoiceDate() {
        return invoiceDate;
    }
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    public long getInvoiceLocation() {
        return invoiceLocation;
    }
    public void setInvoiceLocation(long invoiceLocation) {
        this.invoiceLocation = invoiceLocation;
    }
    public long getInvoiceNumber() {
        return invoiceNumber;
    }
    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

}
