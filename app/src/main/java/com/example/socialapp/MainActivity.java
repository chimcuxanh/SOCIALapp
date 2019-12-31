package com.example.socialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   //view
    Button mRegisterBtn,mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init view
        mRegisterBtn = (Button) findViewById(R.id.register_btn);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        //handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity registeractivity
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

    }
}
