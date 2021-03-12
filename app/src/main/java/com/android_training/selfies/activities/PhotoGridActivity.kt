package com.android_training.selfies.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.android_training.selfies.R
import com.android_training.selfies.adapters.AdapterPhotoGrid
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import kotlinx.android.synthetic.main.activity_photo_grid.*
import kotlinx.android.synthetic.main.row_photo_adapter.*


class PhotoGridActivity : AppCompatActivity() {
    lateinit var adapterPhoto: AdapterPhotoGrid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_grid)
        init()
    }

    private fun init() {
        adapterPhoto = AdapterPhotoGrid(this)
        adapterPhoto.setData(GetPhotoList())

        recycler_view.adapter = adapterPhoto
        recycler_view.layoutManager = GridLayoutManager(this,2)
        recycler_view.addItemDecoration(
            DividerItemDecoration(recycler_view.getContext(), DividerItemDecoration.VERTICAL)
        )
    }

    private fun GetPhotoList(): ArrayList<Bitmap> {
        var mList: ArrayList<Bitmap> = ArrayList()
        mList.add(BitmapFactory.decodeResource(resources, R.drawable.kid))
        mList.add(BitmapFactory.decodeResource(resources, R.drawable.kid))
        mList.add(BitmapFactory.decodeResource(resources, R.drawable.kid))
        mList.add(BitmapFactory.decodeResource(resources, R.drawable.kid))
        return mList
    }

}