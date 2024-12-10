package com.example.testapp;

public class AddListRequest {
    private int userId;
    private String listName;
    private String purchaseIds;

    public AddListRequest(int userId, String listName, String purchaseIds) {
        this.userId = userId;
        this.listName = listName;
        this.purchaseIds = purchaseIds;
    }

    public int getUserId() {
        return userId;
    }

    public String getListName() {
        return listName;
    }

    public String getPurchaseIds() {
        return purchaseIds;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setPurchaseIds(String purchaseIds) {
        this.purchaseIds = purchaseIds;
    }
}
