package com.dji.drone.obstacleTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dji.keysdk.FlightControllerKey
import dji.sdk.sdkmanager.DJISDKManager


class DistanceDiagnosticViewModel: ViewModel() {
    //private val controller = (DJISDKManager.getInstance().product as Aircraft).flightController
    //private var altitudeKey: FlightControllerKey = FlightControllerKey.create(FlightControllerKey.ALTITUDE)
    private var obstacleKey: FlightControllerKey = FlightControllerKey.create(FlightControllerKey.DETECTION_SECTORS)

    private val _distance = MutableLiveData<Array<String>>()
    val distance : LiveData<Array<String>>
        get() = _distance

    init {
        DJISDKManager.getInstance().keyManager?.addListener(obstacleKey
        ) { old: Any?, new: Any? ->
            _distance.value = arrayOf(old.toString(), new.toString())
        }
    }
    //TODO fazer uso do keymanager e listener com o coisa de distancia de obstaculos
}