package com.sam.kmamapsocial.view.event;

import com.sam.kmamapsocial.model.UserInfo;

import java.util.ArrayList;

public interface OnAccountCallBack extends OnShowLoading{
    void toSuccessDataChange(ArrayList<UserInfo> dataUserInfo);

    void toNotSuccessDataChange();
}
