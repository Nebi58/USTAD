package com.nebi.ustad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ErrorActivity extends AppCompatActivity {

    private Button btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_error);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.error_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        registerEventHandlers();

    }

    private void initComponents(){
        btnTryAgain = findViewById(R.id.btnTryAgain);
    }

    private void registerEventHandlers(){
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ErrorActivity.this,PhoneActivity.class);
                startActivity(intent);
            }
        });
    }
}