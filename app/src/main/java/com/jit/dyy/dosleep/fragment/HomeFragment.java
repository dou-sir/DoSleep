package com.jit.dyy.dosleep.fragment;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jit.dyy.dosleep.R;
import com.jit.dyy.dosleep.util.SimpleLineChart;
import com.jit.dyy.dosleep.util.myDB2;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.grade)
    TextView gradeText;
    @BindView(R.id.recentGrade)
    TextView recentGrade;
    @BindView(R.id.suggest)
    TextView suggestion;
    @BindView(R.id.upper)
    LinearLayout upper;
    @BindView(R.id.simpleLineChart)
    SimpleLineChart mSimpleLineChart;
    Unbinder unbinder;
    private boolean isFirstLoading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void init() {
        myDB2 dbHelp = new myDB2(getActivity());
        SQLiteDatabase sqLiteDatabase = dbHelp.getWritableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from grade_table", null);

            int counts = Math.min(cursor.getCount(), 7);
            String[] time = new String[counts];
            double[] grade = new double[counts];
            if (cursor.moveToLast() == true) {
                for (int i = 0; i < counts; i++) {
                    time[i] = cursor.getString(cursor.getColumnIndex("time"));
                    grade[i] = cursor.getDouble(cursor.getColumnIndex("grade"));
                    cursor.moveToPrevious();
                }
            }
//            cursor.close();
            if (time.length != 0) {
                String[] yItem = {"100", "80", "60", "40", "20", "0"};
                mSimpleLineChart.setXItem(time);
                mSimpleLineChart.setYItem(yItem);
                HashMap<Integer, Double> pointMap = new HashMap();
                for (int i = 0; i < time.length; i++) {
                    pointMap.put(i, grade[i] / 100);
                }
                mSimpleLineChart.setData(pointMap);
                SharedPreferences sharedpref = getActivity().getSharedPreferences("info", MODE_PRIVATE);
                String suggest = sharedpref.getString("suggestion", "");
                float gra = sharedpref.getFloat("grade", 0);
                suggestion.setText("DoSlepp的建议：\n"+ suggest);
                long x = Math.round(gra);
                gradeText.setText(x + "");
            } else {
                suggestion.setText("DoSlepp的建议：\n体验一下我们的app吧~");
            }
        } catch (Exception e) {
            Log.i("e", e.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
