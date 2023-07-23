package com.dji.drone.model.room;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "waypoint_actions",
        foreignKeys = @ForeignKey(entity = WaypointEntity.class,
                                    childColumns = "waypoint_id",
                                    parentColumns = "id",
                                    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        indices = @Index(name = "waypoint_id", value = "waypoint_id"))
public class WaypointActionEntity{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int actionType;
    private int actionParam;
    private int waypoint_id;

    public WaypointActionEntity(int actionType, int actionParam) {
        this.actionType = actionType;
        this.actionParam = actionParam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getActionParam() {
        return actionParam;
    }

    public void setActionParam(int actionParam) {
        this.actionParam = actionParam;
    }

    public int getWaypoint_id() {
        return waypoint_id;
    }

    public void setWaypoint_id(int waypoint_id) {
        this.waypoint_id = waypoint_id;
    }
}
