package com.example.testapp;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;



public class ClassifyActivity extends AppCompatActivity {

    private TextView textView;
    private String stringToken = "LA-20e381e84ae14bc18d5acd55cc4bd7af8407420464044cbb99558ac9235f6072";
    private String stringURLEndPoint = "https://api.llama-api.com/chat/completions";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.button), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            textView = findViewById(R.id.textView);
            return insets;
        });
    }

    public void buttonClassify(View view){
        String stringInputText ="Write a poem on clouds?";
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
                                .getString("output");

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
            mapHeader.put("Authorization","Bearer"+stringToken);
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

}