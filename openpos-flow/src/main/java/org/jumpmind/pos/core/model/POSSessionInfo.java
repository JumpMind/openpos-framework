package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jumpmind.pos.util.TypedMap.TypedMapKey;

public class POSSessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final TypedMapKey<String> CART_NAME_ATTRIBUTE = new TypedMapKey<>("cartName");
    public static final TypedMapKey<String> LAST_ACTION_ATTRIBUTE = new TypedMapKey<>("lastAction");
    
    private String transactionId;
    private String operatorName;
    private String operatorLoginId;
    private Boolean registerOpen = null;
    private Boolean storeOpen = null;
    private boolean trainingMode = false;
    private boolean adminMode = false;
    private List<String> itemsToAddToTheSale = new ArrayList<>();
    private int tillThresholdStatus = 0;
    
    private Map<String, Object> attributes = new HashMap<>();

    public POSSessionInfo() {
        operatorLoginId = "";
        operatorName = "";
    }

    public POSSessionInfo( POSSessionInfo other ) {
        this.operatorName = other.operatorName;
        this.operatorLoginId = other.operatorLoginId;
        this.registerOpen = other.registerOpen;
        this.storeOpen = other.storeOpen;
    }

    public POSSessionInfo( String operatorName, String operatorLoginId ) {
        this.operatorName = operatorName;
        this.operatorLoginId = operatorLoginId;
    }
    
    public void endSession() {
        operatorLoginId = "";
        operatorName = "";
        transactionId = null;        
    }
    
    public String getOperatorName() {
        return operatorName;
    }
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    public String getOperatorLoginId() {
        return operatorLoginId;
    }
   
    public void setOperatorLoginId(String operatorLoginId) {
        this.operatorLoginId = operatorLoginId;
    }

    
    public Optional<Boolean> isRegisterOpen() {
        return Optional.ofNullable(this.registerOpen);
    }

    public void setRegisterOpen(Optional<Boolean> registerOpen) {
        this.registerOpen = registerOpen != null ? registerOpen.orElse(null) : null;
    }

    public Optional<Boolean> isStoreOpen() {
        return Optional.ofNullable(this.storeOpen);
    }

    public void setStoreOpen(Optional<Boolean> storeOpen) {
        this.storeOpen = storeOpen != null ? storeOpen.orElse(null) : null;
    }

    public <T> void put(TypedMapKey<T> key, T value) {
        this.attributes.put(key.toString(), value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(TypedMapKey<T> key) {
        return (T)this.attributes.get(key.toString());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T)this.attributes.get(key);
    }
    
    public Object remove(String key) {
        return this.attributes.remove(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T remove(TypedMapKey<T> key) {
        return (T)this.attributes.remove(key.toString());
    }
    
    public void put(String key, Object value) {
        this.attributes.put(key, value);
    }
    
    public boolean is(String key) {
        Object value = this.attributes.get(key);
        return value != null && value.equals(Boolean.TRUE);
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public boolean isTrainingMode() {
        return this.trainingMode;
    }
    
    public boolean getTrainingMode() {
        return this.isTrainingMode();
    }

    public void setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
    }
    
    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }
    
    public boolean isAdminMode() {
        return adminMode;
    }
    
    public void setItemsToAddToTheSale(List<String> itemsToAddToTheSale) {
        this.itemsToAddToTheSale = itemsToAddToTheSale;
    }
    
    public List<String> getItemsToAddToTheSale() {
        return itemsToAddToTheSale;
    }
    
    public boolean hasItemsToAddToTheSale()  {
    	return ((this.itemsToAddToTheSale != null) && (this.itemsToAddToTheSale.size() > 0));
    }
        
    public void clearItemsToAddToTheSale()  {
    	this.itemsToAddToTheSale = new ArrayList<>();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("operatorLoginId", operatorLoginId).
                append("operatorName", operatorName).
                toString();
    }

    public int getTillThresholdStatus() {
        return tillThresholdStatus;
    }

    public void setTillThresholdStatus(int tillThresholdStatus) {
        this.tillThresholdStatus = tillThresholdStatus;
    }
}
