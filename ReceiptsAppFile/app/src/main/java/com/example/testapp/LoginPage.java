package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class LoginPage extends AppCompatActivity {

    private TextView username, password;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, StartPage.class);
                startActivity(intent);
            }
        });

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginButton);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Change to your server's IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = username.getText().toString().trim();
                String newPassword = password.getText().toString().trim();

                if (newUsername.isEmpty() || newPassword.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(newUsername, newPassword);

                apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            // Login success - route to another page (another activity)
//                            Intent intent = new Intent(LoginPage.this, MainActivity.class);  // Replace with your destination activity
//                            startActivity(intent);
//                            finish();
                            LoginResponse loginResponse = response.body();
                            Intent intent = new Intent(LoginPage.this, MainActivity.class);

                            int userId = loginResponse.getUserId();  // Replace with actual userId
                            String name = loginResponse.getName();  // Replace with actual name
                            String username = loginResponse.getUsername();  // Replace with actual username
                            String password = loginResponse.getPassword();  // Replace with actual password
                            System.out.println(userId);
                            User user = new User(userId, name, username, password);
                            intent.putExtra("user", user);  // Pass the Parcelable User object
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed - show error message
                            Toast.makeText(LoginPage.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // Handle failure (e.g., network issues)
                        Toast.makeText(LoginPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}