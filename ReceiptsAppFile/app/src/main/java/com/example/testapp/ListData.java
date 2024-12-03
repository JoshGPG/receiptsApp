package com.example.testapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("list_name")
    private String listName;

    @SerializedName("purchases")
    private String purchases;

    // Constructor
    public ListData(int id, int userId, String listName, String purchases) {
        this.id = id;
        this.userId = userId;
        this.listName = listName;
        this.purchases = purchases;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getPurchases() {
        return purchases;
    }

    public void setPurchases(String purchases) {
        this.purchases = purchases;
    }

    @Override
    public String toString() {
        return "ListData{" +
                "id=" + id +
                ", userId=" + userId +
                ", listName='" + listName + '\'' +
                ", purchases='" + purchases + '\'' +
                '}';
    }
}
