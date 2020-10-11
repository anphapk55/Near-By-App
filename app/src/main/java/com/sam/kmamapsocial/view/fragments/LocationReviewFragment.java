package com.sam.kmamapsocial.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.base.BaseFragment;
import com.sam.kmamapsocial.databinding.FragLocationReviewBinding;
import com.sam.kmamapsocial.presenter.LocationReviewPresenter;
import com.sam.kmamapsocial.utils.Constants;
import com.sam.kmamapsocial.view.event.OnLocationReviewCallBack;

import java.util.ArrayList;

public class LocationReviewFragment extends BaseFragment<LocationReviewPresenter, FragLocationReviewBinding>
        implements OnLocationReviewCallBack, OnMapReadyCallback, LocationListener {

    public static final String TAG = LocationReviewFragment.class.getName();
    private Marker currentLocationMarker;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    ArrayList<String> arrLat = new ArrayList<>();
    ArrayList<String> arrLong = new ArrayList<>();

    private final String[] PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GoogleMap mMap;
    private View mapView;

    @Override
    protected LocationReviewPresenter getPresenter() {
        return new LocationReviewPresenter(this);
    }

    @Override
    protected void initView() {
        showLoading();
        if (checkPermission()) {
            init();
        }
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);//cho phep zoom in zoom out map len
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (getArguments() != null) {
            String latitude = getArguments().getString(Constants.IMAGE_LATITUDE, "21.024392");
            String longitude = getArguments().getString(Constants.IMAGE_LONGITUDE, "105.805197");
            LatLng lng = new LatLng(Float.parseFloat(latitude), Float.parseFloat(longitude));
            CameraPosition position = new CameraPosition(
                    lng, 15, 0, 0);

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(lng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);

            if (getArguments().getStringArrayList(Constants.IMAGE_ARRAY_POINT_LAT) != null) {
                arrLat = getArguments().getStringArrayList(Constants.IMAGE_ARRAY_POINT_LAT);
                arrLong = getArguments().getStringArrayList(Constants.IMAGE_ARRAY_POINT_LONG);
                ArrayList<LatLng> arrLatLng = new ArrayList<>();
                for (int i = 0; i < arrLat.size(); i++) {
                    LatLng latLng = new LatLng(Double.parseDouble(arrLat.get(i)), Double.parseDouble(arrLong.get(i)));
                    arrLatLng.add(latLng);
                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(5);
                    arrayPoints.add(latLng);
                    polylineOptions.addAll(arrayPoints);
                    mMap.addPolyline(polylineOptions);
                }

            }

        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermission()) {
            init();
        } else {
            getParent().showFragment(NewsFeedFragments.TAG, null, null);
        }
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_review);
        if (mapFragment != null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_location_review;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();

        //custom my location button
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 180, 180, 0);
        }


        hideLoading();

    }

    @Override
    public void onLocationChanged(Location location) {

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
}
