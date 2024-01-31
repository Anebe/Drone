package com.dji.drone.obstacleTest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dji.drone.databinding.FragmentObstacleDiagnosticBinding


class ObstacleDiagnosticFragment : Fragment() {

    private val viewModel by viewModels<DistanceDiagnosticViewModel>()
    private lateinit var binding : FragmentObstacleDiagnosticBinding

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
        viewModel.distance.observe(this) {
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