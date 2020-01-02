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
    private Date orderCreated;
    private String orderTotalLabel;
    private String amountDueLabel;
    private String orderDueLabel;
    private String orderCreatedLabel;
    private String orderDueIcon;
    private String itemCountLabel;
    private String orderTotal;
    private String amountDue;
    private String itemCountIcon;
    private TimeUnitLabels timeUnitLabels;

    public OrderSummary() {
    }

    public OrderSummary(String number, String title, String iconName, OrderCustomer customer, String itemCount, String status, String statusLabel,
                        String statusIcon, Date orderDue, Date orderCreated, String orderTotalLabel, String amountDueLabel,
                        String orderDueLabel, String orderCreatedLabel, String orderDueIcon, String itemCountLabel,
                        String orderTotal, String amountDue, String itemCountIcon, TimeUnitLabels timeUnitLabels) {
        this.number = number;
        this.title = title;
        this.iconName = iconName;
        this.customer = customer;
        this.itemCount = itemCount;
        this.status = status;
        this.statusLabel = statusLabel;
        this.statusIcon = statusIcon;
        this.orderDue = orderDue;
        this.orderCreated = orderCreated;
        this.orderTotalLabel = orderTotalLabel;
        this.amountDueLabel = amountDueLabel;
        this.orderDueLabel = orderDueLabel;
        this.orderCreatedLabel = orderCreatedLabel;
        this.orderDueIcon = orderDueIcon;
        this.itemCountLabel = itemCountLabel;
        this.orderTotal = orderTotal;
        this.amountDue = amountDue;
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

    public Date getOrderCreated() {
        return orderCreated;
    }

    public void setOrderCreated(Date orderCreated) {
        this.orderCreated = orderCreated;
    }

    public String getOrderTotalLabel() {
        return orderTotalLabel;
    }

    public void setOrderTotalLabel(String orderTotalLabel) {
        this.orderTotalLabel = orderTotalLabel;
    }

    public String getAmountDueLabel() {
        return amountDueLabel;
    }

    public void setAmountDueLabel(String amountDueLabel) {
        this.amountDueLabel = amountDueLabel;
    }

    public String getOrderDueLabel() {
        return orderDueLabel;
    }

    public void setOrderDueLabel(String orderDueLabel) {
        this.orderDueLabel = orderDueLabel;
    }

    public String getOrderCreatedLabel() {
        return orderCreatedLabel;
    }

    public void setOrderCreatedLabel(String orderCreatedLabel) {
        this.orderCreatedLabel = orderCreatedLabel;
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

    public String getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(String amountDue) {
        this.amountDue = amountDue;
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


