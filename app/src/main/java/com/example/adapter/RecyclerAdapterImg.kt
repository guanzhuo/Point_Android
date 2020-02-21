package com.example.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.entity.UserInfo
import com.example.pointa.R

class RecyclerAdapterImg (context: Context, list: ArrayList<UserInfo>) : RecyclerView.Adapter<RecyclerAdapterImg.ViewHolder>()  {
    private var list: List<UserInfo>? = list
    private var context: Context? = context
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(this.context!!).load(this.list!![position].img).into(holder.bigImg)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.activity_img, parent, false)
        return ViewHolder(view)    }

    override fun getItemCount(): Int {
        return list?.size!!
    }



    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        var bigImg: ImageView = itemView.findViewById(R.id.big_img)
    }
}