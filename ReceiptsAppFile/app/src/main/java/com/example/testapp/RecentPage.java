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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecentPage extends AppCompatActivity {

    private ApiService apiService;
    private RecyclerView recyclerView;
    private User user;
    private ListsAdapter listsAdapter;

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
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        int userId = user.getUserId();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listsAdapter = new ListsAdapter(new ArrayList<>(), list -> {
            Intent intent = new Intent(RecentPage.this, ListItemsActivity.class);
            intent.putExtra("list_name", list.getListName());
            intent.putExtra("purchases", list.getPurchases());
            intent.putExtra("user", user);
            startActivity(intent);
        });
        recyclerView.setAdapter(listsAdapter);

        fetchLists(userId);
    }

    private void fetchLists(int userId) {
        apiService.getLists(userId).enqueue(new Callback<ListsResponse>() {
            @Override
            public void onResponse(Call<ListsResponse> call, Response<ListsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ListData> lists = response.body().getLists();
                    listsAdapter.setLists(lists);
                } else {
                    Toast.makeText(RecentPage.this, "Failed to load lists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListsResponse> call, Throwable t) {
                Toast.makeText(RecentPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}