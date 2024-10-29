package com.example.testapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {

    @GET("users")
    Call<List<User>> getUsers();

    @POST("users")
    Call<Void> addUser(@Body User user);

    // POST request for login
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
