package com.dji.drone.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dji.drone.model.Mission;
import com.dji.drone.model.MissionDAO;
import com.dji.drone.model.MissionDatabase;
import com.dji.drone.model.WaypointActionDAO;
import com.dji.drone.model.WaypointDAO;
import com.dji.drone.model.WaypointPathMaker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;

public class MainViewModel extends AndroidViewModel implements WaypointMissionOperatorListener, FlightControllerState.Callback {

    private MutableLiveData<String> progressUploadWaypointMission;
    private MutableLiveData<LatLng> droneLatLnt;
    private Mission mission;
    private WaypointPathMaker waypointPathMaker;
    private MissionDatabase missionDatabase;
    private MissionDAO missionDAO;
    private WaypointDAO waypointDAO;
    private WaypointActionDAO waypointActionDAO;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mission = new Mission();
        progressUploadWaypointMission = new MutableLiveData<>();
        droneLatLnt = new MutableLiveData<>();
        mission.addEventsListener(this);
        waypointPathMaker = new WaypointPathMaker(2);
    }

    public LiveData<String> getProgressUploadWaypointMission() {
        return progressUploadWaypointMission;
    }

    public LiveData<LatLng> getDroneLatLnt() {
        return droneLatLnt;
    }


    //My Methods--------------------------------------------------
    public void uploadMission(List<LatLng> points) {
        ArrayList<Waypoint> waypoints = new ArrayList<>();

        /*for (LatLng i : points) {
            waypoints.add(new Waypoint(i.latitude, i.longitude, 3f));
        }*/
        DJIError result =  mission.prepareMission(waypointPathMaker.makePath(points, 2.0, 4.0));
        progressUploadWaypointMission.setValue(mission.getACurrentState().toString());
        if(result == null){
            mission.uploadMission();
        }
    }

    public void startMission(){
        progressUploadWaypointMission.setValue(mission.getACurrentState().toString());
        if ((mission.getACurrentState().equals(WaypointMissionState.READY_TO_EXECUTE))){
            mission.startMission();
        }
    }
    //WaypointMissionOperatorListener-------------------------------
    @Override
    public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
        /*Integer porcUpload = waypointMissionDownloadEvent.getProgress().totalWaypointCount/(waypointMissionDownloadEvent.getProgress().downloadedWaypointIndex*100);
        String msg = "State: " + waypointMissionDownloadEvent
                + " " + porcUpload + "%";
        progressUploadWaypointMission.setValue(msg);*/
    }

    @Override
    public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
        /*String msg = "";
        if(waypointMissionUploadEvent.getCurrentState() != null){
            msg += waypointMissionUploadEvent.getCurrentState().toString();
        }
        if(waypointMissionUploadEvent.getProgress() != null){
            msg += waypointMissionUploadEvent.getProgress().uploadedWaypointIndex + " de " + waypointMissionUploadEvent.getProgress().totalWaypointCount;
        }
        progressUploadWaypointMission.setValue(msg);*/
    }

    @Override
    public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
        /*Integer porcUpload = waypointMissionExecutionEvent.getProgress().totalWaypointCount/(waypointMissionExecutionEvent.getProgress().targetWaypointIndex);
        String msg = "State: " + waypointMissionExecutionEvent.getCurrentState()
                + " " + porcUpload + "%";
        progressUploadWaypointMission.setValue(msg);*/
    }

    @Override
    public void onExecutionStart() {
        /*String msg = "State: " + WaypointMissionState.READY_TO_EXECUTE.toString();
        progressUploadWaypointMission.setValue(msg);*/
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
    //FlightControllerState--------------------------------------------
    @Override
    public void onUpdate(@NonNull FlightControllerState flightControllerState) {
        LatLng aux = new LatLng(flightControllerState.getAircraftLocation().getLatitude(), flightControllerState.getAircraftLocation().getLongitude());
        droneLatLnt.setValue(aux);
    }
}
