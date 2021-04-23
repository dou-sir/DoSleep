package com.jit.dyy.dosleep;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jit.dyy.dosleep.fragment.HomeFragment;
import com.jit.dyy.dosleep.fragment.MoreFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.ivMenu)
    RoundedImageView ivMenu;
    @BindView(R.id.tvBarTitle)
    TextView tvBarTitle;
    @BindView(R.id.flMain)
    FrameLayout flMain;
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
    @BindView(R.id.fbSleep)
    FloatingActionButton fbSleep;
    @BindView(R.id.ibRefresh)
    ImageButton ibRefresh;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        HomeFragment homeFragment = new HomeFragment();
//        fmTransaction(homeFragment);
    }

    @OnClick({R.id.ivMenu, R.id.llHome, R.id.llMore, R.id.fbSleep, R.id.ibRefresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMenu:
                dlMenu.openDrawer(GravityCompat.START);
                break;
            case R.id.llHome:
                HomeFragment homeFragment = new HomeFragment();
                fmTransaction(homeFragment);
                ivHome.setImageResource(R.drawable.moon1);
                tvHome.setTextAppearance(getApplicationContext(), R.style.textSelected);
                ivMore.setImageResource(R.drawable.more0);
                tvMore.setTextAppearance(getApplicationContext(), R.style.textUnselected);
//                fbSleep.setVisibility(View.VISIBLE);
                ibRefresh.setVisibility(View.VISIBLE);
                break;
            case R.id.llMore:
                MoreFragment moreFragment = new MoreFragment();
                fmTransaction(moreFragment);
                ivMore.setImageResource(R.drawable.more1);
                tvMore.setTextAppearance(getApplicationContext(), R.style.textSelected);
                ivHome.setImageResource(R.drawable.moon0);
//                fbSleep.setVisibility(View.GONE);
                ibRefresh.setVisibility(View.GONE);
                tvHome.setTextAppearance(getApplicationContext(), R.style.textUnselected);
                break;
            case R.id.fbSleep:
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ibRefresh:
                Animation mAnimationRight = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
                ibRefresh.startAnimation(mAnimationRight);
                HomeFragment homeFragment2 = new HomeFragment();
                fmTransaction(homeFragment2);
                break;
        }

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
        if (resultCode == 22) {
            MoreFragment moreFragment = new MoreFragment();
            fmTransaction(moreFragment);
        }
    }

    private void init() {
        navView.setItemIconTintList( null );
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        HomeFragment homeFragment = new HomeFragment();
                        fmTransaction(homeFragment);
                        ibRefresh.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_community:
                        Intent intent = new Intent(MainActivity.this,WebviewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_diary:
                        startActivity(new Intent(MainActivity.this,DiaryActivity.class));
                        break;
                    case R.id.nav_more:
                        MoreFragment moreFragment = new MoreFragment();
                        fmTransaction(moreFragment);
                        ibRefresh.setVisibility(View.GONE);
                        break;
                }
                dlMenu.closeDrawers();
                return true;
            }
        });

        fragmentManager = getSupportFragmentManager();
        //初始化fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        //todo 传值时做
        fragmentTransaction.add(R.id.flMain, homeFragment);
        fragmentTransaction.commit();
    }

    private void fmTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMain, fragment);
        fragmentTransaction.commit();
    }
}