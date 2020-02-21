package com.example.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.entity.UserInfo
import com.example.pointa.InfoActivity
import com.example.pointa.MainActivity
import com.example.pointa.R
import com.example.pointa.TestActivity

class RecyclerAdapter(context: Context,list: ArrayList<ArrayList<UserInfo>>?,allList:ArrayList<UserInfo>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var list: ArrayList<ArrayList<UserInfo>>? = list
    private var allList :ArrayList<UserInfo>? = allList
    private var context: Context? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.activity_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        for (i in this.list!!){
            holder.textOne.text = this.list?.get(position)!![0].title
            holder.textTwo.text = this.list?.get(position)!![1].title
            holder.userGradeOne.text = "评分："+this.list?.get(position)!![0].grade
            holder.userGradeTwo.text = "评分："+this.list?.get(position)!![1].grade.toString()
            Glide.with(this.context!!).load(this.list?.get(position)!![0].img).into(holder.imgOne)
            Glide.with(this.context!!).load(this.list?.get(position)!![1].img).into(holder.imgTwo)
        }

        holder.itemView.setOnClickListener{
            Log.d("itemView", "点击了列表")
        }
        holder.imgOne.setOnClickListener {
            if (obtainDirection()){
//                intentActivity(this.list?.get(position)!![0])
                intentActivity2(this.allList!!)
            }else{
                var bundle =  Bundle()
                bundle.putSerializable("userInfo",this.list?.get(position)!![0])
                val msg = MainActivity.handler!!.obtainMessage(2,bundle)
                MainActivity.handler!!.sendMessage(msg)
            }
        }
        holder.imgTwo.setOnClickListener {
            if(obtainDirection()) {
//                intentActivity(this.list?.get(position)!![1])
                intentActivity2(this.allList!!)
            }else{
                var bundle =  Bundle()
                bundle.putSerializable("userInfo",this.list?.get(position)!![1])
                val msg = MainActivity.handler!!.obtainMessage(2,bundle)
                MainActivity.handler!!.sendMessage(msg)
            }
        }

    }
    //获取屏幕方向
    private fun obtainDirection(): Boolean {
        var configuration = context!!.resources.configuration
        //获取屏幕方向
        var ori = configuration.orientation
        var temp = true
        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            temp = false
        }else if(ori == Configuration.ORIENTATION_PORTRAIT){
            temp = true
        }
        Log.d("HOV", temp.toString())
        return temp

    }
    private fun intentActivity(userInfo: UserInfo){
        val intent = Intent()
        intent.putExtra("userInfo",userInfo)
        intent.setClass(context,InfoActivity::class.java)
        startActivity(this.context!!,intent,null)
    }
    private fun intentActivity2(userInfo: ArrayList<UserInfo>){
        val intent = Intent()
        intent.putExtra("userInfo",userInfo)
        intent.setClass(context,TestActivity::class.java)
        startActivity(this.context!!,intent,null)
    }

    class ViewHolder(view :View):RecyclerView.ViewHolder(view){
        var textOne: TextView = itemView.findViewById(R.id.user_info_one)
        var textTwo: TextView = itemView.findViewById(R.id.user_info_two)
        var imgOne:ImageView = itemView.findViewById(R.id.user_img_one)
        var imgTwo:ImageView = itemView.findViewById(R.id.user_img_two)
        var userGradeOne:TextView = itemView.findViewById(R.id.user_grade_one)
        var userGradeTwo:TextView = itemView.findViewById(R.id.user_grade_two)
    }
}

