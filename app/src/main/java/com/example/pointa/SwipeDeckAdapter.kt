package com.example.pointa

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.entity.UserInfo

class SwipeDeckAdapter(private val data: ArrayList<UserInfo>, private val context: Context) :
    BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View? = convertView
        if (v == null) {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.test_card2, parent, false)
        }
        val imageView = v!!.findViewById<ImageView>(R.id.offer_image)
//        val textView = v.findViewById<TextView>(R.id.sample_text)
        Glide.with(context).load(data[position].img).into(imageView)
//        val item = data[position].title
//        textView.text = item

        v.setOnClickListener { v ->
            Log.i("Layer type: ", v.layerType.toString())
            Log.i("Hardware Accel type:", View.LAYER_TYPE_HARDWARE.toString())
        }
        return v
    }
}