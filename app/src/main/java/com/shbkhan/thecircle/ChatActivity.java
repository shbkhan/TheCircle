package com.shbkhan.thecircle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.Adapter.ChatAdapter;
import com.shbkhan.thecircle.Model.MessageModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    TextView username;
    EditText message;
    ImageView send;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String myId,otherId;
    DatabaseReference referenceMine,referenceOther,referenceFetch;
    String messageId = String.valueOf(new Date().getTime());
    ArrayList<MessageModel> list;
    RecyclerView rvMessageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        message = findViewById(R.id.etMessageContent);
        send = findViewById(R.id.ivSendMessage);
        username = findViewById(R.id.tvUserNameChatActivity);
        rvMessageContainer = findViewById(R.id.rvChatActivity);

        Intent intent = getIntent();
        username.setText(intent.getStringExtra("username"));

        myId = mAuth.getCurrentUser().getUid();
        otherId = intent.getStringExtra("receiverId");

        referenceMine = firebaseDatabase.getReference().child("Users").child(myId).child("ChatRoom")
                .child(otherId);
        referenceOther = firebaseDatabase.getReference().child("Users").child(otherId).child("ChatRoom")
                .child(myId);


        //Fetching message;
        list = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(list,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvMessageContainer.setLayoutManager(linearLayoutManager);
        rvMessageContainer.setNestedScrollingEnabled(false);
        rvMessageContainer.setAdapter(adapter);



        referenceFetch = firebaseDatabase.getReference().child("Users").child(myId).child("ChatRoom")
                .child(otherId);
        referenceFetch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        MessageModel model = snapshot1.getValue(MessageModel.class);
                        list.add(model);
                        int lastItemPosition = adapter.getLastItemPosition();
                        rvMessageContainer.scrollToPosition(lastItemPosition);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Sending Message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageToSend = message.getText().toString().trim();
                if (!messageToSend.isEmpty()){
                    MessageModel model = new MessageModel();
                    model.setMessage(messageToSend);
                    model.setSentBy(myId);

                    referenceOther.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            referenceMine.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    message.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    referenceOther.removeValue();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
                        }
                    });


                }else{
                    Toast.makeText(ChatActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}