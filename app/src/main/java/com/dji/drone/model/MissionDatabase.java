package com.dji.drone.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { MissionEntity.class, WaypointEntity.class, WaypointActionEntity.class },
        version = 1)
public abstract class MissionDatabase extends RoomDatabase {

    public abstract MissionDAO getMissionDAO();
    public abstract WaypointDAO getWaypointDAO();
    public abstract WaypointActionDAO getWaypointActionDAO();

}