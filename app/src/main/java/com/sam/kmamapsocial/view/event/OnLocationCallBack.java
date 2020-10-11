package com.sam.kmamapsocial.view.event;

public interface OnLocationCallBack extends OnShowLoading {
    void snapShotSuccess(String mPath);

    void snapShotNotSuccess();
}
