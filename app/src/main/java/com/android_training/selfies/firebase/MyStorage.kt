package com.android_training.selfies.firebase

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MyStorage() {

    lateinit var storageRef: StorageReference

    init {
        var location = "gs://b29-class26.appspot.com"
        var storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
//        storageRef = FirebaseStorage.getInstance().getReference("root-element in firebase")
    }

    fun uploadFile(uri: Uri) {
        val imgRef: StorageReference = storageRef.child("images/myImage.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener(
                OnSuccessListener{ taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()
                }
            })
            .addOnFailureListener(OnFailureListener { exception ->
                print(exception.message)
            })
    }
}

fun main(){
    var mStorage = MyStorage()
    var uri = Uri.parse("content://com.android_training.selfies.fileprovider/my_images/SELFIE-20210310_202704_4205275017399749718.jpg")
    mStorage.uploadFile(uri)
}