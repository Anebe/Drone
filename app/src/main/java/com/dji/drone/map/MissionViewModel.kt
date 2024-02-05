package com.dji.drone.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dji.common.model.LocationCoordinate2D
import dji.sdk.mission.MissionControl
import dji.sdk.mission.timeline.TimelineElement
import dji.sdk.mission.timeline.actions.GoToAction
import dji.sdk.mission.timeline.actions.TakeOffAction
import dji.sdk.mission.timeline.triggers.Trigger
import dji.sdk.sdkmanager.DJISDKManager

class MissionViewModel: ViewModel() {

    fun startMission(coordinates: List<LatLng>) {
        val missionManager = MissionControl.getInstance()
        val elements = mutableListOf<TimelineElement>()

        val velocity = 10f
        val altitude = 5f

        elements.add(TakeOffAction())
        for(coord in coordinates){

            val goto = GoToAction(LocationCoordinate2D(coord.latitude,coord.longitude), altitude)
            goto.flightSpeed = velocity

            /*
            val trigger = ObstacleDistanceTrigger()
            trigger.distanceTrigger = 5f
            trigger.setAction {
                missionManager.stopTimeline()
            }
            goto.triggers = listOf(trigger)
             */

            elements.add(goto)
        }

        val error = missionManager.scheduleElements(elements)
        if(error == null){
            missionManager.startTimeline()
        }

    }
}