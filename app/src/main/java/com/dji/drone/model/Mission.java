package com.dji.drone.model;

import android.util.Log;

import java.util.List;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.sdkmanager.DJISDKManager;

public class Mission{
    private final String TAG = getClass().getName();

    private WaypointMissionOperator waypointMissionOperator;
    private WaypointMission.Builder waypointMissionBuilder;

    private final float speed = 10f;
    private final float maxSpeed = 13f;

    public Mission() {
        if (DJISDKManager.getInstance().getMissionControl() != null){
            MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
            waypointMissionOperator = missionControl.getWaypointMissionOperator();
            waypointMissionBuilder = new WaypointMission.Builder();
        }
    }

    public WaypointMissionState getACurrentState(){
        return waypointMissionOperator.getCurrentState();
    }

    public DJIError prepareMission(List<Waypoint> waypointList){
        for (Waypoint item : waypointList) {
            waypointMissionBuilder.addWaypoint(item);
        }

        waypointMissionBuilder.waypointCount(waypointList.size());
        waypointMissionBuilder.autoFlightSpeed(speed);
        waypointMissionBuilder.maxFlightSpeed(maxSpeed);
        waypointMissionBuilder.setExitMissionOnRCSignalLostEnabled(false);
        waypointMissionBuilder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
        waypointMissionBuilder.finishedAction(WaypointMissionFinishedAction.AUTO_LAND);
        waypointMissionBuilder.headingMode(WaypointMissionHeadingMode.AUTO);
        waypointMissionBuilder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        WaypointMission waypointMission = waypointMissionBuilder.build();

        DJIError djiError = waypointMission.checkParameters();
        if(djiError == null){
            djiError = waypointMissionOperator.loadMission(waypointMission);
            if (djiError == null) {
                Log.d(TAG, "LoadWaypoint succeeded");
            } else {
                Log.d(TAG, "LoadWaypoint failed: " + djiError.getDescription() + " " + djiError.getErrorCode());
            }
        }else{
            String msg = "Invalid parameters: " + djiError.getDescription() + " " +djiError.getErrorCode();
            Log.d(TAG, msg);
        }
        return djiError;
    }

    public void uploadMission(){
        WaypointMissionState actualState = waypointMissionOperator.getCurrentState();
        if(actualState.equals(WaypointMissionState.READY_TO_UPLOAD) || actualState.equals(WaypointMissionState.READY_TO_RETRY_UPLOAD)){
            waypointMissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError == null){
                        Log.d(TAG, "Success Upload");
                    }else{
                        Log.d(TAG, "Failed Upload: " + djiError.getDescription() + " " + djiError.getErrorCode());
                    }
                }
            });
        }
    }

    public void startMission(){
        WaypointMissionState actualState = waypointMissionOperator.getCurrentState();
        if(actualState== WaypointMissionState.READY_TO_EXECUTE) {
            waypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError == null){
                        Log.d(TAG, "Success Upload");
                    }else{
                        Log.d(TAG, "Failed Upload: " + djiError.getDescription() + " " + djiError.getErrorCode());
                    }
                }
            });
        }

    }
}
