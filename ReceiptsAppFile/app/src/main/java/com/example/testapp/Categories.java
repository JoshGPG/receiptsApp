package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class Categories extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button groceriesButton = findViewById(R.id.groceriesBtn);
        Button clothingButton = findViewById(R.id.clothingBtn);
        Button electronicsButton = findViewById(R.id.electronicsBtn);
        Button healthButton = findViewById(R.id.healthBtn);
        Button homeButton = findViewById(R.id.homeBtn);
        Button entertainmentButton = findViewById(R.id.enterBtn);

        user = getIntent().getParcelableExtra("user");

        groceriesButton.setOnClickListener(v -> openReceiptListPage("Groceries"));
        clothingButton.setOnClickListener(v -> openReceiptListPage("Clothing"));
        electronicsButton.setOnClickListener(v -> openReceiptListPage("Electronics"));
        healthButton.setOnClickListener(v -> openReceiptListPage("Health and Personal"));
        homeButton.setOnClickListener(v -> openReceiptListPage("Home"));
        entertainmentButton.setOnClickListener(v -> openReceiptListPage("Entertainment"));

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, MainActivity.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);
                finish();
            }
        });
    }

    private void openReceiptListPage(String category) {
        Intent intent = new Intent(Categories.this, ReceiptByCategory.class);
        intent.putExtra("category", category);
        intent.putExtra("user", user);
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

}