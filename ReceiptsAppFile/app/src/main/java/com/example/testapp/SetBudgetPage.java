package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class SetBudgetPage extends AppCompatActivity {

    private EditText budgetEditText, monthsEditText;
    private Button saveButton;
    private ApiService apiService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_budget_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        budgetEditText = findViewById(R.id.budgetEditText);
        monthsEditText = findViewById(R.id.monthsEditText);
        saveButton = findViewById(R.id.saveButton);

        user = getIntent().getParcelableExtra("user");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        saveButton.setOnClickListener(v -> saveBudget());
    }

    private void saveBudget() {
        String budgetStr = budgetEditText.getText().toString().trim();
        String monthsStr = monthsEditText.getText().toString().trim();

        if (budgetStr.isEmpty() || monthsStr.isEmpty()) {
            Toast.makeText(SetBudgetPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = Double.parseDouble(budgetStr);
        int months = Integer.parseInt(monthsStr);

        apiService.addBudget(new BudgetClass(user.getUserId(), budget, months)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SetBudgetPage.this, "Budget set successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SetBudgetPage.this, Budget.class);
                    intent.putExtra("user", user);
                    intent.putExtra("budget", new BudgetClass(user.getUserId(), budget, months));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SetBudgetPage.this, "Failed to set budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SetBudgetPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}