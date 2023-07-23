package com.dji.drone.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "missions")
public class MissionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    @ColumnInfo(defaultValue = "5.0")
    private float auto_flight_speed;
    @ColumnInfo(defaultValue = "10.0")
    private float max_flight_speed;
    @ColumnInfo(defaultValue = "0")
    private int goto_mode;
    @ColumnInfo(defaultValue = "1")
    private int finished_action;
    @ColumnInfo(defaultValue = "4")
    private int heading_mode;
    @ColumnInfo(defaultValue = "0")
    private int flightPath_mode;
    @ColumnInfo(defaultValue = "50")
    private int front_overlap;
    @ColumnInfo(defaultValue = "50")
    private int side_overlap;
    private int spacing;
    private float initial_altitude;
    private float final_altitude;
    private boolean include_roof;
    private int lines;
    private int initial_path_percentage;
    private int final_path_percentage;
    
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

    public float getAuto_flight_speed() {
        return auto_flight_speed;
    }

    public void setAuto_flight_speed(float auto_flight_speed) {
        this.auto_flight_speed = auto_flight_speed;
    }

    public float getMax_flight_speed() {
        return max_flight_speed;
    }

    public void setMax_flight_speed(float max_flight_speed) {
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

    public int getFront_overlap() {
        return front_overlap;
    }

    public void setFront_overlap(int front_overlap) {
        this.front_overlap = front_overlap;
    }

    public int getSide_overlap() {
        return side_overlap;
    }

    public void setSide_overlap(int side_overlap) {
        this.side_overlap = side_overlap;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public float getInitial_altitude() {
        return initial_altitude;
    }

    public void setInitial_altitude(float initial_altitude) {
        this.initial_altitude = initial_altitude;
    }

    public float getFinal_altitude() {
        return final_altitude;
    }

    public void setFinal_altitude(float final_altitude) {
        this.final_altitude = final_altitude;
    }

    public boolean isInclude_roof() {
        return include_roof;
    }

    public void setInclude_roof(boolean include_roof) {
        this.include_roof = include_roof;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getInitial_path_percentage() {
        return initial_path_percentage;
    }

    public void setInitial_path_percentage(int initial_path_percentage) {
        this.initial_path_percentage = initial_path_percentage;
    }

    public int getFinal_path_percentage() {
        return final_path_percentage;
    }

    public void setFinal_path_percentage(int final_path_percentage) {
        this.final_path_percentage = final_path_percentage;
    }
}

