package com.sam.kmamapsocial.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sam.kmamapsocial.databinding.ItemUserChatBinding;
import com.sam.kmamapsocial.model.UserInfo;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<UserInfo> data;

    public ChatListAdapter(ArrayList<UserInfo> data) {
        this.data = data;
    }

    public void setListener(ItemChatListClickListener listener) {
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        ItemUserChatBinding binding = ItemUserChatBinding.inflate(inflater);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.binding.tvMail.setText(data.get(position).getEmailUser());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemUserChatBinding binding;

        public ViewHolder(@NonNull ItemUserChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(UserInfo item) {
            binding.tvMail.setText("samvu.demo");
            binding.tvIsOnline.setText("Đang online");
        }
    }

    public interface ItemChatListClickListener {
        void onItemClick(UserInfo chat);

        void onLongItemClick(UserInfo chat);
    }
}
