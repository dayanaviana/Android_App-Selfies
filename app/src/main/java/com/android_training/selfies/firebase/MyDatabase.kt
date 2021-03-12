package com.android_training.selfies.firebase

import com.android_training.selfies.models.Photo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyDatabase() {
    lateinit var dbReference: DatabaseReference
    init {
        dbReference = FirebaseDatabase.getInstance().getReference(Photo.KEY)
    }
    fun insert(photo: Photo){
        var photoId = dbReference.push().key
        dbReference.child(photoId!!).setValue(photo)
    }
}