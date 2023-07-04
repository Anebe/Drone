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

    private List<Waypoint> waypoints;
}
