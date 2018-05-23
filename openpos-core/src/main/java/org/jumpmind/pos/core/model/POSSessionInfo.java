package org.jumpmind.pos.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jumpmind.pos.util.TypedMap.TypedMapKey;

public class POSSessionInfo {
    public static final TypedMapKey<String> CART_NAME_ATTRIBUTE = new TypedMapKey<>("cartName");
    public static final TypedMapKey<String> LAST_ACTION_ATTRIBUTE = new TypedMapKey<>("lastAction");
    
    private String transactionId;
    private String operatorName;
    private String operatorLoginId;
    private Optional<Boolean> registerOpen = Optional.empty();
    private Optional<Boolean> storeOpen = Optional.empty();
    private boolean trainingMode = false;
    private boolean adminMode = false;
    
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
        return registerOpen;
    }

    public void setRegisterOpen(Optional<Boolean> registerOpen) {
        this.registerOpen = registerOpen;
    }

    public Optional<Boolean> isStoreOpen() {
        return storeOpen;
    }

    public void setStoreOpen(Optional<Boolean> storeOpen) {
        this.storeOpen = storeOpen;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("operatorLoginId", operatorLoginId).
                append("operatorName", operatorName).
                toString();
    }
}
