package org.jumpmind.pos.persist.cars;

import java.math.BigDecimal;
import java.util.Date;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(name="service_invoice")
public class ServiceInvoice extends Entity {
    
    @Column(primaryKey=true)
    private Date invoiceDate;
    @Column(primaryKey=true)
    private long invoiceLocation;
    @Column(primaryKey=true)
    private long invoiceNumber;
    @Column
    private BigDecimal invoiceTotal;
    @Column
    private String invoiceStatus;    
    
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
    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }
    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }
    public String getInvoiceStatus() {
        return invoiceStatus;
    }
    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }
}
