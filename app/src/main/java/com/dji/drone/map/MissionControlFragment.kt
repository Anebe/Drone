package com.dji.drone.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dji.drone.R
import com.dji.drone.databinding.FragmentMissionControlBinding
import com.dji.mapkit.core.models.DJILatLng
import com.google.android.gms.maps.model.LatLng
import dji.common.model.LocationCoordinate2D
import dji.sdk.mission.MissionControl
import dji.sdk.mission.timeline.TimelineElement
import dji.sdk.mission.timeline.actions.GoToAction
import dji.sdk.mission.timeline.actions.TakeOffAction


class MissionControlFragment : Fragment() {
    private lateinit var binding: FragmentMissionControlBinding

    private val missionManager = MissionControl.getInstance()
    val coordinates = mutableListOf<DJILatLng>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMissionControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener(){
        binding.imageButton.setOnClickListener {
            startMission()
        }
    }

    private fun startMission() {
        /*
        val error = missionManager.scheduleElement(TakeOffAction())
        error?.let {
            Toast.makeText(requireContext(), error.description, Toast.LENGTH_LONG).show()
        }
        missionManager.startTimeline()
        */

        Toast.makeText(requireContext(), coordinates.size.toString(), Toast.LENGTH_LONG).show()
        if(coordinates.isNotEmpty()){
            val elements = mutableListOf<TimelineElement>()

            val velocity = 10f
            val altitude = 5f

            elements.add(TakeOffAction())
            //coordinates.removeLast()
            Toast.makeText(requireContext(), coordinates.size.toString(), Toast.LENGTH_LONG).show()
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
            error?.let {
                Toast.makeText(requireContext(), error.description, Toast.LENGTH_LONG).show()
            }
            if(error == null){
                Toast.makeText(requireContext(), missionManager.scheduledCount().toString(), Toast.LENGTH_LONG).show()
                missionManager.startTimeline()
            }
        }

    }
}