package com.dji.drone.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "waypoints",
        foreignKeys = @ForeignKey(entity = MissionEntity.class,
                                    parentColumns = "id",
                                    childColumns = "mission_id",
                                    onDelete = ForeignKey.CASCADE))
public class WaypointEntity {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Double latitude;
    private Double longitude;
    private Float altitude;
    private Integer mission_id;

    public WaypointEntity(Double latitude, Double longitude, Float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public Integer getMission_id() {
        return mission_id;
    }

    public void setMission_id(Integer mission_id) {
        this.mission_id = mission_id;
    }
}
