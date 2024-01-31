package com.dji.drone.map

import dji.common.flightcontroller.ObstacleDetectionSector
import dji.keysdk.FlightControllerKey
import dji.keysdk.KeyManager
import dji.keysdk.callback.KeyListener
import dji.sdk.mission.timeline.triggers.Trigger


class ObstacleDistanceTrigger: Trigger() {

    var distanceTrigger: Float = 0.0f
    private val obstacleKey: FlightControllerKey = FlightControllerKey.create(FlightControllerKey.DETECTION_SECTORS)
    private val obstacleListener: KeyListener = KeyListener{ old: Any?, new: Any? ->
        if(new is Array<*>){
            for(area in new){
                if(area is ObstacleDetectionSector){
                    if(distanceTrigger >= area.obstacleDistanceInMeters){
                        super.action
                    }
                }
            }
        }

    }

    init {
        KeyManager.getInstance().addListener(obstacleKey, obstacleListener)
    }


}