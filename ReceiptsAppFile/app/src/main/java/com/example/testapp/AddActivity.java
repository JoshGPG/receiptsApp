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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
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
                numberOfItems = 0;
            }
        } else {
            numberOfItems = 0;
        }

        user = getIntent().getParcelableExtra("user");
        listNameEditText = findViewById(R.id.ListName);
        datePurchased = findViewById(R.id.DatePurchased);

        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout container = (LinearLayout) scrollView.getChildAt(0);

        for (int i = 0; i < numberOfItems; i++) {
            addInputFields(container, i);
        }

        Button addButton = findViewById(R.id.AddButton);
        addButton.setOnClickListener(view -> addItemsToDatabase());

        Button categorizeButton = findViewById(R.id.categorize_button);
        categorizeButton.setOnClickListener(this::buttonClassify);

        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    private void addInputFields(LinearLayout container, int index) {
        CardView cardView = new CardView(this);
        cardView.setCardElevation(4);
        cardView.setRadius(25);
        cardView.setCardBackgroundColor(Color.parseColor("#D57979"));
        cardView.setUseCompatPadding(true);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(24, 32, 24, 32);

        TextView categoryTextView = new TextView(this);
        categoryTextView.setText("Category");
        categoryTextView.setWidth(250);
        categoryTextView.setTextColor(Color.parseColor("#FFFFFF"));
        categoryTextView.setGravity(Gravity.CENTER);
        categoryTextView.setTypeface(categoryTextView.getTypeface(), Typeface.BOLD);
        row.addView(categoryTextView);
        categoryTextViews.add(categoryTextView);

        EditText itemEditText = new EditText(this);
        itemEditText.setHint("Input Item");
        itemEditText.setGravity(Gravity.CENTER);
        itemEditText.setTextColor(Color.parseColor("#FFFFFF"));
        itemEditText.setHintTextColor(Color.parseColor("#FFFFFF"));
        itemEditText.setTypeface(itemEditText.getTypeface(), Typeface.BOLD);
        itemEditText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        itemEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(itemEditText);
        itemEditTexts.add(itemEditText);

        EditText priceEditText = new EditText(this);
        priceEditText.setHint("Price");
        priceEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceEditText.setWidth(200);
        priceEditText.setTextColor(Color.parseColor("#FFFFFF"));
        priceEditText.setHintTextColor(Color.parseColor("#FFFFFF"));
        priceEditText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        priceEditText.setGravity(Gravity.CENTER);
        priceEditText.setTypeface(priceEditText.getTypeface(), Typeface.BOLD);
        row.addView(priceEditText);
        priceTextViews.add(priceEditText);

        cardView.addView(row);

        container.addView(cardView);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        params.setMargins(16, 16, 16, 16); // Adjust margins as needed
        cardView.setLayoutParams(params);
    }

    public void buttonClassify(View view) {
        for (int i = 0; i < itemEditTexts.size(); i++) {
            final int index = i;
            String itemText = itemEditTexts.get(i).getText().toString().trim();

            if (itemText.isEmpty()) {
                categoryTextViews.get(index).setText("No input provided");
                continue;
            }

            String inputText = "Out of the six categories: Groceries, Clothing, Electronics, Health and Personal, Home, Entertainment, " +
                    "does " + itemText + " belong to? The answer only needs to be the category name.";

            Log.d("DEBUG", "Input text for API: " + inputText);

            JSONObject jsonObject = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();

            try {
                messageObject.put("role", "user");
                messageObject.put("content", inputText);
                messagesArray.put(messageObject);
                jsonObject.put("messages", messagesArray);

                Log.d("DEBUG", "API Request Payload: " + jsonObject.toString());

            } catch (JSONException e) {
                Log.e("DEBUG", "Error creating JSON payload: " + e.getMessage());
                categoryTextViews.get(index).setText("Error creating request");
                continue;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, stringURLEndPoint, jsonObject,
                    response -> {
                        try {
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
                        Log.e("DEBUG", "API Error: " + error.toString());
                        categoryTextViews.get(index).setText("Error categorizing");
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + stringToken);

                    Log.d("DEBUG", "Request Headers: " + headers.toString());

                    return headers;
                }
            };

            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    60000, // Timeout in milliseconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            );
            request.setRetryPolicy(retryPolicy);

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
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}