package org.jumpmind.pos.core.ui.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Address implements Serializable {
    private String address;
    private String city;
    private String state;
    private String postalCode;

    public Address() {

    }

    public Address(String address, String city, String state, String postalCode) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }
}
