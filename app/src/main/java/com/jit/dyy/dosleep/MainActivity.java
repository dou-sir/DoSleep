package com.jit.dyy.dosleep;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.ivMenu)
    RoundedImageView ivMenu;
    @BindView(R.id.tvBarTitle)
    TextView tvBarTitle;
    @BindView(R.id.llMain)
    LinearLayout llMain;
    @BindView(R.id.navView)
    NavigationView navView;
    @BindView(R.id.dlMenu)
    DrawerLayout dlMenu;
    @BindView(R.id.ivHome)
    ImageView ivHome;
    @BindView(R.id.tvHome)
    TextView tvHome;
    @BindView(R.id.llHome)
    LinearLayout llHome;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    @BindView(R.id.tvMore)
    TextView tvMore;
    @BindView(R.id.llMore)
    LinearLayout llMore;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.ivMenu, R.id.llHome, R.id.llMore})
    public void onClick(View view) {
        if (view.getId() == R.id.ivMenu) {
            dlMenu.openDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.llHome) {
            HomeFragment homeFragment = new HomeFragment();
            fmTransaction(homeFragment);
            ivHome.setImageResource(R.drawable.moon1);
            tvHome.setTextAppearance(getApplicationContext(),R.style.textSelected);
            ivMore.setImageResource(R.drawable.more0);
            tvMore.setTextAppearance(getApplicationContext(),R.style.textUnselected);
        }
        if (view.getId() == R.id.llMore) {
            MoreFragment moreFragment = new MoreFragment();
            fmTransaction(moreFragment);
            ivMore.setImageResource(R.drawable.more1);
            tvMore.setTextAppearance(getApplicationContext(),R.style.textSelected);
            ivHome.setImageResource(R.drawable.moon0);
            tvHome.setTextAppearance(getApplicationContext(),R.style.textUnselected);
        }

    }

    private void init(){
        fragmentManager = getSupportFragmentManager();
        //初始化fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        //todo 传值时做
        fragmentTransaction.add(R.id.llMain,homeFragment);
        fragmentTransaction.commit();
    }

    private void fmTransaction(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.llMain,fragment);
        fragmentTransaction.commit();
    }
}
