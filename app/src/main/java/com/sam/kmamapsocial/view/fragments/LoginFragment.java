package com.sam.kmamapsocial.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragmentLoginBinding;
import com.sam.kmamapsocial.model.UserInfo;
import com.sam.kmamapsocial.presenter.LoginPresenter;
import com.sam.kmamapsocial.view.activities.MainActivity;
import com.sam.kmamapsocial.view.event.OnLoginCallBack;

import static com.sam.kmamapsocial.utils.CommonUtils.validateEmail;
import static com.sam.kmamapsocial.utils.CommonUtils.validatePassword;

public class LoginFragment extends BaseFragment<LoginPresenter, FragmentLoginBinding> implements OnLoginCallBack, View.OnClickListener {

    public static final String TAG = LoginFragment.class.getName();
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected void initView() {
        binding.loginButton.setReadPermissions("email", "public_profile");
        binding.loginButton.setFragment(this);

        binding.btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        binding.btnSignupInLogin.setOnClickListener(this);

        loadCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getParent().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showSnackBar(binding.rlContainerLogin, "Successful");
                showLoading();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                showSnackBar(binding.rlContainerLogin, "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                showSnackBar(binding.rlContainerLogin, error.toString());
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        updateCurrentUser(user);
                        goToNewsFeed();
                    }
                } else {
                    showSnackBar(binding.rlContainerLogin, "Authentication failed.");
                }
            }
        });
    }

    private void updateCurrentUser(FirebaseUser firebaseUser) {
        //Cap nhat user voi du lieu lay duoc tu firebaseUser
        UserInfo userInfo = new UserInfo();
        userInfo.setEmailUser(firebaseUser.getEmail());
        userInfo.setUidUser(firebaseUser.getUid());
        if (firebaseUser.getPhotoUrl() != null) {
            userInfo.setUrlAvatarUser(firebaseUser.getPhotoUrl().toString());
        }

        //Luu data user vao firebase bảng User
        myRefUserInfo.child(firebaseUser.getUid()).setValue(userInfo);
    }

    private void goToNewsFeed() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        hideLoading();
        requireActivity().finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                showLoading();
                login();
                break;
            case R.id.btn_signup_in_login:
                getParent().showFragment(SignUpFragment.TAG, LoginFragment.TAG, null);
                break;
            default:
                break;
        }
    }

    private void loadCurrentUser() {

        //Kiem tra xem co data truyen tu man signup sang de fill mail vs pass vua dang ky khong
        if (getArguments() != null) {

            //lay data tu man signup
            String user = getArguments().getString(SignUpFragment.EXTRA_USER, null);
            String password = getArguments().getString(SignUpFragment.EXTRA_PASS, null);

            //Neu user/password != null
            if (password != null && user != null) {

                //set text vao edittext
                binding.edtLoginUsername.setText(getArguments().getString(SignUpFragment.EXTRA_USER));
                binding.edtLoginPassword.setText(getArguments().getString(SignUpFragment.EXTRA_PASS));

                //update current user
                if (mCurrentUser != null) updateCurrentUser(mCurrentUser);
            }
        } else if (mCurrentUser != null) goToNewsFeed(); // Neu ton tai current user -> chuyen den man newsfeed luon
    }

    private void login() {
        try {
            String email = binding.edtLoginUsername.getText().toString();
            String password = binding.edtLoginPassword.getText().toString();

            //Neu email va password empty hoac rong
            if (email.isEmpty() || password.isEmpty()) {
                hideLoading();
                showNotify("Login fail: Email/Password invalid");
            } else if (!validateEmail(email)) {
                //Neu khac dinh dang email -> duoc quy dinh boi regex email
                // ^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,6}$ -> [tu A-Z tu 0 - 9] + @ [tu A-Z tu 0 - 9].[tu A-Z tu 0 - 9 voi 2 - 6 ky tu]
                showNotify("Login fail: Format email wrong");
            } else if (!validatePassword(password)) {
                //Neu khac dinh dang password -> duoc quy dinh boi regex password
                //^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$ -> 8 ky tu yeu cau chu so, chu hoa, chu thuong, ky tu dac biet
                showNotify("Login fail: Format password wrong");
            } else {
                //Check dang nhap voi data tren firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Neu login thanh cong
                                if (task.isSuccessful()) {
                                    showSnackBar(binding.rlContainerLogin, "Login success");
                                    goToNewsFeed();
                                } else {
                                    // Login that bai -> show loi
                                    showSnackBar(binding.rlContainerLogin, "Login fail: " + task.getException().getMessage());
                                }
                                hideLoading();

                            }
                        });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            hideLoading();
        }
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
