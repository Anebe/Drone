package com.dji.drone.model;

import androidx.annotation.NonNull;

import com.dji.drone.model.room.Point2D;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;

public class WaypointPathMaker{
    private static final String TAG = WaypointPathMaker.class.getSimpleName();

    public static List<Waypoint> makePath(List<Point2D> coordinates, int lines,
                                   float initialHeight, float finalHeight,
                                   int startPathPercentage, int pathUsagePercentage){
        List<Waypoint> result = new ArrayList<>();

        List<Point2D> refinedPath = makeSubCoordinates(coordinates, startPathPercentage, pathUsagePercentage);

        float actual_height = initialHeight;
        float height_increment = (finalHeight - initialHeight) / lines;
        boolean switchIncrement = true;
        for (int i = 0; i < lines; i++) {
            Point2D aux = new Point2D();
            for (int j = (switchIncrement) ?  0 : refinedPath.size()-1;
                 (switchIncrement) ? j < refinedPath.size() : j >= 0;
                 j = (switchIncrement) ? j+1 : j-1) {
                aux = refinedPath.get(j);
                result.add(new Waypoint(aux.getLatitude(),aux.getLongitude(), actual_height));
            }
            if(i != lines-1){
                actual_height = Float.sum(actual_height, height_increment);
            }
            switchIncrement = !switchIncrement;
        }
        return result;
    }
    
    public static List<Point2D> makeSubCoordinates(@NonNull List<Point2D> coordinates, int startPathPercentage, int pathUsagePercentage){
        double MIN = 0.01, MAX = 0.99;
        Point2D start, finish;
        ArrayList<Point2D> result = new ArrayList<>();

        double startPath = startPathPercentage/100.0;
        startPath = new BigDecimal(startPath).setScale(2, RoundingMode.HALF_UP).doubleValue();
        start = positionMarkerOnPolygon(coordinates, startPath);

        double finalPath = (pathUsagePercentage+startPathPercentage)/100.0 + MIN;
        finalPath = new BigDecimal(finalPath).setScale(2, RoundingMode.HALF_UP).doubleValue();

        if(Math.abs(startPath - finalPath) > MAX){
            if(startPath > finalPath){
                finalPath = startPath - MAX;
            }else{
                finalPath = startPath + MAX;
            }
        }
        if(finalPath >= 1.0) {
            finalPath -= 1.0;
        }
        finish = positionMarkerOnPolygon(coordinates, finalPath);


        int startIndex = -1, finishIndex = -1;
        int qtdVertex = coordinates.size()-1;
        List<Double> percentageOfWichStroke = percentageOfWichStroke(coordinates);
        double percentageIncrement = 0.0;
        for (int i = 1; startIndex == -1 || finishIndex == -1; i = (i >= qtdVertex)? 1 : i+1) {
            percentageIncrement += percentageOfWichStroke.get(i-1);
            if(startIndex == -1 && percentageIncrement >= startPath){
                startIndex = i;
            }
            if(finishIndex == -1 && percentageIncrement >= finalPath){
                finishIndex = i;
            }
        }

        if(startPath > finalPath){
            finishIndex += qtdVertex;
        }

        result.add(start);
        if(startIndex != finishIndex){
            for (int i = startIndex; i < finishIndex; i++) {
                if(i >= qtdVertex){
                    result.add(coordinates.get(i-(qtdVertex)));
                }else{
                    result.add(coordinates.get(i));
                }
            }
        }
        result.add(finish);
        return result;
    }

    private static Point2D positionMarkerOnPolygon(List<Point2D> polygonPoints, double percentage) {
        int numPoints = polygonPoints.size();
        if (numPoints < 2) {
            return new Point2D();
        }
        double totalDistance = 0.0;
        double targetDistance = percentage * calculatePolygonDistance(polygonPoints);

        for (int i = 1; i < numPoints; i++) {
            Point2D prevPoint = polygonPoints.get(i - 1);
            Point2D currPoint = polygonPoints.get(i);
            double distance = calculateDistance(prevPoint, currPoint);
            totalDistance += distance;

            if (totalDistance >= targetDistance) {
                double fraction = (targetDistance - (totalDistance - distance)) / distance;
                fraction = new BigDecimal(fraction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                return interpolateLatLng(prevPoint, currPoint, fraction);
            }
            //else if(i == numPoints-1){
            //    i = 1;
            //}
        }

        return new Point2D();
    }

    private static double calculatePolygonDistance(List<Point2D> polygonPoints) {
        double totalDistance = 0.0;
        int numPoints = polygonPoints.size();

        for (int i = 1; i < numPoints; i++) {
            Point2D prevPoint = polygonPoints.get(i - 1);
            Point2D currPoint = polygonPoints.get(i);
            double distance = calculateDistance(prevPoint, currPoint);
            totalDistance += distance;
        }

        return totalDistance;
    }
    
    private static double calculateDistance(Point2D point1, Point2D point2) {
        return Math.sqrt(Math.pow((point2.getLatitude() - point1.getLatitude()), 2) +
                Math.pow((point2.getLongitude() - point1.getLongitude()), 2));
    }

    private static Point2D interpolateLatLng(Point2D point1, Point2D point2, double fraction) {
        double lat = point1.getLatitude() + (point2.getLatitude() - point1.getLatitude()) * fraction;
        double lng = point1.getLongitude() + (point2.getLongitude() - point1.getLongitude()) * fraction;

        Point2D result = new Point2D();
        result.setLatitude(lat);
        result.setLongitude(lng);
        return result;
    }

    private static List<Double> percentageOfWichStroke(List<Point2D> point2DList){
        List<Double> result = new ArrayList<>();
        double totalDistance = calculatePolygonDistance(point2DList);

        for (int i = 1; i < point2DList.size(); i++) {
            double distance = calculateDistance(point2DList.get(i-1), point2DList.get(i));
            result.add(distance / totalDistance);
        }
        return result;
    }
}
