package com.shbkhan.thecircle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.Model.NotificationModel;
import com.shbkhan.thecircle.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ProfileSearchAdapter extends RecyclerView.Adapter<ProfileSearchAdapter.viewHolder>{
    ArrayList<AccountSetupModel> list;
    Context context;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference referenceOwn,referenceTarget,referenceNotification;
    String nameOwn,emailOwn,bioOwn,genderOwn,userIdOwn,usernameOwn,currentUserId;
    String nameTarget,emailTarget,bioTarget,genderTarget,userIdTarget,usernameTarget;
    String notificationId;

    public ProfileSearchAdapter(ArrayList<AccountSetupModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_search_items,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AccountSetupModel model = list.get(position);
        nameTarget = model.getName();
        emailTarget = model.getEmail();
        bioTarget = model.getBio();
        genderTarget = model.getGender();
        userIdTarget = model.getUserId();
        usernameTarget = model.getUserId();
        referenceTarget = firebaseDatabase.getReference().child("Users").child(usernameTarget);
        holder.tvUsername.setText(model.getUsername());
        referenceOwn.child("Followers").child(userIdTarget).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.btnFollow.setText("Following");
                    holder.btnFollow.setClickable(false);
                    Toast.makeText(context, "Already Following", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView tvUsername;
        Button btnFollow;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewUserProfileOnSearch);
            tvUsername = itemView.findViewById(R.id.textViewUsernameOnSearch);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            referenceOwn = firebaseDatabase.getReference().child("Users").child(currentUserId);

            referenceOwn.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        AccountSetupModel ownModel = snapshot.getValue(AccountSetupModel.class);
                        nameOwn = ownModel.getName();
                        emailOwn = ownModel.getEmail();
                        bioOwn = ownModel.getBio();
                        genderOwn = ownModel.getGender();
                        usernameOwn = ownModel.getUsername();
                        userIdOwn = ownModel.getUserId();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Objects.equals(userIdOwn, userIdTarget)){
                        notificationId = String.valueOf(new Date().getTime());
                        referenceNotification = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdTarget)
                                .child("Notification").child(notificationId);

                        AccountSetupModel setTargetDataToOwn = new AccountSetupModel(nameTarget,usernameTarget,bioTarget,genderTarget,emailTarget);
                        setTargetDataToOwn.setUserId(userIdTarget);
                        referenceOwn.child("Followers").child(userIdTarget).setValue(setTargetDataToOwn).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    AccountSetupModel setOwnDataToTarget = new AccountSetupModel(nameOwn,usernameOwn,bioOwn,genderOwn,emailOwn);
                                    setOwnDataToTarget.setUserId(userIdOwn);
                                    referenceTarget.child("Followers").child(userIdOwn).setValue(setOwnDataToTarget).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                NotificationModel notificationModel = new NotificationModel();
                                                notificationModel.setReactionBy(userIdOwn);
                                                notificationModel.setReactionType(" started following you.");
                                                referenceNotification.setValue(notificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            btnFollow.setText("Following");
                                                        }
                                                    }
                                                });
                                            } else{

                                            }
                                        }
                                    });
                                } else{
                                    btnFollow.setText("Following");
                                    btnFollow.setClickable(false);
                                    Toast.makeText(context, "Error encountered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        Toast.makeText(context, "cannot follow to self", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
