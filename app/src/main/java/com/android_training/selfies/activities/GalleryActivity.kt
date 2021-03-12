package com.android_training.selfies.activities

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android_training.selfies.R
import com.android_training.selfies.app.Config
import com.android_training.selfies.firebase.MyAuthentication
import com.android_training.selfies.firebase.MyDatabase
import com.android_training.selfies.firebase.MyStorage
import com.android_training.selfies.helpers.DexterHelper
import com.android_training.selfies.helpers.PhotoHelper
import com.android_training.selfies.models.Photo
import kotlinx.android.synthetic.main.activity_gallery.*
import java.io.File
import java.io.IOException


class GalleryActivity : AppCompatActivity() {
    lateinit var photoHelper: PhotoHelper
    lateinit var dexter: DexterHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        init()
    }

    private fun init() {
        photoHelper = PhotoHelper()
        dexter = DexterHelper()
    }

    fun openGallery_onClick(view: View){
        startActivityForResult(getPickImageChooserIntent(), Config.REQUEST_GALERRY);
//        openCamera()
    }
    private fun openCamera() {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoHelper.photoUri)
        startActivityForResult(intent, Config.REQUEST_IMAGE_CAPTURE)
    }

    private fun getPickImageChooserIntent(): Intent? {
        // Determine Uri of camera image to save.
//        val outputFileUri: Uri = photoHelper.getCaptureImageOutputUri()

        val allIntents = ArrayList<Intent>()

        // collect all camera intents
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
//            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoHelper.photoUri)
//            }
            allIntents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }

       // the main intent is the last in the list so pickup the useless one
        var mainIntent: Intent? = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
//            com.android.documentsui.picker.PickActivity
            if (intent.component!!.className == "com.google.android.apps.photos.picker.external.ExternalPickerActivity"
            ) {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap
        var photoName: String = ""

        if (resultCode == Activity.RESULT_OK) {
            var picUri = photoHelper.getPickImageResultUri(data)
            if (picUri != null) {
                photoName = photoHelper.getFileName(picUri)
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, picUri)
//                bitmap = photoHelper.rotateImageIfRequired(bitmap, picUri)
                bitmap = photoHelper.getResizedBitmap(bitmap, 500)
                image_view.setImageBitmap(bitmap)
            } else {
                if (data != null) {
                    photoName = photoHelper.fileName
                    bitmap = data.extras!!.get("data") as Bitmap
                    image_view.setImageBitmap(bitmap)
                }
            }
            //            var userID = MyAuthentication.user.email
            var photo = Photo(fileName = photoName, userId = "dayanatsv@gmail.com")
            var db = MyDatabase().insert(photo)





        }
    }



    private fun checkPermissions(){
        val permissionsRejected = ArrayList<Any>()
        val permissions = ArrayList<Any>()

        var permissionsToRequest = findUnAskedPermissions(permissions)
        if (permissionsToRequest.isNotEmpty())
            requestPermissions(permissionsToRequest, Config.Companion.REQUEST_GALLERY_PERMISSIONS);

    }

    private fun findUnAskedPermissions(permissions: ArrayList<Any>): Array<out String> {
        return arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save file url in bundle as it will be null on scren orientation changes
        outState.putParcelable("photo_uri", photoHelper.photoUri);
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // get the file url
        photoHelper.photoUri = savedInstanceState.getParcelable("photo_uri")!!
    }
}