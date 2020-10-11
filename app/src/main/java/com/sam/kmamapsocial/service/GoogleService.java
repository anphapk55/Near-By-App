package com.sam.kmamapsocial.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.sam.kmamapsocial.R;
import com.sam.kmamapsocial.view.activities.MainActivity;
import com.sam.kmamapsocial.view.fragments.AccountFragment;

import java.util.Timer;
import java.util.TimerTask;

import static com.sam.kmamapsocial.view.App.CHANNEL_ID;

public class GoogleService extends Service implements LocationListener {
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer;
    long notify_interval = 1000;
    public static String str_receiver = "com.sam.kmamapsocial";
    Intent intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(AccountFragment.START_FOREGOUND_SERVICE)) {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Phát hiện em: Đặng Thu Thuỷ")
                    .setContentText("Da thu hoi 1 tin nhan, ban co mu'n xem khong ?")
                    .setSmallIcon(R.drawable.ic_baseline_chat_24)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

        } else if (intent.getAction().equals(AccountFragment.STOP_FOREGOUND_SERVICE)) {
            mTimer.cancel();
            mTimer.purge();
            stopForeground(true);
            stopSelfResult(startId);
            Log.i("Stop Service", "stop Service !");
            stopSelf();
            onDestroy();
        }
        return START_NOT_STICKY;
    }

    public GoogleService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent(str_receiver);
        Log.i("On Create", "service onCreate !");
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

    private void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
        } else {
            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
//                        Log.i("LatByNetwork : ", location.getLatitude() + "");
//                        Log.i("LngByNetwork : ", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        sendLocation(location);
                    }
                }

            }
            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
//                        Log.e("LatByGPS : ", location.getLatitude() + "");
//                        Log.e("LngByGPS : ", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        sendLocation(location);
                    }
                }
            }
        }
    }

    private void fn_setNotificationDetail(Location location) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("KMA Nearby")
                .setContentText("Realtime position sharing enabled," + "Latlng : " + location.getLatitude())
                .setSmallIcon(R.drawable.icon_favorite_black)
                .build();
        startForeground(1, notification);
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getLocation();
                }
            });
        }
    }

    private void sendLocation(Location location) {
        intent.putExtra("latutide", location.getLatitude() + "");
        intent.putExtra("longitude", location.getLongitude() + "");
        sendBroadcast(intent);
    }
}
