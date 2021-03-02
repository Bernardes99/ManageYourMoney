package com.example.manageyourmoney;

import java.util.Date;

public class TransactionObject {

    int id;
    String category;
    String description;
    double quantity;
    Date date;

    public TransactionObject(int id, String category, String description, double quantity, Date date) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.date = date;
    }

    public TransactionObject() {}

    public TransactionObject(String category, double quantity) {
        this.category = category;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
