package com.dji.drone.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface WaypointDAO {

    @Insert
    void insert(WaypointEntity waypoint);

    @Delete
    void delete(WaypointEntity waypoint);

    @Update
    void update(WaypointEntity waypoint);
}