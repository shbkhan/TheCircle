package com.shbkhan.thecircle;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shbkhan.thecircle.Adapter.HomePostAdapter;
import com.shbkhan.thecircle.Adapter.ProfilePostAdapter;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.Model.PostUploadModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    Button editProfileProfileScreen,signOutProfileScreen;
    TextView nameProfileScreen, usernameProfileScreen, bioProfileScreen,numOfCircle,numOfPost;
    ImageView profilePicture;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DatabaseReference reference;
    ProgressDialog progressDialog;
    String currentUserId;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    String username,gender,profileImageUrl;
    StorageReference storageReference;
    DatabaseReference followerReference;
    RecyclerView rvHomePost;
    ArrayList<PostUploadModel> list;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        editProfileProfileScreen = getActivity().findViewById(R.id.buttonEditProfile);
        signOutProfileScreen = getActivity().findViewById(R.id.buttonSignOutProfile);
        nameProfileScreen = getActivity().findViewById(R.id.textViewNameProfile);
        usernameProfileScreen = getActivity().findViewById(R.id.textViewUsernameProfile);
        bioProfileScreen = getActivity().findViewById(R.id.textViewBioProfile);
        profilePicture = getActivity().findViewById(R.id.profilePicture);
        currentUserId = user.getUid();
        numOfPost = getActivity().findViewById(R.id.textViewNumberOfPost);
        numOfCircle = getActivity().findViewById(R.id.textViewNumOfCircle);
        storageReference = FirebaseStorage.getInstance().getReference().child("profilePhotos").child(currentUserId);
        progressDialog = new ProgressDialog(getActivity());


        databaseReference = firebaseDatabase.getReference().child("Users").child(currentUserId);
        followerReference = databaseReference.child("Followers");

        followerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long follower = snapshot.getChildrenCount();
                    String numberOfFollower = String.valueOf(follower);
                    numOfCircle.setText(numberOfFollower);
                } else{
                    numOfCircle.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("selfPost").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long postCount = snapshot.getChildrenCount();
                    numOfPost.setText(String.valueOf(postCount));
                } else{
                    numOfPost.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    Toast.makeText(getActivity(), "Set the user data first", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(),AccountSetup.class);
                    startActivity(intent);
                    getActivity().finish();
                } else{
                    AccountSetupModel accountSetupModel = snapshot.getValue(AccountSetupModel.class);
                    nameProfileScreen.setText(accountSetupModel.getName());
                    usernameProfileScreen.setText(accountSetupModel.getUsername());
                    bioProfileScreen.setText(accountSetupModel.getBio());
                    username = accountSetupModel.getUsername();
                    gender = accountSetupModel.getGender();
                    try {
                        profileImageUrl = accountSetupModel.getProfileImageLink();
                        if (!profileImageUrl.isEmpty()){
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getContext()).load(uri).into(profilePicture);
                                }
                            });
                        }
                    } catch (Exception e){

                    }


                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        signOutProfileScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(),LoginSignUp.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        editProfileProfileScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Edit Profile yet to build", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ProfileEditActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("gender",gender);
                startActivity(intent);
            }
        });
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        rvHomePost = view.findViewById(R.id.rvPostProfile);
        list = new ArrayList<>();

        ProfilePostAdapter adapter = new ProfilePostAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvHomePost.setLayoutManager(linearLayoutManager);
        rvHomePost.setNestedScrollingEnabled(false);
        rvHomePost.setAdapter(adapter);

        reference = firebaseDatabase.getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostUploadModel post = dataSnapshot.getValue(PostUploadModel.class);
                    if (post.getPostedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        list.add(post);
                    }

                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

}