package com.jit.dyy.dosleep;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jit.dyy.dosleep.bean.User;
import com.jit.dyy.dosleep.util.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginByNameActivity extends AppCompatActivity {

    @BindView(R.id.et_uname)
    EditText etUname;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_fgt)
    TextView tvFgt;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_loginbytel)
    TextView tvLoginbytel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_name);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_fgt, R.id.bt_login, R.id.tv_loginbytel })
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_login:
                String nameStr = etUname.getText().toString().trim();
                String pwdStr = etPwd.getText().toString().trim();
                if(nameStr.equals("")||pwdStr.equals("")){
                    Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("userName",nameStr);
                        jsonObject.put("userPwd",pwdStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("bbb",jsonObject.toString());
                    String url="http://m.dosleep.tech/user/login";
                    RequestQueue requestQueue= Volley.newRequestQueue(this);
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                Log.d("aaa", jsonObject.toString());
                                String msg = jsonObject.getString("msg");
                                Log.d("msg", msg);
                                if(msg.equals("登录成功")){
                                    Toast.makeText(LoginByNameActivity.this, "成功", Toast.LENGTH_SHORT).show();
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
                                    DataBaseHelper dbHelper = new DataBaseHelper(LoginByNameActivity.this);
                                    //获取可写入数据库
                                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                                    //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                                    db.execSQL(sql,new Object[]{user.getUserId(),user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                            user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                            user.getSlogan(),user.getRegistration(),user.getState()});
                                    db.close();
                                    if (user.getUserPwd() != null){
                                        finish();
                                    }else {
                                        startActivity(new Intent(LoginByNameActivity.this,PwdActivity.class));
                                    }

                                }else if(msg.equals("用户名或密码错误")){
                                    Toast.makeText(LoginByNameActivity.this, "用户名密码有误", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(LoginByNameActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
                break;
            case R.id.tv_fgt:
                startActivity(new Intent(this,LoginByTelActivity.class));
                finish();
                break;
            case R.id.tv_loginbytel:
                startActivity(new Intent(this,LoginByTelActivity.class));
                finish();
                break;
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
}
