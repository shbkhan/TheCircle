package com.shbkhan.thecircle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginSignUp extends AppCompatActivity {
    Button login,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        //Getting the components from the design.
        login = findViewById(R.id.login_splash_screen);
        signup = findViewById(R.id.sign_up_splash_screen);



        //Login button is clicked.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignUp.this,LoginPage.class);
                startActivity(intent);
            }
        });

        //Sign up button is clicked.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignUp.this,SignupPage.class);
                startActivity(intent);
            }
        });
    }
}