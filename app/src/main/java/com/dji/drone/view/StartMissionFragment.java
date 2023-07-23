package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.viewModel.MissionViewModel;

public class StartMissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private TextView tv_state;
    private TextView tv_number_waypoints;
    private TextView tv_number_photos;
    private TextView tv_total_distance;
    private TextView tv_total_time;
    private TextView tv_parameter_status;

    private MissionViewModel missionViewModel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_mission, container, false);

        initUI();
        initData();
        return view;
    }

    private void initUI(){
        tv_state = view.findViewById(R.id.tv_state_value);
        tv_number_waypoints = view.findViewById(R.id.tv_number_waypoints_value);
        tv_number_photos = view.findViewById(R.id.tv_number_photos_value);
        tv_total_distance = view.findViewById(R.id.tv_total_distance_value);
        tv_total_time = view.findViewById(R.id.tv_total_time_value);
        tv_parameter_status = view.findViewById(R.id.tv_parameter_status_value);
    }

    private void initData(){
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        missionViewModel.prepareMission();

        String aux;

        Float totalSeconds = missionViewModel.getTotalTime();
        int minutes = totalSeconds.intValue() / 60;
        int seconds = totalSeconds.intValue() % 60;
        aux = minutes + "min, " + seconds + "seg.";
        tv_total_time.setText(aux);

        float totalDistanceInMeters = missionViewModel.getTotalDistance();
        if (totalDistanceInMeters >= 1000) {
            int km = (int) (totalDistanceInMeters / 1000);
            int m = (int) (totalDistanceInMeters % 1000);
            aux = km + "km, " + m + " m";
        }else{
            aux = (int)totalDistanceInMeters + " m";
        }
        tv_total_distance.setText(aux);

        aux = missionViewModel.getActualState();
        tv_state.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfWaypoints());
        tv_number_waypoints.setText(aux);
        aux = String.valueOf(missionViewModel.getNumberOfPhotos());
        tv_number_photos.setText(aux);
        aux = missionViewModel.getParameterStatus();
        tv_parameter_status.setText(aux);

    }
}