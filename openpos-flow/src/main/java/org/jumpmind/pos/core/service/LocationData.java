package org.jumpmind.pos.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jumpmind.pos.util.DefaultObjectMapper;

public class LocationData {
    private String type;
    private String postalCode;
    private String country;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        try {
            return DefaultObjectMapper.defaultObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
