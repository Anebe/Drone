package com.dji.drone.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WaypointActionDAO {
    @Insert
    void insert(WaypointActionEntity waypointAction);

    @Update
    void update(WaypointActionEntity waypointAction);
    @Query("SELECT * from waypoint_actions WHERE waypoint_id=:waypoint_id")
    List<WaypointActionEntity> getWaypointActions(int waypoint_id);
}