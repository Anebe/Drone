package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.databinding.FragmentStartMissionBinding;
import com.dji.drone.viewModel.MissionViewModel;

public class StartMissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    //private TextView tv_state;
    //private TextView tv_number_waypoints;
    //private TextView tv_number_photos;
    //private TextView tv_total_distance;
    //private TextView tv_total_time;
    //private TextView tv_parameter_status;
    private FragmentStartMissionBinding binding;
    private MissionViewModel missionViewModel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartMissionBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();
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

        aux = missionViewModel.getActualState();
        binding.tvStateValue.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfWaypoints());
        binding.tvNumberWaypointsValue.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfPhotos());
        binding.tvNumberPhotosValue.setText(aux);
        aux = missionViewModel.getParameterStatus();
        binding.tvParameterStatusValue.setText(aux);

    }
}