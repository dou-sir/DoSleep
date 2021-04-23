package com.jit.dyy.dosleep.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jit.dyy.dosleep.DiaryActivity;
import com.jit.dyy.dosleep.R;
import com.jit.dyy.dosleep.WebviewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements AdapterView.OnItemClickListener {


    @BindView(R.id.lv_more)
    ListView lvMore;

    Unbinder unbinder;
    private List<Map<String, Object>> data;
    private int[] icons = {R.drawable.talk, R.drawable.note};
    private String[] titles = {"梦话社区","梦话日记"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false otherwise.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void init() {
        data = getData();
        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(), //当前activity
                data,//适配器需要解析的数据
                R.layout.lv_style_more,//样式文件
                new String[]{"image", "title"},//data中Map的文字
                new int[]{R.id.iv_row, R.id.tv_row});//样式文件id
        lvMore.setAdapter(adapter);
        lvMore.setOnItemClickListener(this);
    }


    private List<Map<String,Object>> getData(){
        data = new ArrayList<Map<String, Object>>();
        for(int i=0;i<icons.length;i++){
            Map<String,Object> hm = new HashMap<String, Object>();
            hm.put("image",icons[i]);//从图标数组提取图片
            hm.put("title",titles[i]);//从标题数组中提取文字
            data.add(hm);//添加进data
        }
        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        switch (position) {//todo
            case 0:
                intent.setClass(getContext(),WebviewActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent.setClass(getContext(),DiaryActivity.class);
                startActivity(intent);
                break;
        }
    }
}
