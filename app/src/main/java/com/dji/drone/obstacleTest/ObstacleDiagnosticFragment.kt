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
import dji.sdk.sdkmanager.DJISDKManager


class ObstacleDiagnosticFragment : Fragment() {

    private val viewModel by viewModels<DistanceDiagnosticViewModel>()
    private lateinit var binding : FragmentObstacleDiagnosticBinding

    private var altitudeKey: FlightControllerKey = FlightControllerKey.create(FlightControllerKey.ALTITUDE)
    private val altitudeList: KeyListener = KeyListener { o, o1 ->
        if (o1 is Float) {
            val a = (o.toString() + " " + o1.toString() + " " + 45f.toString())
            binding.tvDistance.text = a
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            }
            key.addListener(altitudeKey, altitudeList)


        }
        viewModel.distance.observe(viewLifecycleOwner) {
            var strResult = ""
            for(distance in it){
                strResult += "$distance\n"
            }
            binding.tvDistance.text = strResult
        }
    }

    companion object {
    }
}