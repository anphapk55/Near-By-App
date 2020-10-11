package com.sam.kmamapsocial.view.fragments;

import android.view.View;

import com.bumptech.glide.Glide;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragImageReviewBinding;
import com.sam.kmamapsocial.presenter.ImageReviewPresenter;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.view.event.OnImageReviewCallBack;

public class FragmentImageReview extends BaseFragment<ImageReviewPresenter, FragImageReviewBinding> implements OnImageReviewCallBack {

    public static final String TAG = FragmentImageReview.class.getName();

    @Override
    protected ImageReviewPresenter getPresenter() {
        return new ImageReviewPresenter(this);
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            String imageUrl = getArguments().getString(Constants.NEWS_FEED_IMAGE);
            Glide.with(mContext)
                    .load(imageUrl)
                    .into(binding.ivImage);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_image_review;
    }

    @Override
    public void showLoading() {
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.spinKit.setVisibility(View.GONE);
    }
}
