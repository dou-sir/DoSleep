package com.jit.dyy.dosleep;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jit.dyy.dosleep.receiver.AlarmReceiver;
import com.jit.dyy.dosleep.receiver.MyReceiver;
import com.jit.dyy.dosleep.service.Music;
import com.jit.dyy.dosleep.service.MusicService;
import com.jit.dyy.dosleep.util.myDB2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Math.pow;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.word1)
    TextView word1;
    @BindView(R.id.hourt)
    TextView hourt;
    @BindView(R.id.maohao1)
    TextView maohao1;
    @BindView(R.id.mint)
    TextView mint;
    @BindView(R.id.maohao2)
    TextView maohao2;
    @BindView(R.id.sec)
    TextView sec;
    @BindView(R.id.jishi)
    LinearLayout jishi;
    @BindView(R.id.timeBtn)
    Button timeBtn;
    @BindView(R.id.cancelAlarmBtn)
    Button cancelAlarmBtn;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.start)
    Button start;
    @BindView(R.id.reset)
    Button reset;
    @BindView(R.id.ibClose)
    ImageButton ibClose;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.rlMain)
    RelativeLayout rlMain;
    @BindView(R.id.image1)
    ImageView imageView;
    @BindView(R.id.play_button)
    ImageButton imageButton;
    @BindView(R.id.last_music)
    ImageButton last_music;
    @BindView(R.id.next_music)
    ImageButton next_music;
    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.loaded)
    TextView loaded;
    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.playing)
    TextView playing;
    @BindView(R.id.playTime)
    TextView playTime;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.resTime)
    TextView resTime;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.musicList)
    ListView musicList;

    private List<Music> music = new LinkedList<>();
    private MusicService musicService;
    private boolean isPlaying;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private ObjectAnimator animator;
    private int musicPosition = 0;

    public static String musicWidget = "com.jit.dyy.dosleep.musicOption";
    public static String changeWidget = "com.jit.dyy.dosleep.changeWidget";

    private long timeusedinsec;
    private boolean isstop = false;
    private int alarmHour, alarmMinute;
    private int timeOfSleep;

    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;
    private SensorManager mSensorManager;
    private LocationManager mLocationManager;

    //  初始化位置服务中的一个位置的数据来源
    private String provider;
    String suggest = "";

    int num1 = 0;

    public static final String TAG = "Alarm";
    private Calendar calendar;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!isstop) {
                        updateView();
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    Handler handler = new Handler();
    Runnable update = new Runnable() {
        @Override
        public void run() {
            int current = musicService.getMediaPlayer().getCurrentPosition();
            int res = musicService.getMediaPlayer().getDuration() - current;
            seekBar.setProgress(current);
            playTime.setText(time.format(current));
            resTime.setText(time.format(res));
            if (res <= 0) {
                /*musicService.stop();
                playing.setText(R.string.stop);
//                play.setText(R.string.play);
                imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bofangzhuangtaitingzhi));
                isPlaying = false;
                animator.end();
                seekBar.setProgress(0);
                handler.removeCallbacks(update);*/
                setNext_music();
            }
            handler.postDelayed(update, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        //connection();
        prepareMusic();
        setListView();
        setAnimator();
        connection();
        setSeekBarOption();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(musicWidget);
        registerReceiver(receiver, intentFilter);
        init();

    }

    private void init() {
        //获取日历实例
        calendar = Calendar.getInstance();
        //  获取传感器的管理器
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        //  对Android版本做兼容处理，对于Android 6及以上版本需要向用户请求授权，而低版本的则直接调用
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }//todo




    }

    @OnClick({R.id.reset, R.id.start, R.id.timeBtn, R.id.ibClose,R.id.play_button, R.id.next_music, R.id.last_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                saveData();
                timeusedinsec = 0;
                isstop = true;
                mSensorManager.unregisterListener(mSensorEventListener);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                reset.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                word1.setText("您上次睡了");
                break;
            case R.id.start:
                suggest = "";
                String x = "00";
                hourt.setText(x);
                mint.setText(x);
                sec.setText(x);
                mHandler.removeMessages(1);
                isstop = false;
                mHandler.sendEmptyMessage(1);
                Calendar cl = Calendar.getInstance();
                TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
                cl.setTimeZone(timeZone);
                timeOfSleep = cl.get(Calendar.HOUR_OF_DAY);
                mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,
                        SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
                        SensorManager.SENSOR_DELAY_UI);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                start.setVisibility(View.GONE);
                reset.setVisibility(View.VISIBLE);
                jishi.setVisibility(View.VISIBLE);
                word1.setText("睡眠开始");
                break;
            case R.id.timeBtn:
                Log.d(TAG, "click the time button to set time");
                calendar.setTimeInMillis(System.currentTimeMillis());
                //TimePickerDialog timePickerDialog = new Ti;
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        alarmHour = h;
                        alarmMinute = m;
                        //更新按钮上的时间
                        timeBtn.setText(formatTime(h, m));
                        //设置日历的时间，主要是让日历的年月日和当前同步
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        //设置日历的小时和分钟
                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, m);
                        //将秒和毫秒设置为0
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        //建立Intent和PendingIntent来调用闹钟管理器
                        Intent intent = new Intent(RecordActivity.this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(RecordActivity.this, 0, intent, 0);
                        //获取闹钟管理器
                        AlarmManager alarmManager = (AlarmManager) RecordActivity.this.getSystemService(ALARM_SERVICE);
                        //设置闹钟
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10 * 1000, pendingIntent);
                        Log.d(TAG, "set the time to " + formatTime(h, m));
                        //cancelAlarmBtn.setVisibility(View.VISIBLE);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
            case R.id.cancelAlarmBtn:
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                //获取闹钟管理器
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
//                cancelAlarmBtn.setVisibility(View.INVISIBLE);
                break;
            case R.id.play_button:
                playAndPause();
                break;
            case R.id.next_music:
                setNext_music();
                break;
            case R.id.last_music:
                setLast_music();
                break;
            case R.id.ibClose:
                stop();
                finish();
                break;
        }
    }

    public String formatTime(int h, int m) {
        StringBuffer buf = new StringBuffer();
        if (h < 10) {
            buf.append("0" + h);
        } else {
            buf.append(h);
        }
        buf.append(" : ");
        if (m < 10) {
            buf.append("0" + m);
        } else {
            buf.append(m);
        }
        return buf.toString();
    }

    private void updateView() {
        timeusedinsec += 1;
        int hour = (int) (timeusedinsec / 60 / 60) % 60;
        int minute = (int) (timeusedinsec / 60) % 60;
        int second = (int) (timeusedinsec % 60);
        if (hour < 10)
            hourt.setText("0" + hour);
        else
            hourt.setText("" + hour);
        if (minute < 10)
            mint.setText("0" + minute);
        else
            mint.setText("" + minute);
        if (second < 10)
            sec.setText("0" + second);
        else
            sec.setText("" + second);
    }

    // sensor event listener
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        float[] accValues = new float[3];
        float[] magValues = new float[3];

        //  当传感器数据更新的时候，系统会回调监听器里的onSensorChange函数，便可以在这里对传感器数据进行相应处理
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    // 加速度传感器 xyz轴加速度
                    accValues = event.values;
                    double value = 1;
                    double max = 3;
//                    if (Math.abs(accValues[0]) > value || Math.abs(accValues[1]) > value) {
//                        if (Math.abs(accValues[0]) < max || Math.abs(accValues[1]) < max) {
//                            num1++;
//                        }
//                    }
                    if (Math.abs(accValues[0]) > value || Math.abs(accValues[1]) > value) {
                        if (Math.abs(accValues[0]) < max || Math.abs(accValues[1]) < max) {
                            num1++;
//                            Toast.makeText(RecordActivity.this, "加速度传感器感应"+num1, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = event.values;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void saveData() {
        int numberOfPlay = MyReceiver.getNum2();
        double grade_numberOfPlay = pow(1.01, -numberOfPlay);
        if (numberOfPlay > 20) suggest += "睡觉玩手机会影响主人的学习哦。";

        double grade_numberOfTouch;
        int numberOfTouch = num1;
        if (numberOfTouch > 2000) {
            grade_numberOfTouch = 0.89;
            suggest += "主人最近是不是辗转难眠呢~睡眠质量不够高呢~";
        } else {
            grade_numberOfTouch = 1;
        }

        double sleepHour = (double) (timeusedinsec / 60 / 60) % 60;
        double grade_sumOfSleep;// = Math.pow(Math.E, -Math.pow((sleepHour-8), 2));

        Calendar cl = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
        cl.setTimeZone(timeZone);
        int nowHour = cl.get(Calendar.HOUR_OF_DAY);
        int nowMinute = cl.get(Calendar.MINUTE);
        int subOfAlarm;
        double alarmTime = alarmHour + alarmMinute / 60, nowTime = nowHour + nowMinute / 60;
        if (nowTime > alarmTime) {
            subOfAlarm = (nowHour - alarmHour) * 60 + nowMinute - alarmMinute;
        } else if (nowTime == alarmTime) {
            subOfAlarm = Math.abs(nowMinute - alarmMinute);
        } else {
            subOfAlarm = (alarmHour - nowHour) * 60 + nowMinute - alarmMinute;
        }
        double x1 = subOfAlarm / 20;
        double grade_subOfAlarm; // = (Math.pow(Math.E, (-3-x1))*Math.pow(x1+3, 3))/1.35;

        if (x1 > 2) {
            suggest += "主人最近睡眠不深，不知道怎么了？";
            grade_subOfAlarm = 0.8;
        } else if (x1 > -1) {
            suggest += "主人，您的生物钟很规律哦~希望您继续保持呢~";
            grade_subOfAlarm = 0.99;
        } else {
            suggest += "主人最近有点赖床哦~";
            grade_subOfAlarm = 0.9;
        }


        if (sleepHour > 9.3) {
            suggest += "主人睡的时间太长了噢~dodo早就醒了哼~";
            grade_sumOfSleep = 0.88;
        } else if (sleepHour > 6) {
            suggest += "主人的睡觉时长很健康呐~";
            grade_sumOfSleep = 0.96;
        } else {
            suggest += "dodo最近和主人一样，睡眠缺乏~";
            grade_sumOfSleep = 0.6;
        }


        double grade_timeOfSleep;
        if (timeOfSleep > 22 && timeOfSleep < 23) {
            grade_timeOfSleep = 1;
        } else if (timeOfSleep >= 23) {
            grade_timeOfSleep = 0.95;
            suggest += "另外，主人最近睡得有点迟啊~";
        } else if (timeOfSleep > 0 && timeOfSleep < 2) {
            grade_timeOfSleep = 0.75;
            suggest += "另外，主人最近睡得有点迟啊~";
        } else if (timeOfSleep > 11 && timeOfSleep < 15) {
            grade_timeOfSleep = 1;
        } else if (timeOfSleep > 2 && timeOfSleep < 6) {
            grade_timeOfSleep = 0.65;
            suggest += "最近在赶project吗？这样对身体不好的...\n";
        } else {
            grade_timeOfSleep = 0.95;
        }


        double grade = 100 * grade_subOfAlarm * grade_sumOfSleep * grade_numberOfTouch
                * grade_timeOfSleep * pow(grade_numberOfPlay, 0.2);
//                Math.pow(grade_subOfAlarm, 0.5Math.pow(grade_sumOfSleep, 0.4))*Math.pow(grade_timeOfSleep, 1)
//                **Math.pow(grade_numberOfPlay, 0.2)
//                *grade_numberOfTouch;
        grade = 10 * pow(grade, 0.5);

        Calendar calendar1 = Calendar.getInstance();
        TimeZone timeZone1 = TimeZone.getTimeZone("GTM+8");
        calendar1.setTimeZone(timeZone1);
        int month = calendar1.get(Calendar.MONTH) + 1;
        int day = calendar1.get(Calendar.DAY_OF_MONTH);
        String x = month + "." + day;

        SharedPreferences sharedpref = this.getSharedPreferences("info", MODE_PRIVATE);
        float z = sharedpref.getFloat("grade", 100);
        long grade1 = Math.round(grade);
        z = (float) (0.2 * z + 0.8 * grade1);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putFloat("grade", z);
        editor.putString("suggestion", suggest);
        editor.apply();

        myDB2 dbHelp = new myDB2(this);
        final SQLiteDatabase sqLiteDatabase = dbHelp.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("time", x);
        cv.put("grade", z);
        cv.put("sumOfSleep", sleepHour);
        cv.put("timeOfSleep", timeOfSleep);
        sqLiteDatabase.insert("grade_table", null, cv);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    //todo music
    private void prepareMusic() {
        music.add(new Music("music1", R.raw.music1));
        music.add(new Music("music2", R.raw.music2));
        music.add(new Music("music3", R.raw.music3));
        music.add(new Music("music4", R.raw.music4));
        music.add(new Music("music5", R.raw.music5));
        music.add(new Music("music6", R.raw.music6));
        music.add(new Music("music7", R.raw.music7));
        music.add(new Music("music8", R.raw.music8));
        music.add(new Music("music9", R.raw.music9));
        music.add(new Music("music10", R.raw.music10));
        music.add(new Music("music11", R.raw.music11));
        music.add(new Music("music12", R.raw.music12));
        music.add(new Music("music13", R.raw.music13));
        music.add(new Music("music14", R.raw.music14));
        music.add(new Music("music15", R.raw.music15));
        music.add(new Music("music16", R.raw.music16));
        music.add(new Music("music17", R.raw.music17));
    }

    private void setListView() {
        List<Map<String, String>> ml = new ArrayList<>();
        for (Music i : music) {
            Map<String, String> temp = new HashMap<>();
            temp.put("musicName", i.getMusicName());
            ml.add(temp);
        }
        SimpleAdapter sa = new SimpleAdapter(this, ml, R.layout.music_list,
                new String[]{"musicName"}, new int[]{R.id.text});
        musicList.setAdapter(sa);

        musicList.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                musicList.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicPosition = position;
                changeMusic(position);
            }
        });
    }




    private void setAnimator() {
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
    }

    private void connection() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("111","onServiceConnected");
            musicService = ((MusicService.MyBinder) (service)).getService();
            musicService.init();
            seekBar.setMax(musicService.getMediaPlayer().getDuration());
            handler.post(update);
            animator.setDuration(musicService.getMediaPlayer().getDuration());
            source.setText(music.get(0).getMusicSource());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("111","onServiceDisconnected");
            musicService = null;
        }

    };

    private void setSeekBarOption() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.getMediaPlayer().seekTo(seekBar.getProgress());
                if (animator.isStarted()) {
                    animator.end();
                }
                animator.setDuration(musicService.getMediaPlayer().getDuration() -
                        musicService.getMediaPlayer().getCurrentPosition());
                if (musicService.getMediaPlayer().isPlaying()) {
                    animator.start();
                }
            }
        });
    }

    private void setNext_music() {
        if (musicPosition == musicList.getCount() - 1) {
            musicPosition = 0;
        }
        else {
            musicPosition++;
        }
        changeMusic(musicPosition);
    }

    private void setLast_music() {
        if (musicPosition != 0) {
            musicPosition--;
        }
        changeMusic(musicPosition);
    }

    private void playAndPause() {
        musicService.playAndPause();
        if (isPlaying) {
            playing.setText("暂停");
            //play.setText(R.string.play);
            isPlaying = false;
            animator.pause();
            imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bofangzhuangtaitingzhi));
        } else {
            if (animator.isStarted()) {
                animator.resume();
            } else {
                animator.setDuration(musicService.getMediaPlayer().getDuration());
                animator.start();
            }
            playing.setText("播放中");
            //play.setText(R.string.pause);
            isPlaying = true;
            handler.post(update);
            imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bofangzhuangtai));
        }
        changeWidget(music.get(musicPosition).getMusicName(), isPlaying);
    }

    private void stop() {
//        handler.removeCallbacks(update);
//        seekBar.setProgress(0);
//        resTime.setText(time.format(musicService.getMediaPlayer().getDuration()));
//        playTime.setText(time.format(0));
//        playing.setText("R.string.stop");
//        //play.setText(R.string.play);
//        imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bofangzhuangtaitingzhi));
//        isPlaying = false;
//        animator.end();
//        musicService.stop();
//        changeWidget(music.get(musicPosition).getMusicName(), isPlaying);
        musicService.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            isPlaying = false;
            musicService.playAndPause();
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void changeMusic(int position) {
        if (musicService.getMediaPlayer().isPlaying()) {
            handler.removeCallbacks(update);
            isPlaying = false;
        }
        if (position == 0) {
            musicService.init();
            musicService.getMediaPlayer().start();
        }
        else {
            if (musicService.getMediaPlayer() != null) {
                musicService.setSource(music.get(position).getMusicSource());
                musicService.changeMusic();
            }
        }
        seekBar.setMax(musicService.getMediaPlayer().getDuration());
        if (animator.isStarted()) {
            animator.end();
        }
        animator.setDuration(musicService.getMediaPlayer().getDuration());
        animator.start();
        handler.post(update);
        isPlaying = true;
        //play.setText(R.string.pause);
        imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bofangzhuangtai));
        playing.setText("播放中");
        source.setText(music.get(position).getMusicSource());
        changeWidget(music.get(position).getMusicName(), isPlaying);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RecordActivity.musicWidget)) {
                int action = intent.getIntExtra("musicOption", -1);
                switch (action) {
                    case 0:
                        playAndPause();
                        break;
//                    case 1:
//                        stop();
//                        break;
                    case 2:
                        setNext_music();
                        break;
                    case 3:
                        setLast_music();
                        break;
                    default:
                        break;
                }
            }
        }
    };


    private void changeWidget(String source, boolean isPlaying) {
        Intent intent = new Intent(RecordActivity.changeWidget);
        intent.putExtra("source", source);
        intent.putExtra("isPlaying", isPlaying);
        this.sendBroadcast(intent);
    }

}
