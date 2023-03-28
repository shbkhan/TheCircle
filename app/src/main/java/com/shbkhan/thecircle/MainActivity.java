package com.shbkhan.thecircle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    ImageView homeScreen, searchScreen,notificationScreen,addPostScreen,profileScreen,messageScreen,premiumScreen;
    TextView companyName;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginSignUp.class);
            startActivity(intent);
            finish();
        } else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Things
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (user!=null){
            //Setting default fragment
            Fragment f1 = new HomeScreen();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame,f1).commit();

            //Getting components from the design or declaring the variables.
            homeScreen = findViewById(R.id.imageViewHomePageMain);
            searchScreen = findViewById(R.id.imageViewSearchMain);
            notificationScreen = findViewById(R.id.imageViewNotificationMain);
            addPostScreen = findViewById(R.id.imageViewNewPostMain);
            profileScreen = findViewById(R.id.imageViewProfileMain);
            messageScreen = findViewById(R.id.imageViewMessageMain);
            premiumScreen = findViewById(R.id.imageViewPremiumMain);
            companyName = findViewById(R.id.textViewCompanyNameMain);

            //Home button is clicked.
            homeScreen.setOnClickListener(view -> {
                homeScreen.setImageResource(R.drawable.homepage_white);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("The Circle");

                fragmentManager.beginTransaction().replace(R.id.frame,f1).commit();
            });

            //Search button is clicked.
            searchScreen.setOnClickListener(view -> {
                Fragment f2 = new SearchFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search_white);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("Search");

                fragmentManager.beginTransaction().replace(R.id.frame,f2).commit();

            });

            //Notification button is clicked.
            notificationScreen.setOnClickListener(view -> {
                Fragment f3 = new NotificationFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification_white);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("Notification");

                fragmentManager.beginTransaction().replace(R.id.frame,f3).commit();
            });

            //Add post button is clicked.
            addPostScreen.setOnClickListener(view -> {
                Fragment f4 = new AddNewPostFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post_white);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("New Post");

                fragmentManager.beginTransaction().replace(R.id.frame,f4).commit();
            });

            //Profile button is clicked.
            profileScreen.setOnClickListener(view -> {
                Fragment f5 = new ProfileFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile_white);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("Profile");

                fragmentManager.beginTransaction().replace(R.id.frame,f5).commit();
            });

            //Message button is clicked.
            messageScreen.setOnClickListener(view -> {
                Fragment f6 = new MessageFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages_white);
                premiumScreen.setImageResource(R.drawable.premium);
                companyName.setText("Messages");

                fragmentManager.beginTransaction().replace(R.id.frame,f6).commit();
            });

            //Premium button is clicked.
            premiumScreen.setOnClickListener(view -> {
                Fragment f7 = new PremiumFragment();
                homeScreen.setImageResource(R.drawable.homepage);
                searchScreen.setImageResource(R.drawable.search);
                notificationScreen.setImageResource(R.drawable.notification);
                addPostScreen.setImageResource(R.drawable.add_new_post);
                profileScreen.setImageResource(R.drawable.profile);
                messageScreen.setImageResource(R.drawable.messages);
                premiumScreen.setImageResource(R.drawable.premium_white);
                companyName.setText("Premium");

                fragmentManager.beginTransaction().replace(R.id.frame,f7).commit();
            });
        }

    }

    //When back button button is pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}