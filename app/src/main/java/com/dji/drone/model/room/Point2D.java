package com.dji.drone.model.room;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "points2d",
        foreignKeys = @ForeignKey(entity = MissionEntity.class,
                parentColumns = "id",
                childColumns = "mission_id",
                onDelete = ForeignKey.CASCADE),
        indices = @Index(name = "mission_id_point2d", value = "mission_id"))
public class Point2D {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private double latitude;
    private double longitude;
    private int mission_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMission_id() {
        return mission_id;
    }

    public void setMission_id(int mission_id) {
        this.mission_id = mission_id;
    }
}
