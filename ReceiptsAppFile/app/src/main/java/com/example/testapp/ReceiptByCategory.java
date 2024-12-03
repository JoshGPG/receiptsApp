package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReceiptByCategory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PurchasesAdapter purchasesAdapter;
    private ApiService apiService;
    private String category;
    private int userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt_by_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        category = getIntent().getStringExtra("category");
        userId = getIntent().getIntExtra("userId", 1);
        user = getIntent().getParcelableExtra("user");

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ReceiptByCategory.this, Categories.class);
            intent.putExtra("user", user);  // Pass the Parcelable User object
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        purchasesAdapter = new PurchasesAdapter(new ArrayList<>(), new PurchasesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Purchase purchase) {
                Intent intent = new Intent(ReceiptByCategory.this, EditDeleteReceipt.class);
                intent.putExtra("purchase", purchase);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                intent.putExtra("originalClass", "ReceiptByCategory");
                intent.putExtra("category", category);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(purchasesAdapter);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Fetch purchases for the specified category
        fetchPurchasesByCategory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPurchasesByCategory();
    }

    private void fetchPurchasesByCategory() {
        apiService.getPurchasesByCategory(userId, category).enqueue(new Callback<PurchasesResponse>() {
            @Override
            public void onResponse(Call<PurchasesResponse> call, Response<PurchasesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    purchasesAdapter.setPurchases(response.body().getPurchases());  // Update adapter with data
                } else {
                    Toast.makeText(ReceiptByCategory.this, "Failed to load purchases", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurchasesResponse> call, Throwable t) {
                Toast.makeText(ReceiptByCategory.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}