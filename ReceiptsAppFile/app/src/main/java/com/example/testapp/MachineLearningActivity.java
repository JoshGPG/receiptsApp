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
                    // 获取用户输入的 userId 和 totalBudget
                    int userId = Integer.parseInt(editTextUserId.getText().toString().trim());
                    double totalBudget = Double.parseDouble(editTextTotalBudget.getText().toString().trim());
                    // 初始化 Python 数据模块
                    PyObject dataFileContent = module.callAttr("load_keras_model", "Generated_Purchases_for_Users.csv"); // 替换为实际的 CSV 文件内容


                    // 获取指定用户的最近数据
                    PyObject recentData = module.callAttr("preprocess_data",date_purchased,priceategory);

                    // 计算支出比例
                    PyObject spendingProportionResult = module.callAttr("calculate_spending_proportion", recentData);
                    PyObject monthlyPercentage = spendingProportionResult.callAttr("__getitem__", 0);
                    PyObject categories = spendingProportionResult.callAttr("__getitem__", 1);

                    // 使用模型预测下个月的支出比例
                    PyObject predictedProportion = module.callAttr("predict_next_month_proportion", monthlyPercentage, categories);

                    // 根据预测结果分配预算
                    String result = module.callAttr("allocate_budget", predictedProportion, totalBudget, categories).toString();
                    textViewOutput.setText(result);

                    // 将分配结果保存为 JSON 文件
                    PyObject filePathPy = module.callAttr("save_budget_to_file", predictedProportion, totalBudget, categories);
                    String filePath = filePathPy.toString();

                    // 读取保存的 JSON 文件内容
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

                    // 显示文件内容
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