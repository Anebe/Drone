package com.dji.drone.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dji.drone.R
import com.dji.mapkit.core.models.DJILatLng
import com.dji.mapkit.core.models.annotations.DJIMarker

data class MarkerData(
        var type: MarkerType,
        val draggable: Boolean,
        var latLng: DJILatLng,
)

enum class MarkerType(val iconID: Int) {
    ADD(R.drawable.ic_round_add_circle_24),
    REMOVE(R.drawable.ic_round_remove_circle_24),
    MOVE(R.drawable.ic_round_move_circle_24)
}

class RoutMapViewModel : ViewModel() {
    val markers = MutableLiveData(mutableListOf<MarkerData>())
    val route = MutableLiveData(mutableListOf<DJILatLng>())

    var createRout = MutableLiveData(true)
    var moveRoute = MutableLiveData(true)
    var closeRoute = MutableLiveData(true)


    fun updateRoute(markers: MutableList<DJIMarker>, route:MutableList<DJILatLng>){
        this.route.value?.clear()
        this.markers.value?.clear()
        if(route.isNotEmpty()){

            var type: MarkerType
            for(index in 0 until markers.size-1 step 2){
                type = if(moveRoute.value == true) MarkerType.MOVE else MarkerType.REMOVE

                this.markers.value?.add(MarkerData(type,markers[index].isDraggable, markers[index].position))
                this.markers.value?.add(MarkerData(MarkerType.ADD,markers[index+1].isDraggable, markers[index+1].position))
            }
            this.route.value?.addAll(route)
        }
    }
}