package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;

public class OrderCustomer implements Serializable {
    private String iconName;
    private String fullName;
    private String phone;
    private String email;
    private Address address;

    public OrderCustomer() {

    }

    public OrderCustomer(String iconName, String firstName, String lastName, String phone, String email, String line1, String city, String state, String postalCode) {
        this.fullName = firstName.concat(" ").concat(lastName);
        this.phone = phone;
        this.email = email;
        this.address = new Address(line1, city, state, postalCode);
    }

    public OrderCustomer(String iconName, String fullName, String phone, String email, Address address) {
        this.iconName = iconName;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFullName(String firstName, String lastName) {
        this.fullName = firstName.concat(" ").concat(lastName);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAddress(String line1, String city, String state, String zip) {
        this.address = new Address(line1, city, state, zip);
    }
}
