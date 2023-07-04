package com.dji.drone.view.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dji.drone.R;
import com.dji.drone.viewModel.MainViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapFragment extends Fragment {

    private final String TAG = getClass().getName();

    private CheckBox chk_isPossibleAddPolygon;
    private TextView tv_statusMission;
    private Button btn_start;
    private Button btn_upload;

    private GoogleMap map;
    private PolygonOptions polygonOptions;
    private MarkerOptions markerOptions;

    private MainViewModel mainViewModel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        initUI();
        initData();
        initListener();
        initObserver();

        return view;
    }

    private void initUI() {
        chk_isPossibleAddPolygon = view.findViewById(R.id.chk_isPossibleAddPolygon);
        tv_statusMission = view.findViewById(R.id.tv_statusMission);
        btn_start = view.findViewById(R.id.btn_start);
        btn_upload = view.findViewById(R.id.btn_upload);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(googleMap ->{
                map = googleMap;
                initMapListener();
            });
        }

    }

    private void initData() {
        polygonOptions = new PolygonOptions()
                .strokeColor(Color.BLUE)
                .strokeWidth(2f);

        markerOptions = new MarkerOptions();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getProgressUploadWaypointMission().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_statusMission.setText(s);
            }
        });
        mainViewModel.getDroneLatLnt().observe(getViewLifecycleOwner(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                markerOptions.position(latLng);
            }
        });
    }

    private void initListener(){

        btn_start.setOnClickListener(v -> mainViewModel.startMission());

        btn_upload.setOnClickListener(v -> mainViewModel.uploadMission(polygonOptions.getPoints()));
    }

    private void initObserver(){

    }
    private void initMapListener(){
        if(map != null){
            map.setOnMapClickListener(latLng -> {
                if(!chk_isPossibleAddPolygon.isChecked()){
                    return;
                }
                polygonOptions.add(latLng);
                updateMap();
            });
        }
    }

    private void updateMap(){
        map.clear();
        map.addPolygon(polygonOptions);
        if(markerOptions.getPosition() != null){
            map.addMarker(markerOptions);
        }

    }


}