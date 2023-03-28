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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignupPage extends AppCompatActivity {
    EditText emailSignUp, passwordSignUp,confirmPasswordSignup;
    Button signUp;
    ImageButton googleSignup,facebookSignUp;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //Getting the components from design.
        emailSignUp = findViewById(R.id.editTextEmailSignUp);
        passwordSignUp = findViewById(R.id.editTextPasswordSignUp);
        confirmPasswordSignup = findViewById(R.id.editTextConfirmPasswordSignUp);
        signUp = findViewById(R.id.buttonSignUpSignUp);
        googleSignup = findViewById(R.id.imageButtonGoogle);
        facebookSignUp = findViewById(R.id.imageButtonFacebook);
        googleSignup.setClickable(false);
        googleSignup.setVisibility(View.GONE);
        facebookSignUp.setClickable(false);
        facebookSignUp.setVisibility(View.GONE);



        //Sign Up through email and password.
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Signing up, Please wait");
                progressDialog.setCancelable(false);
                //Fetching email, password and Confirming password.
                String email = emailSignUp.getText().toString().trim();
                String password = passwordSignUp.getText().toString().trim();
                String confirmPassword = confirmPasswordSignup.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(SignupPage.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(confirmPassword)){
                    Toast.makeText(SignupPage.this, "Password should be same in both the fields", Toast.LENGTH_SHORT).show();
                } else{
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SignupPage.this,AccountSetup.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    progressDialog.dismiss();
                                    Toast.makeText(SignupPage.this, "Some error encountered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        Toast.makeText(SignupPage.this, "Email address is not in the right format", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}