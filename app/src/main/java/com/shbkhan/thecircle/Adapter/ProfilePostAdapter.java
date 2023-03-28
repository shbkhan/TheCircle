package com.shbkhan.thecircle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shbkhan.thecircle.CommentActivity;
import com.shbkhan.thecircle.Model.AccountSetupModel;
import com.shbkhan.thecircle.Model.PostUploadModel;
import com.shbkhan.thecircle.ProfileFragment;
import com.shbkhan.thecircle.R;

import java.util.ArrayList;
import java.util.Objects;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.viewHolder> {
    ArrayList<PostUploadModel> list;
    Context context;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    boolean alreadyLiked = false;
    String likeId;

    public ProfilePostAdapter(ArrayList<PostUploadModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_items,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostUploadModel model = list.get(position);
        
        //Set Desc and photo
        holder.desc.setText(model.getPostDesc());
        Glide.with(context).load(model.getPostUrl()).into(holder.postImage);

        //Set username
        String postedBy = model.getPostedBy();
        firebaseDatabase.getReference().child("Users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(AccountSetupModel.class).getUsername();
                holder.username.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        //Like count
        String postId = model.getPostId();
        firebaseDatabase.getReference().child("Posts").child(postId).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int count =(int) snapshot.getChildrenCount();
                    if (count > 1){
                        holder.noOfLikes.setText(count+" Likes");
                    } else{
                        holder.noOfLikes.setText(count+" Like");
                    }
                } else{
                    holder.likes.setImageResource(R.drawable.like_white);
                    holder.noOfLikes.setText("0 Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        //Update Like
        firebaseDatabase.getReference().child("Posts").child(postId).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot  snapshot1 : snapshot.getChildren()){
                    if (Objects.equals(snapshot1.getValue(), FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        likeId = snapshot1.getKey();
                        alreadyLiked = true;
                        holder.likes.setImageResource(R.drawable.like);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "like clicked", Toast.LENGTH_SHORT).show();
                firebaseDatabase.getReference().child("Posts").child(postId).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!alreadyLiked){
                            holder.likes.setImageResource(R.drawable.like_white);
                            firebaseDatabase.getReference().child("Posts").child(postId).child("Likes").push()
                                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                holder.likes.setImageResource(R.drawable.like);
                                                alreadyLiked = true;
                                            } else{

                                            }
                                        }
                                    });
                        }
                        if (alreadyLiked){
                            firebaseDatabase.getReference().child("Posts").child(postId).child("Likes").child(likeId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                holder.likes.setImageResource(R.drawable.like_white);
                                                alreadyLiked = false;
                                            }else{
                                                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",postId);
                intent.putExtra("postedBy",postedBy);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ImageView userProfilePhoto,postImage,likes,comments;
        TextView username,noOfLikes,desc;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePhoto = itemView.findViewById(R.id.imageViewUserProfileOnHome);
            postImage = itemView.findViewById(R.id.imageViewPostOnTheHomeScreen);
            likes = itemView.findViewById(R.id.imageViewLikeOnHomeScreen);
            comments = itemView.findViewById(R.id.imageViewCommentOnHomeScreen);
            username = itemView.findViewById(R.id.textViewUsernameOnHome);
            noOfLikes = itemView.findViewById(R.id.textViewNoOfLikesOnHomeScreen);
            desc = itemView.findViewById(R.id.tvDescriptionOnHome);
        }
    }
}
