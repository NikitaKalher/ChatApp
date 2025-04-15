package com.example.chatapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Models.MessageModel;
import com.example.chatapp.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    private DatabaseReference mDatabase; // Firebase database reference

    FirebaseDatabase database;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        final String senderId=auth.getUid();
        String receiverId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        //when we click on send image message is store in the database,msg store on both side sender and receiver and also editText will be empty
        final String senderRoom=senderId+receiverId;
        final String receiverRoom=receiverId+senderId;

        binding.clearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create scale animation
                Animation anim = new ScaleAnimation(
                        1f, 0.9f, // Start and end values for the X axis scaling
                        1f, 0.9f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(200); // Duration in milliseconds
                v.startAnimation(anim); // Start the animation

                AlertDialog.Builder builder=new AlertDialog.Builder(ChatDetailActivity.this);
                builder.setTitle("Clear Chat");
                builder.setMessage("Do you want to delete all messages?");
                builder.setIcon(R.drawable.del);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get a reference to the Firebase Realtime Database node that contains the chat messages
                        DatabaseReference chatMessagesRef = FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom);

                        // Remove all child nodes from the chat messages node
                        chatMessagesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Clear chat functionality successful
                                    Toast.makeText(ChatDetailActivity.this, "Chat cleared", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Clear chat functionality failed
                                    Toast.makeText(ChatDetailActivity.this, "Failed to clear chat", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChatDetailActivity.super.getOnBackPressedDispatcher();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChatDetailActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

        });

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels=new ArrayList<>();

        final ChatAdapter chatAdapter=new ChatAdapter(messageModels,this,receiverId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        //clear chat
        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messageModels.clear(); //one msg should be update only a time

                        for (DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            MessageModel model=snapshot1.getValue(MessageModel.class);

                            model.setMessageId(snapshot1.getKey());

                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();//used when we want to immediate change at sender side
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= binding.etMsg.getText().toString();
                final MessageModel model=new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                binding.etMsg.setText("");

                database.getReference().child("Chats")
                        .child(senderRoom)
                        .push()     //used to create node
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });

            }
        });

    }
}