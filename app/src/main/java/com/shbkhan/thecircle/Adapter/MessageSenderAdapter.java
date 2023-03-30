package com.shbkhan.thecircle.Adapter;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shbkhan.thecircle.ChatActivity;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.R;

import java.util.ArrayList;

public class MessageSenderAdapter extends RecyclerView.Adapter<MessageSenderAdapter.viewHolder> {
    ArrayList<AccountSetupModel> list;
    Context context;

    public MessageSenderAdapter(ArrayList<AccountSetupModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_sender_list,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AccountSetupModel model = list.get(position);
        holder.username.setText(model.getUsername());
        Glide.with(context).load(model.getProfileImageLink()).placeholder(R.drawable.user).into(holder.userPhoto);
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Start chatting", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("username",model.getUsername());
                intent.putExtra("receiverId",model.getUserId());
                intent.putExtra("dpUrl",model.getProfileImageLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView userPhoto;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUserNameSenderMessage);
            userPhoto = itemView.findViewById(R.id.userPhotoMessageSender);
        }
    }
}
