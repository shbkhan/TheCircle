package com.shbkhan.thecircle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class ProfileEditActivity extends AppCompatActivity {
    EditText etName,etBio;
    Button btnSubmit;
    ImageView imageHolder,addNew;
    Uri uri;
    String username,gender;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    String currentUserId = mAuth.getCurrentUser().getUid();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profilePhotos").child(currentUserId);
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        etName = findViewById(R.id.etName);
        progressDialog = new ProgressDialog(this);
        etBio = findViewById(R.id.etBio);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageHolder = findViewById(R.id.imageButtonSelectProfilePicture);
        addNew = findViewById(R.id.addNewImage);
        reference = firebaseDatabase.getReference().child("Users").child(currentUserId);
        username = getIntent().getStringExtra("username");
        gender = getIntent().getStringExtra("gender");
        Toast.makeText(this, username+" "+gender, Toast.LENGTH_SHORT).show();


        reference = firebaseDatabase.getReference().child("Users").child(currentUserId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AccountSetupModel model = snapshot.getValue(AccountSetupModel.class);
                    etName.setText(model.getName());
                    etBio.setText(model.getBio());
                } else{
                    Toast.makeText(ProfileEditActivity.this, "Error while loading Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                String name = etName.getText().toString();
                String bio = etBio.getText().toString();
                String email = mAuth.getCurrentUser().getEmail();


                UploadTask uploadTask;

                if (!name.isEmpty() && !bio.isEmpty()){
                    if (uri!=null){
                        uploadTask = storageReference.putFile(uri);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String url = String.valueOf(storageReference.getDownloadUrl());
                                AccountSetupModel model = new AccountSetupModel(name,username,bio,gender,email);
                                model.setProfileImageLink(url);
                                reference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(ProfileEditActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileEditActivity.this, "Error uploading", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileEditActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    progressDialog.dismiss();
                    Toast.makeText(ProfileEditActivity.this, "Enter Name and Bio", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && data!=null){
            uri = data.getData();
            Glide.with(this).load(uri).into(imageHolder);
        }
    }
}