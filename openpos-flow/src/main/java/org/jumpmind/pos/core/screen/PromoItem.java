package org.jumpmind.pos.core.screen;

import java.io.Serializable;

public class PromoItem implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String description;
    private String price;
    private String discount;
    private String extPrice;
    
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getDiscount() {
		return discount;
	}
	
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
	public String getExtPrice() {
		return extPrice;
	}
	
	public void setExtPrice(String extPrice) {
		this.extPrice = extPrice;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}