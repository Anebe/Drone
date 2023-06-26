package com.dji.drone.viewModel;

import android.util.Log;

import java.util.ArrayList;
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

public class Mission {
    private final String TAG = getClass().getName();

    private MissionControl missionControl;
    private WaypointMissionOperator waypointMissionOperator;
    private WaypointMission.Builder waypointMissionBuilder;

    private float speed = 10f;
    private float maxSpeed = 3f;
    private WaypointMissionGotoWaypointMode gotoWaypointMode = WaypointMissionGotoWaypointMode.SAFELY;
    private WaypointMissionFinishedAction finishedAction = WaypointMissionFinishedAction.AUTO_LAND;
    private WaypointMissionHeadingMode headingMode = WaypointMissionHeadingMode.AUTO;
    private WaypointMissionFlightPathMode flightPathMode = WaypointMissionFlightPathMode.NORMAL;


    public Mission() {
        if (DJISDKManager.getInstance().getMissionControl() != null){
            missionControl = DJISDKManager.getInstance().getMissionControl();
            waypointMissionOperator = missionControl.getWaypointMissionOperator();
            waypointMissionBuilder = new WaypointMission.Builder();

            Log.d(TAG, waypointMissionOperator.getCurrentState().toString());
        }
    }


    public void prepareMission(List<Waypoint> waypointList){
        for (Waypoint item : waypointList) {
            waypointMissionBuilder.addWaypoint(item);
        }
        waypointMissionBuilder.waypointCount(waypointList.size());
        waypointMissionBuilder.autoFlightSpeed(speed);
        waypointMissionBuilder.maxFlightSpeed(maxSpeed);
        waypointMissionBuilder.setExitMissionOnRCSignalLostEnabled(true);
        waypointMissionBuilder.gotoFirstWaypointMode(gotoWaypointMode);
        waypointMissionBuilder.finishedAction(finishedAction);
        waypointMissionBuilder.headingMode(headingMode);
        waypointMissionBuilder.flightPathMode(flightPathMode);

        WaypointMission waypointMission = waypointMissionBuilder.build();

        DJIError djiError = waypointMission.checkParameters();
        if(djiError== null){
            DJIError error = waypointMissionOperator.loadMission(waypointMission);
            if (error == null) {
                Log.d(TAG, "LoadWaypoint succeeded");
            } else {
                Log.d(TAG, "LoadWaypoint failed: " + error.getDescription() + " " + error.getErrorCode());
            }
        }else{
            String msg = "Invalid parameters: " + djiError.getDescription() + " " +djiError.getErrorCode();
            Log.d(TAG, msg);
        }
    }
    public void uploadMission(){
        WaypointMissionState actualState = waypointMissionOperator.getCurrentState();
        if(actualState== WaypointMissionState.READY_TO_UPLOAD || actualState == WaypointMissionState.READY_TO_RETRY_UPLOAD){
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
