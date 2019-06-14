package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleRetrievalScreen extends Screen {

    private static final long serialVersionUID = 1L;

    List<Sale> sales;

    List<String> columnsToInclude;

    Map<String, String> columnHeaders;

    public SaleRetrievalScreen() {
        this.setId("saleRetrieval");
        this.setScreenType("SaleRetrieval");
        this.setIcon(IconType.RetrieveAction);
    }

    public void setColumnHeaders(Map<String, String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public Map<String, String> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnsToInclude(List<String> columnsToInclude) {
        this.columnsToInclude = columnsToInclude;
    }

    public List<String> getColumnsToInclude() {
        return columnsToInclude;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void addSale(Sale sale) {
        if (this.sales == null) {
            this.sales = new ArrayList<>();
        }
        this.sales.add(sale);
    }

    public void addColumnToInclude(String columnToInclude) {
        if (this.columnsToInclude == null) {
            this.columnsToInclude = new ArrayList<>();
        }
        this.columnsToInclude.add(columnToInclude);
    }

    public void addColumnHeader(String columnId, String headerLabel, boolean include) {
        if (this.columnHeaders == null) {
            this.columnHeaders = new HashMap<>();
        }
        this.columnHeaders.put(columnId, headerLabel);
        if (include) {
            addColumnToInclude(columnId);
        }
    }

    public static class Sale implements Serializable {

        private static final long serialVersionUID = 1L;

        String businessDate;
        String transactionId;
        String workstationId;
        String loyaltyNumber;
        String customerName;
        String tillId;
        String deviceId;
        String grandTotal;
        int lineItemCount;
        int availableToReturnCount;
        long sequenceNumber;
        
        public void setBusinessDate(String businessDate) {
            this.businessDate = businessDate;
        }
        
        public String getBusinessDate() {
            return businessDate;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getWorkstationId() {
            return workstationId;
        }

        public void setWorkstationId(String workstationId) {
            this.workstationId = workstationId;
        }

        public String getLoyaltyNumber() {
            return loyaltyNumber;
        }

        public void setLoyaltyNumber(String loyaltyId) {
            this.loyaltyNumber = loyaltyId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getTillId() {
            return tillId;
        }

        public void setTillId(String tillId) {
            this.tillId = tillId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(String grandTotal) {
            this.grandTotal = grandTotal;
        }

        public void setSequenceNumber(long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public long getSequenceNumber() {
            return sequenceNumber;
        }

        public void setLineItemCount(int lineItemCount) {
            this.lineItemCount = lineItemCount;
        }

        public long getLineItemCount() {
            return lineItemCount;
        }
        
        public void setAvailableToReturnCount(int availableToReturnCount) {
            this.availableToReturnCount = availableToReturnCount;
        }
        
        public int getAvailableToReturnCount() {
            return availableToReturnCount;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((businessDate == null) ? 0 : businessDate.hashCode());
            result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
            result = prime * result + (int) (sequenceNumber ^ (sequenceNumber >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Sale other = (Sale) obj;
            if (businessDate == null) {
                if (other.businessDate != null)
                    return false;
            } else if (!businessDate.equals(other.businessDate))
                return false;
            if (deviceId == null) {
                if (other.deviceId != null)
                    return false;
            } else if (!deviceId.equals(other.deviceId))
                return false;
            if (sequenceNumber != other.sequenceNumber)
                return false;
            return true;
        }

    }

}
