package com.jit.dyy.dosleep;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.jit.dyy.dosleep.service.UserService;
import com.jit.dyy.dosleep.util.DataBaseHelper;
import com.mob.MobSDK;
import com.mob.tools.utils.ResHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginByTelActivity extends AppCompatActivity {

    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_verify)
    EditText etVerify;
    @BindView(R.id.bt_sendmessage)
    Button btSendmessage;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_loginbypwd)
    TextView tvLoginbypwd;

    private EventHandler eventHandler;
    private String phoneNumber;     // 电话号码
    private String verificationCode;  // 验证码
    private TimeCount timeCount;  //按钮倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_tel);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({ R.id.bt_sendmessage, R.id.bt_login, R.id.tv_loginbypwd })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sendmessage:
                //获取验证码间隔时间小于1分钟，进行toast提示，在当前页面不会有这种情况，但是当点击验证码返回上级页面再进入会产生该情况
                if (!TextUtils.isEmpty(etPhonenumber.getText())) {
                    if (etPhonenumber.getText().length() == 11) {
                        phoneNumber = etPhonenumber.getText().toString();
                        SMSSDK.getVerificationCode("86", phoneNumber); // 发送验证码给号码为 phoneNumber 的手机
                        etVerify.requestFocus();
                        timeCount = new TimeCount(60000, 1000);
                        timeCount.start();
                    }
                    else {
                        Toast.makeText(this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
                        etPhonenumber.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                    etPhonenumber.requestFocus();
                }
                break;
            case R.id.bt_login:
                if (!TextUtils.isEmpty(etVerify.getText())) {
                    if (etVerify.getText().length() == 4) {
                        verificationCode = etVerify.getText().toString();
                        //验证
                        SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
                    } else {
                        Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                        etVerify.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    etVerify.requestFocus();
                }
                break;
            case R.id.tv_loginbypwd:
                startActivity(new Intent(this,LoginByNameActivity.class));
                finish();
                break;
        }
    }


    private void init() {
        //手机号编辑框监听
        etPhonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //手机号输入大于10位，获取验证码按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etVerify.setEnabled(etPhonenumber.getText() != null && etPhonenumber.getText().length() > 10);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg=new Message();
                msg.arg1=event;
                msg.arg2=result;
                msg.obj=data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }
    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                if (result == -1)
                    Toast.makeText(LoginByTelActivity.this, "请查收验证码", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LoginByTelActivity.this, "发送失败！当前手机号发送短信的数量超过每日限额。", Toast.LENGTH_SHORT).show();
            }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                //提交验证码成功
                if (result == SMSSDK.RESULT_COMPLETE) {//todo 登录
                Toast.makeText(LoginByTelActivity.this, "验证成功", Toast.LENGTH_SHORT).show();

                setLoginInfo(phoneNumber);
//                MainActivity.myappInfo.getMUser().setUserTel(phoneNumber);
//                MainActivity.myappInfo.setLoginFlag(true);
//                LoginByTelActivity.this.finish();

//                UserService userService = new UserService(LoginByTelActivity.this);
//                int flag = userService.loginByTel(phoneNumber);
//                if (flag == 1){
//                    finish();
//                }else if (flag == 2) {
//                    startActivity(new Intent(LoginByTelActivity.this,PwdActivity.class));
//                }else {
//                    Toast.makeText(LoginByTelActivity.this, "获取信息时出现了问题", Toast.LENGTH_SHORT).show();
//                }

                } else {
                    Toast.makeText(LoginByTelActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };


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

    private void setLoginInfo(String phoneNumber){
        if(phoneNumber == null){
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }else {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("userTel",phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("bbb",jsonObject.toString());
            String url="http://m.dosleep.tech/user/loginByTel";
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d("aaa", jsonObject.toString());
                        String msg = jsonObject.getString("msg");
                        Log.d("msg", msg);
                        Boolean flag = jsonObject.getBoolean("flag");
                        if(flag){
                            Toast.makeText(LoginByTelActivity.this, "成功", Toast.LENGTH_SHORT).show();
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
                            DataBaseHelper dbHelper = new DataBaseHelper(LoginByTelActivity.this);
                            //获取可写入数据库
                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                            //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                            db.execSQL(sql,new Object[]{user.getUserId(),user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                    user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                    user.getSlogan(),user.getRegistration(),user.getState()});
                            db.close();
                            if (user.getUserPwd() != "null"){
                                finish();
                            }else {
                                startActivity(new Intent(LoginByTelActivity.this,PwdActivity.class));
                                finish();
                            }

                        }else {
                            Toast.makeText(LoginByTelActivity.this, "获取信息时出现了问题", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("neterr"+volleyError.toString());
                    Toast.makeText(LoginByTelActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    //验证码倒计时
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btSendmessage.setClickable(false);
            btSendmessage.setText("(" + millisUntilFinished / 1000 + ") ");
        }

        @Override
        public void onFinish() {
            btSendmessage.setText("重新获取");
            btSendmessage.setClickable(true);
        }
    }
}



