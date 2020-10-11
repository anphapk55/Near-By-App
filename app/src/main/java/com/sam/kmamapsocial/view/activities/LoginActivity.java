package com.sam.kmamapsocial.view.activities;

import android.os.Bundle;

import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseActivity;
import com.sam.kmamapsocial.databinding.ActivityLoginBinding;
import com.sam.kmamapsocial.presenter.LoginPresenter;
import com.sam.kmamapsocial.view.event.OnLoginCallBack;
import com.sam.kmamapsocial.view.fragments.LoginFragment;

public class LoginActivity extends BaseActivity<LoginPresenter, ActivityLoginBinding> implements OnLoginCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        showFragment(LoginFragment.TAG, null, null);
    }

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected int getContentId() {
        return R.id.rlContainerLogin;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}