package com.dji.drone;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.secneo.sdk.Helper;

public class MainApplication extends Application {

    private String TAG = getClass().getName();
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MainApplication.this);

        //createDataBase();
    }

    private void createDataBase() {
        try {
            sqLiteDatabase = openOrCreateDatabase("missions", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS missions( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome VARCHAR, " +
                    "auto_flight_speed DECIMAL, " +
                    "max_flight_speed DECIMAL, " +
                    "goto_mode INTEGER, " +
                    "finished_action INTEGER, " +
                    "heading_mode INTEGER, " +
                    "flightPath_mode INTEGER)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS waypoints(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " latitude DECIMAL, " +
                    " longetude DECIMAL, " +
                    " altitude DECIMAL, " +
                    " mission_id INTEGER, " +
                    " FOREIGN KEY(mission_id) REFERENCES missions(id))");
            sqLiteDatabase.close();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }


}
