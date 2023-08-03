package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.databinding.FragmentStartMissionBinding;
import com.dji.drone.viewModel.MissionViewModel;

public class StartMissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private FragmentStartMissionBinding binding;
    private MissionViewModel missionViewModel;
    private LiveData<String> missionState;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartMissionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initData();
        initListener();
        return view;
    }


    private void initData(){
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        missionViewModel.prepareMission();

        String aux;

        Float totalSeconds = missionViewModel.getTotalTime();
        int minutes = totalSeconds.intValue() / 60;
        int seconds = totalSeconds.intValue() % 60;
        aux = minutes + "min, " + seconds + "seg.";
        binding.tvTotalTimeValue.setText(aux);

        float totalDistanceInMeters = missionViewModel.getTotalDistance();
        if (totalDistanceInMeters >= 1000) {
            int km = (int) (totalDistanceInMeters / 1000);
            int m = (int) (totalDistanceInMeters % 1000);
            aux = km + "km, " + m + " m";
        }else{
            aux = (int)totalDistanceInMeters + " m";
        }
        binding.tvTotalDistanceValue.setText(aux);

        missionState = missionViewModel.getActualState();
        //aux = missionViewModel.getActualState();
        //binding.tvStateValue.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfWaypoints());
        binding.tvNumberWaypointsValue.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfPhotos());
        binding.tvNumberPhotosValue.setText(aux);
        aux = missionViewModel.getParameterStatus();


        binding.tvParameterStatusValue.setText(aux);

    }

    private void initListener(){
        binding.imgBtnStart.setOnClickListener(v -> missionViewModel.tryStartMission());
        binding.imgBtnPause.setOnClickListener(v -> missionViewModel.pauseMission());
        binding.imgBtnStop.setOnClickListener(v -> missionViewModel.stopMission());
    }

    private void initObserver(){

        missionState.observe(this, strState -> {
            binding.tvStateValue.setText(strState);
        });
    }
}