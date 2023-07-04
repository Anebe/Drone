package com.dji.drone.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.dji.drone.R;
import com.dji.drone.Util;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.thirdparty.afinal.core.AsyncTask;

public class MainActivity extends AppCompatActivity implements DJISDKManager.SDKManagerCallback{
    private final String TAG = getClass().getName();

    private List<String> missingPermission = new ArrayList<>();
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
            Manifest.permission.READ_PHONE_STATE};


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
        Util.showToast( "registering, pls wait...", this);
        DJISDKManager.getInstance().registerApp(MainActivity.this, MainActivity.this);
    }
    //CALLBACK DJI-SDK-MANAGER-----------------------------------
    @Override
    public void onRegister(DJIError djiError) {
        if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
            Util.showToast("Register Success", this);
            startActivity(new Intent(this, MissionActivity.class));
        } else {
            Util.showToast("Register fails: " + djiError.getDescription() + " " + djiError.getErrorCode(), this);
        }
        Log.d(TAG, djiError.getDescription());
    }

    @Override
    public void onProductDisconnect() {
        Util.showToast("Product Disconnect", this);
    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {
        Util.showToast("Product Connect", this);
    }

    @Override
    public void onProductChanged(BaseProduct baseProduct) {
        Util.showToast("Product Changed", this);
    }

    @Override
    public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
        Util.showToast("Component Product Changed", this);
    }

    @Override
    public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

    }

    @Override
    public void onDatabaseDownloadProgress(long l, long l1) {

    }


}