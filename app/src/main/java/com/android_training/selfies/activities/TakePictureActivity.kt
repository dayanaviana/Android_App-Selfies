package com.android_training.selfies.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.android_training.selfies.MyApplication
import com.android_training.selfies.R
import com.android_training.selfies.app.Config
import com.android_training.selfies.helpers.DexterHelper
import com.android_training.selfies.helpers.PhotoHelper
import com.android_training.selfies.firebase.MyStorage
import kotlinx.android.synthetic.main.activity_take_picture.*
import java.io.FileOutputStream
import java.io.OutputStream

class TakePictureActivity : AppCompatActivity() {

    lateinit var dexter: DexterHelper
    lateinit var photoHelper: PhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        init()
    }
    private fun init() {
        dexter = DexterHelper()
        photoHelper = PhotoHelper()
    }

    override fun onResume() {
        super.onResume()
//        dexter.requestMultiplePermissions()
    }

    fun takePicture_onClick(view: View){
        //Check Permission
        if(dexter.hasCameraPermission()){
            openCamera()
        }else {
            dexter.requestCameraPermission()
        }
    }

    private fun openCamera() {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoHelper.photoUri)
        startActivityForResult(intent, Config.REQUEST_IMAGE_CAPTURE)
    }

    //Method called after camera app finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                Config.REQUEST_IMAGE_THUMBNAIL ->{ //Get the Thumbnail
                    val imageBitmap = data!!.extras!!.get("data") as Bitmap
                       image_view.setImageBitmap(imageBitmap)
                }
                Config.REQUEST_IMAGE_CAPTURE -> {

                    var imgTaken = BitmapFactory.decodeFile(photoHelper.currentPhotoPath)
                    addPictureToGallery2(imgTaken)

                    var storage = MyStorage()
                    storage.uploadFile(photoHelper.photoUri)

                    image_view.setImageBitmap(imgTaken)
                }
            }
        }else{
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }


    //Add photo to a gallery
    private fun addPictureToGallery() {
        MediaStore.Images.Media.insertImage(applicationContext.contentResolver,
            photoHelper.currentPhotoPath,"My Picture Title","My Picture Description")

        var mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = photoHelper.photoUri

        sendBroadcast(mediaScanIntent)
    }
    private fun addPictureToGallery2(bitmap: Bitmap){
        //Output stream
        var fos: OutputStream? = null

        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        fos = FileOutputStream(photoHelper.photoFile)

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            MyApplication.toast("Saved to Gallery")
        }

    }
}