package com.dji.drone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dji.drone.R;
import com.dji.drone.viewModel.MainViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import dji.common.mission.waypoint.WaypointMissionState;

public class MissionActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        View.OnClickListener {

    private final String TAG = getClass().getName();

    private CheckBox chk_isPossibleAddPolygon;
    private TextView tv_statusMission;
    private Button btn_start;
    private Button btn_upload;

    private GoogleMap map;
    private PolygonOptions polygonOptions;
    private MarkerOptions markerOptions;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        initUI();
        initData();
    }

    private void initUI() {
        chk_isPossibleAddPolygon = findViewById(R.id.chk_isPossibleAddPolygon);
        tv_statusMission = findViewById(R.id.tv_statusMission);
        btn_start = findViewById(R.id.btn_start);
        btn_upload = findViewById(R.id.btn_upload);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        btn_start.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }

    private void initData() {
        polygonOptions = new PolygonOptions()
                .strokeColor(Color.BLUE)
                .strokeWidth(2f);

        markerOptions = new MarkerOptions();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getProgressUploadWaypointMission().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_statusMission.setText(s);
            }
        });
        mainViewModel.getDroneLatLnt().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                markerOptions.position(latLng);
            }
        });
    }

    //UI
    @Override
    public void onClick(View v) {
        int actualComponent = v.getId();
        if(actualComponent == R.id.btn_upload){
            mainViewModel.uploadMission(polygonOptions.getPoints());
        }
        if(actualComponent == R.id.btn_start){
            mainViewModel.startMission();
        }
    }

    //------------------------------------------------------------
    private final Observer<WaypointMissionState> waypointMissionStateObserver = new Observer<WaypointMissionState>() {
        @Override
        public void onChanged(WaypointMissionState waypointMissionState) {
            Log.d(TAG, waypointMissionState.toString());
            tv_statusMission.setText(waypointMissionState.toString());
        }
    };
    //MAP--------------------------------------------------------
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(MissionActivity.this);

        updateLocationUI();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(!chk_isPossibleAddPolygon.isChecked()){
            return;
        }

        polygonOptions.add(latLng);

        updateMap();
    }

    private void updateMap(){
        map.clear();
        map.addPolygon(polygonOptions);
        if(markerOptions.getPosition() != null){
            map.addMarker(markerOptions);
        }

    }

    private void updateLocationUI()     {
        if (map == null) {
            return;
        }
        try {
            if (isPermissionLocationGuarantied()) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }

        } catch (SecurityException e)  {
            Log.e(TAG, "Exception: " +  e.getMessage());
        }
    }

    private boolean isPermissionLocationGuarantied(){
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
}