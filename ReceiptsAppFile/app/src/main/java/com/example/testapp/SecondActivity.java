package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {
    private TextView textViewOutput;
    private TextView resultTextView;
    private EditText editTextName, editTextUsername, editTextPassword;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            return insets;
        });

        textViewOutput = findViewById(R.id.pythonOutput);
        Python.start(new AndroidPlatform(getApplicationContext()));

        editTextName = findViewById(R.id.nameTextbox);
        editTextUsername = findViewById(R.id.usernameTextbox);
        editTextPassword = findViewById(R.id.passwordTextbox);

        buttonSubmit = findViewById(R.id.sendBtn);
        resultTextView = findViewById(R.id.DatabaseWords);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    StringBuilder result = new StringBuilder();

                    for (User user : users) {
                        result.append("User: ").append(user.getName()).append(", Username: ").append(user.getUsername()).append("\n");
                    }
                    resultTextView.setText(result.toString());
                } else {
                    resultTextView.setText("Request failed: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                resultTextView.setText("Error: " + t.getMessage());
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (name.isEmpty() || username.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Please enter name, username, and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(69, name, username, password);

                apiService.addUser(newUser).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SecondActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SecondActivity.this, "Failed to add user: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SecondActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ImageButton backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    public void buttonPythonRun(View view){
        Python python = Python.getInstance();

        PyObject pyObjectResult = python.getModule("add_numbers").callAttr("add_numbers", 1000, 20);

        textViewOutput.setText(pyObjectResult.toString());
    }
}