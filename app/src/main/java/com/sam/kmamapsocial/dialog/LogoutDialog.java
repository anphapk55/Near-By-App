package com.sam.kmamapsocial.dialog;

import android.content.Context;
import android.view.View;

import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseDialog;
import com.sam.kmamapsocial.databinding.DialogLogOutBinding;

public class LogoutDialog extends BaseDialog<DialogLogOutBinding> {

    private LogoutDialogCallBack listener;

    public void setListener(LogoutDialogCallBack listener) {
        this.listener = listener;
    }

    public LogoutDialog(Context context) {
        super(context);
    }

    @Override
    protected void initViews() {
        binding.tvYes.setOnClickListener(this);
        binding.tvNo.setOnClickListener(this);
        binding.ivClose.setOnClickListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_log_out;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yes:
                if (listener != null) {
                    listener.onLogOut();
                    dismiss();
                }
                break;
            case R.id.iv_close:
            case R.id.tv_no:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface LogoutDialogCallBack {
        void onLogOut();
    }

}
