package com.dji.drone.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Point2dDAO {

    @Insert
    long insert(Point2D point2d);

    @Delete
    void delete(Point2D point2d);

    @Update
    void update(Point2D point2d);

    @Query("SELECT * FROM points2d")
    LiveData<List<Point2D>> getAllPoints2d();
    
}
