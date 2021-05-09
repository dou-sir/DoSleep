package com.jit.dyy.dosleep;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.tools.utils.ResHelper;

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
                        Toast.makeText(this, "请输入完整的电话号码", Toast.LENGTH_SHORT).show();
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
                //todo
                this.finish();
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
                Toast.makeText(LoginByTelActivity.this, "请查收验证码", Toast.LENGTH_SHORT).show();
            }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                if (result == SMSSDK.RESULT_COMPLETE) {//todo 登录
                Toast.makeText(LoginByTelActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(LoginByTelActivity.this,MainActivity.class);
//                User user = new User();
//                user.setPhone(phoneNumber);
//                user=userService.addUser(user);
//                intent.putExtra("user",user);
//                startActivity(intent);
//                LoginByTelActivity.this.finish();
                } else {
                    Toast.makeText(LoginByTelActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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



