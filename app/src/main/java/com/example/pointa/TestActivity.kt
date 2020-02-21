package com.example.pointa
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity

import com.daprlabs.aaron.swipedeck.SwipeDeck
import com.example.entity.UserInfo
import com.example.util.DBUtil
import java.util.ArrayList
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.Toast

class TestActivity : AppCompatActivity() {
    private var cardStack: SwipeDeck? = null
    private var adapter: SwipeDeckAdapter? = null
    private var testData: ArrayList<UserInfo>? = null
    private var content :Context = this
    private lateinit var linear :LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        initView()
        testData = intent.getSerializableExtra("userInfo") as ArrayList<UserInfo>?
        adapter = SwipeDeckAdapter(this.testData!!,this)
        if (cardStack != null) {
            cardStack!!.setAdapter(adapter!!)
        }
        cardStack!!.setLeftImage(R.id.left_image)
        cardStack!!.setRightImage(R.id.right_image)
        cardStack!!.setCallback(object : SwipeDeck.SwipeDeckCallback {
            override fun cardSwipedLeft(stableId: Long) {
                Log.i("activity",
                    "card was swiped left, position in adapter: $stableId"
                )
                //左滑0
                var str = "update user set grade = '0' where id = \'${testData!![stableId.toInt()].id}\'"
                Log.i("str",str)
                updateData(str)
                Toast.makeText(content,"左滑，分数为0",Toast.LENGTH_SHORT).show()
            }
            override fun cardSwipedRight(stableId: Long) {
                Log.i("activity",
                    "card was swiped left, position in adapter: $stableId"
                )
//                右滑5
                var str = "update user set grade = '5' where id = \'${testData!![stableId.toInt()].id}\'"
                Log.i("str",str)
                updateData(str)
                Toast.makeText(content,"右滑，分数为5",Toast.LENGTH_SHORT).show()
            }
        })
        setGestureListener()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setGestureListener() {
        var mPosX: Float = 0F
        var mPosY: Float = 0f
        var mCurPosX: Float = 0F
        var mCurPosY: Float = 0F//记录mListViewDevice 滑动的位置

        // 是要监听的视图 mAlertImageViewD
        linear!!.setOnTouchListener(View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPosX = event.x
                    mPosY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    mCurPosX = event.x
                    mCurPosY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if (mCurPosX - mPosX > 0 && Math.abs(mCurPosX - mPosX) > 25) {
                        Log.i("右滑动：","11")
                        cardStack!!.swipeTopCardRight(180)
                    } else if (mCurPosX - mPosX < 0 && Math.abs(mCurPosX - mPosX) > 25) {
                        Log.i("左滑动：","22")
                        cardStack!!.swipeTopCardLeft(500)
                    }
                }
            }
            true
        })
    }

    private fun updateData(str:String){
        try {
            val dbUtil = DBUtil(this)
            val sql = dbUtil.writableDatabase
            sql.execSQL(str)
        }catch (e:Exception){
            Log.e("Exception", e.toString())
        }

    }
    private fun initView() {
        cardStack = findViewById(R.id.swipe_deck)
        testData = ArrayList()
        linear = findViewById(R.id.linear)
    }
}