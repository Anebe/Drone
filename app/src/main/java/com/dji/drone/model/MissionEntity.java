package com.dji.drone.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;

@Entity(tableName = "missions")
public class MissionEntity {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;
    private Double auto_flight_speed;
    private Double max_flight_speed;
    private Integer goto_mode;
    private Integer finished_action;
    private Integer heading_mode;
    private Integer flightPath_mode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAuto_flight_speed() {
        return auto_flight_speed;
    }

    public void setAuto_flight_speed(Double auto_flight_speed) {
        this.auto_flight_speed = auto_flight_speed;
    }

    public Double getMax_flight_speed() {
        return max_flight_speed;
    }

    public void setMax_flight_speed(Double max_flight_speed) {
        this.max_flight_speed = max_flight_speed;
    }

    public Integer getGoto_mode() {
        return goto_mode;
    }

    public void setGoto_mode(Integer goto_mode) {
        this.goto_mode = goto_mode;
    }

    public Integer getFinished_action() {
        return finished_action;
    }

    public void setFinished_action(Integer finished_action) {
        this.finished_action = finished_action;
    }

    public Integer getHeading_mode() {
        return heading_mode;
    }

    public void setHeading_mode(Integer heading_mode) {
        this.heading_mode = heading_mode;
    }

    public Integer getFlightPath_mode() {
        return flightPath_mode;
    }

    public void setFlightPath_mode(Integer flightPath_mode) {
        this.flightPath_mode = flightPath_mode;
    }
}
