package com.shbkhan.thecircle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.Model.NotificationModel;
import com.shbkhan.thecircle.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
    ArrayList<NotificationModel> list;
    Context context;
    DatabaseReference reference;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_items,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel model = list.get(position);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getReactionBy());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AccountSetupModel model1 = snapshot.getValue(AccountSetupModel.class);
                    holder.username.setText(model1.getUsername());
                    Glide.with(context).load(model1.getProfileImageLink()).placeholder(R.drawable.user).into(holder.userPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.reactionDone.setText(model.getReactionType());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView username,reactionDone;
        ImageView userPhoto;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textViewUsernameOnNotification);
            reactionDone = itemView.findViewById(R.id.textViewActionTakenOnNotification);
            userPhoto = itemView.findViewById(R.id.imageViewUserProfileOnNotification);
        }
    }
}
