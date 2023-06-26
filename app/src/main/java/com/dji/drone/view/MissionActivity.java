package com.dji.drone.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class MissionActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        View.OnClickListener {
    private final String TAG = getClass().getName();

    private CheckBox chk_isPossibleAddPolygon;
    private Button btn_upload;
    private Button btn_start;
    private Button btn_load;

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void initData() {
        polygonOptions = new PolygonOptions()
                .strokeColor(Color.BLUE)
                .strokeWidth(1f);

        markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        waypointPathMaker = new WaypointPathMaker(3);
        mission = new Mission();

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