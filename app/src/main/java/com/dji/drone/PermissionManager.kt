package com.dji.drone

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionManager()
{
    companion object {
        private val REQUIRED_PERMISSION_LIST = listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
        )

        fun checkPermissions(activity: Activity): List<String>{
            val missingPermission : MutableList<String> = ArrayList()
            for (eachPermission in REQUIRED_PERMISSION_LIST) {

                if(isPermissionGuarantied(eachPermission, activity))
                {
                    missingPermission.add(eachPermission)
                }

            }
            return missingPermission
        }

        fun isPermissionGuarantied(permission: String, activity: Activity):Boolean {
            val isGuarantied = ContextCompat.checkSelfPermission(
                activity,
                permission
            )
            return (isGuarantied != PackageManager.PERMISSION_GRANTED)
        }

        fun requestPermissions(activity: Activity, requestCode: Int, permissions: List<String>) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray<String>(),
                requestCode
            )
        }

        fun checkAndRequestPermissions(activity: Activity, requestCode: Int) {
            val missingPermission = checkPermissions(activity)

            if (missingPermission.isNotEmpty()) {
               requestPermissions(activity, requestCode, missingPermission)
            }
        }
    }
}