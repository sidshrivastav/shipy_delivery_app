package com.pb.ensadelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QRScanner extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 232;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        if (!checkPermissions()) {
            requestPermissions();
        }else {

            qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
            qrCodeReaderView.setOnQRCodeReadListener(this);

            // Use this function to enable/disable decoding
            qrCodeReaderView.setQRDecodingEnabled(true);

            // Use this function to change the autofocus interval (default is 5 secs)
            qrCodeReaderView.setAutofocusInterval(2000L);


            // Use this function to set back camera preview
            qrCodeReaderView.setBackCamera();
        }
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent intent = new Intent(this, WebView.class);
        intent.putExtra("link", text);
        startActivity(intent);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.root),
                    "Permission required",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(QRScanner.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(QRScanner.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
                qrCodeReaderView.setOnQRCodeReadListener(QRScanner.this);

                // Use this function to enable/disable decoding
                qrCodeReaderView.setQRDecodingEnabled(true);

                // Use this function to change the autofocus interval (default is 5 secs)
                qrCodeReaderView.setAutofocusInterval(2000L);


                // Use this function to set back camera preview
                qrCodeReaderView.setBackCamera();
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

}
