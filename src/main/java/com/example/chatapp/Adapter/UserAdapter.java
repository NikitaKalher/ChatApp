package com.example.chatapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatDetailActivity;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<Users> list;
    private Context context;
    private DatabaseReference chatRef;



    public void updateUsers(ArrayList<Users> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);

        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar).into(holder.imageView);
        holder.userName.setText(users.getUserName());

        // Set the last message
        setLastMessage(users.getUserId(), holder.lastMsg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("profilePic", users.getProfilePic());
                intent.putExtra("userName", users.getUserName());
                context.startActivity(intent);
            }
        });
    }

    private void setLastMessage(String userId, TextView lastMsgView) {
        chatRef.child(FirebaseAuth.getInstance().getUid() + userId)
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String message = dataSnapshot.child("message").getValue(String.class);
                                lastMsgView.setText(message);
                            }
                        } else {
                            lastMsgView.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        lastMsgView.setText("");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView userName, lastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNamelist);
            lastMsg = itemView.findViewById(R.id.lastMsg);
        }
    }
}

