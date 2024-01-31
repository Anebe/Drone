package com.dji.drone.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dji.drone.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class MarkerUtil{
//    private List<Marker> add;
//    private List<Marker> remove;
//    private List<Marker> move;
//    private Polyline line;
//
//    public MarkerUtil(List<Marker> add, List<Marker> remove, List<Marker> move, Polyline line) {
//        this.add = add;
//        this.remove = remove;
//        this.move = move;
//        this.line = line;
//    }
//
//    public List<Marker> getAdd() {
//        return add;
//    }
//
//    public void setAdd(List<Marker> add) {
//        this.add = add;
//    }
//
//    public List<Marker> getRemove() {
//        return remove;
//    }
//
//    public void setRemove(List<Marker> remove) {
//        this.remove = remove;
//    }
//
//    public List<Marker> getMove() {
//        return move;
//    }
//
//    public void setMove(List<Marker> move) {
//        this.move = move;
//    }
//
//    public Polyline getLine() {
//        return line;
//    }
//
//    public void setLine(Polyline line) {
//        this.line = line;
//    }
//
//    private MarkerOptions getInstanceAddMarkerOptions(){
//        return new MarkerOptions()
//                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_add_circle_24))
//                .anchor(0.5f,0.5f)
//                .position(new LatLng(0.0,0.0));
//    }
//
//    private MarkerOptions getInstanceRemoveMarkerOptions(){
//        return new MarkerOptions()
//                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_remove_circle_24))
//                .anchor(0.5f,0.5f)
//                .position(new LatLng(0.0,0.0));
//    }
//
//    private MarkerOptions getInstanceMoveMarkerOptions(){
//        return new MarkerOptions()
//                .icon(bitmapDescriptorFromVector(R.drawable.ic_round_move_circle_24))
//                .anchor(0.5f,0.5f)
//                .draggable(true)
//                .position(new LatLng(0.0,0.0));
//    }
//
//    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }

}
