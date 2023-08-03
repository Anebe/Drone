package com.dji.drone.model;

public class DetectionResult {
    public int id;
    public int[][] detection;

    public DetectionResult(int id, int[][] detection) {
        this.id = id;
        this.detection = detection;
    }
}
