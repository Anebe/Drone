package com.dji.drone.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.databinding.FragmentMapBinding;
import com.dji.drone.viewModel.MissionViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
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

    private FragmentMapBinding binding;

    private GoogleMap map;
    private Polygon polygon;
    private List<Marker> addMarkers;
    private List<Marker> removeMarkers;
    private List<Marker> moveMarkers;

    private MissionViewModel missionViewModel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        view = binding.getRoot();


        initUI();
        initData();
        initListener();
        initObserver();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(missionViewModel.getActualMission() != null){
            missionViewModel.updateLatLng((moveMarkers.size() > 0) ?
                    polygon.getPoints() :
                    new ArrayList<>());
        }
    }

    private void initUI() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(googleMap ->{
                map = googleMap;
                initMapData();
                initMapListener();
            });
        }
        switchButtons();
    }

    private void initData() {
        addMarkers = new ArrayList<>();
        removeMarkers = new ArrayList<>();
        moveMarkers = new ArrayList<>();

        missionViewModel = new ViewModelProvider(requireActivity()).get(MissionViewModel.class);
    }

    private void initListener(){
        binding.imageButtonCreate.setOnClickListener(v -> {
            if(map != null){
                PolygonOptions polygonOptions = getInstancePolygonOptions();

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
        });
        binding.imageButtonDelete.setOnClickListener(v -> {
            if(map != null){
                map.clear();
                addMarkers.clear();
                removeMarkers.clear();
                moveMarkers.clear();
                switchButtons();
            }
        });
        

        binding.switchMarkerControl.setOnClickListener(v -> {
            updateMarkerVisible();
            if(binding.switchMarkerControl.isChecked()){
                binding.switchMarkerControl.setThumbResource(R.drawable.ic_round_add_circle_24);
            }else{
                binding.switchMarkerControl.setThumbResource(R.drawable.ic_round_remove_circle_24);
            }

        });
    }

    private void initObserver(){
    }

    private void initMapData(){
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if(missionViewModel.getActualMission() != null){
            List<LatLng> latLngs = missionViewModel.getAllLatLng();
            if(latLngs.size() > 0){

                PolygonOptions polygonOptions = getInstancePolygonOptions();
                for (LatLng item: latLngs) {
                    polygonOptions.add(item);
                }

                map.clear();
                polygon = map.addPolygon(polygonOptions);
                switchButtons();
                createUpdateMarker(latLngs.size()-1);
            }
        }
    }
    //-------------------------------------------------------
    private LatLng positionMarkerOnPolygon(List<LatLng> polygonPoints, double percentage) {
        int numPoints = polygonPoints.size();
        if (numPoints < 2) {
            return null;
        }
        double totalDistance = 0.0;
        double targetDistance = percentage * calculatePolygonDistance(polygonPoints);

        for (int i = 1; i < numPoints; i++) {
            LatLng prevPoint = polygonPoints.get(i - 1);
            LatLng currPoint = polygonPoints.get(i);
            double distance = calculateDistance(prevPoint, currPoint);
            totalDistance += distance;

            if (totalDistance >= targetDistance) {
                double fraction = (targetDistance - (totalDistance - distance)) / distance;
                return interpolateLatLng(prevPoint, currPoint, fraction);
            }else if(i == numPoints-1){
                i = 1;
            }
        }

        return null;
    }

    private double calculatePolygonDistance(List<LatLng> polygonPoints) {
        double totalDistance = 0.0;
        int numPoints = polygonPoints.size();

        for (int i = 1; i < numPoints; i++) {
            LatLng prevPoint = polygonPoints.get(i - 1);
            LatLng currPoint = polygonPoints.get(i);
            double distance = calculateDistance(prevPoint, currPoint);
            totalDistance += distance;
        }

        return totalDistance;
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        return Math.sqrt(Math.pow((point2.latitude - point1.latitude), 2) +
                Math.pow((point2.longitude - point1.longitude), 2));
    }

    private LatLng interpolateLatLng(LatLng point1, LatLng point2, double fraction) {
        double lat = point1.latitude + (point2.latitude - point1.latitude) * fraction;
        double lng = point1.longitude + (point2.longitude - point1.longitude) * fraction;
        return new LatLng(lat, lng);
    }
    //-------------------------------------------------------

    @SuppressLint("PotentialBehaviorOverride")//Not using clustering, GeoJson, or KML
    private void initMapListener(){
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                updateMoveMarkerPosition(marker);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                updateMoveMarkerPosition(marker);
            }
        });

        map.setOnMarkerClickListener(marker -> {
            if(addMarkers.contains(marker) && binding.switchMarkerControl.isChecked()){
                int nextIndex = addMarkers.indexOf(marker) + 1;
                List<LatLng> aux = polygon.getPoints();
                aux.add(nextIndex, marker.getPosition());
                polygon.setPoints(aux);

                createUpdateMarker(1);
                return true;
            }
            else if(removeMarkers.contains(marker) && !binding.switchMarkerControl.isChecked() && removeMarkers.size() > 3){
                int actualIndex = removeMarkers.indexOf(marker);

                removeMarkers.remove(actualIndex).remove();
                addMarkers.remove(actualIndex).remove();
                moveMarkers.remove(actualIndex).remove();

                List<LatLng> copy = polygon.getPoints();
                copy.remove(actualIndex);
                if(actualIndex == 0){
                    copy.set(copy.size()-1, copy.get(0));
                }
                polygon.setPoints(copy);
                updateAddMarkersPosition();
                return true;
            }
            return false;
        });
        
    }
    //--------------------------------------------------------
    private void switchButtons(){
        if(binding.imageButtonDelete.isEnabled()){
            binding.imageButtonDelete.setEnabled(false);
            binding.imageButtonDelete.setAlpha(0.4f);
            binding.imageButtonCreate.setEnabled(true);
            binding.imageButtonCreate.setAlpha(1f);
        }
        else if(binding.imageButtonCreate.isEnabled()){
            binding.imageButtonCreate.setEnabled(false);
            binding.imageButtonCreate.setAlpha(0.4f);
            binding.imageButtonDelete.setEnabled(true);
            binding.imageButtonDelete.setAlpha(1f);
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createUpdateMarker(int qtd){
        MarkerOptions addMO = getInstanceAddMarkerOptions();
        MarkerOptions removeMO = getInstanceRemoveMarkerOptions();
        MarkerOptions moveMO = getInstanceMoveMarkerOptions();

        for (int i = 0; i < qtd; i++) {
            addMarkers.add(map.addMarker(addMO));
            removeMarkers.add(map.addMarker(removeMO));
            moveMarkers.add(map.addMarker(moveMO));
        }
        updateMarkersPosition();
        updateMarkerVisible();
    }

    private void updateMarkerVisible(){
        boolean setMoveVisible = binding.switchMarkerControl.isChecked();

        for (int i = 0; i < moveMarkers.size(); i++) {
            moveMarkers.get(i).setVisible(setMoveVisible);
            removeMarkers.get(i).setVisible(!setMoveVisible);
        }
    }

    private void updateMarkersPosition(){
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

    private void updateAddMarkersPosition(){
        for (int i = 0; i < addMarkers.size(); i++) {

            double avgLatitude;
            double avgLongitude;
            double actualLatitude = polygon.getPoints().get(i).latitude;
            double actualLongitude = polygon.getPoints().get(i).longitude;

            if(i == polygon.getPoints().size()-1){
                avgLatitude = (actualLatitude + polygon.getPoints().get(0).latitude)/2;
                avgLongitude = (actualLongitude + polygon.getPoints().get(0).longitude)/2;
            }
            else{
                avgLatitude = (actualLatitude + polygon.getPoints().get(i+1).latitude)/2;
                avgLongitude = (actualLongitude + polygon.getPoints().get(i+1).longitude)/2;
            }
            LatLng avg = new LatLng(avgLatitude, avgLongitude);

            addMarkers.get(i).setPosition(avg);
        }
    }

    private void updateMoveMarkerPosition(Marker marker){
        if(moveMarkers.contains(marker)){
            int vertexIndex = moveMarkers.indexOf(marker);
            List<LatLng> copy = polygon.getPoints();
            copy.set(vertexIndex, marker.getPosition());
            if(vertexIndex == 0){
                copy.set(copy.size()-1, marker.getPosition());
            }
            polygon.setPoints(copy);
            updateAddMarkersPosition();
            updateMarkersPosition();
        }
    }
    //----------------------------------------------------------------
    private PolygonOptions getInstancePolygonOptions(){
        return new PolygonOptions()
                .strokeColor(Color.BLUE)
                .strokeWidth(3f);
    }

    private MarkerOptions getInstanceAddMarkerOptions(){
        return new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_add_circle_24))
                .anchor(0.5f,0.5f)
                .position(new LatLng(0.0,0.0));
    }

    private MarkerOptions getInstanceRemoveMarkerOptions(){
        return new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_remove_circle_24))
                .anchor(0.5f,0.5f)
                .position(new LatLng(0.0,0.0));
    }

    private MarkerOptions getInstanceMoveMarkerOptions(){
        return new MarkerOptions()
                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_move_circle_24))
                .anchor(0.5f,0.5f)
                .draggable(true)
                .position(new LatLng(0.0,0.0));
    }

}