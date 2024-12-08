package com.example.testapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddActivity extends AppCompatActivity {

    private User user;
    private ApiService apiService;

    private EditText[] itemEditTexts;
    private TextView[] categoryTextViews;
    private EditText[] priceTextViews;

    private EditText listNameEditText;
//    private EditText datePurchasedEditText;
    private TextView datePurchased;

    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        user = getIntent().getParcelableExtra("user");

        listNameEditText = findViewById(R.id.ListName);
//        datePurchasedEditText = findViewById(R.id.DatePurchased);
        datePurchased = findViewById(R.id.DatePurchased);

        Button addButton = findViewById(R.id.AddButton); // Replace with the actual ID of your Add button
        addButton.setOnClickListener(view -> addItemsToDatabase());

        itemEditTexts = new EditText[]{
                findViewById(R.id.Item1),
                findViewById(R.id.Item2),
                findViewById(R.id.Item3),
                findViewById(R.id.Item4),
                findViewById(R.id.Item5),
                findViewById(R.id.Item6),
                findViewById(R.id.Item7)
        };

        categoryTextViews = new TextView[]{
                findViewById(R.id.Category1),
                findViewById(R.id.Category2),
                findViewById(R.id.Category3),
                findViewById(R.id.Category4),
                findViewById(R.id.Category5),
                findViewById(R.id.Category6),
                findViewById(R.id.Category7)
        };

        priceTextViews = new EditText[]{
                findViewById(R.id.Price1),
                findViewById(R.id.Price2),
                findViewById(R.id.Price3),
                findViewById(R.id.Price4),
                findViewById(R.id.Price5),
                findViewById(R.id.Price6),
                findViewById(R.id.Price7)
        };

        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    public void buttonClassify(View view) {
        for (int i = 0; i < itemEditTexts.length; i++) {
            final int index = i;
            String itemText = itemEditTexts[i].getText().toString().trim();

            if (itemText.isEmpty()) {
                categoryTextViews[index].setText("No input provided");
                continue;
            }

            String inputText = "Out of the six categories: Groceries, Clothing, Electronics, Health and Personal, Home, Entertainment, " +
                    "does " + itemText + " belong to? The answer only needs to be the category name.";

            JSONObject jsonObject = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();

            try {
                messageObject.put("role", "user");
                messageObject.put("content", inputText);
                messagesArray.put(messageObject);
                jsonObject.put("messages", messagesArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, stringURLEndPoint, jsonObject,
                    response -> {
                        try {
                            String output = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            categoryTextViews[index].setText(output.trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> categoryTextViews[index].setText("Error categorizing")) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + stringToken);
                    return headers;
                }
            };

            RetryPolicy retryPolicy = new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(retryPolicy);

            Volley.newRequestQueue(this).add(request);
        }
    }

    private void addItemsToDatabase() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Use 10.0.2.2 for local server in emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        // Create a list of Purchase objects
        List<Purchase> purchases = new ArrayList<>();
        for (int i = 0; i < itemEditTexts.length; i++) {
            String itemName = itemEditTexts[i].getText().toString().trim();
            String category = categoryTextViews[i].getText().toString().trim();
            String priceText = priceTextViews[i].getText().toString().trim();
            String datePurchasedText = datePurchased.getText().toString().trim();

            // Skip if the item name, category, or price is empty
            if (itemName.isEmpty() || category.isEmpty() || priceText.isEmpty() || datePurchasedText.isEmpty()) {
                continue;
            }

            // Parse the price
            double itemPrice;
            try {
                itemPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format for item: " + itemName, Toast.LENGTH_SHORT).show();
                continue;
            }

            // Validate the date format
            if (!isValidDate(datePurchasedText)) {
                Toast.makeText(this, "Invalid date format: " + datePurchasedText, Toast.LENGTH_SHORT).show();
                continue;
            }

            // Add the purchase to the list
            purchases.add(new Purchase(0, user.getUserId(), itemName, itemPrice, category, datePurchasedText));
        }

        // Send each purchase to the database
        for (Purchase purchase : purchases) {
            apiService.addPurchase(purchase).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddActivity.this, "Failed to add item.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
        private boolean isValidDate(String date) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // yyyy-mm-dd format
        return date.matches(datePattern);
    }
}
//
//public class AddActivity extends AppCompatActivity {
//
//    private User user;
//    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
//    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";
//
//    // Arrays to hold references to EditTexts and TextViews
//    private EditText[] itemEditTexts;
//    private TextView[] categoryTextViews;
//    private TextView[] priceTextViews;
//
//
//    private TextView listName = findViewById(R.id.ListName);
//    private TextView datePurchased = findViewById(R.id.DatePurchased);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_add);
//
//        // Initialize arrays for the EditTexts and TextViews
//        itemEditTexts = new EditText[]{
//                findViewById(R.id.Item1),
//                findViewById(R.id.Item2),
//                findViewById(R.id.Item3),
//                findViewById(R.id.Item4),
//                findViewById(R.id.Item5),
//                findViewById(R.id.Item6),
//                findViewById(R.id.Item7)
//        };
//
//        categoryTextViews = new TextView[]{
//                findViewById(R.id.Category1),
//                findViewById(R.id.Category2),
//                findViewById(R.id.Category3),
//                findViewById(R.id.Category4),
//                findViewById(R.id.Category5),
//                findViewById(R.id.Category6),
//                findViewById(R.id.Category7)
//        };
//
//        priceTextViews = new TextView[]{
//                findViewById(R.id.Price1),
//                findViewById(R.id.Price2),
//                findViewById(R.id.Price3),
//                findViewById(R.id.Price4),
//                findViewById(R.id.Price5),
//                findViewById(R.id.Price6),
//                findViewById(R.id.Price7)
//        };
//
//        user = getIntent().getParcelableExtra("user");
//
//        Button backButton = findViewById(R.id.BackButton);
//        backButton.setOnClickListener(view -> {
//            Intent intent = new Intent(AddActivity.this, MainActivity.class);
//            intent.putExtra("user", user); // Pass the Parcelable User object
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    public void buttonClassify(View view) {
//        for (int i = 0; i < itemEditTexts.length; i++) {
//            final int index = i; // Preserve the index for the response handling
//            String itemText = itemEditTexts[i].getText().toString().trim();
//
//            // Skip empty fields
//            if (itemText.isEmpty()) {
//                categoryTextViews[index].setText("No input provided");
//                continue;
//            }
//
//            String stringInputText = "Out of the six categories: Groceries, Clothing, Electronics, Health and Personal, Home, Entertainment, does " +
//                    itemText +
//                    " belong to? The answer only needs to be the category name.";
//
//            JSONObject jsonObject = new JSONObject();
//            JSONObject jsonObjectMessage = new JSONObject();
//            JSONArray jsonObjectMessageArray = new JSONArray();
//
//            try {
//                jsonObjectMessage.put("role", "user");
//                jsonObjectMessage.put("content", stringInputText);
//
//                jsonObjectMessageArray.put(0, jsonObjectMessage);
//                jsonObject.put("messages", jsonObjectMessageArray);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                    stringURLEndPoint,
//                    jsonObject,
//                    response -> {
//                        try {
//                            String stringOutput = response.getJSONArray("choices")
//                                    .getJSONObject(0)
//                                    .getJSONObject("message")
//                                    .getString("content");
//
//                            // Update the corresponding TextView
//                            categoryTextViews[index].setText(stringOutput);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    },
//                    error -> categoryTextViews[index].setText("Error: " + error.toString())) {
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> mapHeader = new HashMap<>();
//                    mapHeader.put("Content-Type", "application/json");
//                    mapHeader.put("Authorization", "Bearer " + stringToken);
//                    return mapHeader;
//                }
//            };
//
//            int intTimeoutPeriod = 60000; // 60 seconds
//            RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//
//            jsonObjectRequest.setRetryPolicy(retryPolicy);
//            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
//        }
//    }
//
//
//    public void save(View view) {
//        String csvFile = "./data.csv";
//        try (FileWriter fileWriter = new FileWriter(csvFile);
//             BufferedWriter writer = new BufferedWriter(fileWriter)) {
//
//            // Save all non-empty items and their categories to the file
//            for (int i = 0; i < itemEditTexts.length; i++) {
//                String itemText = itemEditTexts[i].getText().toString().trim();
//                String categoryText = categoryTextViews[i].getText().toString();
//
//                if (!itemText.isEmpty()) {
//                    writer.write(itemText + "," + categoryText);
//                    writer.newLine();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isValidDate(String date) {
//        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // yyyy-mm-dd format
//        return date.matches(datePattern);
//    }
//}
