package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListItemsActivity extends AppCompatActivity {

    private ApiService apiService;
    private RecyclerView recyclerView;
    private PurchasesAdapter purchasesAdapter;
    private User user;
    private String listName;
    private String purchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Retrieve extras
        user = getIntent().getParcelableExtra("user");
        listName = getIntent().getStringExtra("list_name");
        purchases = getIntent().getStringExtra("purchases"); // Comma-separated purchase IDs

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ListItemsActivity.this, RecentPage.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml("<u>" + listName + "</u>"));

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);


        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        purchasesAdapter = new PurchasesAdapter(new ArrayList<>(), purchase -> {
            // Navigate to EditDeleteReceipt
            Intent intent = new Intent(ListItemsActivity.this, EditDeleteReceipt.class);
            intent.putExtra("purchase", purchase);
            intent.putExtra("user", user);
            startActivity(intent);
        });
        recyclerView.setAdapter(purchasesAdapter);

        // Fetch purchases
        fetchPurchases(purchases);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPurchases(purchases);
    }

    private void fetchPurchases(String purchaseIds) {
        apiService.getPurchasesByIds(purchaseIds).enqueue(new Callback<PurchasesResponse>() {
            @Override
            public void onResponse(Call<PurchasesResponse> call, Response<PurchasesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Purchase> purchasesList = response.body().getPurchases();
                    purchasesAdapter.setPurchases(purchasesList); // Populate RecyclerView with purchases
                } else {
                    Toast.makeText(ListItemsActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurchasesResponse> call, Throwable t) {
                Toast.makeText(ListItemsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}