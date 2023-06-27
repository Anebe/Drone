package com.dji.drone.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dji.drone.model.Mission;
import com.dji.drone.view.MissionActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;

public class MainViewModel extends AndroidViewModel implements WaypointMissionOperatorListener {

    private MutableLiveData<String> progressUploadWaypointMission;

    private Mission mission;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mission = new Mission();
        progressUploadWaypointMission = new MutableLiveData<>();
        mission.addEventsListener(this);
    }

    public LiveData<String> getProgressUploadWaypointMission() {
        return progressUploadWaypointMission;
    }

    //////////////////////////////////
    @Override
    public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
        Integer porcUpload = waypointMissionDownloadEvent.getProgress().totalWaypointCount/(waypointMissionDownloadEvent.getProgress().downloadedWaypointIndex*100);
        String msg = "State: " + waypointMissionDownloadEvent
                + " " + porcUpload + "%";
        progressUploadWaypointMission.setValue(msg);
    }

    @Override
    public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
        Integer porcUpload = waypointMissionUploadEvent.getProgress().totalWaypointCount/(waypointMissionUploadEvent.getProgress().uploadedWaypointIndex*100);
        String msg = "State: " + waypointMissionUploadEvent.getCurrentState()
                + " " + porcUpload + "%";
        progressUploadWaypointMission.setValue(msg);
    }

    @Override
    public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
        Integer porcUpload = waypointMissionExecutionEvent.getProgress().totalWaypointCount/(waypointMissionExecutionEvent.getProgress().targetWaypointIndex);
        String msg = "State: " + waypointMissionExecutionEvent.getCurrentState()
                + " " + porcUpload + "%";
        progressUploadWaypointMission.setValue(msg);
    }

    @Override
    public void onExecutionStart() {
        String msg = "State: " + WaypointMissionState.READY_TO_EXECUTE.toString();
        progressUploadWaypointMission.setValue(msg);
    }

    @Override
    public void onExecutionFinish(@Nullable DJIError djiError) {
        String msg;
        if(djiError != null){
            msg = djiError.getDescription() + djiError.getErrorCode();
        }else{
            msg = "Foi de boa";
        }

        progressUploadWaypointMission.setValue(msg);
    }
    ////////////////////////////////////////////////////////////////
    public void startMission(List<LatLng> points) {
        ArrayList<Waypoint> waypoints = new ArrayList<>();

        for (LatLng i : points) {
            waypoints.add(new Waypoint(i.latitude, i.longitude, 3f));
        }
        DJIError result =  mission.prepareMission(waypoints);
        if(result == null){
            mission.uploadMission();
        }
        progressUploadWaypointMission.setValue(mission.getACurrentState().toString());
    }
}
