package com.sam.kmamapsocial.model;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.sam.kmamapsocial.BR;

public class UserInfo extends BaseObservable {

    private String emailUser;
    private String uidUser;
    private String urlAvatarUser;
    private double totalRunUser;

    public UserInfo() {
    }



    @Bindable
    public double getTotalRunUser() {
        return totalRunUser;
    }

    public void setTotalRunUser(double totalRunUser) {
        this.totalRunUser = totalRunUser;
        notifyPropertyChanged(BR.totalRunUser);
    }

    @Bindable
    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
        notifyPropertyChanged(BR.emailUser);
    }

    @Bindable
    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
        notifyPropertyChanged(BR.uidUser);
    }

    @Bindable
    public String getUrlAvatarUser() {
        return urlAvatarUser;
    }

    public void setUrlAvatarUser(String urlAvatarUser) {
        this.urlAvatarUser = urlAvatarUser;
        notifyPropertyChanged(BR.urlAvatarUser);
    }

    @BindingAdapter({"android:urlAvatarUser"})
    public static void loadImage(ImageView view, String imgUrl) {
        Glide.with(view.getContext())
                .load(imgUrl)
                .circleCrop()
                .into(view);
    }
}
