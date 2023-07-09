package com.dji.drone.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import dji.common.mission.waypoint.Waypoint;

@Entity(tableName = "missions")
public class MissionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private double auto_flight_speed;
    private double max_flight_speed;
    private int goto_mode;
    private int finished_action;
    private int heading_mode;
    private int flightPath_mode;

    @Ignore
    private List<WaypointEntity> waypoints;
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAuto_flight_speed() {
        return auto_flight_speed;
    }

    public void setAuto_flight_speed(double auto_flight_speed) {
        this.auto_flight_speed = auto_flight_speed;
    }

    public double getMax_flight_speed() {
        return max_flight_speed;
    }

    public void setMax_flight_speed(double max_flight_speed) {
        this.max_flight_speed = max_flight_speed;
    }

    public int getGoto_mode() {
        return goto_mode;
    }

    public void setGoto_mode(int goto_mode) {
        this.goto_mode = goto_mode;
    }

    public int getFinished_action() {
        return finished_action;
    }

    public void setFinished_action(int finished_action) {
        this.finished_action = finished_action;
    }

    public int getHeading_mode() {
        return heading_mode;
    }

    public void setHeading_mode(int heading_mode) {
        this.heading_mode = heading_mode;
    }

    public int getFlightPath_mode() {
        return flightPath_mode;
    }

    public void setFlightPath_mode(int flightPath_mode) {
        this.flightPath_mode = flightPath_mode;
    }

    public List<WaypointEntity> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointEntity> waypoints) {
        this.waypoints = waypoints;
    }
}
