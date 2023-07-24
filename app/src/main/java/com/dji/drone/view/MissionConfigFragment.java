package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.databinding.FragmentMissionConfigBinding;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.viewModel.MissionViewModel;

public class MissionConfigFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private FragmentMissionConfigBinding binding;
    private MissionViewModel missionViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMissionConfigBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initData();
        initListener();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(missionViewModel.getActualMission() != null){
            int spacing;
            int lines;
            float speed;
            float initialAltitude;
            float finalAltitude;
            
            String aux = binding.editTextSpeed.getText().toString();
            speed = (aux.equals("")) ? 0f : Float.parseFloat(aux);
            
            aux = binding.editTextSpacing.getText().toString();
            spacing = (aux.equals("")) ? 0 : Integer.parseInt(aux);

            aux = binding.editTextInitialAltitude.getText().toString();
            initialAltitude = (aux.equals("")) ? 0f : Float.parseFloat(aux);

            aux = binding.editTextFinalAltitude.getText().toString();
            finalAltitude = (aux.equals("")) ? 0f :  Float.parseFloat(aux);

            aux = binding.editTextNumberSignedLines.getText().toString();
            lines = (aux.equals("")) ? 0 : Integer.parseInt(aux);

            MissionEntity missionEntity = missionViewModel.getActualMission();
            
            missionEntity.setName(binding.editTextName.getText().toString());
            missionEntity.setAuto_flight_speed(speed);
            missionEntity.setMax_flight_speed(speed);
            missionEntity.setSpacing(spacing);
            missionEntity.setInitial_altitude(initialAltitude);
            missionEntity.setFinal_altitude(finalAltitude);
            missionEntity.setFinished_action(binding.spinnerFinishedAction.getSelectedItemPosition());
            missionEntity.setGoto_mode(binding.spinnerGotoMode.getSelectedItemPosition());
            
            missionEntity.setInclude_roof(binding.checkBoxIncludeRoof.isChecked());
            missionEntity.setLines(lines);
            //missionEntity.setFront_overlap(binding.skFrontOverlap.getProgress());
            //missionEntity.setSide_overlap(binding.skSideOverlap.getProgress());
            missionEntity.setInitial_path_percentage(binding.skBeginningPath.getProgress());
            missionEntity.setFinal_path_percentage(binding.skPathSize.getProgress());
            missionViewModel.updateMission();
        }
    }

    private void initData(){
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);

        if(missionViewModel.getActualMission() != null){
            MissionEntity missionEntity = missionViewModel.getActualMission();

            String msg;
            
            binding.skFrontOverlap.setProgress(missionEntity.getFront_overlap());
            msg = missionEntity.getFront_overlap() + "%";
            binding.tvFrontOverlapValue.setText(msg);
            
            binding.skSideOverlap.setProgress(missionEntity.getSide_overlap());
            msg = missionEntity.getSide_overlap() + "%";
            binding.tvSideOverlapValue.setText(msg);

            binding.skBeginningPath.setProgress(missionEntity.getInitial_path_percentage());
            msg = missionEntity.getInitial_path_percentage() + "%";
            
            binding.tvBeginningPathValue.setText(msg);

            binding.skPathSize.setProgress(missionEntity.getFinal_path_percentage());
            msg = missionEntity.getFinal_path_percentage() + "%";
            binding.tvPathSizeValue.setText(msg);

            binding.editTextName.setText(missionEntity.getName());
            binding.editTextSpeed.setText(String.valueOf(missionEntity.getAuto_flight_speed()));
            binding.editTextSpacing.setText(String.valueOf(missionEntity.getSpacing()));
            binding.editTextInitialAltitude.setText(String.valueOf(missionEntity.getInitial_altitude()));
            binding.editTextFinalAltitude.setText(String.valueOf(missionEntity.getFinal_altitude()));
            binding.spinnerFinishedAction.setSelection(missionEntity.getFinished_action());
            binding.spinnerGotoMode.setSelection(missionEntity.getGoto_mode());
            binding.checkBoxIncludeRoof.setChecked(missionEntity.isInclude_roof());
            binding.editTextNumberSignedLines.setText(String.valueOf(missionEntity.getLines()));
        }

    }

    private void initListener(){
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            private void update(){
                String msg;
                msg = binding.skFrontOverlap.getProgress() + "%";
                binding.tvFrontOverlapValue.setText(msg);
                msg = binding.skPathSize.getProgress() + "%";
                binding.tvPathSizeValue.setText(msg);
                msg = binding.skSideOverlap.getProgress() + "%";
                binding.tvSideOverlapValue.setText(msg);
                msg = binding.skBeginningPath.getProgress() + "%";
                binding.tvBeginningPathValue.setText(msg);
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                update();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                update();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                update();
            }
        };

        binding.skSideOverlap.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.skFrontOverlap.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.skPathSize.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.skBeginningPath.setOnSeekBarChangeListener(seekBarChangeListener);

        }
}