package com.sam.kmamapsocial.presenter;

import android.graphics.Bitmap;
import android.os.Environment;

import com.sam.kmamapsocial.base.BasePresenter;
import com.sam.kmamapsocial.view.event.OnLocationCallBack;

import java.io.File;
import java.io.FileOutputStream;

public class LocationPresenter extends BasePresenter<OnLocationCallBack> {

    private static final String TAG = LocationPresenter.class.getName();

    public LocationPresenter(OnLocationCallBack mCallBack) {
        super(mCallBack);
    }

    public void snapShot(Bitmap snapshot, String mPath) {
        mCallBack.showLoading();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + System.currentTimeMillis();
        File outputDir = new File(path);
        try {
            boolean isDirectoryCreated = outputDir.exists();

            if (!isDirectoryCreated) {
                isDirectoryCreated = outputDir.mkdirs();
            }
            if (isDirectoryCreated) {
                String pathFile = path + File.separator + "capture.png";
                File newFile = new File(pathFile);
                FileOutputStream out = new FileOutputStream(newFile);
                snapshot.compress(Bitmap.CompressFormat.PNG, 90, out);
                // Get File Directory
                mPath = newFile.getAbsolutePath();
                mCallBack.snapShotSuccess(mPath);
                mCallBack.hideLoading();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCallBack.hideLoading();
            mCallBack.snapShotNotSuccess();
        }
    }
}
