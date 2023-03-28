package com.shbkhan.thecircle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.shbkhan.thecircle.Model.MessageModel;
import com.shbkhan.thecircle.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {
    ArrayList<MessageModel> list;
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserId;

    public ChatAdapter(ArrayList<MessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatting_message_item,parent,false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MessageModel model = list.get(position);
        String message = model.getMessage();
        String sentBy = model.getSentBy();
        holder.messageLeft.setVisibility(View.INVISIBLE);
        holder.messageRight.setVisibility(View.INVISIBLE);
        currentUserId = mAuth.getCurrentUser().getUid();
        if (sentBy.equals(currentUserId)){
            holder.messageRight.setText(message);
            holder.messageRight.setVisibility(View.VISIBLE);
        } else{
            holder.messageLeft.setText(message);
            holder.messageLeft.setVisibility(View.VISIBLE);
        }


    }
    public int getLastItemPosition() {
        return getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView messageLeft,messageRight;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            messageLeft = itemView.findViewById(R.id.tvChatLeft);
            messageRight = itemView.findViewById(R.id.tvChatRight);
        }
    }
}
