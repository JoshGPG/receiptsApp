package com.example.testapp;

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

    // POST request for login
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("checkUsername")
    Call<UsernameCheckResponse> checkUsernameExists(@Query("username") String username);

    @GET("/purchases/{userId}")
    Call<PurchasesResponse> getPurchases(@Path("userId") int userId);

    @GET("/purchases/{userId}/{category}")
    Call<PurchasesResponse> getPurchasesByCategory(
            @Path("userId") int userId,
            @Path("category") String category
    );

    @PUT("/updatePurchase")
    Call<Void> updatePurchase(@Body PurchaseUpdateRequest purchaseUpdateRequest);

    @DELETE("/deletePurchase/{id}")
    Call<Void> deletePurchase(@Path("id") int purchaseId);


}
