package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditDeleteReceipt extends AppCompatActivity {
    private EditText itemNameEditText, priceEditText, categoryEditText;
    private Button saveButton, deleteButton;
    private Spinner categorySpinner;
    private Purchase purchase;
    private User user;
    private ApiService apiService;
    private String originatingClass;
    private String category;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_delete_receipt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        originatingClass = getIntent().getStringExtra("originalClass");

        category = getIntent().getStringExtra("category");
        userId = getIntent().getIntExtra("userId", 1);


        ImageButton backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                if ("ReceiptByCategory".equals(originatingClass)) {
                    intent = new Intent(EditDeleteReceipt.this, ReceiptByCategory.class);
                    intent.putExtra("category", category);
                    intent.putExtra("userId", userId);
                } else if ("RecentPage".equals(originatingClass)) {
                    intent = new Intent(EditDeleteReceipt.this, RecentPage.class);
                } else {
                    // Default to a fallback page if the originatingClass is unknown
                    intent = new Intent(EditDeleteReceipt.this, MainActivity.class);
                }
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });

        user = getIntent().getParcelableExtra("user");
        purchase = getIntent().getParcelableExtra("purchase");

//        System.out.println(purchase.getPurchaseId());

        // Initialize UI components
        itemNameEditText = findViewById(R.id.itemNameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Populate fields with current data
        itemNameEditText.setText(purchase.getItemName());
        priceEditText.setText(String.valueOf(purchase.getPrice()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set the spinner's selection to the current category
        String currentCategory = purchase.getCategory();
        int categoryPosition = adapter.getPosition(currentCategory);
        if (categoryPosition >= 0) {
            categorySpinner.setSelection(categoryPosition);
        }

        if (purchase.getId() == 0 || purchase.getItemName() == null || purchase.getCategory() == null || purchase.getPrice() == 0) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up save button
        saveButton.setOnClickListener(v -> {
            String updatedItemName = itemNameEditText.getText().toString().trim();
            double updatedPrice = Double.parseDouble(priceEditText.getText().toString().trim());
            String updatedCategory = categorySpinner.getSelectedItem().toString();

            // Create the request object with only the necessary fields
            PurchaseUpdateRequest updateRequest = new PurchaseUpdateRequest(
                    purchase.getId(), updatedItemName, updatedPrice, updatedCategory);

            // Log JSON to verify it matches what the server expects
            Gson gson = new Gson();
            String requestJson = gson.toJson(updateRequest);
            Log.d("EditDeleteReceipt", "Request JSON: " + requestJson);

            apiService.updatePurchase(updateRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditDeleteReceipt.this, "Receipt updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after successful update
                    } else {
                        Toast.makeText(EditDeleteReceipt.this, "Failed to update receipt. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e("EditDeleteReceipt", "Error response: " + errorBody);
                            Toast.makeText(EditDeleteReceipt.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditDeleteReceipt.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EditDeleteReceipt", "Network failure: " + t.getMessage(), t);
                }
            });
        });


        // Set up delete button
        deleteButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Purchase")
                    .setMessage("Are you sure you want to delete this purchase?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Call delete API if confirmed
                        apiService.deletePurchase(purchase.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(EditDeleteReceipt.this, "Receipt deleted successfully!", Toast.LENGTH_SHORT).show();
                                    finish();  // Close the activity to return to the list
                                } else {
                                    Toast.makeText(EditDeleteReceipt.this, "Failed to delete receipt. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(EditDeleteReceipt.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}