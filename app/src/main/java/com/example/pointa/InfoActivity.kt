package com.example.pointa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.RecyclerAdapterImg
import com.example.entity.UserInfo


class InfoActivity : AppCompatActivity(){
    private var list:  ArrayList<UserInfo>? = ArrayList()

    private lateinit var button :Button
    var userInfo = UserInfo()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        initView()
    }

    private fun initView() {
        button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, TestActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        }
        userInfo = intent.getSerializableExtra("userInfo") as UserInfo
        list!!.add(userInfo)
        val recycle = findViewById<RecyclerView>(R.id.recycler_img)
        val layoutManager = LinearLayoutManager(this)
        recycle.layoutManager = layoutManager
        recycle.itemAnimator = DefaultItemAnimator()
        val adapter = RecyclerAdapterImg(this, this.list!!)
        recycle.adapter = adapter
    }
}