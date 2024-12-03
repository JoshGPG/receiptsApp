package com.example.testapp.Classify;

import android.os.Bundle;
import android.view.View;
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
import com.example.testapp.R;


public class ClassifyActivity3 extends AppCompatActivity {

    private TextView textView;
    private String stringToken = "LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.categorize_button), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            textView = findViewById(R.id.textView);
            TextView Textview = findViewById(R.id.Category5);
            return insets;
        });
    }

    public void buttonClassify(View view){
        EditText editText = findViewById(R.id.Item3);
        String stringInputText ="Of the six categories Groceries, Clothing, Electronic, Health and Personal, Home, Entertainment does  "+ editText.getText().toString() +" belong to? The answer only needs to be the category name.";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectMessage =new JSONObject();
        JSONArray jsonObjectMessageArray=new JSONArray();
        try {
            jsonObjectMessage.put("role","user");
            jsonObjectMessage.put("content", stringInputText);

            jsonObjectMessageArray.put(0,jsonObjectMessage);
            jsonObject.put("messages",jsonObjectMessageArray);

        }catch(JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                stringURLEndPoint,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String stringOutput = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            textView.setText(stringOutput);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Content-Type", "application/json");
                mapHeader.put("Authorization","Bearer LA-b8373a79433f4ba38d1487c23352a6b3984ebef9250a4bfa88457fb659498d01");
                return mapHeader;
            }
        };
        int intTimeoutPeriod = 60000; //60 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    public void save(View view){
        TextView textView = findViewById(R.id.Category5);
        String csvFile ="./data.csv";
        FileWriter fileWriter =null;
        try{
            fileWriter =new FileWriter(csvFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(textView.getText().toString()+textView.getText().toString());
            writer.close();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
