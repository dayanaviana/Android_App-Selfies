package com.android_training.selfies.models

import android.graphics.Bitmap
import android.net.Uri

data class Photo (
    var userId: String? = null,
    var url: String? = null,
    var uri: Uri? = null,
    var fileName: String? = null,
    var bitmap: Bitmap? = null
){
    companion object{
        var KEY = "PHOTO"
    }
}