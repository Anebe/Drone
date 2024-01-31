package com.dji.drone.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dji.drone.R
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.*

data class MarkerData(
    var icon: BitmapDescriptor,
    val draggable: Boolean,
    var latLng: LatLng,
)
data class PolygonData(
    val coordinates: MutableList<LatLng>,
)

class RoutMapViewModel : ViewModel(){

    /*
    private var polygon: Polygon? = null
    private val markers: MutableList<Marker> = ArrayList()

    private var moveMarkerMode = true
    */
    //var polygon = MutableLiveData<Polygon?>()
    //val addMarkers = MutableLiveData<MutableList<Marker>>()
    //val removeMarkers = MutableLiveData<MutableList<Marker>>()
    //val moveMarkers = MutableLiveData<MutableList<Marker>>()

    var markers = MutableLiveData<MutableList<MarkerData>>()
    var polygon = MutableLiveData<PolygonData>()

    /*fun createInitialBaseRoute(centerGeo: Projection, centerScreen: Point): PolygonOptions{
        val polygonOptions: PolygonOptions = getInstancePolygonOptions()

        val diff = 100
        val point = Point(centerScreen.x, centerScreen.y)

        point.y -= diff
        val triangulo1 = centerGeo.fromScreenLocation(point)

        point.x += diff
        point.y = centerScreen.y + diff
        val triangulo2 = centerGeo.fromScreenLocation(point)

        point.x = centerScreen.x - diff
        point.y = centerScreen.y + diff
        val triangulo3 = centerGeo.fromScreenLocation(point)

        polygonOptions.add(triangulo1)
        polygonOptions.add(triangulo2)
        polygonOptions.add(triangulo3)
        polygon.value?.coordinates ?:  += polygonOptions.points as MutableList
        return polygonOptions

        //TODO lembrar de usar o tag do marker para reconhecelo quando for fazer comparações(ex: a tag ser o index dele na lista para encontra-lo)
        //polygon = map.addPolygon(polygonOptions)
        //createMarker(3)
        //updateMarker()
    }*/
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