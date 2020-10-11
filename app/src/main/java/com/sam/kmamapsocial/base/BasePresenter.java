package com.sam.kmamapsocial.base;
import com.sam.kmamapsocial.view.event.OnShowLoading;

public abstract class BasePresenter<T extends OnShowLoading> {
    protected T mCallBack;

    public BasePresenter(T mCallBack) {
        this.mCallBack = mCallBack;
    }

}
