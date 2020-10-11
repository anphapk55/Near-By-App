package com.sam.kmamapsocial.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.model.Image;
import com.sam.kmamapsocial.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public abstract class BaseFragment<T extends BasePresenter, BD extends ViewDataBinding> extends Fragment {

    public static final String TAG = BaseFragment.class.getName();
    //Firebase
    protected FirebaseStorage storage = FirebaseStorage.getInstance();
    protected StorageReference storageRef = storage.getReference();
    protected FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    protected DatabaseReference myRef = mDatabase.getReference();
    protected DatabaseReference myRefUserInfo = mDatabase.getReference();
    protected FirebaseAuth mAuth;
    protected FirebaseUser mCurrentUser;

    protected T mPresenter;
    protected BD binding;
    protected Context mContext;
    protected View mRootView;

    protected onBaseFragmentClick listener;

    protected String myAvatar = "";
    protected String myEmail;
    protected String myUid;
    protected String myDisplay;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mContext = getActivity();
        mPresenter = getPresenter();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        myRef = mDatabase.getReference("Images");
        myRefUserInfo = mDatabase.getReference("Users");

        if (mCurrentUser != null) {
            myEmail = mCurrentUser.getEmail();
            myUid = mCurrentUser.getUid();
            myDisplay = mCurrentUser.getDisplayName();
            if (mCurrentUser.getPhotoUrl() != null) {
                myAvatar = mCurrentUser.getPhotoUrl().toString();
            }
//            myRefUserInfo.child(myUid).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    if (snapshot.child("urlAvatarUser").exists())
//                    myAvatar = snapshot.child("urlAvatarUser").getValue().toString();
//                    showNotifyLong(String.valueOf());
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    if (snapshot.child("urlAvatarUser").exists())
//                    myAvatar = snapshot.child("urlAvatarUser").getValue().toString();
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }

        initView();
        return binding.getRoot();
    }

    public interface onBaseFragmentClick {
        void onHideLoading();
    }

    public void setListener(onBaseFragmentClick listener) {
        this.listener = listener;
    }

    protected abstract T getPresenter();

    protected abstract void initView();

    protected abstract int getLayoutId();

    protected void setTitle(String title) {
        Objects.requireNonNull(getActivity()).setTitle(title);
    }

    public <G extends View> G findViewById(int idView) {
        return findViewById(idView, null);
    }

    public <G extends View> G findViewById(int idView, View.OnClickListener event) {
        G view = mRootView.findViewById(idView);
        if (event != null) {
            view.setOnClickListener(event);
        }
        return view;
    }

    protected BaseActivity getParent() {
        return (BaseActivity) getActivity();
    }

    protected void showNotify(String text) {
        Toast.makeText(mContext, "" + text, Toast.LENGTH_SHORT).show();
    }

    protected void showNotify(int text) {
        Toast.makeText(mContext, "" + text, Toast.LENGTH_SHORT).show();
    }

    protected void showNotifyLong(String text) {
        Toast.makeText(mContext, "" + text, Toast.LENGTH_LONG).show();
    }

    protected void showNotifyLong(int text) {
        Toast.makeText(mContext, "" + text, Toast.LENGTH_LONG).show();
    }

    protected void showSnackBar(View view, String text) {
        Snackbar snackbar = Snackbar
                .make(view, "" + text, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    protected void showSnackBarLong(View view, String text) {
        Snackbar snackbar = Snackbar
                .make(view, "" + text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    protected void SendImageLocationStatus(final ImageView ivInputImage,
                                           final EditText edtImageName,
                                           final String latitude, final String longitude) {

        // if input editext in Share Fragment null
        if (edtImageName.getText().toString().isEmpty() || edtImageName.getText() == null) {
            showNotify("Please again.Empty image or text...");
            if (listener != null) {
                listener.onHideLoading();
            }
            return;
        }

        // Get current time
        final String date = CommonUtils.getInstance().getDate();

        Calendar calendar = Calendar.getInstance();//get time system

        // Set url to upload screenshot image
        StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");
        ivInputImage.setDrawingCacheEnabled(true);
        ivInputImage.buildDrawingCache();

        //check image error
        try {
            // Create Bitmap image from geted image
            Bitmap bitmap = ((BitmapDrawable) ivInputImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    showNotify(exception.toString());
                    if (listener != null)
                        listener.onHideLoading();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                    // Create Uri
                    uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url;
                            url = task.getResult();

                            // Set detail information of image
                            Image image = new Image();
                            image.setDate(date);
                            image.setMail(mCurrentUser.getEmail());
                            image.setUid(mCurrentUser.getUid());
                            image.setUrlAvatar(myAvatar);
                            image.setLatitude(latitude);
                            image.setLongitude(longitude);

                            if (edtImageName.getText().toString().isEmpty() || edtImageName.getText() != null) {
                                image.setName(edtImageName.getText().toString());
                            }
                            if (url == null) return;
                            image.setUrl(url.toString());

                            // Upload image and detail data to firebase
                            myRef.child("" + image.getId()).setValue(image, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        // saved success to firebase
                                        showNotify("Success to save Image");
                                        ivInputImage.setImageResource(R.drawable.ic_camera_36dp);
                                        edtImageName.setText("");
                                    } else {
                                        //saved unsuccess to firebase
                                        showNotify("Fail to save dataImage");
                                    }
                                    if (listener != null) {
                                        listener.onHideLoading();
                                    }
                                }
                            });
                        }
                    });
                }
            });

        } catch (Exception ex) {
            // Error create Bitmap image
            ex.printStackTrace();

            //Set detail information of image
            Image image = new Image();
            image.setDate(date);
            image.setUid(mCurrentUser.getUid());
            image.setMail(mCurrentUser.getEmail());
            if (edtImageName.getText().toString().isEmpty() || edtImageName.getText() != null) {
                image.setName(edtImageName.getText().toString());
            }
            // Save image and it's data to Firebase
            myRef.child("" + image.getId()).setValue(image, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        showNotify("Success to save Image");
                        edtImageName.setText("");
                    } else {
                        showNotify("Fail to save dataImage");
                    }
                }
            });
        }
    }
}
