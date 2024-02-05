package com.dji.drone.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dji.drone.R
import com.dji.drone.databinding.FragmentRouteMapBinding
import com.dji.mapkit.core.maps.DJIMap
import com.dji.mapkit.core.maps.koy
import com.dji.mapkit.core.models.DJIBitmapDescriptor
import com.dji.mapkit.core.models.DJIBitmapDescriptorFactory
import com.dji.mapkit.core.models.DJILatLng
import com.dji.mapkit.core.models.annotations.DJIMarker
import com.dji.mapkit.core.models.annotations.DJIMarkerOptions
import com.dji.mapkit.core.models.annotations.DJIPolygon
import com.dji.mapkit.core.models.annotations.DJIPolygonOptions
import dji.common.model.LocationCoordinate2D
import dji.sdk.mission.MissionControl
import dji.sdk.mission.timeline.TimelineElement
import dji.sdk.mission.timeline.actions.GoToAction
import dji.sdk.mission.timeline.actions.TakeOffAction

class RouteMapFragment : Fragment() {

    private lateinit var binding: FragmentRouteMapBinding
    private var map: DJIMap? = null
    private var polygon: DJIPolygon? = null
    private var markers = mutableListOf<DJIMarker>()

    private val viewModel by viewModels<RoutMapViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteMapBinding.inflate(inflater, container, false)
        binding.map.initGoogleMap {
            map = it
            initData()
            initListener()
            initObserver()
        }
        binding.map.onCreate(savedInstanceState)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        polygon?.let {
            viewModel.updateRoute(markers, it.points)
        } ?: run{
            viewModel.updateRoute(markers, mutableListOf())
        }
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    private fun initData(){
        binding.map.showAllFlyZones()
    }

    private fun initListener(){
        binding.imageButtonCreate.setOnClickListener {
            createInitialBaseRoute()
        }

        binding.imageButtonDelete.setOnClickListener {
            clear()
        }

        binding.switchMarkerControl.setOnClickListener {
            viewModel.moveRoute.value = binding.switchMarkerControl.isChecked
        }


        val missionManager = MissionControl.getInstance()
        binding.start.setOnClickListener{

            polygon?.points?.let {
                val elements = mutableListOf<TimelineElement>()
                val coordinates = it
                val velocity = 10f
                val altitude = 5f

                Toast.makeText(requireContext(), coordinates.size.toString(), Toast.LENGTH_LONG).show()
                elements.add(TakeOffAction())
                //coordinates.removeLast()
                Toast.makeText(requireContext(), coordinates.size.toString(), Toast.LENGTH_LONG).show()
                for(coord in coordinates){

                    val goto = GoToAction(LocationCoordinate2D(coord.latitude,coord.longitude), altitude)
                    goto.flightSpeed = velocity


                    val trigger = ObstacleDistanceTrigger()
                    trigger.distanceTrigger = 1f
                    trigger.setAction {
                        missionManager.stopTimeline()
                    }
                    goto.triggers = listOf(trigger)


                    elements.add(goto)
                }

                val error = missionManager.scheduleElements(elements)
                error?.let {
                    Toast.makeText(requireContext(), error.description, Toast.LENGTH_LONG).show()
                }
                if(error == null){
                    Toast.makeText(requireContext(), missionManager.scheduledCount().toString(), Toast.LENGTH_LONG).show()
                    missionManager.startTimeline()
                }
            }
        }

        binding.stop.setOnClickListener{
            if(missionManager.isTimelineRunning){
                missionManager.stopTimeline()
                missionManager.unscheduleEverything()
            }
        }

        map?.let{
            it.setOnMarkerDragListener( object: DJIMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: DJIMarker?) {

                    marker?.let {
                        moveRoute(marker)
                    }
                }

                override fun onMarkerDrag(marker: DJIMarker?) {
                    marker?.let {
                        moveRoute(marker)
                    }
                }

                override fun onMarkerDragEnd(marker: DJIMarker?) {
                    marker?.let {
                        moveRoute(marker)
                    }
                }

            })

            it.setOnMarkerClickListener { marker ->
                if(viewModel.moveRoute.value == true){

                    val indexMark = markers.indexOf(marker)
                    if(indexMark != 0 && indexMark%2 != 0){
                        val index = indexInRoute(marker)
                        increaseRoute(marker.position, index+1)
                        return@setOnMarkerClickListener true
                    }
                    return@setOnMarkerClickListener false
                }
                else{
                    decreaseRoute(marker)
                    return@setOnMarkerClickListener true
                }
            }
        }
    }

    private fun initObserver(){
        viewModel.route.observe(this){ newRout ->
            if(polygon == null){
                if(newRout.isNotEmpty()){
                    val polyOp = getInstancePolygonOptions()
                    polyOp.addAll(newRout)
                    polygon = map?.addPolygon(polyOp)
                }
            }else{
                polygon?.let {
                    it.points = newRout
                }
            }
        }
        viewModel.markers.observe(this){
            for (mark in it){
                val markOp = markerOptionsFactory(mark.type.iconID,mark.draggable)
                markOp.position(mark.latLng)
                markOp.title(it.indexOf(mark).toString())
                map?.let{ map ->
                    markers.add(map.addMarker(markOp))
                }
            }

        }
        viewModel.createRout.observe(this){
            switchButtons(it)
        }
        viewModel.moveRoute.observe(this){
            switchMarkers()
        }
    }

    //------------------------------------------------
    private fun clear(){
        map?.clear()
        polygon = null
        markers.clear()
        viewModel.createRout.value = true
    }

    private fun createInitialBaseRoute() {
        val polygonOptions = getInstancePolygonOptions()
        lateinit var projection: koy
        lateinit var cameraCenter: Point

        map?.let{
            projection = it.projection
            cameraCenter = projection.fdd(it.cameraPosition.target)
        }

        val diff = 100
        val point = Point(cameraCenter.x, cameraCenter.y)

        point.y -= diff
        val triangle1 = projection.fdd(point)

        point.x += diff
        point.y = cameraCenter.y + diff
        val triangle2 = projection.fdd(point)

        point.x = cameraCenter.x - diff
        point.y = cameraCenter.y + diff
        val triangle3 = projection.fdd(point)

        polygonOptions.add(triangle1)
        polygonOptions.add(triangle2)
        polygonOptions.add(triangle3)

        polygon = map?.addPolygon(polygonOptions)
        updateNumbersMarker()

        viewModel.createRout.value = false
    }

    private fun updateNumbersMarker(){
        val routeSize = sizeRoute()

        if(routeSize*2 > markers.size){
            val repeat = (routeSize*2 - markers.size)/2
            for (index in 0 until repeat){
                var markOp: DJIMarkerOptions = if (viewModel.moveRoute.value == true) {
                    markerOptionsFactory(MarkerType.MOVE.iconID,true)
                } else {
                    markerOptionsFactory(MarkerType.REMOVE.iconID,false)
                }

                map?.let { map ->
                    markers.add(map.addMarker(markOp))
                }

                markOp = markerOptionsFactory(MarkerType.ADD.iconID, false)
                map?.let { map ->
                    markers.add(map.addMarker(markOp))
                }
            }
        }

        polygon?.points?.let {
            var markerIndex = 0
            for(index in 0 until routeSize){
                markers[markerIndex].position = it[index]
                markerIndex =nextIndexMarker(markerIndex)
                markers[markerIndex].position = avgMarker(it[index], it[nextIndexPolygon(index)])
                markerIndex =nextIndexMarker(markerIndex)
            }
        }
    }

    private fun avgMarker(m1: DJILatLng, m2: DJILatLng): DJILatLng {
        return DJILatLng((m1.latitude + m2.latitude)/2,
            (m1.longitude + m2.longitude) /2)
    }

    private fun switchButtons(createRoute: Boolean) {
        val alphaDisable = 0.4f
        val alphaEnable = 1f
        if (createRoute) {
            binding.imageButtonDelete.isEnabled = false
            binding.imageButtonDelete.alpha = alphaDisable
            binding.imageButtonCreate.isEnabled = true
            binding.imageButtonCreate.alpha = alphaEnable
        }
        else {
            binding.imageButtonCreate.isEnabled = false
            binding.imageButtonCreate.alpha = alphaDisable
            binding.imageButtonDelete.isEnabled = true
            binding.imageButtonDelete.alpha = alphaEnable
        }
    }

    private fun switchMarkers(){
        val (icon,drag) =
            if (binding.switchMarkerControl.isChecked) Pair(R.drawable.ic_round_move_circle_24, true)
            else Pair(R.drawable.ic_round_remove_circle_24, false)

        binding.switchMarkerControl.setThumbResource(icon)

        for(index in 0 until markers.size-1 step 2){
            markers[index].setIcon(bitmapDescriptorFromVector(icon, requireContext()))
            markers[index].isDraggable = drag
        }
    }
    //----------------------------------------------
    private fun nextIndexPolygon(index: Int): Int {
        polygon?.points?.let{
            if(index == it.size - 1)
                return 0
            return  index+1
        }
        return -1
    }

    private fun previousIndexPolygon(index: Int): Int {
        polygon?.points?.let{
            if(index == 0 )
                return it.size - 2
            return  index-1
        }
        return -1
    }

    private fun nextIndexMarker(index: Int): Int {
        if(index == markers.size - 1)
            return 0
        return  index+1
    }

    private fun previousIndexMarker(index: Int): Int {
        if(index == 0)
            return markers.size - 1
        return  index-1
    }

    private fun sizeRoute(): Int {
        polygon?.points?.let{
            if(viewModel.closeRoute.value == true){
                return (it.size-1)
            }else{
                return (it.size-2)
            }
        }
        return 0
    }

    private fun indexInRoute(marker: DJIMarker): Int{
        var index = markers.indexOf(marker)
        index = if(index == 0) 0 else index/2
        return index
    }
    //-----------------------------------------------------

    private fun increaseRoute(position: DJILatLng, index: Int){
        polygon?.let{
            val copyPoint = it.points
            copyPoint.add(index, position)
            it.points = copyPoint

            updateNumbersMarker()
        }
    }

    private fun decreaseRoute(marker: DJIMarker){
        val indexPoly = indexInRoute(marker)
        val indexMarker = markers.indexOf(marker)
        polygon?.let{
            val copyPoints = it.points

            markers.removeAt(indexMarker).remove()
            markers.removeAt(indexMarker).remove()
            copyPoints.removeAt(indexPoly)
            it.points = copyPoints
        }
    }

    private fun moveRoute(vertex: DJIMarker){
        val indexPolygon = indexInRoute(vertex)
        val indexMark = markers.indexOf(vertex)

        moveVertexOfPolygon(indexPolygon, vertex.position)
        polygon?.points?.let {
            markers[nextIndexMarker(indexMark)].position = avgMarker(it[indexPolygon], it[nextIndexPolygon(indexPolygon)])
            markers[previousIndexMarker(indexMark)].position = avgMarker(it[indexPolygon], it[previousIndexPolygon(indexPolygon)])

        }
        //polygon?.let { viewmodel.updateRoute(vertex, it) }
    }

    private fun moveVertexOfPolygon(index: Int, position: DJILatLng){
        val pointsCopy = polygon?.points?.toMutableList()

        pointsCopy?.let{
            val finalIndex = pointsCopy.size - 1
            if(index == 0 || index == finalIndex){
                pointsCopy[0] = position
                pointsCopy[finalIndex] = position
            }else{
                pointsCopy[index] = position
            }
            polygon?.points = pointsCopy
        }

    }

    //---------------------------------------------------------------------
    private fun getInstancePolygonOptions(): DJIPolygonOptions {
        return DJIPolygonOptions()
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.gray_600))
            .strokeWidth(7f)
            .fillColor(0)
    }

    private fun markerOptionsFactory(id: Int, draggable: Boolean): DJIMarkerOptions {
        val icon = bitmapDescriptorFromVector(id, requireContext())
        return DJIMarkerOptions()
            .icon(icon)
            .anchor(0.5f, 0.5f)
            .draggable(draggable)
            .position(DJILatLng(0.0, 0.0))
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int, context: Context): DJIBitmapDescriptor {
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
            return DJIBitmapDescriptorFactory.fromBitmap(bitmap)
        }
}