package com.shbkhan.thecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    EditText emailLoginPage, passwordLoginPage;
    TextView forgotPassword;
    Button login;
    ImageView googleLogin, facebookLogin;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        //Getting components from the design.
        emailLoginPage = findViewById(R.id.editTextEmail);
        passwordLoginPage = findViewById(R.id.editTextPassword);
        forgotPassword = findViewById(R.id.textViewForgotPassword);
        login = findViewById(R.id.buttonLogin);
        googleLogin = findViewById(R.id.imageViewGoogle);
        facebookLogin = findViewById(R.id.imageViewFacebook);
        googleLogin.setClickable(false);
        googleLogin.setVisibility(View.GONE);
        facebookLogin.setClickable(false);
        facebookLogin.setVisibility(View.GONE);

        //Login Button is clicked.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Signing in, Please wait");
                progressDialog.setCancelable(false);
                //Getting Email and Password.
                String email = emailLoginPage.getText().toString().trim();
                String password = passwordLoginPage.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginPage.this, "Enter valid email and password", Toast.LENGTH_SHORT).show();
                } else{
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        progressDialog.show();
                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginPage.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginPage.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        Toast.makeText(LoginPage.this, "Email address is not in the right format", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //Forgot Password is clicked.
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Google Login is clicked.
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Facebook login is clicked.
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}