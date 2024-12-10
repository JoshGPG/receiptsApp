package com.example.testapp;

import com.google.gson.annotations.SerializedName;

public class PurchaseUpdateRequest {

    @SerializedName("id")
    private int id;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price")
    private double price;

    @SerializedName("category")
    private String category;

    public PurchaseUpdateRequest(int id, String itemName, double price, String category) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
