package com.sam.kmamapsocial.view.event;

import com.sam.kmamapsocial.model.Image;

import java.util.ArrayList;

public interface OnNewsFeedCallBack extends OnShowLoading {

    void toNotSuccessDataChange();

    void toSuccessDataChange(ArrayList<Image> dataImage);
}
