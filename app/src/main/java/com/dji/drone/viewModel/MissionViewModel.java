package com.dji.drone.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.dji.drone.model.Mission;
import com.dji.drone.model.MissionRepository;
import com.dji.drone.model.WaypointPathMaker;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.model.room.Point2D;
import com.google.android.gms.maps.model.LatLng;

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
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.sdkmanager.DJISDKManager;

public class MissionViewModel extends AndroidViewModel{
    private final String TAG = getClass().getSimpleName();

    private Mission mission;
    private WaypointPathMaker waypointPathMaker;

    //------------------------------
    private MissionEntity actualMission;
    private MissionRepository missionRepository;
    //-------------------------------
    private WaypointMission.Builder missionBuilder;
    private WaypointMissionOperator waypointMissionOperator;


    public MissionViewModel(@NonNull Application application) {
        super(application);
        mission = new Mission();

        missionRepository = new MissionRepository(application);
        missionBuilder = new WaypointMission.Builder();
    }

    //My Methods--------------------------------------------------
    public void uploadMission(List<LatLng> points) {

        //DJIError result =  mission.prepareMission(waypointPathMaker.makePath(points, 2.0, 17.0));

        ArrayList<Waypoint> waypointArrayList = new ArrayList<>();
        for (LatLng item : points) {
            waypointArrayList.add(new Waypoint(item.latitude, item.longitude, 2f));
        }
        DJIError result =  mission.prepareMission(waypointArrayList);

        if(result == null){
            mission.uploadMission();
        }
    }

    public void startMission(){
        if ((mission.getACurrentState().equals(WaypointMissionState.READY_TO_EXECUTE))){
            mission.startMission();
        }
    }

    public List<LatLng> TESTE(@NonNull List<LatLng> entrada, int start, int size){
        return convertToLatLng(WaypointPathMaker.makeSubCoordinates(convertToPoint2D(entrada), start, size));
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

                String aux = "";
                for(Waypoint item : missionBuilder.getWaypointList()){
                    aux += "(" + item.coordinate.getLatitude() + ", "
                            + item.coordinate.getLongitude() + ", "
                            + item.altitude + ")";
                }
                Log.d(TAG, aux);
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
        MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
        if(missionControl != null){
            WaypointMissionOperator operator = missionControl.getWaypointMissionOperator();
            return operator.getCurrentState().getName();
        }

        return "NONE";

    }

    public String getParameterStatus() {
        DJIError error = missionBuilder.checkParameters();
        if(error == null){
            return "Ok";
        }else{
            return error.getDescription() + "("+error.getErrorCode() + ")";
        }
    }

    public List<Waypoint> convertToWaypoint(@NonNull List<Point2D> point2DList){
        ArrayList<Waypoint> result = new ArrayList<>();
        for (int i = 0; i < point2DList.size(); i++) {
            double latitude = point2DList.get(i).getLatitude();
            double longitude = point2DList.get(i).getLongitude();
            Waypoint aux = new Waypoint(latitude, longitude, actualMission.getFinal_altitude());
            result.add(aux);
        }
        return result;
    }
    public List<Point2D> convertToPoint2D(@NonNull List<LatLng> latLngList){
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
    //--------------------------------------------------------------

    public void setActualMission(int missionId) {
        if(missionId != -1) {
            actualMission = missionRepository.getMission(missionId);
        }

    }

    public MissionEntity getActualMission() {
        return actualMission;
    }



}
