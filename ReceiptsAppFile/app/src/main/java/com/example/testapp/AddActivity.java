package com.example.testapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddActivity extends AppCompatActivity {

    private User user;
    private ApiService apiService;

    private EditText[] itemEditTexts;
    private TextView[] categoryTextViews;
    private EditText[] priceTextViews;

    private EditText listNameEditText;
    private TextView datePurchased;

    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        user = getIntent().getParcelableExtra("user");

        listNameEditText = findViewById(R.id.ListName);
        datePurchased = findViewById(R.id.DatePurchased);

        Button addButton = findViewById(R.id.AddButton);
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

        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                intent.putExtra("user", user);  // Pass the Parcelable User object
                startActivity(intent);
                finish();
            }
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
        String listName = listNameEditText.getText().toString().trim();
        String datePurchasedText = datePurchased.getText().toString().trim();

        if (listName.isEmpty() || datePurchasedText.isEmpty()) {
            Toast.makeText(this, "List name and date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(datePurchasedText)) {
            Toast.makeText(this, "Invalid date format. Use yyyy-mm-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Purchase> purchases = new ArrayList<>();
        for (int i = 0; i < itemEditTexts.length; i++) {
            String itemName = itemEditTexts[i].getText().toString().trim();
            String category = categoryTextViews[i].getText().toString().trim();
            String priceText = priceTextViews[i].getText().toString().trim();

            if (itemName.isEmpty() || category.isEmpty() || priceText.isEmpty()) {
                continue;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price for item: " + itemName, Toast.LENGTH_SHORT).show();
                continue;
            }

            purchases.add(new Purchase(0, user.getUserId(), itemName, price, category, datePurchasedText));
        }

        if (purchases.isEmpty()) {
            Toast.makeText(this, "No valid items to add", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DEBUG", "Number of purchases: " + purchases.size());
        Log.d("DEBUG", "ApiService initialized: " + (apiService != null));

        List<Integer> purchaseIds = new ArrayList<>();
        for (Purchase purchase : purchases) {
            apiService.addPurchase(purchase).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            int purchaseId = Integer.parseInt(responseBody.trim());
                            purchaseIds.add(purchaseId);
                            Log.d("DEBUG", "Added purchase ID: " + purchaseId);

                            // If all purchases have been processed, create the list
                            if (purchaseIds.size() == purchases.size()) {
                                addListToDatabase(listName, purchaseIds);
                            }
                        } catch (Exception e) {
                            Log.d("DEBUG", "Error parsing response: " + e.getMessage());
                        }
                    } else {
                        Log.d("DEBUG", "Failed to add purchase. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("DEBUG", "Error in addPurchase API call: " + t.getMessage());
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

    private boolean isValidDate(String date) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return date.matches(datePattern);
    }
}
