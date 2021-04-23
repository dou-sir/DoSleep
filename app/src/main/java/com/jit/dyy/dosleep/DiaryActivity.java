package com.jit.dyy.dosleep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.jit.dyy.dosleep.util.myDB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiaryActivity extends AppCompatActivity {

    @BindView(R.id.diary_list)
    ListView diaryList;
    @BindView(R.id.add_diary)
    ImageButton addDiary;
    @BindView(R.id.RLdiary)
    RelativeLayout RLdiary;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.ibClose)
    ImageButton ibClose;

    SimpleAdapter simpleAdapter;
    List<Map<String, Object>> data;
    public static boolean tag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        ButterKnife.bind(this);
        init();
//        Intent intent = getIntent();
//        if ( !intent.getBooleanExtra("tag",true) ) {
//            RLdiary.setVisibility(View.VISIBLE);
//            login.setVisibility(View.GONE);
//        } else {
//            RLdiary.setVisibility(View.GONE);
//            login.setVisibility(View.VISIBLE);
//        }
        if (!tag) {
            RLdiary.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }
        if (tag) {
            RLdiary.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.login, R.id.add_diary, R.id.ibClose})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.login:
                RLdiary.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                break;
            case R.id.add_diary:
                Intent intent = new Intent(this, EditDiaryActivity.class);
                startActivityForResult(intent, 12);
                finish();
                break;
            case R.id.ibClose:
                finish();
                break;
        }
    }

    public void init() {

        myDB dbHelp = new myDB(this);
        final SQLiteDatabase sqLiteDatabase = dbHelp.getWritableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery("select * from diary_table", null);
        int counts = cursor.getCount();
        final String[] date = new String[counts];
        final String[] month = new String[counts];
        final String[] weekday = new String[counts];

        final String[] time = new String[counts];
        final String[] diary = new String[counts];
        final String[] diary_summary = new String[counts];
        data = new ArrayList<>();
        if (cursor.moveToFirst() == true) {
            for (int i = 0; i < counts; i++) {
                month[i] = cursor.getString(cursor.getColumnIndex("month"));
                date[i] = cursor.getString(cursor.getColumnIndex("date"));
                weekday[i] = cursor.getString(cursor.getColumnIndex("week"));
                time[i] = cursor.getString(cursor.getColumnIndex("time"));
                diary[i] = cursor.getString(cursor.getColumnIndex("diary"));
                diary_summary[i] = getSummary(diary[i]);
                cursor.moveToNext();
            }
        }
//        cursor.close();
        for (int i = 0; i < counts; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("month", month[i]);
            temp.put("date", date[i]);
            temp.put("week", weekday[i]);
            temp.put("time", time[i]);
            temp.put("diary", diary_summary[i]);
            data.add(temp);
        }
        simpleAdapter = new SimpleAdapter(this, data, R.layout.diary_list,
                new String[]{"month", "date", "week", "time", "diary"}, new int[]{R.id.item_month,
                R.id.item_date, R.id.item_week, R.id.item_time, R.id.item_summary});
        if (cursor.moveToFirst() == true)
            diaryList.setAdapter(simpleAdapter);

        diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiaryActivity.this, EditDiaryActivity.class);
                intent.putExtra("diary_position", position);
                startActivityForResult(intent,12);
            }
        });

        diaryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivity.this);
                builder.setInverseBackgroundForced(true);
                builder.setMessage("是否删除？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String whereClaus = "diary = ?";
                        String[] whereArgs = {diary[position]};
                        sqLiteDatabase.delete("diary_table", whereClaus, whereArgs);
                        data.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                return true;
            }
        });

    }

    public String getSummary(String string) {
        String r_string;
        if (string.length() >= 18) {
            r_string = string.substring(0, 11) + "…";
        } else {
            r_string = string;
        }
        return r_string;
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 12 ) { //&& resultCode == 21
            finish();
            tag = false;
        }
    }
}
