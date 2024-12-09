package com.example.testapp;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddActivity extends AppCompatActivity {


    private User user;
    private ApiService apiService;
    private int numberOfItems;

    private List<EditText> itemEditTexts = new ArrayList<>();
    private List<TextView> categoryTextViews = new ArrayList<>();
    private List<EditText> priceTextViews = new ArrayList<>();

    private EditText listNameEditText;
    private EditText datePurchased;

    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Adjust for your local server setup
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Get number of items from intent
        String numOfItemsString = getIntent().getStringExtra("numOfItems");
        if (numOfItemsString != null && !numOfItemsString.isEmpty()) {
            try {
                numberOfItems = Integer.parseInt(numOfItemsString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of items!", Toast.LENGTH_SHORT).show();
                numberOfItems = 0; // Default to 0 if invalid
            }
        } else {
            numberOfItems = 0; // Default to 0 if not provided
        }

        user = getIntent().getParcelableExtra("user");

        // Initialize input fields for list name and date
        listNameEditText = findViewById(R.id.ListName);
        datePurchased = findViewById(R.id.DatePurchased);

        // Create dynamic input fields
        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout container = (LinearLayout) scrollView.getChildAt(0); // LinearLayout inside ScrollView

        for (int i = 0; i < numberOfItems; i++) {
            addInputFields(container, i);
        }

        // Handle Add Items button
        Button addButton = findViewById(R.id.AddButton);
        addButton.setOnClickListener(view -> addItemsToDatabase());

        // Handle Categorize button
        Button categorizeButton = findViewById(R.id.categorize_button);
        categorizeButton.setOnClickListener(this::buttonClassify);

        // Handle Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    private void addInputFields(LinearLayout container, int index) {
        // Create a CardView to wrap the row
        CardView cardView = new CardView(this);
        cardView.setCardElevation(4); // Add shadow
        cardView.setRadius(25); // Rounded corners
        cardView.setCardBackgroundColor(Color.parseColor("#D57979")); // Red background color
        cardView.setUseCompatPadding(true); // Add padding for compatibility

        // Create a horizontal layout for input row
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(24, 32, 24, 32);

        // Create Category TextView
        TextView categoryTextView = new TextView(this);
        categoryTextView.setText("Category");
        categoryTextView.setWidth(250); // Adjust width as needed
        categoryTextView.setTextColor(Color.parseColor("#FFFFFF")); // Set white text color
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setTypeface(categoryTextView.getTypeface(), Typeface.BOLD); // Set text to bold
//        categoryTextView.setPadding(10, 0, 10, 0);
        row.addView(categoryTextView);
        categoryTextViews.add(categoryTextView);

        // Create Item EditText
        EditText itemEditText = new EditText(this);
        itemEditText.setHint("Input Item");
        itemEditText.setGravity(Gravity.CENTER);
        itemEditText.setTextColor(Color.parseColor("#FFFFFF")); // Set white text color
        itemEditText.setHintTextColor(Color.parseColor("#FFFFFF")); // Light gray hint color
        itemEditText.setTypeface(itemEditText.getTypeface(), Typeface.BOLD); // Set text to bold
        itemEditText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        itemEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(itemEditText);
        itemEditTexts.add(itemEditText);

        // Create Price EditText
        EditText priceEditText = new EditText(this);
        priceEditText.setHint("Price");
        priceEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceEditText.setWidth(200); // Adjust width as needed
        priceEditText.setTextColor(Color.parseColor("#FFFFFF")); // Set white text color
        priceEditText.setHintTextColor(Color.parseColor("#FFFFFF")); // Light gray hint color
        priceEditText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        priceEditText.setGravity(Gravity.CENTER);
        priceEditText.setTypeface(priceEditText.getTypeface(), Typeface.BOLD); // Set text to bold
        row.addView(priceEditText);
        priceTextViews.add(priceEditText);

        // Add the row to the CardView
        cardView.addView(row);

        // Add the CardView to the container
        container.addView(cardView);

        // Add margin to the CardView
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        params.setMargins(16, 16, 16, 16); // Adjust margins as needed
        cardView.setLayoutParams(params);
    }

    public void buttonClassify(View view) {
        for (int i = 0; i < itemEditTexts.size(); i++) {
            final int index = i; // Preserve the index
            String itemText = itemEditTexts.get(i).getText().toString().trim();

            // Skip if the input field is empty
            if (itemText.isEmpty()) {
                categoryTextViews.get(index).setText("No input provided");
                continue;
            }

            String inputText = "Out of the six categories: Groceries, Clothing, Electronics, Health and Personal, Home, Entertainment, " +
                    "does " + itemText + " belong to? The answer only needs to be the category name.";

            // Debug: Log the input text being sent
            Log.d("DEBUG", "Input text for API: " + inputText);

            JSONObject jsonObject = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();

            try {
                messageObject.put("role", "user");
                messageObject.put("content", inputText);
                messagesArray.put(messageObject);
                jsonObject.put("messages", messagesArray);

                // Debug: Log the API request payload
                Log.d("DEBUG", "API Request Payload: " + jsonObject.toString());

            } catch (JSONException e) {
                Log.e("DEBUG", "Error creating JSON payload: " + e.getMessage());
                categoryTextViews.get(index).setText("Error creating request");
                continue;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, stringURLEndPoint, jsonObject,
                    response -> {
                        try {
                            // Debug: Log the API response
                            Log.d("DEBUG", "API Response: " + response.toString());

                            String output = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            categoryTextViews.get(index).setText(output.trim());
                        } catch (JSONException e) {
                            Log.e("DEBUG", "Error parsing JSON response: " + e.getMessage());
                            categoryTextViews.get(index).setText("Error parsing response");
                        }
                    },
                    error -> {
                        // Debug: Log the error from the API call
                        Log.e("DEBUG", "API Error: " + error.toString());
                        categoryTextViews.get(index).setText("Error categorizing");
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + stringToken);

                    // Debug: Log the headers
                    Log.d("DEBUG", "Request Headers: " + headers.toString());

                    return headers;
                }
            };

            // Set a retry policy with increased timeout
            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    60000, // Timeout in milliseconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            );
            request.setRetryPolicy(retryPolicy);

            // Add the request to the Volley queue
            Volley.newRequestQueue(this).add(request);
        }
    }

    private void addItemsToDatabase() {
        String listName = listNameEditText.getText().toString().trim();
        String datePurchasedText = datePurchased.getText().toString().trim();

        if (listName.isEmpty() || datePurchasedText.isEmpty()) {
            Toast.makeText(this, "List name and date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isValidDate(datePurchasedText)) {
                Toast.makeText(this, "Invalid date format or value. Use yyyy-mm-dd and ensure the date is valid.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        for (TextView categoryTextView : categoryTextViews) {
            String category = categoryTextView.getText().toString().trim();
            if (category.isEmpty() || category.equalsIgnoreCase("Error categorizing")) {
                Toast.makeText(this, "Please complete categorization for all items.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<Purchase> purchases = new ArrayList<>();
        for (int i = 0; i < itemEditTexts.size(); i++) {
            String itemName = itemEditTexts.get(i).getText().toString().trim();
            String category = categoryTextViews.get(i).getText().toString().trim();
            String priceText = priceTextViews.get(i).getText().toString().trim();

            if (itemName.isEmpty() || category.isEmpty() || priceText.isEmpty()) {
                continue;
            }

            try {
                double price = Double.parseDouble(priceText);
                purchases.add(new Purchase(0, user.getUserId(), itemName, price, category, datePurchasedText));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price for item: " + itemName, Toast.LENGTH_SHORT).show();
            }
        }

        if (purchases.isEmpty()) {
            Toast.makeText(this, "No valid items to add", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> purchaseIds = new ArrayList<>();
        for (Purchase purchase : purchases) {
            apiService.addPurchase(purchase).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            int purchaseId = Integer.parseInt(responseBody.trim());
                            purchaseIds.add(purchaseId);

                            Log.d("AddActivity", "Added purchase ID: " + purchaseId);

                            // If all purchases are processed, add the list
                            if (purchaseIds.size() == purchases.size()) {
                                addListToDatabase(listName, purchaseIds);
                            }
                        } catch (Exception e) {
                            Log.e("AddActivity", "Error parsing response: " + e.getMessage());
                        }
                    } else {
                        Log.e("AddActivity", "Failed to add purchase. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("AddActivity", "Error in addPurchase API call: " + t.getMessage());
                }
            });
        }
    }

    private void addListToDatabase(String listName, List<Integer> purchaseIds) {
        String purchaseIdsString = joinIds(purchaseIds);
        AddListRequest addListRequest = new AddListRequest(user.getUserId(), listName, purchaseIdsString);

        Log.d("AddListRequest", "UserID: " + user.getUserId() + ", ListName: " + listName + ", Purchases: " + purchaseIdsString);

        apiService.addList(addListRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddActivity.this, "List added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddActivity.this, "Failed to add list.", Toast.LENGTH_SHORT).show();
                    Log.e("AddListResponse", "Response code: " + response.code() + ", Message: " + response.message());
                    try {
                        Log.e("AddListResponse", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddListRequest", "Error: " + t.getMessage());
            }
        });
    }

    private String joinIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isValidDate(String date) {
        try {
            // Parse the date string using the LocalDate class
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // If parsing succeeds, the date is valid
            return true;
        } catch (DateTimeParseException e) {
            // If parsing fails, the date is invalid
            return false;
        }
    }
}