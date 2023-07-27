package com.dji.drone.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dji.drone.model.MissionRepository;
import com.dji.drone.model.WaypointPathMaker;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.model.room.Point2D;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MissionViewModel extends AndroidViewModel implements FlightControllerState.Callback {
    private final String TAG = getClass().getSimpleName();

    //------------------------------
    private MissionEntity actualMission;
    private final MissionRepository missionRepository;
    //-------------------------------
    private WaypointMission.Builder missionBuilder;
    private FlightController flightController;
    //-------------------------------------------
    private MutableLiveData<Point2D> droneLatLng;


    public MissionViewModel(@NonNull Application application) {
        super(application);
        //mission = new Mission();

        missionRepository = new MissionRepository(application);
        droneLatLng = new MutableLiveData<>();
        Aircraft aircraft = (Aircraft)DJISDKManager.getInstance().getProduct();
        if(aircraft != null){
            flightController = aircraft.getFlightController();
            flightController.setStateCallback(this);
        }
    }

    public LiveData<Point2D> getDroneLatLng() {
        return droneLatLng;
    }

    public void setActualMission(int missionId) {
        if(missionId != -1) {
            actualMission = missionRepository.getMission(missionId);
        }

    }

    public MissionEntity getActualMission() {
        return actualMission;
    }
    //My Methods--------------------------------------------------

    public List<LatLng> getPath2D(@NonNull List<LatLng> entrada, int start, int size){
        return convertToLatLng(WaypointPathMaker.makeSubCoordinates(convertToPoint2D(entrada), start, size));
    }

    private List<Waypoint> convertToWaypoint(@NonNull List<Point2D> point2DList){
        ArrayList<Waypoint> result = new ArrayList<>();
        for (int i = 0; i < point2DList.size(); i++) {
            double latitude = point2DList.get(i).getLatitude();
            double longitude = point2DList.get(i).getLongitude();
            Waypoint aux = new Waypoint(latitude, longitude, actualMission.getFinal_altitude());
            result.add(aux);
        }
        return result;
    }
    private List<Point2D> convertToPoint2D(@NonNull List<LatLng> latLngList){
        ArrayList<Point2D> result = new ArrayList<>();
        for (int i = 0; i < latLngList.size(); i++) {
            double latitude = latLngList.get(i).latitude;
            double longitude = latLngList.get(i).longitude;
            Point2D aux = new Point2D();
            aux.setLatitude(latitude);
            aux.setLongitude(longitude);
            result.add(aux);
        }
        return result;
    }
    private List<LatLng> convertToLatLng(@NonNull List<Point2D> point2DList){
        ArrayList<LatLng> result = new ArrayList<>();
        for (int i = 0; i < point2DList.size(); i++) {
            double latitude = point2DList.get(i).getLatitude();
            double longitude = point2DList.get(i).getLongitude();
            LatLng aux = new LatLng(latitude, longitude);
            result.add(aux);
        }
        return result;
    }

    //DATABASE-------------------------------------------------------
    public long updateMission(){
        return missionRepository.updateMission(actualMission);
    }

    public boolean updateLatLng(@NonNull List<LatLng> latLngList){
        List<Point2D> point2DList = new ArrayList<>();

        for (LatLng item : latLngList) {
            Point2D aux = new Point2D();
            aux.setMission_id(actualMission.getId());
            aux.setLatitude(item.latitude);
            aux.setLongitude(item.longitude);
            point2DList.add(aux);
        }
        return missionRepository.insertUpdatePoint2d(actualMission.getId(), point2DList);
    }

    public List<LatLng> getAllLatLng(){

        List<Point2D> point2DList = missionRepository.getPoint2dOfMission(actualMission.getId());
        if(point2DList != null){
            List<LatLng> result = new ArrayList<>();

            for (Point2D item : point2DList) {
                result.add(new LatLng(item.getLatitude(), item.getLongitude()));
            }
            return result;
        }
        return null;
    }
    //DJI------------------------------
    public void tryStartMission() {
        WaypointMissionOperator waypointMissionOperator = getWaypointMissionOperator();

        if(waypointMissionOperator == null){
            return;
        }
        if(missionBuilder == null){
            prepareMission();
        }

        WaypointMission djiMission = missionBuilder.build();
        DJIError result = djiMission.checkParameters();

        if(result == null){
            result = waypointMissionOperator.loadMission(djiMission);
            if (result == null) {
                Log.d(TAG, "LoadWaypoint succeeded");
                uploadMission();
            } else {
                Log.d(TAG, "LoadWaypoint failed: " + result.getDescription() + " " + result.getErrorCode());
            }
        }else{
            String msg = "Invalid parameters: " + result.getDescription() + " " +result.getErrorCode();
            Log.d(TAG, msg);
        }
    }

    private void uploadMission(){
        WaypointMissionState actualState = Objects.requireNonNull(getWaypointMissionOperator()).getCurrentState();

        if(actualState.equals(WaypointMissionState.READY_TO_UPLOAD) || actualState.equals(WaypointMissionState.READY_TO_RETRY_UPLOAD)){
            getWaypointMissionOperator().uploadMission(djiError -> {
                if(djiError == null){
                    startMission();
                    Log.d(TAG, "Success Upload");
                }else{
                    Log.d(TAG, "Failed Upload: " + djiError.getDescription() + " " + djiError.getErrorCode());
                }
            });
        }
    }

    private void startMission(){
        if ((Objects.requireNonNull(getWaypointMissionOperator()).getCurrentState().equals(WaypointMissionState.READY_TO_EXECUTE))){
            getWaypointMissionOperator().startMission(djiError -> {
                if(djiError == null){
                    Log.d(TAG, "Start Success");
                }else{
                    Log.d(TAG, "Start Failed: " + djiError.getDescription() + " " + djiError.getErrorCode());
                }
            });
        }
    }

    public void prepareMission(){
        if(actualMission != null){
            missionBuilder = new WaypointMission.Builder();

            List<Point2D> point2DList = missionRepository.getPoint2dOfMission(actualMission.getId());

            if(!point2DList.isEmpty()){

                //List<Waypoint> waypointList = convertToWaypoint(point2DList);
                List<Waypoint> waypointList = WaypointPathMaker.makePath(point2DList, actualMission.getLines(),
                        actualMission.getInitial_altitude(), actualMission.getFinal_altitude(),
                        actualMission.getInitial_path_percentage(), actualMission.getFinal_path_percentage());

                for (Waypoint item : waypointList) {
                    item.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0));
                    item.shootPhotoDistanceInterval = 1f;
                    missionBuilder.addWaypoint(item);
                }

                missionBuilder.waypointCount(waypointList.size());
                missionBuilder.autoFlightSpeed(actualMission.getAuto_flight_speed());
                missionBuilder.maxFlightSpeed(actualMission.getMax_flight_speed());
                missionBuilder.setExitMissionOnRCSignalLostEnabled(false);
                WaypointMissionGotoWaypointMode gotoWaypointMode = WaypointMissionGotoWaypointMode.find(actualMission.getGoto_mode());
                missionBuilder.gotoFirstWaypointMode(gotoWaypointMode);
                WaypointMissionFinishedAction finishedAction = WaypointMissionFinishedAction.find(actualMission.getFinished_action());
                missionBuilder.finishedAction(finishedAction);
                WaypointMissionHeadingMode headingMode = WaypointMissionHeadingMode.find(actualMission.getHeading_mode());
                missionBuilder.headingMode(headingMode);
                WaypointMissionFlightPathMode flightPathMode = WaypointMissionFlightPathMode.find(actualMission.getFlightPath_mode());
                missionBuilder.flightPathMode(flightPathMode);

                //String aux = "";
                //for(Waypoint item : missionBuilder.getWaypointList()){
                //    aux += "(" + item.coordinate.getLatitude() + ", "
                //            + item.coordinate.getLongitude() + ", "
                //            + item.altitude + ")";
                //}
                //Log.d(TAG, aux);
            }
        }
    }

    public Float getTotalTime(){
        return missionBuilder.calculateTotalTime();
    }

    public float getTotalDistance(){
        return missionBuilder.calculateTotalDistance();
    }

    public int getNumberOfWaypoints() {
        return missionBuilder.getWaypointCount();
    }

    public int getNumberOfPhotos() {
        return 0;
    }

    public String getActualState() {
        WaypointMissionOperator operator = getWaypointMissionOperator();
        if(operator != null){
            return operator.getCurrentState().getName();
        }
        return "NONE";

    }

    private WaypointMissionOperator getWaypointMissionOperator(){
        MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
        if(missionControl != null){
            return missionControl.getWaypointMissionOperator();
        }

        return null;
    }

    public String getParameterStatus() {
        DJIError error = missionBuilder.checkParameters();
        if(error == null){
            return "Ok";
        }else{
            return error.getDescription() + "("+error.getErrorCode() + ")";
        }
    }

    @Override
    public void onUpdate(@NonNull FlightControllerState flightControllerState) {
        Point2D point2D = new Point2D();
        point2D.setLatitude(flightControllerState.getAircraftLocation().getLatitude());
        point2D.setLongitude(flightControllerState.getAircraftLocation().getLongitude());
        droneLatLng.setValue(point2D);
    }
}
