package com.example.chatapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.Fragments.ChatsFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
          //  case 0:return new RecentlyChats();
            case 0:return new ChatsFragment();
            default:return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    //set Page title

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
      /*  if(position==0){
            title="CHATS";
        }*/
        if(position==0){
            title="Chats";
        }
        return title;
    }
}

