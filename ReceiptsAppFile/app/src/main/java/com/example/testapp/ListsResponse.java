package com.example.testapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("lists")
    private List<ListData> lists;

    public ListsResponse(boolean success, List<ListData> lists) {
        this.success = success;
        this.lists = lists;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ListData> getLists() {
        return lists;
    }

    public void setLists(List<ListData> lists) {
        this.lists = lists;
    }

    @Override
    public String toString() {
        return "ListsResponse{" +
                "success=" + success +
                ", lists=" + lists +
                '}';
    }
}
