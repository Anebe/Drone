package com.dji.drone.obstacleTest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dji.drone.databinding.FragmentObstacleDiagnosticBinding
import dji.keysdk.FlightControllerKey
import dji.keysdk.KeyManager
import dji.keysdk.callback.KeyListener
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager


class ObstacleDiagnosticFragment : Fragment() {

    private lateinit var binding : FragmentObstacleDiagnosticBinding

    private var altitudeKey: FlightControllerKey = FlightControllerKey.
    createFlightAssistantKey(FlightControllerKey.DETECTION_SECTORS)
    private val altitudeList: KeyListener = KeyListener { o, o1 ->
        val a = (o.toString() + "\n" + o1.toString() + "\n" + 45f.toString())
        binding.tvDistance.text = a
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
            (DJISDKManager.getInstance().product as Aircraft)
                .flightController
                .flightAssistant?.
                setVisionDetectionStateUpdatedCallback {
                    val detect = it.detectionSectors
                    binding.tvDistance2.text = detect.toString()
            }
        }
    }

    companion object {
    }
}