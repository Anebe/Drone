package com.dji.drone.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MissionDAO {

    @Insert
    void insert(MissionEntity mission);

    @Delete
    void delete(MissionEntity mission);

    @Update
    void update(MissionEntity mission);

    @Query("SELECT * from missions where id = :mission_id")
    MissionEntity getMission(int mission_id);

    @Query("SELECT * from missions")
    List<MissionEntity> getAll();
}