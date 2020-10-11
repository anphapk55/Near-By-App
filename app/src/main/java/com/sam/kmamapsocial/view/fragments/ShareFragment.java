package com.sam.kmamapsocial.view.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.bumptech.glide.Glide;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragShareBinding;
import com.sam.kmamapsocial.presenter.SharePresenter;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.view.event.OnShareCallBack;

import java.util.ArrayList;

public class ShareFragment extends BaseFragment<SharePresenter, FragShareBinding> implements OnShareCallBack,
        View.OnClickListener, BaseFragment.onBaseFragmentClick {

    public static final String TAG = ShareFragment.class.getName();
    private String imgFilePath;
    private String latitude = "0";
    private String longitude = "0";

//    private ArrayList<String> arrayPointLat;
//    private ArrayList<String> arrayPointLong;

    @Override
    protected SharePresenter getPresenter() {
        return new SharePresenter(this);
    }

    @Override
    protected void initView() {
        setTitle("Share Location");

//        arrayPointLat = new ArrayList<>();
//        arrayPointLong = new ArrayList<>();

        if (getArguments() != null) {
            imgFilePath = getArguments().getString(Constants.PATH_IMAGE, "");
            latitude = getArguments().getString(Constants.LATITUDE_ME, "");
            longitude = getArguments().getString(Constants.LONGITUDE_ME, "");
//            arrayPointLat = getArguments().getStringArrayList(Constants.ARRAY_POINT_LAT);
//            arrayPointLong = getArguments().getStringArrayList(Constants.ARRAY_POINT_LONG);
        }

        //Born bitmap image from screenshot image and show
        if (imgFilePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFilePath);
            Glide.with(mContext)
                    .load(myBitmap)
                    .centerCrop()
                    .into(binding.ivShareImage);

            binding.tvLatitude.setText(latitude);
            binding.tvLongitude.setText(longitude);
        }

        binding.btnShare.setOnClickListener(this);
        setListener(this);
        hideLoading();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_share;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                showLoading();
                SendImageLocationStatus(
                        binding.ivShareImage,
                        binding.edtInput, latitude, longitude);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHideLoading() {
        hideLoading();
        getParent().showFragment(NewsFeedFragments.TAG, null, null);
    }
}
