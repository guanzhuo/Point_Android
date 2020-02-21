package com.example.pointa

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adapter.RecyclerAdapter
import com.example.entity.UserInfo
import com.example.util.DBUtil
import com.example.util.HttpUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.show.api.ShowApiRequest
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException

class MainActivity : AppCompatActivity() ,HttpUtils.BaseCallBack{
    private lateinit var recycle : RecyclerView
    private lateinit var linear : LinearLayout
    private lateinit var tempImg:ImageView
    private lateinit var linearGone:LinearLayout
    private lateinit var infoTitle:TextView
    private lateinit var infoGrade:TextView
    private lateinit var layoutManager :  LinearLayoutManager
    private var list:  ArrayList<ArrayList<UserInfo>>? = ArrayList()
    private var allList :ArrayList<UserInfo> = ArrayList()
    private lateinit var adapter : RecyclerAdapter
    companion object{
        var handler: Handler? = null
    }
    override fun onSuccess(response: Response?, json: String?) {
        Log.d("TAG","请求成功 $json")
    }

    override fun onFail(request: Request?, e: IOException?) {

    }

    override fun onError(response: Response?, code: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        dataHttp()
        getPermission()
    }

    private fun initView(){
        tempImg = findViewById(R.id.temp_img)
        linear = findViewById(R.id.linear)
        recycle = findViewById(R.id.recycle)
        infoTitle = findViewById(R.id.info_title)
        infoGrade = findViewById(R.id.info_grade)
        linearGone = findViewById(R.id.linear_gone)
        handler = Handler{
            when(it.what){
                1 ->{
                    try {
                        adapter = RecyclerAdapter(this,list,allList)
                        recycle.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }catch (ex:Throwable){
                        ex.printStackTrace()
                    }
                }
                2->{
                    Log.d("2", "handler else${it.what}")
                    var bundle: Bundle
                    bundle = it.obj as Bundle
                    var tempUser = bundle.get("userInfo") as UserInfo
                    fixImg(tempUser)
                }
                else->{
                    Log.d("else", "handler else")
                }
            }
            false
        }
        layoutManager = LinearLayoutManager(this)
        recycle.layoutManager = layoutManager
        recycle.itemAnimator = DefaultItemAnimator()
        adapter = RecyclerAdapter(this,list,allList)
        recycle.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        queryData()
        val msg = Message()
        msg.what = 1
        handler!!.sendMessage(msg)
    }
    //修改view背景
    private fun fixImg(userInfo: UserInfo){
        Glide.with(this).load(userInfo.img).into(tempImg)
        infoTitle.text = userInfo.title
        infoGrade.text = "评分："+userInfo.grade
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.e("横屏","当前横屏")
            linearGone.visibility = View.VISIBLE
            linear.scrollTo(80,0)
            Glide.with(this).load(list!![0][0].img).into(tempImg)
            infoTitle.text = list!![0][0].title
            infoGrade.text = "评分："+list!![0][0].grade

        }else{
            linearGone.visibility = View.GONE
            linear.scrollTo(0,0)
            Log.e("竖屏","当前竖屏")
        }
    }
    //数据库查询数据
    private fun queryData(){
        val dbUtil = DBUtil(this)
        val sql = dbUtil.writableDatabase
        val findDB = sql.rawQuery("select * from user",null)
        var sqlData = ArrayList<UserInfo>()
        while (findDB.moveToNext()){
            Log.d("取值：", findDB.getString(findDB.getColumnIndex("id")))
            val tempUser = UserInfo()
            tempUser.id = findDB.getString(findDB.getColumnIndex("id"))
            tempUser.img = findDB.getString(findDB.getColumnIndex("img"))
            tempUser.title = findDB.getString(findDB.getColumnIndex("title"))
            tempUser.grade = findDB.getInt(findDB.getColumnIndex("grade"))
            tempUser.type = findDB.getInt(findDB.getColumnIndex("type"))
            sqlData.add(tempUser)
        }
        allList = sqlData
        Log.d("sqlData的值：", sqlData.toString())
        var data = ArrayList<ArrayList<UserInfo>>()
        var tempData = ArrayList<UserInfo>()
        var remainder = sqlData.size % 2
        var nums = sqlData.size / 2
        var n = 0
        for (i in 0 until nums){
            tempData.add(sqlData[n])
            tempData.add(sqlData[n+1])
            data.add(tempData)
            tempData =  ArrayList()
            if(n<nums){
                n += 2
            }
        }
        if (remainder!=0){
            tempData.add(sqlData[sqlData.size-1])
            data.add(tempData)
        }
        Log.d("finallyData:", data.toString())
        list = data
    }

    //获取数据
    private fun dataHttp() {
        var appId = "147786"
        var secret = "76437bc6b4934a3b9fca79ca88ed8cc3"
        val dbUtil = DBUtil(this)
        val sql = dbUtil.writableDatabase
        Thread{
            kotlin.run {
                var showStr = ShowApiRequest("http://route.showapi.com/341-2",appId,secret)
                    .addTextPara("page","1")
                    .addTextPara("maxResult","20")
                    .post()
                val strToJson = JsonParser.parseString(showStr)
                val contentList = strToJson.asJsonObject.get("showapi_res_body").asJsonObject.get("contentlist")
                val userInfoList = ArrayList<UserInfo>()
                for (i in  contentList.asJsonArray){
                    val userInfo:UserInfo = Gson().fromJson(i,UserInfo::class.javaObjectType)
                    userInfoList.add(userInfo)
                    //查询数据库是否有相同ID
                    val querySql = "select * from "+DBUtil.USER_TABLE_NAME +" where id = ?"
                    val cursor = sql.rawQuery(querySql, arrayOf(userInfo.id))
                    if(cursor.moveToFirst()){
                        Log.d("数据库", "数据存在")
                    }else{
                        val values = ContentValues()
                        values.put(DBUtil.ID,userInfo.id)
                        values.put(DBUtil.TITLE,userInfo.title)
                        values.put(DBUtil.IMG,userInfo.img)
                        values.put(DBUtil.TYPE,userInfo.type)
                        values.put(DBUtil.GRADE,userInfo.grade)
                        Log.d("数据库", userInfo.id+userInfo.title)
                        val index =sql.insert(DBUtil.USER_TABLE_NAME,null, values)
                        if (!index.equals(-1)){
                            Log.d("数据库", "数据库插入成功")
                        }
                    }
                }
                //数据库中提取数据
                queryData()
                Log.d("finallyData----:", list!![0][0].img)
                val msg = Message()
                msg.what = 1
                handler!!.sendMessage(msg)
            }
        }.start()
    }

    private fun getPermission(){
        // 需要动态申请的权限
        val permission = Manifest.permission.INTERNET
        //查看是否已有权限
        val checkSelfPermission = ActivityCompat.checkSelfPermission(this,permission)
        if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED) {
            //已经获取到权限  获取用户媒体资源
            print("网络权限获取成功")
        }else{
            //没有拿到权限  是否需要在第二次请求权限的情况下
            // 先自定义弹框说明 同意后在请求系统权限(就是是否需要自定义DialogActivity)
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){

            }else{
                myRequestPermission()
            }
        }
    }
    private fun myRequestPermission() {
        //可以添加多个权限申请
        val permissions = arrayOf(Manifest.permission.INTERNET)
        requestPermissions(permissions,1)
    }
}
