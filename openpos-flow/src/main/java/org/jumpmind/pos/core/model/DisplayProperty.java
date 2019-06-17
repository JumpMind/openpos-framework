package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class DisplayProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	private String label;
	private String value;
	private String valueFormatter;
	
	public DisplayProperty() {
	}

	public DisplayProperty(String label, String value, String valueFormatter) {
	    this.label = label;
	    this.value = value;
	    this.valueFormatter = valueFormatter;
	}

	public DisplayProperty(String label, String value) {
	    this(label, value, null);
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValueFormatter() {
		return valueFormatter;
	}
	
	public void setValueFormatter(String valueFormatter) {
		this.valueFormatter = valueFormatter;
	}
}
