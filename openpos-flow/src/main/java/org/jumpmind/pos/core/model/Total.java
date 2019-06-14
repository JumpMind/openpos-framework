package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class Total implements Serializable {
    public enum TotalType {
        Amount,
        Quantity
    }

	private static final long serialVersionUID = 1L;

	private String name;
	private String amount;
	private TotalType type;

	public Total( String name, String amount ) {
	    this(name, amount, TotalType.Amount);
	}

    public Total( String name, String amount, TotalType type ) {
        this.name = name;
        this.amount = amount;
        this.type = type;
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

    public TotalType getType() {
        return type;
    }

    public void setType(TotalType type) {
        this.type = type;
    }
}
