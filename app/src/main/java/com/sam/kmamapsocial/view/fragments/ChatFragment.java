package com.sam.kmamapsocial.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.kmamapsocial.adapter.ChatListAdapter;
import com.sam.kmamapsocial.databinding.FragmentChatRootBinding;
import com.sam.kmamapsocial.model.UserInfo;

import java.util.ArrayList;


public class ChatFragment extends Fragment {
    public static final String TAG = ChatFragment.class.getName();
    private ArrayList<UserInfo> dataUser;
    FragmentChatRootBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        binding = FragmentChatRootBinding.inflate(inflater);
        dataUser = bornTempData(30);
        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rccChatRoot;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        ChatListAdapter adapter = new ChatListAdapter(dataUser);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<UserInfo> bornTempData(int size) {
        ArrayList<UserInfo> tempList = new ArrayList<UserInfo>();
        for (int i = 0; i < size; i++) {
            UserInfo tempItem = new UserInfo();
            tempItem.setEmailUser("samvu." + i);
            tempList.add(tempItem);
        }
        return tempList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}