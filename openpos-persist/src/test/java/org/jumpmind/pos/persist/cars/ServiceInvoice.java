package org.jumpmind.pos.persist.cars;

import java.math.BigDecimal;
import java.util.Date;

import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="service_invoice")
public class ServiceInvoice extends AbstractModel {
    
    private static final long serialVersionUID = 1L;
    @ColumnDef(primaryKey=true)
    private Date invoiceDate;
    @ColumnDef(primaryKey=true)
    private long invoiceLocation;
    @ColumnDef(primaryKey=true)
    private long invoiceNumber;
    @ColumnDef
    private BigDecimal invoiceTotal;
    @ColumnDef
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
