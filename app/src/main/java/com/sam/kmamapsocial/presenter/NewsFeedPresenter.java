package com.sam.kmamapsocial.presenter;

import com.google.firebase.database.DataSnapshot;
import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.model.Image;
import com.sam.kmamapsocial.view.event.OnNewsFeedCallBack;

import java.util.ArrayList;

public class NewsFeedPresenter extends BasePresenter<OnNewsFeedCallBack> {

    public NewsFeedPresenter(OnNewsFeedCallBack mCallBack) {
        super(mCallBack);
    }

    public void dataChange(ArrayList<Image> dataImage, DataSnapshot dataSnapshot) {
        mCallBack.showLoading();
        try {
            dataImage.clear();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                Image image = data.getValue(Image.class);
                dataImage.add(image);
            }
            mCallBack.toSuccessDataChange(dataImage);
            mCallBack.hideLoading();
        } catch (Exception ex) {
            mCallBack.toNotSuccessDataChange();
            mCallBack.hideLoading();
        }
    }
}
