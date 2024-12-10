package com.example.testapp;

import java.io.Serializable;

public class BudgetClass implements Serializable {
    private int userId;
    private double budget;
    private int months;

    public BudgetClass() {}

    public BudgetClass(int userId, double budget, int months) {
        this.userId = userId;
        this.budget = budget;
        this.months = months;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }
}