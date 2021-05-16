package com.jit.dyy.dosleep.service;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jit.dyy.dosleep.MainActivity;
import com.jit.dyy.dosleep.bean.User;
import com.jit.dyy.dosleep.util.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserService {
    private DataBaseHelper dbHelper;
    private Context mcontext;
    int resultNum = 0;
//    private User user;

    public UserService(Context context) {
        this.mcontext = context;
        dbHelper = new DataBaseHelper(context);
    }

    public int loginByTel(String phoneNumber){
        if(phoneNumber == null){
            Toast.makeText(mcontext, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("userTel",phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("bbb",jsonObject.toString());
            String url="http://m.dosleep.tech/user/loginByTel";
            RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d("aaa", jsonObject.toString());
                        String msg = jsonObject.getString("msg");
                        Log.d("msg", msg);
                        Boolean flag = jsonObject.getBoolean("flag");
                        if(flag){
                            Toast.makeText(mcontext, "成功", Toast.LENGTH_SHORT).show();
                            JSONObject detail = jsonObject.getJSONObject("detail");
                            User user = new User();
                            user.setUserId(detail.getInt("userId"));
                            user.setUserName(detail.getString("userName"));
                            user.setUserPwd(detail.getString("userPwd"));
                            user.setUserTel(detail.getString("userTel"));
                            user.setSex(detail.getString("sex"));
                            user.setBirth(getStringDate( detail.getString("birth") ));
                            user.setArea(detail.getString("area"));
                            user.setHeadImg(detail.getString("headImg"));
                            user.setSlogan(detail.getString("slogan"));
                            user.setRegistration(getStringDate( detail.getString("registration") ));
                            user.setState(detail.getInt("state"));

                            MainActivity.myappInfo.setMUser(user);

                            String sql= "insert into tb_user (userId, userName, userPwd, userTel, sex, birth, area, " +
                                    "headImg, slogan, registration, state)values(?,?,?,?,?,?,?,?,?,?,?)";
                            DataBaseHelper dbHelper = new DataBaseHelper(mcontext);
                            //获取可写入数据库
                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                            //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                            db.execSQL(sql,new Object[]{user.getUserId(),user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                    user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                    user.getSlogan(),user.getRegistration(),user.getState()});
                            db.close();
                            if (user.getUserPwd() != "null"){
                                resultNum = 1;
                                System.out.println("aaab"+resultNum+"**"+user.getUserPwd());
                            }else {
                                resultNum = 2;
                                System.out.println("aaaab"+resultNum+"**");
                            }

                        }else {
                            Toast.makeText(mcontext, "出现错误", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("neterr"+volleyError.toString());
                    Toast.makeText(mcontext, "网络出错", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        return resultNum;
    }

    public void setMUser(){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int userId = 0;
        try {
            Cursor cursor = db.rawQuery("select userId from tb_user where state=1", null);
            if (cursor.moveToNext()) {
                userId = cursor.getInt(cursor.getColumnIndex("userId"));
//                user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
//                user.setSlogan(cursor.getString(cursor.getColumnIndex("slogan")));
//                user.setState(cursor.getInt(cursor.getColumnIndex("state")));
            }
            //关闭连接
            db.close();
            cursor.close();
            //请求
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("userId", userId);
            Log.d("setMUserbbb",jsonObject.toString());
            final String url="http://m.dosleep.tech/user/find";
            RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d("aaa", jsonObject.toString());
                        String msg = jsonObject.getString("msg");
                        Boolean flag = jsonObject.getBoolean("flag");
                        Log.d("msg", msg);
                        if( flag ){
//                            Toast.makeText(mcontext, "成功", Toast.LENGTH_SHORT).show();
                            JSONObject detail = jsonObject.getJSONObject("detail");
                            User user = new User();
                            user.setUserId(detail.getInt("userId"));
                            user.setUserName(detail.getString("userName"));
                            user.setUserPwd(detail.getString("userPwd"));
                            user.setUserTel(detail.getString("userTel"));
                            user.setSex(detail.getString("sex"));
                            user.setBirth(getStringDate( detail.getString("birth") ));
                            user.setArea(detail.getString("area"));
                            user.setHeadImg(detail.getString("headImg"));
                            user.setSlogan(detail.getString("slogan"));
                            user.setRegistration(getStringDate( detail.getString("registration") ));
                            user.setState(detail.getInt("state"));

                            MainActivity.myappInfo.setMUser(user);

                            String sql= "update tb_user set userName=?,userPwd=?,userTel=?,sex=?,birth=?,area=?," +
                                    "headImg=?,slogan=? where userId=?";
//                            String sql= "insert into tb_user (userId, userName, userPwd, userTel, sex, birth, area, " +
//                                    "headImg, slogan, registration, state)values(?,?,?,?,?,?,?,?,?,?,?)";
                            //获取可写入数据库
                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                            //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                            db.execSQL(sql,new Object[]{user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                    user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                    user.getSlogan(),user.getUserId()});
                            db.close();
//                            String sql="delete from tb_order where oid=?";
//                            //获取可写入数据库
//                            SQLiteDatabase db=dbHelper.getWritableDatabase();
//                            //execSQL 方法无返回值    new Object[]{nid}   nid的值会赋值给delete from tb_note where nid=?的问号
//                            //如果有多个问号       需要传入多个参数
//                            db.execSQL(sql,new Object[]{oid});
//                            db.close();

                        }else if(msg.equals("获取失败")){
//                            Toast.makeText(mcontext, "用户已注销", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(mcontext, "网络出错", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.i("db.err", e.toString());
        }
    }

    public void logoutUser(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql="delete from tb_user where userId=?";
        try {
            //
            //execSQL 方法无返回值    new Object[]{nid}   nid的值会赋值给delete from tb_note where nid=?的问号
            //如果有多个问号       需要传入多个参数
            db.execSQL(sql,new Object[]{MainActivity.myappInfo.getMUser().getUserId()});
            //关闭连接
            db.close();
//            //请求
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("userId", userId);
//            Log.d("setMUserbbb",jsonObject.toString());
//            final String url="http://m.dosleep.tech/user/find";
//            RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
//            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject jsonObject) {
//                    try {
//                        Log.d("aaa", jsonObject.toString());
//                        String msg = jsonObject.getString("msg");
//                        Boolean flag = jsonObject.getBoolean("flag");
//                        Log.d("msg", msg);
//                        if( flag ){
//                            Toast.makeText(mcontext, "成功", Toast.LENGTH_SHORT).show();
//                            JSONObject detail = jsonObject.getJSONObject("detail");
//                            User user = new User();
//                            user.setUserId(detail.getInt("userId"));
//                            user.setUserName(detail.getString("userName"));
//                            user.setUserPwd(detail.getString("userPwd"));
//                            user.setUserTel(detail.getString("userTel"));
//                            user.setSex(detail.getString("sex"));
//                            user.setBirth(getStringDate( detail.getString("birth") ));
//                            user.setArea(detail.getString("area"));
//                            user.setHeadImg(detail.getString("headImg"));
//                            user.setSlogan(detail.getString("slogan"));
//                            user.setRegistration(getStringDate( detail.getString("registration") ));
//                            user.setState(detail.getInt("state"));
//
//                            MainActivity.myappInfo.setMUser(user);
//
//                            String sql= "update tb_user set userName=?,userPwd=?,userTel=?,sex=?,birth=?,area=?," +
//                                    "headImg=?,slogan=? where userId=?";
////                            String sql= "insert into tb_user (userId, userName, userPwd, userTel, sex, birth, area, " +
////                                    "headImg, slogan, registration, state)values(?,?,?,?,?,?,?,?,?,?,?)";
//                            //获取可写入数据库
//                            SQLiteDatabase db = dbHelper.getReadableDatabase();
//                            //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
//                            db.execSQL(sql,new Object[]{user.getUserName(),user.getUserPwd(),user.getUserTel(),
//                                    user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
//                                    user.getSlogan(),user.getUserId()});
//                            db.close();
//
//
//                        }else if(msg.equals("获取失败")){
//                            Toast.makeText(mcontext, "用户已注销", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    Toast.makeText(mcontext, "网络出错", Toast.LENGTH_SHORT).show();
//                }
//            });
//            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.i("db.err", e.toString());
        }
    }

    private Date getStringDate(String dateStr) {
        Date date = new Date();
        //注意format的格式要与日期String的格式相匹配
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(dateStr);
            System.out.println(date.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private String getDateString(Date date) {
        String dateStr = "";
        //format的格式可以任意
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateStr = sdf.format(date);
            System.out.println(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    private User getUser(JSONObject detail) {
        User user = new User();


        return user;
    }
}
