package com.dji.drone;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity{
    private final String TAG = getClass().getName();

    private final List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();
    }

    //PERMISSION-------------------------------------------------
    public void checkAndRequestPermissions() {
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        if (!missingPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }else{
            startSDKRegistration();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        String msg;
        if(missingPermission.isEmpty()){
            msg = "All permissions granted";
            startSDKRegistration();
        }else{
            msg = "Some permissions not granted: ";

        }
        Log.d(TAG, msg);
    }
    //DRONE------------------------------------------------------
    private void startSDKRegistration() {
        DJISDKManager.getInstance().registerApp(MainActivity.this, new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError djiError) {
                if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                    Log.d(TAG, "Register Success");
                } else {
                    Log.d(TAG, "Register fails: " + djiError.getDescription() + " " + djiError.getErrorCode());
                }
            }

            @Override
            public void onProductDisconnect() {
                Log.d(TAG, "Product Disconnect");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                Log.d(TAG, "Product Connect");
            }

            @Override
            public void onProductChanged(BaseProduct baseProduct) {
                Log.d(TAG, "Product Changed");
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                Log.d(TAG, "Component Product Changed");
            }

            @Override
            public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

            }

            @Override
            public void onDatabaseDownloadProgress(long l, long l1) {

            }
        });
    }
    //CALLBACK DJI-SDK-MANAGER-----------------------------------



}