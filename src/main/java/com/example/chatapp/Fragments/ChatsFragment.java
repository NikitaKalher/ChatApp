package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Adapter.SearchUserAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Models.Users;
import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    private ChatAdapter chatAdapter;

    private ArrayList<Users> userList;
    private ArrayList<Users> originalList; // Store the original list of users
    private DatabaseReference databaseReference;
    private SearchView searchView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        userList = new ArrayList<>();
        originalList = new ArrayList<>();

        adapter = new UserAdapter(userList, getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);


        // Initialize searchView
        searchView = binding.searchView;


        // Uncomment this to enable searching
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });

        //get data from database
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                originalList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    // jo user login hain vo list me show na ho
                    /*if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))*/ {
                        userList.add(users);
                        originalList.add(users); // Add user to original list
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        return binding.getRoot();

    }

    private void searchUsers(String query) {
        userList.clear(); // Clear current list to show search results
        for (Users user : originalList) {
            if (user.getUserName().toLowerCase().contains(query.toLowerCase())) {
                userList.add(user); // Add matching user to list
            }
        }
        adapter.notifyDataSetChanged(); // Update RecyclerView with search results
    }
}

