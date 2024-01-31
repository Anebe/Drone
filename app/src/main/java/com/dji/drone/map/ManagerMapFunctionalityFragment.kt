package com.dji.drone.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dji.drone.R
import com.dji.drone.databinding.FragmentManagerMapFunctionalityBinding




class ManagerMapFunctionalityFragment : Fragment() {

    private val viewModel by viewModels<MissionViewModel>()
    private lateinit var binding: FragmentManagerMapFunctionalityBinding
    private lateinit var map: RoutMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManagerMapFunctionalityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initListener()
    }

    private fun initData(){
        map = binding.mapView.getFragment<RoutMapFragment>()
        switchButtons()
    }

    private fun initListener() {
        binding.imageButtonCreate.setOnClickListener {
            map.createInitialBaseRoute()
            switchButtons()
        }


        binding.imageButtonDelete.setOnClickListener {
            map.clear()
            switchButtons()
        }


        binding.switchMarkerControl.setOnClickListener {
            map.updateMarkerVisible()

            if (binding.switchMarkerControl.isChecked) {
                binding.switchMarkerControl.setThumbResource(R.drawable.ic_round_add_circle_24)
            } else {
                binding.switchMarkerControl.setThumbResource(R.drawable.ic_round_remove_circle_24)
            }
        }

        binding.imageButtonStart.setOnClickListener {

            viewModel.startMission(map.coordinates)
        }

    }

    private fun switchButtons() {
        val alphaDisable = 0.4f
        val alphaEnable = 1f
        if (binding.imageButtonDelete.isEnabled) {
            binding.imageButtonDelete.isEnabled = false
            binding.imageButtonDelete.alpha = alphaDisable
            binding.imageButtonCreate.isEnabled = true
            binding.imageButtonCreate.alpha = alphaEnable
        } else if (binding.imageButtonCreate.isEnabled) {
            binding.imageButtonCreate.isEnabled = false
            binding.imageButtonCreate.alpha = alphaDisable
            binding.imageButtonDelete.isEnabled = true
            binding.imageButtonDelete.alpha = alphaEnable
        }
    }

}