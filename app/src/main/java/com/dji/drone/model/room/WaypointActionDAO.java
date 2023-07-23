package com.dji.drone.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void delete(WaypointActionEntity waypointAction);
}