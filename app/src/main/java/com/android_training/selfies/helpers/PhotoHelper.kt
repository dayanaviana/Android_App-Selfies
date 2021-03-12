package com.android_training.selfies.helpers

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.ParseException
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.android_training.selfies.MyApplication
import java.io.File
import java.util.*


class PhotoHelper() {
    lateinit var mContext: Context
    lateinit var photoFile: File
    lateinit var photoUri: Uri
    lateinit var currentPhotoPath: String
    lateinit var fileName: String
    init {
        mContext = MyApplication.appContext
        photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            mContext,
            "com.android_training.selfies.fileprovider",
            photoFile
        )
    }

    //Get URI to image received from capture by camera.
    fun getCaptureImageOutputUri(): Uri {
        var outputFileUri: Uri? = null
        val getImage: File? = mContext.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.getPath(), "myPicture.png"))
        }
        return outputFileUri!!
    }

    fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.getAction()
            isCamera = (action != null && action == MediaStore.ACTION_IMAGE_CAPTURE)
        }
        return if(isCamera){getCaptureImageOutputUri()} else {data!!.getData()}
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        //scale the bitmap by width or height(whichever is larger)
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

//    @Throws(IOException::class)
    fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap {
        val exitInterface = ExifInterface(selectedImage.path!!)
        val orientation =
            exitInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270F)
            else -> img
        }
    }
    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        //Devices like Samsung galaxy are known to capture the image in landscape orientation.
        //Retrieving the image and displaying as it is can cause it to be displayed in the wrong orientation.
        //Hence we’ve called thi method
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg =
            Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = mContext.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    private fun createImageFile(): File {
        // Create an image file name

        //Call requires Android 7.0 - API level 24
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

//        Public Storage Directory -> Deprecated
//        val storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        //Photos to remain private to your app only (Does not require WRITE_EXTERNAL_STORAGE permission)
        val storageDir: File = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        fileName = "SELFIE-${timeStamp}"
        return File.createTempFile(
            "SELFIE-${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //DONT USE IF GETTING DATE FROM DEVICE: Format changes often
    private fun convertDateForPath(date: String): String {
        //Wed Mar 10 17:57:13 CST 2021
        val inputFormat = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        val outputFormat = java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
        try {
            val finalStr: String = outputFormat.format(inputFormat.parse(date))
            println(finalStr)
            return finalStr
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}