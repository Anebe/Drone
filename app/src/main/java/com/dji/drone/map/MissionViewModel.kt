package com.dji.drone.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dji.common.model.LocationCoordinate2D
import dji.sdk.mission.timeline.TimelineElement
import dji.sdk.mission.timeline.actions.GoToAction
import dji.sdk.mission.timeline.triggers.Trigger
import dji.sdk.sdkmanager.DJISDKManager

class MissionViewModel: ViewModel() {


    fun startMission(coordinates: List<LatLng>) {
        val missionManager = DJISDKManager.getInstance().missionControl
        val elements: MutableList<TimelineElement> = emptyList<TimelineElement>().toMutableList()

        val velocity = 10f
        val altitude = 5f
        for(coord in coordinates){

            val goto = GoToAction(LocationCoordinate2D(coord.latitude,coord.longitude), altitude)
            goto.flightSpeed = velocity

            val trigger = ObstacleDistanceTrigger()
            trigger.distanceTrigger = 5f
            trigger.setAction {
                missionManager.stopTimeline()
            }
            goto.triggers = listOf(trigger)

            elements.add(goto)
        }
        missionManager.scheduleElements(elements)
        missionManager.startTimeline()

    }
}