package com.skyblu.data.permissions

import android.app.Activity
import android.content.Context
import com.skyblu.configuration.PERMISSIONS
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * An implementation that requests and checks permissions using EasyPermissions
 */
class EasyPermissions(
    val context: Context,
    val permissions: String = PERMISSIONS
) : PermissionsInterface {

    override fun requestPermissions(activity: Activity) {
        EasyPermissions.requestPermissions(
            activity.let {
                PermissionRequest.Builder(
                    it,
                    1,
                    permissions
                )
                    .build()
            },
        )
    }

    override fun checkPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            context,
            PERMISSIONS
        )
    }
}