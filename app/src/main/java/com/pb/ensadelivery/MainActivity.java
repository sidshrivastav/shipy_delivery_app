package com.pb.ensadelivery;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.pb.ensadelivery.models.OrderModel;
import com.pb.ensadelivery.retrofit.ApiClient;
import com.pb.ensadelivery.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RipplePulseLayout mRipplePulseLayout;
    TextView layoutGetOrders;

    private LocationUpdatesService mService = null;
    private boolean mBound = false;

    private static final String TAG = "resPMain";

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    LottieAnimationView animationView;

    DatabaseHelper db;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);
        layoutGetOrders = findViewById(R.id.layoutGetOrders);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);

        animationView.setVisibility(View.GONE);
        layoutGetOrders.setVisibility(View.VISIBLE);

        db = new DatabaseHelper(this);
        db.truncateTable();

        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }


        layoutGetOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRipplePulseLayout.startRippleAnimation();

                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);

                Call<List<OrderModel>> call = apiService.getOrders("1");

                call.enqueue(new Callback<List<OrderModel>>() {
                    @Override
                    public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {

                        animationView.setVisibility(View.VISIBLE);
                        layoutGetOrders.setVisibility(View.GONE);

                        animationView.setAnimation("check.json");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animationView.playAnimation();
                            }
                        }, 100);

                        animationView.loop(false);

                        List<OrderModel> list = response.body();
                        Log.d("","");

                        for(int i=0;i<list.size();i++){
                            db.insertOrder(list.get(i));
                        }
                        mRipplePulseLayout.stopRippleAnimation();

                        if (!checkPermissions()) {
                            requestPermissions();
                        } else {
                            mService.requestLocationUpdates();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                        Log.d("","");
                    }
                });
            }
        });

        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE);
        }

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.root),
                        "Permission required",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
