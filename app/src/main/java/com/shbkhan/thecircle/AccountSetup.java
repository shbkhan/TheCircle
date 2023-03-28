package com.shbkhan.thecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shbkhan.thecircle.Model.AccountSetupModel;

import java.util.HashMap;
import java.util.Map;

public class AccountSetup extends AppCompatActivity {
    //imageButtonSelectProfilePicture, editTextNameAccountSetup, editTextUsernameAccountSetup,textViewAvailableAccountSetup
    //textViewUserNameIsAvailable, editTextBioAccountSetup,buttonSignUpAccountSetup
    ImageView selectProfilePicture;
    EditText name, username, bio;
    Button signup;
    TextView available,usernameIsAvailable;
    Spinner gender;
    final String[] gendersToSelect = {"Select Gender", "Male", "Female"};
    String genderSelected = "", nameFetch, usernameFetch,bioFetch;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DocumentReference reference;
    Boolean userNameAvailable;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        String currentUserId = user.getUid();


        //Getting components from the design.
        selectProfilePicture = findViewById(R.id.imageButtonSelectProfilePicture);
        name = findViewById(R.id.etName);
        username = findViewById(R.id.editTextUserNameAccountSetup);
        bio = findViewById(R.id.etBio);
        signup = findViewById(R.id.btnSubmit);
        available = findViewById(R.id.textViewAvailableAccountSetup);
        usernameIsAvailable = findViewById(R.id.textViewUsernameIsAvailable);
        gender = findViewById(R.id.genderAccountSetup);
        progressDialog = new ProgressDialog(this);

        //Setting dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AccountSetup.this, android.R.layout.simple_spinner_dropdown_item,gendersToSelect);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        genderSelected = "Male";
                        break;
                    case 2:
                        genderSelected = "Female";
                        break;
                    default:
                        genderSelected = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                genderSelected = "";
            }
        });

        //Sign up button is clicked.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                reference = db.collection("User").document(currentUserId);
                databaseReference = firebaseDatabase.getReference().child("Users").child(currentUserId);

                //Getting name, username and bio
                nameFetch = name.getText().toString().trim();
                usernameFetch = username.getText().toString().trim();
                bioFetch = bio.getText().toString().trim();
                if (!TextUtils.isEmpty(nameFetch) && !TextUtils.isEmpty(usernameFetch) && !TextUtils.isEmpty(bioFetch) && !genderSelected.equals("") && userNameAvailable){
                    progressDialog.show();
                    AccountSetupModel accountSetupModel = new AccountSetupModel(nameFetch,usernameFetch,bioFetch,genderSelected,mAuth.getCurrentUser().getEmail());
                    accountSetupModel.setUserId(currentUserId);
                    databaseReference.setValue(accountSetupModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AccountSetup.this, "Account Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                Map<String,String> map1 = new HashMap<>();
                                map1.put("Username",usernameFetch);
                                map1.put("userId",currentUserId);
                                db.collection("UserNames").add(map1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(AccountSetup.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else{
                                            progressDialog.dismiss();
                                            Toast.makeText(AccountSetup.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(AccountSetup.this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else{
                    progressDialog.dismiss();
                    Toast.makeText(AccountSetup.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Available is clicked,
        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameIsAvailable.setVisibility(View.INVISIBLE);
                userNameAvailable = true;
                String userNameEntered = username.getText().toString();
                if (!TextUtils.isEmpty(userNameEntered)){
                    db.collection("UserNames").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult() == null){
                                    userNameAvailable = true;

                                } else{
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        if (document.getString("Username").equals(userNameEntered)){
                                            userNameAvailable = false;
                                            break;
                                        }
                                    }
                                }
                                if (userNameAvailable == true){
                                    usernameIsAvailable.setText("UserName is Available");
                                    usernameIsAvailable.setVisibility(View.VISIBLE);
                                } else{
                                    usernameIsAvailable.setText("UserName is not Available");
                                    usernameIsAvailable.setVisibility(View.VISIBLE);
                                }
                            } else{
                                Toast.makeText(AccountSetup.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else{
                    Toast.makeText(AccountSetup.this, "Enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}