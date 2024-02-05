package com.dji.drone.map

import dji.common.flightcontroller.ObstacleDetectionSector
import dji.keysdk.FlightControllerKey
import dji.keysdk.KeyManager
import dji.keysdk.callback.KeyListener
import dji.sdk.mission.timeline.triggers.BatteryPowerLevelTrigger
import dji.sdk.mission.timeline.triggers.WaypointReachedTrigger
import dji.sdk.mission.timeline.triggers.Trigger
import dji.sdk.mission.timeline.triggers.TriggerEvent


class ObstacleDistanceTrigger: Trigger() {

    var distanceTrigger: Float = 0.0f

    private val obstacleKey: FlightControllerKey = FlightControllerKey.createFlightAssistantKey(FlightControllerKey.DETECTION_SECTORS)
    private val obstacleListener: KeyListener = KeyListener{ _: Any?, new: Any? ->
        if(new is Array<*>){
            for(area in new){
                if(area is ObstacleDetectionSector){
                    if(distanceTrigger >= area.obstacleDistanceInMeters){
                        super.action
                        notifyListenersOfEvent(TriggerEvent.ACTION_TRIGGERED, null)
                    }
                }
            }
        }
    }

    init {
        KeyManager.getInstance().addListener(obstacleKey, obstacleListener)
    }


}