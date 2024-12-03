package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePage extends AppCompatActivity {
    private EditText username, password, name;
    private Button buttonCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePage.this, StartPage.class);
                startActivity(intent);
                finish();
            }
        });


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        buttonCreateUser = findViewById(R.id.createButton);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        buttonCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameSend = username.getText().toString().trim();
                String passwordSend = password.getText().toString().trim();
                String nameSend = name.getText().toString().trim();


                if (usernameSend.isEmpty() || passwordSend.isEmpty() || nameSend.isEmpty()) {
                    Toast.makeText(CreatePage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                apiService.checkUsernameExists(usernameSend).enqueue(new Callback<UsernameCheckResponse>() {
                    @Override
                    public void onResponse(Call<UsernameCheckResponse> call, Response<UsernameCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean exists = response.body().isExists();
                            Log.d("CreatePage", "Username exists: " + exists);

                            if (!exists) {
                                createUser(apiService, usernameSend, passwordSend, nameSend);
                            } else {
                                Toast.makeText(CreatePage.this, "Username already taken", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("CreatePage", "Failed to check username. Response code: " + response.code() + ", Message: " + response.message());
                            Toast.makeText(CreatePage.this, "Failed to check username", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<UsernameCheckResponse> call, Throwable t) {
                        Toast.makeText(CreatePage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void createUser(ApiService apiService, String username, String password, String name) {
        // Create a new user request
        CreateUser newUser = new CreateUser(name, username, password);

        apiService.createUser(newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreatePage.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreatePage.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreatePage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}