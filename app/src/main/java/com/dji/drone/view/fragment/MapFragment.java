package com.dji.drone.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.viewModel.MainViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private TextView tv_statusMission;
    private Button btn_start;
    private Button btn_upload;
    private ImageButton btn_create;
    private ImageButton btn_delete;
    private Switch swh_marker_control;

    private GoogleMap map;
    private Polygon polygon;
    private List<Marker> addMarkers;
    private List<Marker> removeMarkers;
    private List<Marker> moveMarkers;

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
        tv_statusMission = view.findViewById(R.id.tv_statusMission);
        btn_start = view.findViewById(R.id.btn_start);
        btn_upload = view.findViewById(R.id.btn_upload);
        btn_create = view.findViewById(R.id.imageButtonCreate);
        btn_delete = view.findViewById(R.id.imageButtonDelete);
        swh_marker_control = view.findViewById(R.id.switchMarkerControl);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(googleMap ->{
                map = googleMap;
                map.getUiSettings().setTiltGesturesEnabled(false);
                map.getUiSettings().setRotateGesturesEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                initMapListener();
            });
        }

    }

    private void initData() {
        addMarkers = new ArrayList<>();
        removeMarkers = new ArrayList<>();
        moveMarkers = new ArrayList<>();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        btn_delete.setEnabled(false);
    }

    private void initListener(){

        btn_start.setOnClickListener(v -> mainViewModel.startMission());

        btn_upload.setOnClickListener(v -> mainViewModel.uploadMission(polygon.getPoints()));

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map != null){
                    PolygonOptions polygonOptions = new PolygonOptions()
                            .strokeColor(Color.BLUE)
                            .strokeWidth(3f);

                    Projection projection = map.getProjection();
                    Point cameraCenter = projection.toScreenLocation(map.getCameraPosition().target);
                    int diff = 100;
                    LatLng triangulo1 = projection.fromScreenLocation(new Point(cameraCenter.x - diff, cameraCenter.y-diff));
                    LatLng triangulo2 = projection.fromScreenLocation(new Point(cameraCenter.x + diff, cameraCenter.y-diff));
                    LatLng triangulo3 = projection.fromScreenLocation(new Point(cameraCenter.x-diff, cameraCenter.y+diff));

                    polygonOptions.add(triangulo1);
                    polygonOptions.add(triangulo2);
                    polygonOptions.add(triangulo3);
                    polygon = map.addPolygon(polygonOptions);

                    createUpdateMarker(3);
                    switchButtons();

                }

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map != null){
                    map.clear();
                    addMarkers.clear();
                    removeMarkers.clear();
                    moveMarkers.clear();
                    switchButtons();
                }
            }
        });

        swh_marker_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMarkerVisible();
                if(swh_marker_control.isChecked()){
                    swh_marker_control.setThumbResource(R.drawable.ic_round_add_circle_24);
                }else{
                    swh_marker_control.setThumbResource(R.drawable.ic_round_remove_circle_24);
                }

            }
        });
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void initMapListener(){
        if(map != null){

            map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(@NonNull Polygon polygon) {
                    if(!swh_marker_control.isChecked()){
                        //polygon
                    }
                }
            });

            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {
                    if(moveMarkers.contains(marker)){
                        int vertexIndex = moveMarkers.indexOf(marker);
                        List<LatLng> copy = polygon.getPoints();
                        copy.set(vertexIndex, marker.getPosition());
                        if(vertexIndex == 0){
                            copy.set(copy.size()-1, marker.getPosition());
                        }
                        polygon.setPoints(copy);
                        //updateAddMarkerPosition();
                        updateMarkerPosition();
                    }
                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    Log.d(TAG, "PEGUEI2");
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {
                    Log.d(TAG, "PEGUEI3");
                }
            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    if(!swh_marker_control.isChecked() || !addMarkers.contains(marker)){
                        return false;
                    }

                    int nextIndex = addMarkers.indexOf(marker) + 1;
                    List<LatLng> aux = polygon.getPoints();
                    aux.add(nextIndex, marker.getPosition());
                    polygon.setPoints(aux);

                    createUpdateMarker(1);
                    return true;
                }
            });
        }
    }

    private void initObserver(){
        mainViewModel.getProgressUploadWaypointMission().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_statusMission.setText(s);
            }
        });
    }

    private void switchButtons(){
        if(btn_create.isEnabled()){
            btn_create.setEnabled(false);
            btn_create.setAlpha(0.4f);
            btn_delete.setEnabled(true);
            btn_delete.setAlpha(1f);
        }else if(btn_delete.isEnabled()){
            btn_delete.setEnabled(false);
            btn_delete.setAlpha(0.4f);
            btn_create.setEnabled(true);
            btn_create.setAlpha(1f);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createUpdateMarker(int qtd){
        MarkerOptions addMO = new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_add_circle_24))
                .anchor(0.5f,0.5f)
                .position(new LatLng(0.0,0.0));
        MarkerOptions removeMO = new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_remove_circle_24))
                .anchor(0.5f,0.5f)
                .position(new LatLng(0.0,0.0));
        MarkerOptions moveMO = new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_baseline_open_with_24))
                .anchor(0.5f,0.5f)
                .draggable(true)
                .position(new LatLng(0.0,0.0));

        for (int i = 0; i < qtd; i++) {
            addMarkers.add(map.addMarker(addMO));
            removeMarkers.add(map.addMarker(removeMO));
            moveMarkers.add(map.addMarker(moveMO));
        }
        updateMarkerPosition();
        updateMarkerVisible();
    }

    private void updateMarkerVisible(){
        boolean setMoveVisible = swh_marker_control.isChecked();

        for (int i = 0; i < moveMarkers.size(); i++) {
            moveMarkers.get(i).setVisible(setMoveVisible);
            removeMarkers.get(i).setVisible(!setMoveVisible);
        }
    }

    private void updateMarkerPosition(){
        List<LatLng> points = polygon.getPoints();
        for (int i = 0; i < addMarkers.size(); i++) {

            double avgLatitude;
            double avgLongitude;
            double actualLatitude = points.get(i).latitude;
            double actualLongitude = points.get(i).longitude;

            if(i == polygon.getPoints().size()-1){
                avgLatitude = (actualLatitude + points.get(0).latitude)/2.0;
                avgLongitude = (actualLongitude + points.get(0).longitude)/2.0;
            }
            else{
                avgLatitude = (actualLatitude + points.get(i+1).latitude)/2.0;
                avgLongitude = (actualLongitude + points.get(i+1).longitude)/2.0;
            }
            LatLng avg = new LatLng(avgLatitude, avgLongitude);

            addMarkers.get(i).setPosition(avg);

            LatLng actualLatLng = new LatLng(actualLatitude, actualLongitude);
            removeMarkers.get(i).setPosition(actualLatLng);
            moveMarkers.get(i).setPosition(actualLatLng);
        }
    }

    private void updateAddMarkerPosition(){
        for (int i = 0; i < addMarkers.size(); i++) {

            double avgLatitude;
            double avgLongitude;
            double actualLatitude = polygon.getPoints().get(i).latitude;
            double actualLongetude = polygon.getPoints().get(i).longitude;

            if(i == polygon.getPoints().size()-1){
                avgLatitude = (actualLatitude + polygon.getPoints().get(0).latitude)/2;
                avgLongitude = (actualLongetude + polygon.getPoints().get(0).longitude)/2;
            }
            else{
                avgLatitude = (actualLatitude + polygon.getPoints().get(i+1).latitude)/2;
                avgLongitude = (actualLongetude + polygon.getPoints().get(i+1).longitude)/2;
            }
            LatLng avg = new LatLng(avgLatitude, avgLongitude);

            addMarkers.get(i).setPosition(avg);
        }
    }
}