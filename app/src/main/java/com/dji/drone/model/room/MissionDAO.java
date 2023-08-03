package com.dji.drone.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MissionDAO {

    @Insert
    Long insert(MissionEntity mission);

    @Delete
    Integer delete(MissionEntity mission);

    @Update
    Integer update(MissionEntity mission);

    @Query("SELECT * from missions where id = :mission_id")
    MissionEntity getMission(int mission_id);

    @Query("SELECT * from missions")
    List<MissionEntity> getAll();

    @Query("SELECT * from points2d where points2d.mission_id = :mission_id")
    List<Point2D> getAllPoint2d(int mission_id);

}