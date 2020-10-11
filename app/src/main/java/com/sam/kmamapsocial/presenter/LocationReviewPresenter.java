package com.sam.kmamapsocial.presenter;

import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.view.event.OnLocationReviewCallBack;

public class LocationReviewPresenter extends BasePresenter<OnLocationReviewCallBack> {
    public LocationReviewPresenter(OnLocationReviewCallBack mCallBack) {
        super(mCallBack);
    }
}
