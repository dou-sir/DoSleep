package com.jit.dyy.dosleep;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jit.dyy.dosleep.bean.User;
import com.jit.dyy.dosleep.service.UserService;
import com.jit.dyy.dosleep.util.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePwdActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_verify)
    EditText etVerify;
    @BindView(R.id.bt_sendmessage)
    Button btSendmessage;
    @BindView(R.id.ll_tel)
    LinearLayout llTel;
    @BindView(R.id.et_pwd1)
    EditText etPwd1;
    @BindView(R.id.et_pwd2)
    EditText etPwd2;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_conf)
    Button btConf;
    @BindView(R.id.tv_byTel)
    TextView tvByTel;
    @BindView(R.id.tv_byPwd)
    TextView tvByPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_conf, R.id.bt_cancel, R.id.tv_byTel, R.id.tv_byPwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_byTel:
                etPwd.setVisibility(View.GONE);
                llTel.setVisibility(View.VISIBLE);
                tvByTel.setVisibility(View.GONE);
                tvByPwd.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_byPwd:
                etPwd.setVisibility(View.VISIBLE);
                llTel.setVisibility(View.GONE);
                tvByTel.setVisibility(View.VISIBLE);
                tvByPwd.setVisibility(View.GONE);
                break;
            case R.id.bt_cancel:
                finish();
                break;
            case R.id.bt_conf:
                String pwdStr = etPwd.getText().toString().trim();
                String pwdStr1 = etPwd1.getText().toString().trim();
                String pwdStr2 = etPwd2.getText().toString().trim();
                if (pwdStr.equals("")) {
                    Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                }else if (pwdStr1.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (pwdStr2.equals("")) {
                    Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
                }else if (!pwdStr1.equals(pwdStr2)){
                    Toast.makeText(this, "两次密码不一致请重新输入", Toast.LENGTH_SHORT).show();
                }else {
//                    Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();

                    updatePwd();
//                    UserService userService = new UserService(this);
//                    userService.setMUser();

                }
                break;
        }

    }

    private void updatePwd() {

        //验证
        User user = MainActivity.myappInfo.getMUser();
        user.setUserPwd(etPwd.getText().toString().trim());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("userName",user.getUserName());
            jsonObject.put("userPwd",user.getUserPwd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("bbb",jsonObject.toString());
        String url="http://m.dosleep.tech/user/verify";
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.d("aaa", jsonObject.toString());
                    String msg = jsonObject.getString("msg");
                    Boolean flag = jsonObject.getBoolean("flag");
                    Log.d("msg", msg);
                    if( flag ){


                        //验证通过，修改
                        JSONObject jsonObject2=new JSONObject();
                        try {
                            jsonObject2.put("userId",MainActivity.myappInfo.getMUser().getUserId());
                            jsonObject2.put("userPwd",etPwd1.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("bbb",jsonObject2.toString());
                        String url="http://m.dosleep.tech/user/update";
                        RequestQueue requestQueue= Volley.newRequestQueue(UpdatePwdActivity.this);
                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject2, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    Log.d("aaa", jsonObject.toString());
                                    String msg = jsonObject.getString("msg");
                                    Boolean flag = jsonObject.getBoolean("flag");
                                    Log.d("msg", msg);
                                    if( flag ){
                                        Toast.makeText(UpdatePwdActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                        JSONObject detail = jsonObject.getJSONObject("detail");
                                        User user = new User();
                                        user.setUserId(detail.getInt("userId"));
                                        user.setUserName(detail.getString("userName"));
                                        user.setUserPwd(detail.getString("userPwd"));
                                        user.setUserTel(detail.getString("userTel"));
                                        user.setSex(detail.getString("sex"));
                                        user.setArea(detail.getString("area"));
                                        user.setHeadImg(detail.getString("headImg"));
                                        user.setSlogan(detail.getString("slogan"));
                                        user.setState(detail.getInt("state"));

                                        MainActivity.myappInfo.setMUser(user);

                                        String sql= "update tb_user set userName=?,userPwd=?,userTel=?,sex=?,birth=?,area=?," +
                                                "headImg=?,slogan=? where userId=?";

                                        //获取可写入数据库
                                        SQLiteOpenHelper dbHelper = new DataBaseHelper(UpdatePwdActivity.this);;
                                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                                        //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                                        db.execSQL(sql,new Object[]{user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                                user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                                user.getSlogan(),user.getUserId()});
                                        db.close();

                                        finish();

                                    }else if(msg.equals("修改失败")){
                                        Toast.makeText(UpdatePwdActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(UpdatePwdActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);


                    }else if(msg.equals("用户名或密码错误")){
                        Toast.makeText(UpdatePwdActivity.this, "旧密码验证失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(UpdatePwdActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);


    }

    private void verifyUser() {



    }

}
