package com.dji.drone.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "waypoints",
        foreignKeys = @ForeignKey(entity = MissionEntity.class,
                                    parentColumns = "id",
                                    childColumns = "mission_id",
                                    onDelete = ForeignKey.CASCADE),
        indices = @Index(name = "mission_id", unique = true, value = "mission_id"))
public class WaypointEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private double latitude;
    private double longitude;
    private Float altitude;
    private int mission_id;

    @Ignore
    private List<WaypointActionEntity> actions;

    public WaypointEntity(double latitude, double longitude, Float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

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

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public int getMission_id() {
        return mission_id;
    }

    public void setMission_id(int mission_id) {
        this.mission_id = mission_id;
    }

    public List<WaypointActionEntity> getActions() {
        return actions;
    }

    public void setActions(List<WaypointActionEntity> actions) {
        this.actions = actions;
    }
}
