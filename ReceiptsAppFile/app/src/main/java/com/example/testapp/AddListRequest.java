package com.example.testapp;

import java.util.List;

public class AddListRequest {
    private int userId;
    private String listName;
    private List<Purchase> purchases;

    // Constructor
    public AddListRequest(int userId, String listName, List<Purchase> purchases) {
        this.userId = userId;
        this.listName = listName;
        this.purchases = purchases;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }
}
