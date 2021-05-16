package com.jit.dyy.dosleep;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_modify)
    TextView tvModify;
    @BindView(R.id.rl_bt)
    RelativeLayout rlBt;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_conf)
    Button btConf;
    @BindView(R.id.ll_bt)
    LinearLayout llBt;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.et_sex)
    EditText etSex;
    @BindView(R.id.et_birth)
    EditText etBirth;
    @BindView(R.id.et_area)
    EditText etArea;
    @BindView(R.id.et_slogan)
    EditText etSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);
        setInfo();
    }

    @OnClick({R.id.bt_conf, R.id.bt_cancel, R.id.tv_back, R.id.tv_modify})
//
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_modify:
                enableEdit();
                break;
            case R.id.bt_cancel:
                disableEdit();
                setInfo();
                setInfo();
                break;
            case R.id.bt_conf:
                disableEdit();
                User editedUser = getEditedUser();
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("userId",MainActivity.myappInfo.getMUser().getUserId());
                    jsonObject.put("userName",editedUser.getUserName());
                    jsonObject.put("userTel",editedUser.getUserTel());
                    jsonObject.put("sex",editedUser.getSex());
                    //!!!要日期转换
                    jsonObject.put("birth",getDateTime(editedUser.getBirth())); //!!!要日期转换
                    jsonObject.put("area",editedUser.getArea());
                    jsonObject.put("slogan",editedUser.getSlogan());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("bbb",jsonObject.toString());
                String url="http://m.dosleep.tech/user/update";
                RequestQueue requestQueue= Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            Log.d("updateaaa:", jsonObject.toString());
                            String msg = jsonObject.getString("msg");
                            Boolean flag = jsonObject.getBoolean("flag");
                            Log.d("msg", msg);
                            if( flag ){
                                Toast.makeText(EditInfoActivity.this, "成功", Toast.LENGTH_SHORT).show();
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
                                SQLiteOpenHelper dbHelper = new DataBaseHelper(EditInfoActivity.this);;
                                SQLiteDatabase db = dbHelper.getReadableDatabase();
                                //        new String[] { }对应sql语句的'?'  有几个'?'就需要几个参数
                                db.execSQL(sql,new Object[]{user.getUserName(),user.getUserPwd(),user.getUserTel(),
                                        user.getSex(),user.getBirth(),user.getArea(),user.getHeadImg(),
                                        user.getSlogan(),user.getUserId()});
                                db.close();

                                disableEdit();

                            }else if(msg.equals("用户名已被占用")) {
                                Toast.makeText(EditInfoActivity.this, "用户名已被占用", Toast.LENGTH_SHORT).show();
                            }else if(msg.equals("手机号已被占用")) {
                                Toast.makeText(EditInfoActivity.this, "手机号已被占用", Toast.LENGTH_SHORT).show();
                            }else if(msg.equals("修改失败")) {
                                Toast.makeText(EditInfoActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("updateaaaa"+volleyError.getCause()+"***"+volleyError.getCause());
                        Toast.makeText(EditInfoActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
                break;
        }

    }


    private User getEditedUser() {
        User user = new User();
        user.setUserName(etName.getText().toString().trim());
        user.setUserTel(etTel.getText().toString().trim());
        user.setSex(etSex.getText().toString().trim());
        user.setBirth(getStringDate(etBirth.getText().toString().trim()));
        user.setArea(etArea.getText().toString().trim());
        user.setSlogan(etSlogan.getText().toString().trim());
        return user;
    }

    private void disableEdit() {
        llBt.setVisibility(View.GONE);
        rlBt.setVisibility(View.VISIBLE);
        tvTitle.setText("个人信息");
        etName.setEnabled(false);
        etTel.setEnabled(false);
        etSex.setEnabled(false);
        etBirth.setEnabled(false);
        etArea.setEnabled(false);
        etSlogan.setEnabled(false);
    }

    private void enableEdit() {
        llBt.setVisibility(View.VISIBLE);
        rlBt.setVisibility(View.GONE);
        tvTitle.setText("修改个人信息");
        etName.setEnabled(true);
        etTel.setEnabled(true);
        etSex.setEnabled(true);
        etBirth.setEnabled(true);
        etArea.setEnabled(true);
        etSlogan.setEnabled(true);
    }

    private void setInfo() {
        User mUser = MainActivity.myappInfo.getMUser();
        System.out.println("aaamUser:"+mUser.toString()+"**");
        etName.setText(mUser.getUserName());
        etTel.setText(mUser.getUserTel());
        if(!mUser.getSex().equals("null"))
            etSex.setText(mUser.getSex());
        else
            etSex.setText("");
        if(mUser.getBirth()!=null)
            etBirth.setText(getDateString(mUser.getBirth()));
        else
            etBirth.setText("");
        if(!mUser.getArea().equals("null"))
            etArea.setText(mUser.getArea());
        else
            etArea.setText("");
        if(!mUser.getSlogan().equals("null"))
            etSlogan.setText(mUser.getSlogan());
        else
            etSlogan.setText("");
    }

    private Date getStringDate(String dateStr) {
        Date date = new Date();
        //注意format的格式要与日期String的格式相匹配
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateStr = sdf.format(date);
            System.out.println(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    private String getDateTime(Date birth) {
        Calendar   calendar = new GregorianCalendar();
        calendar.setTime(birth);
        calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
        birth=calendar.getTime();
        return getDateString(birth);
    }
}
