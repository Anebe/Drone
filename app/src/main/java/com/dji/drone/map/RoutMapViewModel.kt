package com.dji.drone.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dji.drone.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.*

data class MarkerData(
        var icon: BitmapDescriptor,
        val draggable: Boolean,
        var latLng: LatLng,
)

class RoutMapViewModel : ViewModel() {


    var markers = MutableLiveData<MutableList<MarkerData>>()
    var polygon = MutableLiveData<MutableList<LatLng>>()

    //TODO lembrar de usar o tag do marker para reconhecelo quando for fazer comparações(ex: a tag ser o index dele na lista para encontra-lo)


    fun createInitialBaseRoute(map: GoogleMap) {
        val polygonOptions: PolygonOptions = getInstancePolygonOptions()
        val projection: Projection = map.projection
        val cameraCenter = projection.toScreenLocation(map.cameraPosition.target)

        val diff = 100
        val pointTriangle = Point(cameraCenter.x, cameraCenter.y)

        pointTriangle.y -= diff
        val triangulo1 = projection.fromScreenLocation(pointTriangle)

        pointTriangle.x += diff
        pointTriangle.y = cameraCenter.y + diff
        val triangulo2 = projection.fromScreenLocation(pointTriangle)

        pointTriangle.x = cameraCenter.x - diff
        pointTriangle.y = cameraCenter.y + diff
        val triangulo3 = projection.fromScreenLocation(pointTriangle)

        polygonOptions.add(triangulo1)
        polygonOptions.add(triangulo2)
        polygonOptions.add(triangulo3)


        val poly = map.addPolygon(polygonOptions)
        for (point in poly.points){
            polygon.value?.add(point)
        }
        //createMarker(3)
        //updateMarker()
    }

    /*
    fun createMarker(qtd: Int)
    {
        val context = requireContext()
        val addMO: MarkerOptions = markerOptionsFactory(
                R.drawable.ic_round_add_circle_24, false, context)
        val removeMO: MarkerOptions = markerOptionsFactory(
                R.drawable.ic_round_remove_circle_24, false, context)
        val moveMO: MarkerOptions = markerOptionsFactory(
                R.drawable.ic_round_move_circle_24, true, context)

        for (i in 0 until qtd) {
            moveMO.visible(isMoveMarkerVisible)
            removeMO.visible(!isMoveMarkerVisible)

            map.addMarker(addMO)?.let {
                it.tag = addMarkers.size
                addMarkers.add(it)
            }
            map.addMarker(removeMO)?.let { removeMarkers.add(it) }
            map.addMarker(moveMO)?.let { moveMarkers.add(it) }
        }
    }
    */

    companion object {
        private fun getInstancePolygonOptions(): PolygonOptions {
            return PolygonOptions()
                    .strokeColor(R.color.light_blue_400)
                    .strokeWidth(7f)
        }

        private fun markerOptionsFactory(id: Int, draggable: Boolean, context: Context): MarkerOptions {
            val icon = bitmapDescriptorFromVector(id, context)
            return MarkerOptions()
                    .icon(icon)
                    .anchor(0.5f, 0.5f)
                    .draggable(draggable)
                    .position(LatLng(0.0, 0.0))
        }

        private fun bitmapDescriptorFromVector(vectorResId: Int, context: Context): BitmapDescriptor {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable!!.setBounds(
                    0,
                    0,
                    vectorDrawable.intrinsicWidth,
                    vectorDrawable.intrinsicHeight
            )
            val bitmap = Bitmap.createBitmap(
                    vectorDrawable.intrinsicWidth,
                    vectorDrawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

    }


}