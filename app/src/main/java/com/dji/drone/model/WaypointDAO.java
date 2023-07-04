package com.dji.drone.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WaypointDAO {

    @Insert
    void insert(WaypointEntity waypoint);

    @Delete
    void delete(WaypointEntity waypoint);

    @Update
    void update(WaypointEntity waypoint);

    @Query("SELECT * from waypoints WHERE mission_id=:mission_id")
    List<WaypointEntity> getWaypoints(int mission_id);
}