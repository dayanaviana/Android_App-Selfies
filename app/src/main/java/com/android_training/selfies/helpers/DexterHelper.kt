package com.android_training.selfies.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.android_training.selfies.MyApplication
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class DexterHelper() {
    private var mContext: Context
    init {
        mContext = MyApplication.appContext
    }
    fun hasCameraPermission(): Boolean{
        var permission = ContextCompat.checkSelfPermission(mContext,
            Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }
    fun requestSinglePermission(permission: String) {
        Dexter.withContext(mContext)
            .withPermission(permission)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //Permission Granted
                    var name = response!!.requestedPermission.name
                    MyApplication.toast("$name \n Permission GRANTED")
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    //Permission Denied
                    var name = response!!.requestedPermission.name
                    MyApplication.toast("$name \n Permission DENIED")
                }

            }).check()
    }
    fun requestCameraPermission() {
        requestSinglePermission("Manifest.permission.CAMERA")
    }

    fun requestMultiplePermissions() {
        Dexter.withContext(mContext)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    //ALL Permissions Granted
                    if(report!!.areAllPermissionsGranted()){
                        MyApplication.toast("ALL Permission granted")
                    }
                    //ANY permanent Denial?
                    if(report!!.isAnyPermissionPermanentlyDenied){
//                      Toast.makeText(applicationContext, "Permission denied permanently", Toast.LENGTH_LONG).show()
                        showGoToSettingsDialog()
                    }
                    if(!report!!.deniedPermissionResponses.isEmpty()){
                        var denies: MutableList<PermissionDeniedResponse> = report!!.deniedPermissionResponses
                        if(!denies.isEmpty()){
                            var str = ""
                            for (deny in denies){
                                str += deny.permissionName + "\n"
                            }
                            MyApplication.toast("Permissions denied: \n $str")
                        }
                    }
                    if(!report!!.grantedPermissionResponses.isEmpty()){
                        var grants: MutableList<PermissionGrantedResponse> = report!!.grantedPermissionResponses
                        if(!grants.isEmpty()){
                            var str = ""
                            for(grant in grants){
                                str += grant.permissionName + "\n"
                            }
//                            MyApplication.toast("Permissions granted: \n $str")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).onSameThread().check()
    }

    fun requestMultiplePermissions(permissions: Collection<String>) {
        Dexter.withContext(mContext)
            .withPermissions(permissions)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    //ALL Permissions Granted
                    if(report!!.areAllPermissionsGranted()){
                        MyApplication.toast("ALL Permissions granted")
                    }
                    //ANY permanent Denial?
                    if(report!!.isAnyPermissionPermanentlyDenied){
//                      Toast.makeText(applicationContext, "Permission denied permanently", Toast.LENGTH_LONG).show()
                        showGoToSettingsDialog()
                    }
                    if(!report!!.deniedPermissionResponses.isEmpty()){
                        var denies: MutableList<PermissionDeniedResponse> = report!!.deniedPermissionResponses
                        if(!denies.isEmpty()){
                            var str = ""
                            for (deny in denies){
                                str += deny.permissionName + "\n"
                            }
                            MyApplication.toast("Permissions denied: \n $str")
                        }
                    }
                    if(!report!!.grantedPermissionResponses.isEmpty()){
                        var grants: MutableList<PermissionGrantedResponse> = report!!.grantedPermissionResponses
                        if(!grants.isEmpty()){
                            var str = ""
                            for(grant in grants){
                                str += grant.permissionName + "\n"
                            }
                            MyApplication.toast("Permissions granted: \n $str")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).onSameThread().check()
    }
    fun showGoToSettingsDialog(){
        var builder = AlertDialog.Builder(mContext)
        builder.setTitle("Need Permission")
        builder.setMessage("Please, give this permission")
        builder.setPositiveButton("Go to Settings", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
                openAppSettings()
            }
        })
        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }
        })
        builder.show()
    }
    fun openAppSettings(){
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        var uri = Uri.fromParts("package", mContext.packageName, null)
        intent.setData(uri)
        startActivityForResult(mContext as Activity, intent, 999, null)
    }
}