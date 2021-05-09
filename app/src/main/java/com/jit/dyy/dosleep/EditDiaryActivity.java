package com.jit.dyy.dosleep;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jit.dyy.dosleep.util.JsonParser;
import com.jit.dyy.dosleep.util.myDB;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditDiaryActivity extends Activity {

    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.diary_text)
    EditText diaryText;
    @BindView(R.id.add_diary)
    ImageButton addDiary;

    private static final String TABLE_NAME = "diary_table";
    int year;
    int month;
    int day;
    int minute;
    int hour;
    int weekday;
//    //定义Activity退出动画的成员变量
//    protected int activityCloseEnterAnimation;
//    protected int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);
        ButterKnife.bind(this);

//        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
//        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
//        activityStyle.recycle();
//        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
//        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
//        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
//        activityStyle.recycle();
//
//        //设置布局在底部
//        getWindow().setGravity(Gravity.BOTTOM);
//        //设置布局填充满宽度
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
//        getWindow().setAttributes(layoutParams);
//        585d429d b3ed3453
        verifyStoragePermissions();
        String APP_ID = "585d429d";
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + APP_ID);

        final Intent intent = new Intent(EditDiaryActivity.this, DiaryActivity.class);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Calendar calendar = Calendar.getInstance();
            TimeZone timeZone = java.util.TimeZone.getTimeZone("GMT+8");
            calendar.setTimeZone(timeZone);

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
            minute = calendar.get(Calendar.MINUTE);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            weekday = calendar.get(Calendar.DAY_OF_WEEK);

            String weekdayTrans = null;
            if (weekday == 1) {
                weekdayTrans = "周日";
            } else if (weekday == 2) {
                weekdayTrans = "周一";
            } else if (weekday == 3) {
                weekdayTrans = "周二";
            } else if (weekday == 4) {
                weekdayTrans = "周三";
            } else if (weekday == 5) {
                weekdayTrans = "周四";
            } else if (weekday == 6) {
                weekdayTrans = "周五";
            } else if (weekday == 7) {
                weekdayTrans = "周六";
            }

            date.setText(year+"年"+month+"月"+day+"日"+" "+hour+":"+minute +" "+ weekdayTrans);

            final String finalWeekdayTrans = weekdayTrans;
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(diaryText.getText().toString())) {
                        Toast.makeText(EditDiaryActivity.this, "日记内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        myDB mydb = new myDB(getBaseContext());
                        SQLiteDatabase db = mydb.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("date", day);
                        cv.put("month", month);
                        cv.put("week", finalWeekdayTrans);
                        cv.put("time", hour+":"+minute);
                        cv.put("diary", diaryText.getText().toString());
                        cv.put("sum_time", date.getText().toString());
                        db.insert(TABLE_NAME, null, cv);
                        db.close();
//                        intent.putExtra("tag",false);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            int  position;
            position = extras.getInt("diary_position");
            myDB dbHelp = new myDB(this);
            final SQLiteDatabase sqLiteDatabase = dbHelp.getWritableDatabase();
            final Cursor cursor = sqLiteDatabase.rawQuery("select * from diary_table", null);
            cursor.moveToPosition(position);
            date.setText(cursor.getString(cursor.getColumnIndex("sum_time")));
            diaryText.setText(cursor.getString(cursor.getColumnIndex("diary")));

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues cv = new ContentValues();
                    cv.put("diary", diaryText.getText().toString());
                    String whereClause = "_id = ?";
                    String[] whereArgs = {cursor.getString(cursor.getColumnIndex("_id"))};
                    sqLiteDatabase.update("diary_table", cv, whereClause, whereArgs);
//                    intent.putExtra("tag",false);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        FlowerCollector.onResume(this);
//    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        FlowerCollector.onPause(this);
//    }

    @OnClick({R.id.cancel, R.id.add_diary})// R.id.save,
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_diary:
                RecognizerDialog recognizerDialog = new RecognizerDialog(this, null);
                recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
                recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                recognizerDialog.setListener(mRecoListener);
                recognizerDialog.show();
                break;
            case R.id.cancel:
                Intent intent = new Intent(EditDiaryActivity.this, DiaryActivity.class);
//                intent.putExtra("tag",false);
                startActivity(intent);
                finish();
                break;
        }
    }

    private RecognizerDialogListener mRecoListener = new RecognizerDialogListener() {

        @Override
        public void onError(SpeechError speechError) {
        }

        @Override
        public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {
            String text = diaryText.getText().toString();
            text += JsonParser.parseIatResult(recognizerResult.getResultString());
            diaryText.setText(text);
        }

    };

    private void verifyStoragePermissions() {
        String[] permissions = new String[]{
                "android.permission.RECORD_AUDIO",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.READ_PHONE_STATE",
                "android.permission.CHANGE_NETWORK_STATE",
                "android.permission.INTERNET",
                "android.permission.ACCESS_WIFI_STATE"
        };
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EditDiaryActivity.this, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {}

            @Override
            public void onDenied(String permission) {}
        });
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
