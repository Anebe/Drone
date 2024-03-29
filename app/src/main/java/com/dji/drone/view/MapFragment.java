package com.dji.drone.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.dji.drone.R;
import com.dji.drone.databinding.FragmentMapBinding;
import com.dji.drone.model.room.Point2D;
import com.dji.drone.viewModel.MissionViewModel;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private FragmentMapBinding binding;

    private GoogleMap map;
    private Polygon polygon;
    private Polyline prePath;
    private List<Marker> addMarkers;
    private List<Marker> removeMarkers;
    private List<Marker> moveMarkers;
    //private Polyline lineBuild;
    //private Polyline linePath;
    //private Marker droneMarker;
    private MissionViewModel missionViewModel;
    private LatLng droneLatLng;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

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
            boolean result = missionViewModel.updateLatLng((moveMarkers.size() > 0) ?
                    prePath.getPoints() :
                    new ArrayList<>());
            Log.d(TAG, "Update LatLng in database: " + result);
        }
    }

    private void initUI() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(googleMap ->{
                map = googleMap;
                initMapData();
                initMapListener();
                initMapObserver();
            });
        }
        switchButtons();
    }

    private void initData() {
        droneLatLng = new LatLng(0.0,0.0);

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

                //if(polygon != null){
                //    map.clear();
                //    PolylineOptions polylineOptions = getInstancePolylineOptions();
                //    int lastIndex = polygon.getPoints().size()-1;
                //    polylineOptions.add(polygon.getPoints().subList(0, lastIndex).toArray(new LatLng[0]));
                //    lineBuild = map.addPolyline(polylineOptions);
                //    polygon = null;
                //    createUpdateMarker(lineBuild.getPoints().size());
                //}
                //binding.grpCreatePath.setVisibility(View.VISIBLE);
                //binding.grpManagePath.setVisibility(View.GONE);
            }
        });

        binding.imageButtonDelete.setOnClickListener(v -> {
            if(map != null){
                map.clear();
                addMarkers.clear();
                removeMarkers.clear();
                moveMarkers.clear();
                polygon = null;

                //lineBuild = null;
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

//        binding.imgBntConfirm.setOnClickListener(v ->{
//            binding.grpCreatePath.setVisibility(View.GONE);
//            binding.grpManagePath.setVisibility(View.VISIBLE);
//
//            if(lineBuild != null){
//                map.clear();
//                PolygonOptions polygonOptions = getInstancePolygonOptions();
//                polygonOptions.add(lineBuild.getPoints().toArray(new LatLng[0]));
//                polygon = map.addPolygon(polygonOptions);
//                lineBuild = null;
//            }
//        });
//
//        binding.btnDronePos.setOnClickListener(v -> {
//            Point2D point2D = missionViewModel.getDroneLatLng().getValue();
//
//            if(point2D != null){
//                LatLng latLng = new LatLng(point2D.getLatitude(), point2D.getLongitude());
//
//                if(lineBuild == null){
//                    lineBuild = map.addPolyline(getInstancePolylineOptions().add(latLng));
//                }
//                else{
//                    List<LatLng> latLngList = lineBuild.getPoints();
//                    latLngList.add(latLng);
//                    lineBuild.setPoints(latLngList);
//                }
//                createUpdateMarker(1);
//            }
//
//        });
        binding.imgBtnCreate.setOnClickListener(view -> {
            addPathOnDrone();
        });
    }

    private void initObserver(){
        missionViewModel.getDroneLatLng().observe(this, dronePoint2d -> {
            droneLatLng = new LatLng(dronePoint2d.getLatitude(), dronePoint2d.getLongitude());
        });
    }

    private void addPathOnDrone() {
        if(map != null){
            if(prePath == null){
                PolylineOptions polylineOptions = getInstancePolylineOptions();

                polylineOptions.add(droneLatLng);

                prePath = map.addPolyline(polylineOptions);
            }

            createUpdateMarker(1);
        }
    }

    @SuppressLint("MissingPermission")
    private void initMapData(){
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//        if(missionViewModel.getActualMission() != null){
//            List<LatLng> latLngList = missionViewModel.getAllLatLng();
//            if(latLngList.size() > 0){
//
//                PolygonOptions polygonOptions = getInstancePolygonOptions();
//                for (LatLng item: latLngList) {
//                    polygonOptions.add(item);
//                }
//
//                map.clear();
//                polygon = map.addPolygon(polygonOptions);
//                switchButtons();
//                createUpdateMarker(latLngList.size()-1);
//            }
//        }

        //droneMarker = map.addMarker(getInstanceDroneMarkerOption());

        //Verify permission, justify @SuppressLint
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

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

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
//                if(binding.grpCreatePath.getVisibility() == View.VISIBLE){
//                    if(lineBuild == null){
//                        lineBuild = map.addPolyline(getInstancePolylineOptions().add(latLng));
//                    }
//                    else{
//                        List<LatLng> latLngList = lineBuild.getPoints();
//                        latLngList.add(latLng);
//                        lineBuild.setPoints(latLngList);
//                    }
//                    createUpdateMarker(1);
//                }
            }
        });

    }

    private void initMapObserver(){
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
        //List<LatLng> points = polygon.getPoints();
        List<LatLng> points = prePath.getPoints();
        //List<LatLng> points = lineBuild.getPoints();

        for (int i = 0; i < addMarkers.size(); i++) {

            double avgLatitude;
            double avgLongitude;
            double actualLatitude = points.get(i).latitude;
            double actualLongitude = points.get(i).longitude;

            if(i == prePath.getPoints().size()-1){
            //if(i == lineBuild.getPoints().size()-1){
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
            double actualLatitude = prePath.getPoints().get(i).latitude;
            double actualLongitude = prePath.getPoints().get(i).longitude;

            if(i == prePath.getPoints().size()-1){
                avgLatitude = (actualLatitude + prePath.getPoints().get(0).latitude)/2;
                avgLongitude = (actualLongitude + prePath.getPoints().get(0).longitude)/2;
            }
            else{
                avgLatitude = (actualLatitude + prePath.getPoints().get(i+1).latitude)/2;
                avgLongitude = (actualLongitude + prePath.getPoints().get(i+1).longitude)/2;
            }
            LatLng avg = new LatLng(avgLatitude, avgLongitude);

            addMarkers.get(i).setPosition(avg);
        }
    }

    private void updateMoveMarkerPosition(Marker marker){
        if(moveMarkers.contains(marker)){
            int vertexIndex = moveMarkers.indexOf(marker);
            List<LatLng> copy = prePath.getPoints();
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

    @NonNull
    private PolygonOptions getInstancePolygonOptions(){
        return new PolygonOptions()
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.light_blue_400))
                .strokeWidth(7f);
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

    private MarkerOptions getInstanceDroneMarkerOption(){
        return new MarkerOptions()
                .icon(bitmapDescriptorFromVector(dji.midware.R.drawable.btn_draw_start_plane))
                .anchor(0.5f,0.5f)
                .draggable(false)
                .visible(false)
                .position(new LatLng(0.0,0.0));
    }

    private PolylineOptions getInstancePolylineOptions(){
        return new PolylineOptions()
                .color(ContextCompat.getColor(requireContext(), R.color.teal_200))
                .width(9f);
    }
}