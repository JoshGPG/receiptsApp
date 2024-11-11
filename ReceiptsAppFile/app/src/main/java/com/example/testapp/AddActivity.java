package com.example.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ImageButton;
import androidx.drawerlayout.widget.DrawerLayout;

public class AddActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        drawerLayout = findViewById(R.id.drawer_layout);
//        ImageButton hamburgerIcon = findViewById(R.id.hamburger_icon);
//
//        hamburgerIcon.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

//        Button recentButton = findViewById(R.id.recentBtn);
//        Button categoriesButton = findViewById(R.id.categoriesBtn);
//        Button addButton = findViewById(R.id.addBtn);
//
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AddActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        recentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AddActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        categoriesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AddActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//



    }

}