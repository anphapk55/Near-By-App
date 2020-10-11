package com.sam.kmamapsocial.base;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Constructor;

public abstract class BaseActivity<T extends BasePresenter, BD extends ViewDataBinding> extends AppCompatActivity {

    protected T mPresenter;
    protected BD binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getPresenter();
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected abstract T getPresenter();

    protected abstract int getLayoutId();

    public void showFragment(String tag, String tagBackStack, Bundle bundle) {
        try {
            //injection
            Class<?> clazz = Class.forName(tag);
            Constructor<?> constructor = clazz.getConstructor();
            BaseFragment frg = (BaseFragment) constructor.newInstance();

            //Set argument
            frg.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setCustomAnimations(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);

            if (tagBackStack != null)
                ft.addToBackStack(tagBackStack);

            ft.replace(getContentId(), frg).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract int getContentId();
}
