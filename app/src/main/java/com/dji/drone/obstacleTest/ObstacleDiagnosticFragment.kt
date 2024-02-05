package com.dji.drone.obstacleTest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dji.drone.databinding.FragmentObstacleDiagnosticBinding
import dji.common.flightcontroller.ObstacleDetectionSector
import dji.keysdk.FlightControllerKey
import dji.keysdk.KeyManager
import dji.keysdk.callback.KeyListener
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager


class ObstacleDiagnosticFragment : Fragment() {

    private lateinit var binding : FragmentObstacleDiagnosticBinding

    private var altitudeKey: FlightControllerKey = FlightControllerKey.
    createFlightAssistantKey(FlightControllerKey.DETECTION_SECTORS)
    private val altitudeList: KeyListener = KeyListener { _, o1 ->

        if(o1 is Array<*>){
            if(o1.isArrayOf<ObstacleDetectionSector>()){
                var texto = ""
                for (section in o1.indices){
                    texto += (o1[section] as ObstacleDetectionSector).obstacleDistanceInMeters.toString() + "\n"
                }
                binding.tvDistance.text = texto
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObstacleDiagnosticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
    }

    private fun initObservers(){
        binding.button.setOnClickListener {
            val key = KeyManager.getInstance()
            if (key == null){
                binding.tvDistance.text = "NULO"
                return@setOnClickListener
            }
            key.addListener(altitudeKey, altitudeList)
        }

        binding.button2.setOnClickListener {
            val drone = DJISDKManager.getInstance().product
            if(drone != null && drone is Aircraft){
                drone.flightController
                        .flightAssistant?.
                        setVisionDetectionStateUpdatedCallback { vision ->
                            val detect = vision.detectionSectors
                            var texto = ""
                            if (detect != null) {
                                for (section in detect.indices){
                                    texto += (detect[section] as ObstacleDetectionSector).obstacleDistanceInMeters.toString() + "\n"
                                }
                            }
                            binding.tvDistance2.text = detect.toString()
                        }
            }

        }
    }
}