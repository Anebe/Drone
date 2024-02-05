package com.dji.drone;

import android.app.Application;
import android.content.Context;

import com.secneo.sdk.Helper;

public class MainApplication extends Application {

    private final String TAG = getClass().getName();

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MainApplication.this);
    }
}
