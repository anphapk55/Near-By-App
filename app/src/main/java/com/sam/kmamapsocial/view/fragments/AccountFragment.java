package com.sam.kmamapsocial.view.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragmentAccountBinding;
import com.sam.kmamapsocial.dialog.LogoutDialog;
import com.sam.kmamapsocial.model.UserInfo;
import com.sam.kmamapsocial.presenter.AccountPresenter;
import com.sam.kmamapsocial.service.GoogleService;
import com.sam.kmamapsocial.utils.CommonUtils;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.utils.PrefUtil;
import com.sam.kmamapsocial.view.activities.LoginActivity;
import com.sam.kmamapsocial.view.event.OnAccountCallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountFragment extends BaseFragment<AccountPresenter, FragmentAccountBinding>
        implements OnAccountCallBack, ValueEventListener, View.OnClickListener, LogoutDialog.LogoutDialogCallBack {

    public static final String TAG = AccountFragment.class.getName();
    public static String STOP_FOREGOUND_SERVICE = "StopForegroundService";
    public static String START_FOREGOUND_SERVICE = "StartForegroundService";

    private ArrayList<UserInfo> dataUserInfo;
    private String mailInfo = "";
    private String uidInfo = "";
    private String urlAvatar = "";

    Float latitude, longitude;
    Geocoder geocoder;
    private final String[] PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected AccountPresenter getPresenter() {
        return new AccountPresenter(this);
    }

    @Override
    protected void initView() {
        if (getArguments() == null) {
            myAvatar = PrefUtil.getString(mContext, Constants.MY_AVATAR_URL, "");
            mailInfo = mCurrentUser.getEmail();
            uidInfo = mCurrentUser.getUid();
            urlAvatar = myAvatar;
        } else {
            mailInfo = getArguments().getString(Constants.USER_MAIL_DIFF, "");
            uidInfo = getArguments().getString(Constants.USER_UID_DIFF, "");
            urlAvatar = getArguments().getString(Constants.USER_AVATAR_URL_DIFF, "");
        }
        dataUserInfo = new ArrayList<>();
        myRefUserInfo.addValueEventListener(this);
        binding.btnPostStatus.setOnClickListener(this);
        if (myEmail.equals(mailInfo)) {
            binding.btnPostStatus.setVisibility(View.VISIBLE);
        }
        binding.ivAvatar.setOnClickListener(this);
        binding.btnPostStatus.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
        binding.btnSetAvatar.setOnClickListener(this);
        geocoder = new Geocoder(mContext, Locale.getDefault());

        if (isMyServiceRunning(GoogleService.class)) {
            binding.btnSwitch.setChecked(true);
        } else {
            binding.btnSwitch.setChecked(false);
        }
        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!CommonUtils.getInstance().isLocationEnabled(mContext)) {
                        Toast.makeText(mContext, "You need enable GPS to use it !", Toast.LENGTH_SHORT).show();
                    }
                    if (checkPermission()) {
                        runService();
                    }
                } else {
                    stopService();
                }
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : PERMISSION) {
                if (ActivityCompat.checkSelfPermission(mContext, s) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSION, 0);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermission()) {
            runService();
            Toast.makeText(mContext, "Permission be allowed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "You need agree permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void runService() {
        myRefUserInfo.child(myUid).child("sharing").setValue(true);
        Intent startIntent = new Intent(getActivity().getBaseContext(), GoogleService.class);
        startIntent.setAction(START_FOREGOUND_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startService(startIntent);
        }
    }

    private void stopService() {
        myRefUserInfo.child(myUid).child("sharing").setValue(false);
        Intent stopIntent = new Intent(getActivity().getBaseContext(), GoogleService.class);
        stopIntent.setAction(STOP_FOREGOUND_SERVICE);
        getActivity().startService(stopIntent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = Float.valueOf(intent.getStringExtra("latutide"));
            longitude = Float.valueOf(intent.getStringExtra("longitude"));
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String cityName = addresses.get(0).getAdminArea();
                String adress = addresses.get(0).getFeatureName() + " , " + addresses.get(0).getSubAdminArea();
                String countryName = addresses.get(0).getCountryName();
                binding.tvAdress.setText(adress);
                binding.tvCity.setText(cityName);
                binding.tvCountry.setText(countryName);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            binding.tvLat.setText("Latitude : " + latitude);
            binding.tvLong.setText("Longitude  : " + longitude);
            myRefUserInfo.child(myUid).child("realtimeLat").setValue(latitude);
            myRefUserInfo.child(myUid).child("realtimeLng").setValue(longitude);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post_status:
                showNotify("Clicked Post Status");
                break;
            case R.id.btn_set_avatar:
                showNotify("Clicked Change Avatar");
                break;
            case R.id.iv_avatar:
                Bundle bundleAvatar = new Bundle();
                bundleAvatar.putString(Constants.NEWS_FEED_IMAGE, urlAvatar);
                getParent().showFragment(FragmentImageReview.TAG, AccountFragment.TAG, bundleAvatar);
                break;
            case R.id.btn_logout:
                LogoutDialog logoutDialog = new LogoutDialog(getContext());
                logoutDialog.setListener(this);
                logoutDialog.show();
                break;
            default:
                break;
        }
    }
    @Override
    public void onLogOut() {
        stopService();
        logout();
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        mPresenter.dataUserInfo(dataUserInfo, snapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    @Override
    public void toSuccessDataChange(ArrayList<UserInfo> dataUserInfo) {
        for (UserInfo i : dataUserInfo) {
            if (mailInfo.equals(i.getEmailUser())) {
                Glide.with(mContext)
                        .load(i.getUrlAvatarUser()).placeholder(R.drawable.bg_profile_avatar)
                        .circleCrop()
                        .into(binding.ivAvatar);
//                binding.accountName.setText(i.getEmailUser());
                binding.accountName.setText(CommonUtils.getInstance().splitEmail(i.getEmailUser()));
            }
        }
    }

    @Override
    public void toNotSuccessDataChange() {
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