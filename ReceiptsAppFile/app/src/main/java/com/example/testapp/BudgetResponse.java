package com.example.testapp;

public class BudgetResponse {

    private boolean success;
    private String message;
    private BudgetClass budget;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BudgetClass getBudget() {
        return budget;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBudget(BudgetClass budget) {
        this.budget = budget;
    }
}
