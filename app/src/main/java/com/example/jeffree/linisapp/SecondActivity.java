package com.example.jeffree.linisapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private Button loginBtn2;
    private TextView regBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        loginBtn2 = (Button) findViewById(R.id.login_btn2);
        regBtn2 = (TextView) findViewById(R.id.start_reg_btn);


        loginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent2 = new Intent(SecondActivity.this, LoginActivity.class);
                startActivity(loginIntent2);
            }
        });

        regBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent regIntent2 = new Intent(SecondActivity.this, RegisterActivity.class);
                startActivity(regIntent2);

            }
        });
    }
}
