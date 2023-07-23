package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.dji.drone.R;
import com.dji.drone.viewModel.MissionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    private MissionViewModel missionViewModel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mission, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.navigationView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        Integer id = MissionFragmentArgs.fromBundle(getArguments()).getMissionId();
        missionViewModel.setActualMission(id);

        //MissionNavigationDirections.GotoMap gotoMap = MissionNavigationDirections.gotoMap();
        //gotoMap.setMissionId(id);
        //navController.popBackStack();
        //navController.navigate(gotoMap);
    }
}