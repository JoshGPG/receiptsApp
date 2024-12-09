package com.example.testapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.item1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        User user = getIntent().getParcelableExtra("user");

        Button recentButton = findViewById(R.id.recentBtn);
        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecentPage.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Ensure your base URL is correct
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        if (user != null) {
            String name = user.getName();
            int userId = user.getUserId();
            String username = user.getUsername();
            String password = user.getPassword();

            Log.d("MainActivity", "Received User - ID: " + userId + ", Name: " + name + ", Username: " + username);
//            System.out.println(userId);
            Toast.makeText(this, "Welcome, " + userId, Toast.LENGTH_SHORT).show();
        }

        TextView name = findViewById(R.id.name);
        String fullName = user.getName();
        String firstName = fullName.contains(" ") ? fullName.split(" ")[0] : fullName;
        name.setText(" " + firstName);


        ImageButton hamburgerIcon = findViewById(R.id.hamburger_icon);
        hamburgerIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        Button budgetButton = findViewById(R.id.budgetBtn);
        budgetButton.setOnClickListener(v -> {
            apiService.getBudget(user.getUserId()).enqueue(new Callback<BudgetResponse>() {
                @Override
                public void onResponse(Call<BudgetResponse> call, Response<BudgetResponse> response) {
                    Toast.makeText(MainActivity.this, "IM HERE", Toast.LENGTH_SHORT).show();
                    if (response.isSuccessful() && response.body() != null) {
                        BudgetResponse budgetResponse = response.body();
                        if (budgetResponse.isSuccess()) {
                            // Budget exists, navigate to BudgetPage
                            Intent intent = new Intent(MainActivity.this, Budget.class);
                            intent.putExtra("user", user);
                            intent.putExtra("budget", budgetResponse.getBudget());
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(MainActivity.this, SetBudgetPage.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
//                            System.out.println("Response not successful or body null");
//                            Toast.makeText(MainActivity.this, "Failed to load budget info", Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                        Intent intent = new Intent(MainActivity.this, SetBudgetPage.class);
//                        intent.putExtra("user", user);
//                        startActivity(intent);
                        System.out.println("Response not successful or body null");
                        Toast.makeText(MainActivity.this, "Failed to load budget info", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BudgetResponse> call, Throwable t) {
                    System.out.println("API call failed: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button categoriesButton = findViewById(R.id.categoriesBtn);
        Button addButton = findViewById(R.id.addBtn);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NumberOfItems.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);
            }
        });

        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecentPage.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);
            }
        });

        categoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Categories.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);}
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Check if the logout item was clicked
                if (item.getItemId() == R.id.logout_button) {
                    // Handle logout here
                    Intent intent = new Intent(MainActivity.this, StartPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear the back stack
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

}