package com.example.testapp;

public class Purchase {
    private int user_id;
    private String item_name;
    private double price;
    private String category;
    private String date_purchased;

    // Getters
    public int getUserId() { return user_id; }
    public String getItemName() { return item_name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getDatePurchased() { return date_purchased; }
}
