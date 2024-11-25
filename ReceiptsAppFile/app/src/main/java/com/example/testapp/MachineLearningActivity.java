package com.example.testapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MachineLearningActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_learning);

        EditText editTextUserId = findViewById(R.id.editTextUserId);
        EditText editTextTotalBudget = findViewById(R.id.editTextTotalBudget);
        Button buttonRunML = findViewById(R.id.buttonRunML);
        TextView textViewOutput = findViewById(R.id.textViewOutput);
        ImageView imageViewOutput = findViewById(R.id.imageViewOutput);

        // Initialize Python environment
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python python = Python.getInstance();
        PyObject module = python.getModule("ML");

        buttonRunML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int userId = Integer.parseInt(editTextUserId.getText().toString().trim());
                    double totalBudget = Double.parseDouble(editTextTotalBudget.getText().toString().trim());

                    // Call Python script to generate budget allocation and save to file
                    PyObject filePathPy = module.callAttr("save_budget_to_file", new double[]{0.2, 0.3, 0.1, 0.1, 0.15, 0.15}, totalBudget, new String[]{"clothing", "entertainment", "electronics", "home", "health and personal", "groceries"});
                    String filePath = filePathPy.toString();

                    // Read generated JSON file
                    File file = new File(filePath);
                    if (!file.exists()) {
                        textViewOutput.setText("File not found. Please run the Python script to generate data.");
                        return;
                    }

                    StringBuilder text = new StringBuilder();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                    // Display file content
                    textViewOutput.setText("Suggested Budget Allocation for Next Month:\n" + text.toString());
                } catch (NumberFormatException e) {
                    textViewOutput.setText("Invalid input: " + e.getMessage());
                } catch (IOException e) {
                    textViewOutput.setText("Error reading file: " + e.getMessage());
                } catch (Exception e) {
                    textViewOutput.setText("Error: " + e.getMessage());
                }
            }
        });
    }
}
