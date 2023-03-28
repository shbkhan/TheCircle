package com.shbkhan.thecircle;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shbkhan.thecircle.Model.PostUploadModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;


public class AddNewPostFragment extends Fragment {
    final String[] postType = {"Photo","Video"};
    Spinner postSelectionSpinner;
    ImageView imageViewSelectImage;
    VideoView videoViewSelectVideo;
    Button buttonDoThePost;
    EditText editTextDescription;
    Uri uri;
    boolean imageSelected = false,videoSelected = false;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference referenceOwn,referenceFollower,referencePost,universalPost;
    String postId;
    ProgressDialog progressDialog;
    
    public AddNewPostFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        postSelectionSpinner = getActivity().findViewById(R.id.postSelection);
        imageViewSelectImage = getActivity().findViewById(R.id.imageViewImageSelected);
        videoViewSelectVideo = getActivity().findViewById(R.id.videoViewVideoSelected);
        buttonDoThePost = getActivity().findViewById(R.id.buttonDoThePost);
        editTextDescription = getActivity().findViewById(R.id.editTextDescriptionAddNewPost);
        postId = String.valueOf(new Date().getTime());
        progressDialog = new ProgressDialog(getActivity());

        storageReference = firebaseStorage.getReference().child("Posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(postId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,postType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSelectionSpinner.setAdapter(adapter);
        postSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        imageViewSelectImage.setVisibility(View.VISIBLE);
                        videoViewSelectVideo.setVisibility(View.INVISIBLE);
                        buttonDoThePost.setVisibility(View.VISIBLE);
                        editTextDescription.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        videoViewSelectVideo.setVisibility(View.VISIBLE);
                        imageViewSelectImage.setVisibility(View.INVISIBLE);
                        buttonDoThePost.setVisibility(View.VISIBLE);
                        editTextDescription.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageViewSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);

            }
        });
        videoViewSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "VideoView tapped", Toast.LENGTH_SHORT).show();
            }
        });
        buttonDoThePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageSelected || videoSelected){
                    String desc = editTextDescription.getText().toString().trim();
                    if (!desc.isEmpty()){
                        progressDialog.setMessage("Please wait");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        PostUploadModel postUploadModel = new PostUploadModel();
                                        postUploadModel.setPostId(postId);
                                        postUploadModel.setPostUrl(uri.toString());
                                        postUploadModel.setPostTime(postId);
                                        postUploadModel.setPostDesc(desc);
                                        postUploadModel.setPostedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        universalPost = firebaseDatabase.getReference().child("Posts").child(postId);
                                        universalPost.setValue(postUploadModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    referenceOwn = firebaseDatabase.getReference().child("Users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("selfPost");
                                                    referenceOwn.push().setValue(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Uri uri1 = null;
                                                                imageViewSelectImage.setImageURI(uri);
                                                                editTextDescription.setText("");
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                            } else{
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getContext(), "Error encountered", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                } else{
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "Error encountered", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Enter the description about the post", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(getContext(), "Select the media first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_post, container, false);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && data!=null){
            uri = data.getData();
            Glide.with(this).load(uri).into(imageViewSelectImage);
            imageSelected = true;
        }

    }

}