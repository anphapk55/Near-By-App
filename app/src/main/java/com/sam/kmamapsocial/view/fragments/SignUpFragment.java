package com.sam.kmamapsocial.view.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragmentSignupBinding;
import com.sam.kmamapsocial.presenter.RegisterPresenter;
import com.sam.kmamapsocial.view.event.OnRegisterCallBack;

import static com.sam.kmamapsocial.utils.CommonUtils.validateEmail;
import static com.sam.kmamapsocial.utils.CommonUtils.validatePassword;

public class SignUpFragment extends BaseFragment<RegisterPresenter, FragmentSignupBinding>
        implements OnRegisterCallBack, View.OnClickListener {
    public static final String TAG = SignUpFragment.class.getName();
    public static final String EXTRA_USER = "extra.user";
    public static final String EXTRA_PASS = "extra.pass";
    private FirebaseAuth mAuth;

    @Override
    protected RegisterPresenter getPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected void initView() {
        mAuth = FirebaseAuth.getInstance();

        binding.btnSignup.setOnClickListener(this);
        binding.btnBackInSignup.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signup;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                showLoading();
                register();
                break;
            case R.id.btn_back_in_signup:
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                break;
            default:
                break;
        }
    }

    private void register() {
        try {
            final String email = binding.edtUsernameInSignup.getText().toString();
            final String password = binding.edtPasswordInSignup.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                hideLoading();
                showNotify("Register fail: Email/Password invalid");
            } else if (!validateEmail(email)) {
                showNotify("Register fail: Format email wrong");
            } else if (!validatePassword(password)) {
                showNotify("Register fail: Format password wrong");
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    showSnackBar(binding.rlContainerRegister, "Register success, you will going to login");
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_USER, email);
                                    bundle.putString(EXTRA_PASS, password);
                                    getParent().showFragment(LoginFragment.TAG, null, bundle);
                                } else {
                                    if (task.getException() != null)
                                        showNotify("Register fail: " + task.getException().getMessage());
                                }
                                hideLoading();
                            }
                        });
            }
        } catch (Exception ex) {
            showSnackBar(binding.rlContainerRegister, ex.getLocalizedMessage());
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
