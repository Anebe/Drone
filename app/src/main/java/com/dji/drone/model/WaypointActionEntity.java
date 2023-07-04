package com.dji.drone.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;

@Entity(tableName = "waypoint_actions",
        foreignKeys = @ForeignKey(entity = WaypointEntity.class,
                                    childColumns = "id",
                                    parentColumns = "waypoint_id",
                                    onDelete = ForeignKey.CASCADE))
public class WaypointActionEntity{

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer actionType;
    private Integer actionParam;
    private Integer waypoint_id;

    public WaypointActionEntity(Integer actionType, Integer actionParam) {
        this.actionType = actionType;
        this.actionParam = actionParam;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Integer getActionParam() {
        return actionParam;
    }

    public void setActionParam(Integer actionParam) {
        this.actionParam = actionParam;
    }
}
