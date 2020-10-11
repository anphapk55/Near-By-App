package com.sam.kmamapsocial.view.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.adapter.NewsFeedAdapter;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragmentNewFeedsBinding;
import com.sam.kmamapsocial.model.Image;
import com.sam.kmamapsocial.presenter.NewsFeedPresenter;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.view.event.OnNewsFeedCallBack;

import java.util.ArrayList;
import java.util.Collections;

public class NewsFeedFragments extends BaseFragment<NewsFeedPresenter, FragmentNewFeedsBinding> implements
        ValueEventListener, OnNewsFeedCallBack, NewsFeedAdapter.ImageListener,
        OnSuccessListener<Void>, OnFailureListener, View.OnClickListener, BaseFragment.onBaseFragmentClick {

    public static final String TAG = NewsFeedFragments.class.getName();
    private ArrayList<Image> dataImage;
    private NewsFeedAdapter newsFeedAdapter;

    @Override
    protected NewsFeedPresenter getPresenter() {
        return new NewsFeedPresenter(this);
    }

    @Override
    protected void initView() {
        showLoading();
        //RecyclerView NewsFeed
        newsFeedAdapter = new NewsFeedAdapter(mContext);
        binding.recyclerNewsFeed.setAdapter(newsFeedAdapter);
        dataImage = new ArrayList<>();
        myRef.addValueEventListener(this);
        myRefUserInfo.addValueEventListener(this);
        newsFeedAdapter.setImageListener(this);
        setListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_feeds;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getRef().equals(myRef)) {
            mPresenter.dataChange(dataImage, dataSnapshot);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }

    @Override
    public void onSuccess(Void aVoid) {
    }

    @Override
    public void onFailure(@NonNull Exception e) {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void showLoading() {
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void onHideLoading() {
        hideLoading();
    }

    //Error Update data
    @Override
    public void toNotSuccessDataChange() {
        showNotify("To Data change not Success");
    }

    //Update data when data change
    @Override
    public void toSuccessDataChange(ArrayList<Image> dataImage) {
        Collections.reverse(dataImage);
        newsFeedAdapter.setData(dataImage);
        newsFeedAdapter.notifyDataSetChanged();
    }

    @Override
    public void toImageLocation(String latitude, String longitude) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.IMAGE_LATITUDE, latitude);
        bundle.putString(Constants.IMAGE_LONGITUDE, longitude);
        getParent().showFragment(LocationReviewFragment.TAG, NewsFeedFragments.TAG, bundle);
    }
}
