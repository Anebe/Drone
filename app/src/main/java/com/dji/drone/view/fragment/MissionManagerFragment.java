package com.dji.drone.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dji.drone.R;

import dji.thirdparty.afinal.core.AsyncTask;

public class MissionManagerFragment extends Fragment {

    private SeekBar skb_frontOverlap;
    private SeekBar skb_sideOverlap;
    private TextView tv_frontOverlapValue;
    private TextView tv_sideOverlapValue;

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mission_manager, container, false);

        skb_frontOverlap = view.findViewById(R.id.sk_front_overlap);
        skb_sideOverlap = view.findViewById(R.id.sk_side_overlap);

        tv_frontOverlapValue = view.findViewById(R.id.tv_front_overlap_value);
        tv_sideOverlapValue = view.findViewById(R.id.tv_side_overlap_value);

        initListener();
        return view;
    }

    private void initListener(){
        skb_sideOverlap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_sideOverlapValue.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        skb_frontOverlap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_frontOverlapValue.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}