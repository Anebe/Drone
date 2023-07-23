package com.dji.drone;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.secneo.sdk.Helper;

public class MainApplication extends Application {

    private String TAG = getClass().getName();

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MainApplication.this);
    }
}
