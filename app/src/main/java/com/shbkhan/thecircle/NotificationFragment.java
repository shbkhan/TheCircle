package com.shbkhan.thecircle;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.Adapter.NotificationAdapter;
import com.shbkhan.thecircle.Model.NotificationModel;

import java.util.ArrayList;
import java.util.Collections;


public class NotificationFragment extends Fragment {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    RecyclerView rvContainer;
    ArrayList<NotificationModel> list;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        rvContainer = view.findViewById(R.id.rvNotification);
        list = new ArrayList<>();

        NotificationAdapter adapter = new NotificationAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvContainer.setLayoutManager(linearLayoutManager);
        rvContainer.setNestedScrollingEnabled(false);
        rvContainer.setAdapter(adapter);

        databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Notification");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        NotificationModel model = snapshot1.getValue(NotificationModel.class);
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

        return view;
    }
}