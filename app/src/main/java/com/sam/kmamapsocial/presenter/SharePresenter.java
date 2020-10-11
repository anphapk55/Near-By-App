package com.sam.kmamapsocial.presenter;

import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.view.event.OnShareCallBack;

public class SharePresenter extends BasePresenter<OnShareCallBack> {
    public SharePresenter(OnShareCallBack mCallBack) {
        super(mCallBack);
    }
}
