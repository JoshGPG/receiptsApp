package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Purchase implements Parcelable {

    @SerializedName("purchase_id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price")
    private double price;

    @SerializedName("category")
    private String category;

    @SerializedName("date_purchased")
    private String datePurchased;

    // Constructor, getters, and other methods here
    // Getters
    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getDatePurchased() { return datePurchased; }

    // Setters
    public void setId(int purchase_id) {
        this.id = purchase_id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setItemName(String item_name) {
        this.itemName = itemName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDatePurchased(String date_purchased) {
        this.datePurchased = datePurchased;
    }

    protected Purchase(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        itemName = in.readString();
        price = in.readDouble();
        category = in.readString();
        datePurchased = in.readString();
    }

    public static final Creator<Purchase> CREATOR = new Creator<Purchase>() {
        @Override
        public Purchase createFromParcel(Parcel in) {
            return new Purchase(in);
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(itemName);
        dest.writeDouble(price);
        dest.writeString(category);
        dest.writeString(datePurchased);
    }
}