package com.sam.kmamapsocial.base;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseDialog<BD extends ViewDataBinding> extends Dialog implements View.OnClickListener {

    protected BD binding;

    public BaseDialog(Context context) {
        super(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, getLayoutDialog(), null, false);
        setContentView(binding.getRoot());
        initViews();
    }

    protected abstract void initViews();

    protected abstract int getLayoutDialog();
}
