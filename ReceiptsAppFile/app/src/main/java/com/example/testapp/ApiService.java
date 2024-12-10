package com.example.testapp;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {

    @GET("users")
    Call<List<User>> getUsers();

    @POST("users")
    Call<Void> createUser(@Body CreateUser user);

    @POST("users")
    Call<Void> addUser(@Body User user);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("checkUsername")
    Call<UsernameCheckResponse> checkUsernameExists(@Query("username") String username);

    @GET("/purchases/{userId}/{category}")
    Call<PurchasesResponse> getPurchasesByCategory(
            @Path("userId") int userId,
            @Path("category") String category
    );

    @PUT("/updatePurchase")
    Call<Void> updatePurchase(@Body PurchaseUpdateRequest purchaseUpdateRequest);

    @DELETE("/deletePurchase/{id}")
    Call<Void> deletePurchase(@Path("id") int purchaseId);

    @GET("/lists/{userId}")
    Call<ListsResponse> getLists(@Path("userId") int userId);

    @GET("/purchasesByIds")
    Call<PurchasesResponse> getPurchasesByIds(@Query("ids") String purchaseIds);

    @GET("budget/{userId}")
    Call<BudgetResponse> getBudget(@Path("userId") int userId);

    @POST("/budget")
    Call<Void> addBudget(@Body BudgetClass budget);

    @POST("/purchases")
    Call<ResponseBody> addPurchase(@Body Purchase purchase);

    @POST("/addList")
    Call<Void> addList(@Body AddListRequest addListRequest);



}
