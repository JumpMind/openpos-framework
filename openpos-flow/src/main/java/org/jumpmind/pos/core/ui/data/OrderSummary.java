package org.jumpmind.pos.core.ui.data;

import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

@Builder
public class OrderSummary implements Serializable {
    private String number;
    private String title;
    private String iconName;
    private OrderCustomer customer;
    private String itemCount;
    private String status;
    private String statusLabel;
    private String statusIcon;
    private Date orderDue;
    private String orderTotalLabel;
    private String orderDueLabel;
    private String orderDueIcon;
    private String itemCountLabel;
    private String orderTotal;
    private String itemCountIcon;
    private TimeUnitLabels timeUnitLabels;

    public OrderSummary() {
    }

    public OrderSummary(String number, String title, String iconName, OrderCustomer customer, String itemCount, String status, String statusLabel,
                        String statusIcon, Date orderDue, String orderTotalLabel, String orderDueLabel, String orderDueIcon, String itemCountLabel,
                        String orderTotal, String itemCountIcon, TimeUnitLabels timeUnitLabels) {
        this.number = number;
        this.title = title;
        this.iconName = iconName;
        this.customer = customer;
        this.itemCount = itemCount;
        this.status = status;
        this.statusLabel = statusLabel;
        this.statusIcon = statusIcon;
        this.orderDue = orderDue;
        this.orderTotalLabel = orderTotalLabel;
        this.orderDueLabel = orderDueLabel;
        this.orderDueIcon = orderDueIcon;
        this.itemCountLabel = itemCountLabel;
        this.orderTotal = orderTotal;
        this.itemCountIcon = itemCountIcon;
        this.timeUnitLabels = timeUnitLabels;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public OrderCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(OrderCustomer customer) {
        this.customer = customer;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public Date getOrderDue() {
        return orderDue;
    }

    public void setOrderDue(Date orderDue) {
        this.orderDue = orderDue;
    }

    public String getOrderTotalLabel() {
        return orderTotalLabel;
    }

    public void setOrderTotalLabel(String orderTotalLabel) {
        this.orderTotalLabel = orderTotalLabel;
    }

    public String getOrderDueLabel() {
        return orderDueLabel;
    }

    public void setOrderDueLabel(String orderDueLabel) {
        this.orderDueLabel = orderDueLabel;
    }

    public String getOrderDueIcon() {
        return orderDueIcon;
    }

    public void setOrderDueIcon(String orderDueIcon) {
        this.orderDueIcon = orderDueIcon;
    }

    public String getItemCountLabel() {
        return itemCountLabel;
    }

    public void setItemCountLabel(String itemCountLabel) {
        this.itemCountLabel = itemCountLabel;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getItemCountIcon() {
        return itemCountIcon;
    }

    public void setItemCountIcon(String itemCountIcon) {
        this.itemCountIcon = itemCountIcon;
    }

    public TimeUnitLabels getTimeUnitLabels() {
        return timeUnitLabels;
    }

    public void setTimeUnitLabels(TimeUnitLabels timeUnitLabels) {
        this.timeUnitLabels = timeUnitLabels;
    }
}


