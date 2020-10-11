package com.sam.kmamapsocial.presenter;

import com.google.firebase.database.DataSnapshot;
import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.model.UserInfo;
import com.sam.kmamapsocial.view.event.OnAccountCallBack;

import java.util.ArrayList;

public class AccountPresenter extends BasePresenter<OnAccountCallBack> {
    public AccountPresenter(OnAccountCallBack mCallBack) {
        super(mCallBack);
    }

    public void dataUserInfo(ArrayList<UserInfo> dataUserInfo, DataSnapshot dataSnapshot) {
        mCallBack.showLoading();
        try {
            dataUserInfo.clear();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                UserInfo userInfo = data.getValue(UserInfo.class);
                dataUserInfo.add(userInfo);
            }
            mCallBack.toSuccessDataChange(dataUserInfo);
            mCallBack.hideLoading();
        } catch (Exception ex) {
            ex.printStackTrace();
            mCallBack.toNotSuccessDataChange();
            mCallBack.hideLoading();
        }
    }
}
