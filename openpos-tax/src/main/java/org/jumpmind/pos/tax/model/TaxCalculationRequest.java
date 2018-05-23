package org.jumpmind.pos.tax.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaxCalculationRequest {

    private List<TaxableItem> taxableItems = new ArrayList<TaxableItem>();

    private Date calculateDate;

    private String geoCode;

    private String customerType;

    public TaxCalculationRequest() {
    }

    public TaxCalculationRequest(List<TaxableItem> taxableItems, Date calculateDate, String geoCode, String customerType) {
        this.taxableItems = taxableItems;
        this.calculateDate = calculateDate;
        this.geoCode = geoCode;
        this.customerType = customerType;
    }

    public List<TaxableItem> getTaxableItems() {
        return taxableItems;
    }

    public void setTaxableItems(List<TaxableItem> taxableItems) {
        this.taxableItems = taxableItems;
    }

    public void addTaxableItem(TaxableItem taxableItem) {
        this.taxableItems.add(taxableItem);
    }

    public Date getCalculateDate() {
        return calculateDate;
    }

    public void setCalculateDate(Date calculateDate) {
        this.calculateDate = calculateDate;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

}
