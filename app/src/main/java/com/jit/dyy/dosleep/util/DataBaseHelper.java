package com.jit.dyy.dosleep.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DOSLEEP.db";
    private static final String TABLE_NAME = "tb_user";
    private static final int DB_VERSION = 1;


    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //自定义构造方法 会创建一个my_db作为名字的数据库
    public DataBaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    //一般会在onCreate 进行数据库初始化 程序第一次创建时，调用DataBaseHelper构造方法

    @Override
    public void onCreate(SQLiteDatabase db) {
        //1、用户表
        db.execSQL("create table if not exists "+TABLE_NAME+"(userId integer primary key autoincrement,"+
                "userName String,userPwd String , userTel String, sex String, birth Date, area String," +
                " headImg String, slogan String, registration Date, state int )"); //
//        //2、report表
//        db.execSQL("create table if not exists tb_shop(sid integer primary key autoincrement,"+
//                "sname String,sphone String, saddress String, simg int )");
//        //3. Diary
//        db.execSQL("create table if not exists tb_order(oid integer primary key autoincrement,"+
//                "uid integer,date String,sname String,ophone String,oaddress String,oitems String,cost int)");
//        //4、表
//        db.execSQL("create table if not exists tb_goods(gid integer primary key autoincrement,"+
//                "gname String,sid int,price int,gimg int)");

        //用户
//        db.execSQL("insert into tb_user values(0,'cxk','123','2019/06/06',1,'6655','鸡你太美',1)");

        db.execSQL("insert into tb_user values(1,null,null,null,null,null,null,null,null,null,1)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
