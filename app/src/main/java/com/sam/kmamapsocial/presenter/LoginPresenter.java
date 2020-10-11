package com.sam.kmamapsocial.presenter;

import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.view.event.OnLoginCallBack;

public class LoginPresenter extends BasePresenter<OnLoginCallBack> {
    public LoginPresenter(OnLoginCallBack mCallBack) {
        super(mCallBack);
    }
}
