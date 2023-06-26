package com.dji.drone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dji.drone.R;
import com.dji.drone.viewModel.Mission;
import com.dji.drone.viewModel.WaypointPathMaker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionState;

public class MissionActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        View.OnClickListener {
    private final String TAG = getClass().getName();

    private CheckBox chk_isPossibleAddPolygon;
    private Button btn_upload;
    private Button btn_start;
    private Button btn_load;
    private TextView tv_statusMission;

    private GoogleMap map;
    private PolygonOptions polygonOptions;
    private MarkerOptions markerOptions;
    private LocationManager locationManager;
    private static final int DEFAULT_ZOOM = 15;

    private WaypointPathMaker waypointPathMaker;
    private Mission mission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        initUI();
        initData();
    }

    private void initUI() {
        chk_isPossibleAddPolygon = findViewById(R.id.chk_isPossibleAddPolygon);
        btn_load = findViewById(R.id.btn_load);
        btn_upload = findViewById(R.id.btn_upload);
        btn_start = findViewById(R.id.btn_start);
        tv_statusMission = findViewById(R.id.tv_statusMission);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        btn_upload.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        btn_start.setOnClickListener(this);
    }

    private void initData() {
        polygonOptions = new PolygonOptions()
                .strokeColor(Color.BLUE)
                .strokeWidth(2f);

        markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        waypointPathMaker = new WaypointPathMaker(3);
        mission = new ViewModelProvider(this).get(Mission.class);
        mission._missionState.observe(this, waypointMissionStateObserver);


    }

    //UI
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                mission.prepareMission(waypointPathMaker.makePath(polygonOptions.getPoints(), 1.0, 4.0));
                break;
            case R.id.btn_upload:
                mission.uploadMission();
                break;
            case R.id.btn_start:
                mission.startMission();
                break;
            default:
                break;
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
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (isLocationPermissionGranted()) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e)  {
            Log.e(TAG, "Exception: " +  e.getMessage());
        }
    }

    private boolean isLocationPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
}