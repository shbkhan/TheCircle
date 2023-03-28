package com.shbkhan.thecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shbkhan.thecircle.Adapter.CommentAdapter;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.Model.CommentModel;
import com.shbkhan.thecircle.Model.NotificationModel;
import com.shbkhan.thecircle.Model.PostUploadModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ImageView userPhoto;
    TextView username,description;
    EditText commentSection;
    Button postButton;
    RecyclerView rvCommentContainer;
    String postId,postedBy;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference1,databaseReference2;
    String commentId;
    ArrayList<CommentModel> list;
    String notificationId;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        userPhoto = findViewById(R.id.ivUserPhoto);
        username = findViewById(R.id.tvUsernameComment);
        description = findViewById(R.id.tvDescriptionComment);
        commentSection = findViewById(R.id.etComment);
        postButton = findViewById(R.id.btnPostComment);
        rvCommentContainer = findViewById(R.id.rvComment);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        databaseReference1 = firebaseDatabase.getReference();

        list = new ArrayList<>();
        CommentAdapter adapter = new CommentAdapter(list,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvCommentContainer.setLayoutManager(linearLayoutManager);
        rvCommentContainer.setNestedScrollingEnabled(false);
        rvCommentContainer.setAdapter(adapter);

        databaseReference1.child("Posts").child(postId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                CommentModel model = snapshot1.getValue(CommentModel.class);
                                list.add(model);
                            }
                            Collections.reverse(list);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Setting username
        databaseReference1.child("Users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AccountSetupModel model = snapshot.getValue(AccountSetupModel.class);
                    username.setText(model.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Setting description
        databaseReference1.child("Posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PostUploadModel model = snapshot.getValue(PostUploadModel.class);
                    description.setText(model.getPostDesc());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Uploading the comment
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentToDo = commentSection.getText().toString().trim();
                commentId = String.valueOf(new Date().getTime());
                if (!commentToDo.isEmpty()){
                    CommentModel model = new CommentModel(commentToDo,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference1.child("Posts").child(postId).child("Comments").child(commentId)
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    notificationId = String.valueOf(new Date().getTime());
                                    NotificationModel model1 = new NotificationModel();
                                    model1.setReactionType("commented on your photo");
                                    model1.setReactionBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    model1.setReactionOn(postId);

                                    reference = firebaseDatabase.getReference().child("Users").child(postedBy)
                                            .child("Notification").child(notificationId);
                                    reference.setValue(model1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                commentSection.setText("");
                                                recreate();
                                                Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CommentActivity.this, "Error sending notification", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CommentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }
}