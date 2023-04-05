package com.shbkhan.thecircle;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.shbkhan.thecircle.Adapter.ProfileSearchAdapter;
import com.shbkhan.thecircle.Model.AccountSetupModel;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,databaseReference2;
    FirebaseFirestore db;
    RecyclerView rvSearchContainer;
    ArrayList<AccountSetupModel> list;
    EditText usernameSearch;
    ImageView searchLogo;
    Button btnFollow;
    ProgressDialog progressDialog;
    String searchedUID;
    boolean userPresent = false;


    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        rvSearchContainer = view.findViewById(R.id.rvSearchContainer);
        usernameSearch = view.findViewById(R.id.editTextUserNameToSearch);
        searchLogo = view.findViewById(R.id.imageViewButtonToSearch);


        progressDialog = new ProgressDialog(getContext());
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        searchLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                progressDialog.setCancelable(false);
                userPresent = false;
                list.clear();
                String username = usernameSearch.getText().toString().trim();
                if (!username.isEmpty()){
                    db.collection("UserNames").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(QueryDocumentSnapshot snapshot: task.getResult()){
                                    if (snapshot.getString("Username").equals(username)){
                                        userPresent = true;
                                        searchedUID = snapshot.getString("userId");
                                        databaseReference = firebaseDatabase.getReference().child("Users").child(searchedUID);
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                AccountSetupModel model1 = snapshot.getValue(AccountSetupModel.class);
                                                list.add(model1);
                                                ProfileSearchAdapter adapter = new ProfileSearchAdapter(list,getContext());
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                                                rvSearchContainer.setLayoutManager(linearLayoutManager);
                                                rvSearchContainer.setNestedScrollingEnabled(false);
                                                rvSearchContainer.setAdapter(adapter);
                                                progressDialog.dismiss();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;
                                    } else{

                                    }
                                }

                                if (!userPresent){
                                    Toast.makeText(getContext(), "No such user", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            } else{
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Some error encountered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Enter username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}