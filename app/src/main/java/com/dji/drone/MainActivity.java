package com.dji.drone;


import android.Manifest;
import android.content.pm.PackageManager;
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

    private static final int REQUEST_PERMISSION_CODE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager.Companion.checkAndRequestPermissions(this, REQUEST_PERMISSION_CODE);
        List<String> missingPermission = PermissionManager.Companion.checkPermissions(this);
        if(missingPermission.isEmpty()){
            startSDKRegistration();
        }


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
}