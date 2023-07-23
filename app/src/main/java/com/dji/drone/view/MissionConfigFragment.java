package com.dji.drone.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.model.room.MissionEntity;
import com.dji.drone.viewModel.MissionViewModel;

public class MissionConfigFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private SeekBar skb_frontOverlap;
    private SeekBar skb_sideOverlap;
    private TextView tv_frontOverlapValue;
    private TextView tv_sideOverlapValue;
    private EditText et_name;
    private EditText et_speed;
    private EditText et_spacing;
    private EditText et_initialAltitude;
    private EditText et_finalAltitude;
    private Spinner sp_finishedAction;
    private Spinner sp_gotoMode;
    private CheckBox chk_includeRoof;
    private EditText et_lines;
    private SeekBar skb_pathSize;
    private SeekBar skb_tv_beginningPath;
    private TextView tv_pathSizeValue;
    private TextView tv_beginningPathValue;



    private MissionViewModel missionViewModel;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mission_config, container, false);

        initUI();
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

            String aux = et_speed.getText().toString();
            speed = (aux.equals("")) ? 0f : Float.parseFloat(aux);

            aux = et_spacing.getText().toString();
            spacing = (aux.equals("")) ? 0 : Integer.parseInt(aux);

            aux = et_initialAltitude.getText().toString();
            initialAltitude = (aux.equals("")) ? 0f : Float.parseFloat(aux);

            aux = et_finalAltitude.getText().toString();
            finalAltitude = (aux.equals("")) ? 0f :  Float.parseFloat(aux);

            aux = et_lines.getText().toString();
            lines = (aux.equals("")) ? 0 : Integer.parseInt(aux);

            MissionEntity missionEntity = missionViewModel.getActualMission();
            missionEntity.setName(et_name.getText().toString());
            missionEntity.setAuto_flight_speed(speed);
            missionEntity.setMax_flight_speed(speed);
            missionEntity.setSpacing(spacing);
            missionEntity.setInitial_altitude(initialAltitude);
            missionEntity.setFinal_altitude(finalAltitude);
            missionEntity.setFinished_action(sp_finishedAction.getSelectedItemPosition());
            missionEntity.setGoto_mode(sp_gotoMode.getSelectedItemPosition());
            missionEntity.setInclude_roof(chk_includeRoof.isChecked());
            missionEntity.setLines(lines);
            //missionEntity.setFront_overlap(skb_frontOverlap.getProgress());
            //missionEntity.setSide_overlap(skb_sideOverlap.getProgress());
            missionEntity.setInitial_path_percentage(skb_tv_beginningPath.getProgress());
            missionEntity.setFinal_path_percentage(skb_pathSize.getProgress());
            missionViewModel.updateMission();
        }
    }

    private void initUI(){
        skb_frontOverlap = view.findViewById(R.id.sk_front_overlap);
        skb_sideOverlap = view.findViewById(R.id.sk_side_overlap);
        tv_frontOverlapValue = view.findViewById(R.id.tv_front_overlap_value);
        tv_sideOverlapValue = view.findViewById(R.id.tv_side_overlap_value);
        skb_tv_beginningPath = view.findViewById(R.id.sk_beginning_path);
        skb_pathSize = view.findViewById(R.id.sk_path_size);
        tv_beginningPathValue = view.findViewById(R.id.tv_beginning_path_value);
        tv_pathSizeValue = view.findViewById(R.id.tv_path_size_value);
        et_name = view.findViewById(R.id.editTextName);
        et_finalAltitude = view.findViewById(R.id.editTextFinalAltitude);
        et_spacing = view.findViewById(R.id.editTextSpacing);
        et_speed = view.findViewById(R.id.editTextSpeed);
        et_initialAltitude = view.findViewById(R.id.editTextInitialAltitude);
        sp_gotoMode = view.findViewById(R.id.spinnerGotoMode);
        sp_finishedAction = view.findViewById(R.id.spinnerFinishedAction);
        chk_includeRoof = view.findViewById(R.id.checkBoxIncludeRoof);
        et_lines = view.findViewById(R.id.editTextNumberSignedLines);
    }

    private void initData(){
        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);

        if(missionViewModel.getActualMission() != null){
            MissionEntity missionEntity = missionViewModel.getActualMission();

            String msg;
            skb_frontOverlap.setProgress(missionEntity.getFront_overlap());
            msg = missionEntity.getFront_overlap() + "%";
            tv_frontOverlapValue.setText(msg);

            skb_sideOverlap.setProgress(missionEntity.getSide_overlap());
            msg = missionEntity.getSide_overlap() + "%";
            tv_sideOverlapValue.setText(msg);

            skb_tv_beginningPath.setProgress(missionEntity.getInitial_path_percentage());
            msg = missionEntity.getInitial_path_percentage() + "%";
            tv_beginningPathValue.setText(msg);

            skb_pathSize.setProgress(missionEntity.getFinal_path_percentage());
            msg = missionEntity.getFinal_path_percentage() + "%";
            tv_pathSizeValue.setText(msg);

            et_name.setText(missionEntity.getName());
            et_speed.setText(String.valueOf(missionEntity.getAuto_flight_speed()));
            et_spacing.setText(String.valueOf(missionEntity.getSpacing()));
            et_initialAltitude.setText(String.valueOf(missionEntity.getInitial_altitude()));
            et_finalAltitude.setText(String.valueOf(missionEntity.getFinal_altitude()));
            sp_finishedAction.setSelection(missionEntity.getFinished_action());
            sp_gotoMode.setSelection(missionEntity.getGoto_mode());
            chk_includeRoof.setChecked(missionEntity.isInclude_roof());
            et_lines.setText(String.valueOf(missionEntity.getLines()));
        }

    }

    private void initListener(){
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            private void update(){
                String msg;
                msg = skb_frontOverlap.getProgress() + "%";
                tv_frontOverlapValue.setText(msg);
                msg = skb_pathSize.getProgress() + "%";
                tv_pathSizeValue.setText(msg);
                msg = skb_sideOverlap.getProgress() + "%";
                tv_sideOverlapValue.setText(msg);
                msg = skb_tv_beginningPath.getProgress() + "%";
                tv_beginningPathValue.setText(msg);
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

        skb_sideOverlap.setOnSeekBarChangeListener(seekBarChangeListener);

        skb_frontOverlap.setOnSeekBarChangeListener(seekBarChangeListener);

        skb_pathSize.setOnSeekBarChangeListener(seekBarChangeListener);

        skb_tv_beginningPath.setOnSeekBarChangeListener(seekBarChangeListener);

        }
}