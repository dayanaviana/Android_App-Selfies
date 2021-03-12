package com.android_training.selfies.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android_training.selfies.MyApplication
import com.android_training.selfies.R
import kotlinx.android.synthetic.main.row_photo_adapter.view.*

class AdapterPhotoGrid(var mContext: Context): RecyclerView.Adapter<AdapterPhotoGrid.MyViewHolder>() {

    lateinit var mList: ArrayList<Bitmap>

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(bitmap: Bitmap){
            itemView.image_view.setImageBitmap(bitmap)

            itemView.setOnClickListener {
                MyApplication.toast("Image Clicked")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.row_photo_adapter, parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    fun setData(data: ArrayList<Bitmap>){
        mList = data
    }
}