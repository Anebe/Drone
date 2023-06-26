package com.dji.drone.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionState;


public class WaypointPathMaker{
    private Integer lines;

    public WaypointPathMaker(Integer lines) {
        this.lines = lines;
    }

    public List<Waypoint> makePath(List<LatLng> coordinates, Double initialHeight, Double finalHeight){
        ArrayList<Waypoint> result = new ArrayList<>();

        float actual_height = initialHeight.floatValue();
        float height_increment = (Float.sum(finalHeight.floatValue(), initialHeight.floatValue() * (-1))) / lines;
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < coordinates.size(); j++) {
                LatLng aux = coordinates.get(j);
                result.add(new Waypoint(aux.latitude,aux.longitude, actual_height));
            }
            actual_height = Float.sum(actual_height, height_increment);
        }
        /*for (int i = 0; i < coordinates.size(); i++) {
            List<Waypoint> aux;
            if (i == coordinates.size() - 1) {
                aux = makeSidePath(coordinates.get(i), coordinates.get(0), initialHeight, finalHeight);
            } else {
                aux = makeSidePath(coordinates.get(i), coordinates.get(i + 1), initialHeight, finalHeight);
            }
            result.addAll(aux);
        }*/
        Log.i(this.getClass().getName(), result.toString());
        return result;
    }

    /*private List<Waypoint> makeSidePath(LatLng first, LatLng last, Double initialHeight, Double finalHeight){
        ArrayList<Waypoint> result = new ArrayList<>();

        float actual_height = initialHeight.floatValue();
        float height_increment = (Float.sum(finalHeight.floatValue(), initialHeight.floatValue() * (-1))) / lines;
        for (int i = 0; i < lines; i++) {
            if(i % 2 == 0){
                result.add(new Waypoint(first.latitude, first.longitude,  actual_height));
                result.add(new Waypoint(last.latitude, last.longitude, actual_height));
            }
            else {
                result.add(new Waypoint(last.latitude, last.longitude, actual_height));
                result.add(new Waypoint(first.latitude, first.longitude,  actual_height));
            }
            actual_height = Float.sum(actual_height, height_increment);
        }
        return result;
    }*/
}
