package com.sam.kmamapsocial.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseActivity;
import com.sam.kmamapsocial.databinding.ActivityMainBinding;
import com.sam.kmamapsocial.dialog.LogoutDialog;
import com.sam.kmamapsocial.presenter.ImagePresenter;
import com.sam.kmamapsocial.view.event.OnImageCallBack;
import com.sam.kmamapsocial.view.fragments.AccountFragment;
import com.sam.kmamapsocial.view.fragments.ChatFragment;
import com.sam.kmamapsocial.view.fragments.LocationFragment;
import com.sam.kmamapsocial.view.fragments.NewsFeedFragments;

public class MainActivity extends BaseActivity<ImagePresenter, ActivityMainBinding>
        implements OnImageCallBack, View.OnClickListener, LogoutDialog.LogoutDialogCallBack {

    public static final String TAG = MainActivity.class.getName();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference myRefContainer = mDatabase.getReference();
        initView();
    }

    @Override
    protected ImagePresenter getPresenter() {
        return new ImagePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getContentId() {
        return R.id.frame_main_container;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.bot_ic_home:
                    fragment = new NewsFeedFragments();
                    loadFragment(fragment);
                    return true;
                case R.id.bot_ic_mylocation:
                    fragment = new LocationFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bot_ic_chat:
                    fragment = new ChatFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bot_ic_my_account:
                    fragment = new AccountFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        //Setup bottom navigation
        BottomNavigationView navigation = findViewById(R.id.main_nav_bottom);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showFragment(NewsFeedFragments.TAG, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sub_menu_logout) {
            LogoutDialog logoutDialog = new LogoutDialog(this);
            logoutDialog.setListener(this);
            logoutDialog.show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onLogOut() {
        toLogOut();
    }

    private void toLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}