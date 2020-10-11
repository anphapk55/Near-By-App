package com.sam.kmamapsocial.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragmentMyLocationBinding;
import com.sam.kmamapsocial.presenter.LocationPresenter;
import com.sam.kmamapsocial.utils.CommonUtils;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.utils.PrefUtil;
import com.sam.kmamapsocial.view.event.OnLocationCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationFragment extends BaseFragment<LocationPresenter, FragmentMyLocationBinding> implements OnLocationCallBack,
        OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener, ValueEventListener {

    public static final String TAG = LocationFragment.class.getName();
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    private GoogleMap mMap;
    private Geocoder geocoder;
    private Circle circle;
    private Marker marker;
    private Marker currentLocationMarker;
    private ArrayList<LatLng> arrayPoints;
    private ArrayList<String> arrayPointsLat;
    private ArrayList<String> arrayPointsLong;
    Map<String, Marker> mNamedMarkers = new HashMap<String, Marker>();
    LatLng currentLng;

    private final String[] PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected LocationPresenter getPresenter() {
        return new LocationPresenter(this);
    }

    @Override
    protected void initView() {
        setTitle("Location");
        if (!CommonUtils.getInstance().isLocationEnabled(mContext)) {
            showNotifyLong(mContext.getString(R.string.you_need_turn_on_gps));
        }
        if (checkPermission() && checkPermissionStorage()) {
            init();
        }
        binding.ivScreenShot.setOnClickListener(this);
        arrayPoints = new ArrayList<>();
        arrayPointsLat = new ArrayList<>();
        arrayPointsLong = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_location;
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

    private boolean checkPermissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : PERMISSION_STORAGE) {
                if (ActivityCompat.checkSelfPermission(mContext, s) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSION_STORAGE, 0);
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
            init();
        } else if (checkPermissionStorage()) {
            CaptureMapScreen();
        } else {
            showNotify("You need agree permission");
            getParent().showFragment(NewsFeedFragments.TAG, null, null);
        }
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (manager != null) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, 1, this);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1, 1, this);
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        if (mMap != null) mMap.clear();
        myRefUserInfo.child(myUid).child("totalRunUser").addValueEventListener(this);
        databaseReference.addChildEventListener(markerUpdateListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(mContext);
        initMap();
        hideLoading();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition position = new CameraPosition(currentLng, 15, 0, 0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

        if (circle != null) circle.remove();

        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(300)
                .strokeColor(Color.rgb(90, 24, 154))
                .fillColor(Color.argb(35, 90, 24, 154)).strokeWidth(1)
        );
        if (currentLocationMarker != null) currentLocationMarker.remove();

        //Set data on list
        setDataUpdate(location);
    }

    private void setDataUpdate(Location location) {
        LatLng currentLng = new LatLng(location.getLatitude(), location.getLongitude());

        arrayPointsLat.add(String.valueOf(location.getLatitude()));
        arrayPointsLong.add(String.valueOf(location.getLongitude()));

        MarkerOptions marker = new MarkerOptions();
        marker.position(currentLng);
        currentLocationMarker = mMap.addMarker(marker);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.parseColor("#6A1B9A"));
        polylineOptions.width(5);
        arrayPoints.add(currentLng);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivScreenShot:
                CaptureMapScreen();
                break;
            default:
                break;
        }
    }

    public void CaptureMapScreen() {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                String mPath = "";
                mPresenter.snapShot(snapshot, mPath);
            }
        };
        mMap.snapshot(callback);
    }

    @Override
    public void snapShotSuccess(String mPath) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PATH_IMAGE, "" + mPath);
        bundle.putString(Constants.LATITUDE_ME, "" + currentLng.latitude);
        bundle.putString(Constants.LONGITUDE_ME, "" + currentLng.longitude);
//        bundle.putStringArrayList(Constants.ARRAY_POINT_LAT, arrayPointsLat);
//        bundle.putStringArrayList(Constants.ARRAY_POINT_LONG, arrayPointsLong);
        getParent().showFragment(ShareFragment.TAG, LocationFragment.TAG, bundle);
    }

    @Override
    public void snapShotNotSuccess() {
        showNotify("snapShotNotSuccess");
    }

    ChildEventListener markerUpdateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (snapshot.child("realtimeLng").exists() && snapshot.child("realtimeLat").exists()
                    && snapshot.child("emailUser").exists() && !snapshot.child("emailUser").getValue().equals(mCurrentUser.getEmail())
                    && snapshot.child("sharing").exists()
                    && snapshot.child("sharing").getValue(Boolean.class)) {

                String key = snapshot.getKey();

                Double lng = snapshot.child("realtimeLng").getValue(Double.class);
                Double lat = snapshot.child("realtimeLat").getValue(Double.class);
                String title = snapshot.child("emailUser").getValue(String.class);
                LatLng location = new LatLng(lat, lng);

                Marker marker = mNamedMarkers.get(key);

                if (marker == null) {
                    MarkerOptions options = getMarkerOptions(key, title, location);
                    marker = mMap.addMarker(options.position(location));
                    mNamedMarkers.put(key, marker);
                } else {
                    marker.setPosition(location);
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (snapshot.child("realtimeLng").exists() && snapshot.child("realtimeLat").exists()
                    && snapshot.child("emailUser").exists() && !snapshot.child("emailUser").getValue().equals(mCurrentUser.getEmail())
                    && snapshot.child("sharing").exists()) {

                String key = snapshot.getKey();
                Marker marker = mNamedMarkers.get(key);

                if (snapshot.child("sharing").getValue(Boolean.class)) {
                    Log.d(TAG, "Location for '" + key + "' was updated.");
                    Double lng = snapshot.child("realtimeLng").getValue(Double.class);
                    Double lat = snapshot.child("realtimeLat").getValue(Double.class);
                    String title = snapshot.child("emailUser").getValue(String.class);
                    LatLng location = new LatLng(lat, lng);

                    if (marker == null) {
                        MarkerOptions options = getMarkerOptions(key, title, location);
                        marker = mMap.addMarker(options.position(location));
                        mNamedMarkers.put(key, marker);
                    } else {
                        marker.setPosition(location);
                    }
                } else {
                    if (marker != null)
                        mNamedMarkers.remove(key);
                    marker.remove();
                }

            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            String key = snapshot.getKey();
            Log.d(TAG, "Location as '" + key + "' was removed.");
            Marker marker = mNamedMarkers.get(key);
            if (marker != null){
                mNamedMarkers.remove(key);
                marker.remove();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.d(TAG, "Priority for " + snapshot.getKey() + " was changed.");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(mContext, "Failed to load location markers.", Toast.LENGTH_SHORT).show();
        }
    };

    private MarkerOptions getMarkerOptions(String key, String title, LatLng location) {
        return new MarkerOptions().title(title).snippet(getAddressNameByPosition(location)).
                icon(bitmapDescriptorFromVector(mContext, R.drawable.icon_other_people));
    }

    //Create Bitmap image from vector image
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 120, 120);
        Bitmap bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getAddressNameByPosition(LatLng lng) {
        try {
            List<Address> arr = geocoder.getFromLocation(lng.latitude, lng.longitude, 1);
            String address = "";
            if (arr.size() > 0) address = arr.get(0).getAddressLine(0);
            return address;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    // Draw maker with custom data
    private Marker drawMarker(String title, String snippet, float hue, LatLng lng) {
        MarkerOptions options = new MarkerOptions();
        options.position(lng);
        options.icon(BitmapDescriptorFactory.defaultMarker(hue));
        options.title(title);
        options.snippet(snippet);
        return mMap.addMarker(options);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (marker != null) marker.remove();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (marker != null) marker.remove();
        String address = getAddressNameByPosition(latLng);
        marker = drawMarker("Current Pin", address, BitmapDescriptorFactory.HUE_CYAN, latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void showLoading() {
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

}
