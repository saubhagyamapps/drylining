package com.app.drylining;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.ui.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.quickblox.auth.session.QBSettings;

import java.util.ArrayList;
import java.util.HashMap;

import lal.adhish.gifprogressbar.GifView;

public class SplashActivity extends Activity implements DialogInterface.OnClickListener {

    CountDownTimer lTimer;
    boolean isTablet;
    private ApplicationData appData;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1111;

    static final String APP_ID = "69976";
    static final String AUTH_KEY = "KDtgekHTPepMj6D";
    static final String AUTH_SECRET = "jWW9c3AtJZ-bQRO";
    static final String ACCOUNT_KEY = "6Z4aqN3Lvm2LhhpPRDAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        appData = ApplicationData.getSharedInstance();
        GifView pGif = (GifView) findViewById(R.id.progressBar);

        pGif.setImageResource(R.drawable.loader);
        lTimer = new CountDownTimer(AppConstant.SPLASH_SCREEN_TIMEOUT, 5000) {
            public void onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions();
                } else {
                    closeScreen();
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    private void closeScreen() {
        Intent lIntent = new Intent();
        AppDebugLog.println("userType is " + appData.getUserType());
        lIntent.setClass(this, MainActivity.class);
        startActivity(lIntent);
        this.finish();
    }

    /**
     * Created by Nasir on 01/06/16.
     */
    @TargetApi(23)
    private void requestPermissions() {
        Context context = ApplicationData.getSharedInstance().getAppcontext();
        ArrayList<String> requiredPermissions = new ArrayList<String>();

        String[] allPermissions = {
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.VIBRATE,
                android.Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
        };

        for (String permission : allPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        if (requiredPermissions.size() > 0) {
            requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

        } else {
            closeScreen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AppDebugLog.println("In onRequestPermissionsResult : " + requestCode + " : " + permissions.length + " : " + grantResults.length);

        boolean isAllPermissionGranted = true;

        HashMap<String, Integer> perms = new HashMap<String, Integer>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
        for (String permission : perms.keySet()) {
            int result = perms.get(permission);

            String resultStr = "";
            if (result == PackageManager.PERMISSION_GRANTED) {
                resultStr = "GRANTED";
            } else {
                resultStr = "DENIED";
                isAllPermissionGranted = false;
                break;
            }
            AppDebugLog.println("Permission & result In onRequestPermissionsResult : " + resultStr + " : " + permission);
        }
        AppDebugLog.println("isAllPermissionGranted In onRequestPermissionsResult : " +
                isAllPermissionGranted);
        if (isAllPermissionGranted) {
            closeScreen();
        } else {
            ApplicationData.getSharedInstance().showConfirmationAlert(this, getString(R.string.alert_title_message), getString(R.string.alert_body_permission_not_granted),
                    getString(R.string.btn_retry), getString(R.string.btn_cancel), this, this);
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            requestPermissions();
        }
    }
}
