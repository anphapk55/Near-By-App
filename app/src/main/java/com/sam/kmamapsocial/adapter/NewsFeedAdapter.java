package com.sam.kmamapsocial.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.databinding.ItemFeedBinding;
import com.sam.kmamapsocial.model.Image;
import com.sam.kmamapsocial.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private ArrayList<Image> data;
    private LayoutInflater inflater;
    private Context context;
    private ImageListener imageListener;

    public void setImageListener(ImageListener imageListener) {
        this.imageListener = imageListener;
    }

    public NewsFeedAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Image> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }
        ItemFeedBinding binding = ItemFeedBinding.inflate(inflater);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.bindData(data.get(i), i, imageListener);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemFeedBinding binding;

        public ViewHolder(@NonNull ItemFeedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(final Image item, final int position, final ImageListener listener) {

            binding.tvPublicAccountName.setText(CommonUtils.getInstance().splitEmail(item.getMail()));

            binding.tvPublicItemFeed.setText(item.getName());

//            binding.tvItemFeedLatlg.setText(String.format("Lat: %s _ Lng: %s", item.getLatitude(), item.getLongitude()));

            binding.tvItemFeedLatlg.setText(String.format("Lat : %s | Lng : %s", CommonUtils.getInstance().
                    formatDoubleToString(Double.valueOf(item.getLatitude())), CommonUtils.getInstance()
                    .formatDoubleToString(Double.valueOf(item.getLongitude()))));

            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss a");
            try {
                Date d = df.parse(item.getDate());
                if (d != null) {
                    long milliseconds = d.getTime();
                    binding.tvItemPublicFeedUploadDate.setText(CommonUtils.getTimeAgo(milliseconds));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (item.getUrl() != null) {
                Glide.with(context)
                        .load(item.getUrl())
                        .centerCrop()
                        .apply(new RequestOptions().placeholder(R.drawable.img_noimage).error(R.drawable.img_noimage))
                        .into(binding.ivPublicItemImageFeed);
            } else {
                binding.ivPublicItemImageFeed.getLayoutParams().height = 0;
            }

            Glide.with(context)
                    .load(item.getUrlAvatar())
                    .centerCrop()
                    .circleCrop()
                    .apply(new RequestOptions().placeholder(R.drawable.unsplash_men_avatar).error(R.drawable.unsplash_men_avatar))
                    .into(binding.imgAvt);

            binding.ivItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.toImageLocation(item.getLatitude(), item.getLongitude());
                    }
                }
            });
        }
    }

    public interface ImageListener {
        void toImageLocation(String latitude, String longitude);
    }
}
