package com.example.testapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.HashMap;
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
public class AddActivity extends AppCompatActivity {

    private User user;
    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";

    // Arrays to hold references to EditTexts and TextViews
    private EditText[] itemEditTexts;
    private TextView[] categoryTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        EditText datePurchasedEditText = findViewById(R.id.DatePurchased);

        // Add TextWatcher to validate the date format
        datePurchasedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!isValidDate(input)) {
                    datePurchasedEditText.setError("Invalid date format! Use yyyy-mm-dd");
                }
            }
        });

        // Initialize arrays for the EditTexts and TextViews
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

        user = getIntent().getParcelableExtra("user");

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            intent.putExtra("user", user); // Pass the Parcelable User object
            startActivity(intent);
            finish();
        });
    }

    public void buttonClassify(View view) {
        for (int i = 0; i < itemEditTexts.length; i++) {
            final int index = i; // Preserve the index for the response handling
            String itemText = itemEditTexts[i].getText().toString().trim();

            // Skip empty fields
            if (itemText.isEmpty()) {
                categoryTextViews[index].setText("No input provided");
                continue;
            }

            String stringInputText = "Out of the six categories: Groceries, Clothing, Electronics, Health and Personal, Home, Entertainment, does " +
                    itemText +
                    " belong to? The answer only needs to be the category name.";

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectMessage = new JSONObject();
            JSONArray jsonObjectMessageArray = new JSONArray();

            try {
                jsonObjectMessage.put("role", "user");
                jsonObjectMessage.put("content", stringInputText);

                jsonObjectMessageArray.put(0, jsonObjectMessage);
                jsonObject.put("messages", jsonObjectMessageArray);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    stringURLEndPoint,
                    jsonObject,
                    response -> {
                        try {
                            String stringOutput = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            // Update the corresponding TextView
                            categoryTextViews[index].setText(stringOutput);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> categoryTextViews[index].setText("Error: " + error.toString())) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> mapHeader = new HashMap<>();
                    mapHeader.put("Content-Type", "application/json");
                    mapHeader.put("Authorization", "Bearer " + stringToken);
                    return mapHeader;
                }
            };

            int intTimeoutPeriod = 60000; // 60 seconds
            RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonObjectRequest.setRetryPolicy(retryPolicy);
            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
        }
    }

    public void save(View view) {
        String csvFile = "./data.csv";
        try (FileWriter fileWriter = new FileWriter(csvFile);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            // Save all non-empty items and their categories to the file
            for (int i = 0; i < itemEditTexts.length; i++) {
                String itemText = itemEditTexts[i].getText().toString().trim();
                String categoryText = categoryTextViews[i].getText().toString();

                if (!itemText.isEmpty()) {
                    writer.write(itemText + "," + categoryText);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidDate(String date) {
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$"; // yyyy-mm-dd format
        return date.matches(datePattern);
    }
}