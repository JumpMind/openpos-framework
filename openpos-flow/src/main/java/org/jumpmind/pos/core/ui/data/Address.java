package org.jumpmind.pos.core.ui.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class Address implements Serializable {
    private String attention;
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
