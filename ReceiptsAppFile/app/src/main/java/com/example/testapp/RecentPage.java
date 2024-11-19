package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecentPage extends AppCompatActivity {

    private ApiService apiService;
    private RecyclerView recyclerView;
    private PurchasesAdapter purchasesAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = getIntent().getParcelableExtra("user");

        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(RecentPage.this, MainActivity.class);
            intent.putExtra("user", user);  // Pass the Parcelable User object
            startActivity(intent);
            finish();
        });

        int userId = user.getUserId();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter with a click listener
        purchasesAdapter = new PurchasesAdapter(new ArrayList<>(), new PurchasesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Purchase purchase) {
                Intent intent = new Intent(RecentPage.this, EditDeleteReceipt.class);
                intent.putExtra("purchase", purchase);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                intent.putExtra("originalClass", "RecentPage");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(purchasesAdapter);

        // Fetch purchases for the user
        fetchPurchases(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPurchases(user.getUserId());
    }


    private void fetchPurchases(int userId) {
        apiService.getPurchases(userId).enqueue(new Callback<PurchasesResponse>() {
            @Override
            public void onResponse(Call<PurchasesResponse> call, Response<PurchasesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Purchase> purchasesList = response.body().getPurchases();

                    // Sort the list by date in descending order (most recent first)
                    Collections.sort(purchasesList, new Comparator<Purchase>() {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        @Override
                        public int compare(Purchase p1, Purchase p2) {
                            try {
                                return dateFormat.parse(p2.getDatePurchased().split("T")[0])
                                        .compareTo(dateFormat.parse(p1.getDatePurchased().split("T")[0]));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    purchasesAdapter.setPurchases(purchasesList);
                } else {
                    Toast.makeText(RecentPage.this, "Failed to load purchases", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurchasesResponse> call, Throwable t) {
                Toast.makeText(RecentPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}