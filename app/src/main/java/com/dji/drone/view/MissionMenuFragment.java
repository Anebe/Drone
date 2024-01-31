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
import com.dji.drone.databinding.FragmentMissionMenuBinding;
import com.dji.drone.viewModel.MissionViewModel;

public class MissionMenuFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private FragmentMissionMenuBinding binding;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMissionMenuBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initUI();

        return view;
    }

    private void initUI() {
        MissionViewModel missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.navigationView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        int id = MissionMenuFragmentArgs.fromBundle(getArguments()).getMissionId();
        missionViewModel.setActualMission(id);
    }
}