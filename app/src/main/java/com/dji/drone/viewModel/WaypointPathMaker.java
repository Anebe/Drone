package com.dji.drone.viewModel;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;


public class WaypointPathMaker {
    private Integer lines;
    //private Double lineSpacing;

    /*
    private enum SIDE_PATH{
        TOP_LEFT,
        TOP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    };
    */
    public WaypointPathMaker(Integer lines, Double lineSpacing) {
        this.lines = lines;
        //this.lineSpacing = lineSpacing;
    }
    public WaypointPathMaker(Integer lines) {
        this.lines = lines;
    }

    public List<Waypoint> makePath(List<LatLng> coordinates, Double initialHeight, Double finalHeight){
        ArrayList<Waypoint> result = new ArrayList<>();

        for (int i = 0; i < coordinates.size(); i++) {
            List<Waypoint> aux;
            if (i == coordinates.size() - 1) {
                aux = makeSidePath(coordinates.get(i), coordinates.get(0), initialHeight, finalHeight);
            } else {
                aux = makeSidePath(coordinates.get(i), coordinates.get(i + 1), initialHeight, finalHeight);
            }
            result.addAll(aux);
        }
        Log.i(this.getClass().getName(), result.toString());
        return result;
    }

    private List<Waypoint> makeSidePath(LatLng first, LatLng last, Double initialHeight, Double finalHeight){
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
    }
}
