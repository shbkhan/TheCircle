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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.Adapter.MessageSenderAdapter;
import com.shbkhan.thecircle.Model.AccountSetupModel;

import java.util.ArrayList;
import java.util.Collections;


public class MessageFragment extends Fragment {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    RecyclerView rvChatContainer;
    ArrayList<AccountSetupModel> list;

    public MessageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        rvChatContainer = view.findViewById(R.id.rvMessageList);
        list = new ArrayList<>();

        MessageSenderAdapter adapter = new MessageSenderAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvChatContainer.setLayoutManager(linearLayoutManager);
        rvChatContainer.setNestedScrollingEnabled(false);
        rvChatContainer.setAdapter(adapter);


        reference = firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        AccountSetupModel model = snapshot1.getValue(AccountSetupModel.class);
                        list.add(model);
                    }
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();

                } else{
                    Toast.makeText(getContext(), "Follow someone first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}