package com.example.testapp;

import java.util.List;

public class PurchasesResponse {
    private boolean success;
    private List<Purchase> purchases;

    public boolean isSuccess() {
        return success;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }
}
