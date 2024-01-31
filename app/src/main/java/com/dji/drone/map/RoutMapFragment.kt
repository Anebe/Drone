package com.dji.drone.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dji.drone.R
import com.dji.drone.databinding.FragmentRoutMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dji.sdk.mission.timeline.TimelineElement

class RoutMapFragment : Fragment() {

    private var binding: FragmentRoutMapBinding? = null
    private val viewmodel by viewModels<RoutMapViewModel>()

    private lateinit var map: GoogleMap
    private var polygon: Polygon? = null
    private var addMarkers: MutableList<Marker> = ArrayList()
    private val removeMarkers: MutableList<Marker> = ArrayList()
    private val moveMarkers: MutableList<Marker> = ArrayList()

    private var isMoveMarkerVisible = true

    val coordinates: List<LatLng>
        get() = polygon?.points as List<LatLng>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        binding = FragmentRoutMapBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            initData()
            initListener()
            initObserver()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initData()
    {
        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        //Verify permission, justify @SuppressLint
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ){
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }

        //TODO carregar informação de missão se tiver
    }

    @SuppressLint("PotentialBehaviorOverride") //Not using clustering, GeoJson, or KML
    private fun initListener()
    {
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
                updateMoveMarkerPosition(marker)
            }
            override fun onMarkerDragEnd(marker: Marker) {}
            override fun onMarkerDragStart(marker: Marker) {
                updateMoveMarkerPosition(marker)
            }
        })

        map.setOnMarkerClickListener { marker: Marker ->
            updateRoute(marker)
            false
        }

    }

    private fun initObserver(){

    }

    fun createInitialBaseRoute()
    {
        val polygonOptions: PolygonOptions = getInstancePolygonOptions()
        val projection: Projection = map.projection
        val cameraCenter = projection.toScreenLocation(map.cameraPosition.target)

        val diff = 100
        val point = Point(cameraCenter.x, cameraCenter.y)

        point.y -= diff
        val triangulo1 = projection.fromScreenLocation(point)

        point.x += diff
        point.y = cameraCenter.y + diff
        val triangulo2 = projection.fromScreenLocation(point)

        point.x = cameraCenter.x - diff
        point.y = cameraCenter.y + diff
        val triangulo3 = projection.fromScreenLocation(point)

        polygonOptions.add(triangulo1)
        polygonOptions.add(triangulo2)
        polygonOptions.add(triangulo3)

        polygon = map.addPolygon(polygonOptions)
        createMarker(3)
        updateMarker()
    }

    private fun createMarker(qtd: Int)
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

            map.addMarker(addMO)?.let { addMarkers.add(it) }
            map.addMarker(removeMO)?.let { removeMarkers.add(it) }
            map.addMarker(moveMO)?.let { moveMarkers.add(it) }
        }
    }

    private fun updateMarker()
    {
        updateMarkersPosition()
    }

    private fun updateMarkersPosition()
    {
        val points: MutableList<LatLng> = polygon?.points as MutableList<LatLng>

        for (i in addMarkers.indices) {
            var avgLatitude: Double
            var avgLongitude: Double

            val actualLatitude = points[i].latitude
            val actualLongitude = points[i].longitude

            val index: Int = if (i == points.size - 1) 0 else i + 1

            avgLatitude = (actualLatitude + points[index].latitude) / 2.0
            avgLongitude = (actualLongitude + points[index].longitude) / 2.0

            val avg = LatLng(avgLatitude, avgLongitude)
            addMarkers[i].position = avg
            val actualLatLng = LatLng(actualLatitude, actualLongitude)
            removeMarkers[i].position = actualLatLng
            moveMarkers[i].position = actualLatLng
        }

    }

    private fun updateRoute(marker: Marker)
    {
        if (addMarkers.contains(marker) && isMoveMarkerVisible) {
            increaseRoute(marker)
        }
        else if (removeMarkers.contains(marker) && !isMoveMarkerVisible && removeMarkers.size > 3) {
            shortenRoute(marker)
        }
    }

    private fun increaseRoute(marker: Marker)
    {
        val nextIndex = addMarkers.indexOf(marker) + 1
        polygon?.let {
            val aux = it.points
            aux.add(nextIndex, marker.position)
            it.points = aux
            createMarker(1)
            updateMarkersPosition()
        }
    }

    private fun shortenRoute(marker: Marker)
    {
        val actualIndex = removeMarkers.indexOf(marker)
        removeMarkers.removeAt(actualIndex).remove()
        addMarkers.removeAt(actualIndex).remove()
        moveMarkers.removeAt(actualIndex).remove()
        val copy = polygon!!.points
        copy.removeAt(actualIndex)
        if (actualIndex == 0) {
            copy[copy.size - 1] = copy[0]
        }
        polygon!!.points = copy
        updateMarkersPosition()
    }

    fun updateMarkerVisible()
    {
        isMoveMarkerVisible = !isMoveMarkerVisible
        for (i in moveMarkers.indices) {
            moveMarkers[i].isVisible = isMoveMarkerVisible
            removeMarkers[i].isVisible = !isMoveMarkerVisible
        }
    }

    private fun updateMoveMarkerPosition(marker: Marker)
    {
        if (moveMarkers.contains(marker)) {

            val vertexIndex = moveMarkers.indexOf(marker)

            var copy: MutableList<LatLng>? = null
            polygon?.let { poly -> copy = poly.points}

            copy?.set(vertexIndex, marker.position)
            if (vertexIndex == 0) {
                copy?.let { cp -> cp.set(cp.size - 1, marker.position) }
            }
            polygon?.let { poly -> poly.points = copy as List<LatLng>}
            updateMarkersPosition()
        }
    }

    fun clear()
    {
        map.clear()
        addMarkers.clear()
        removeMarkers.clear()
        moveMarkers.clear()
        polygon = null
    }
    //----------------------------------------------------------------
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