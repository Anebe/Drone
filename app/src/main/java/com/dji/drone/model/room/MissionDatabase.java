package com.dji.drone.model.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        MissionEntity.class,
        WaypointEntity.class,
        WaypointActionEntity.class,
        Point2D.class},
        version = 9)
public abstract class MissionDatabase extends RoomDatabase {

    private static MissionDatabase instance;

    public static synchronized MissionDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MissionDatabase.class, "mission_database")
                    .fallbackToDestructiveMigration()//.allowMainThreadQueries()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract MissionDAO getMissionDAO();
    public abstract WaypointDAO getWaypointDAO();
    public abstract WaypointActionDAO getWaypointActionDAO();
    public abstract Point2dDAO getPoint2dDAO();

    private static RoomDatabase.Callback roomCallback = new Callback() {

        private void add(){
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                MissionEntity mission = new MissionEntity();
                mission.setName("Missao 1");
                instance.getMissionDAO().insert(mission);
            });
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            add();
        }


        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);

            add();
        }
    };
}