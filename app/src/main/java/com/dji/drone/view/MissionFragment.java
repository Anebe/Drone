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
import com.dji.drone.databinding.FragmentMissionBinding;
import com.dji.drone.databinding.FragmentMissionConfigBinding;
import com.dji.drone.viewModel.MissionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MissionFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private FragmentMissionBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMissionBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initUI();

        return view;
    }

    private void initUI() {
        MissionViewModel missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.navigationView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        int id = MissionFragmentArgs.fromBundle(getArguments()).getMissionId();
        missionViewModel.setActualMission(id);
    }
}