package com.jit.dyy.dosleep;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jit.dyy.dosleep.bean.MyappInfo;
import com.jit.dyy.dosleep.bean.User;
import com.jit.dyy.dosleep.fragment.HomeFragment;
import com.jit.dyy.dosleep.fragment.MoreFragment;
import com.jit.dyy.dosleep.service.UserService;
import com.jit.dyy.dosleep.util.DataBaseHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.ui.companent.CircleImageView;

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

    public static MyappInfo myappInfo = new MyappInfo();

    private UserService userService;
    private RoundedImageView iconHead;
    private TextView tvUsername,tvUsersay;
    private ImageButton ibLogout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myappInfo = (MyappInfo) getApplication();
        userService = new UserService(this);
        userService.setMUser();
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        if (MainActivity.myappInfo.getLoginFlag()){
//            userService.loginByTel(MainActivity.myappInfo.getMUser().getUserTel());
//            MainActivity.myappInfo.setLoginFlag(false);
//        }
        setHeadInfo();
    }

    @OnClick({R.id.ivMenu, R.id.llHome, R.id.llMore, R.id.fbSleep, R.id.ibRefresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMenu:
                System.out.println("aaa"+myappInfo.getMUser().toString());
                setHeadInfo();
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
        //nav_head
        View nav_head = navView.getHeaderView(0);
        iconHead = nav_head.findViewById(R.id.icon_head);
        tvUsername = nav_head.findViewById(R.id.tv_username);
        tvUsersay = nav_head.findViewById(R.id.tv_usersay);
        ibLogout = nav_head.findViewById(R.id.ib_logout);

        iconHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"aaa",Toast.LENGTH_SHORT).show();
                if (myappInfo.getMUser().getState() == 0)
                    startActivity(new Intent(MainActivity.this, LoginByTelActivity.class));
                else
                    startActivity(new Intent(MainActivity.this, EditInfoActivity.class));
            }
        });
        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"aaa",Toast.LENGTH_SHORT).show();
                if (myappInfo.getMUser().getState() == 0)
                    startActivity(new Intent(MainActivity.this, LoginByTelActivity.class));
//                    System.out.println("aaaa"+userService.loginByTel("18252626192")+"***");
                else
                    setLogoutInfo();
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

    private void setHeadInfo() {
        if(myappInfo.getMUser().getState() != 0){
            User mUser = myappInfo.getMUser();
            if (mUser.getUserName().length()>11){
                tvUsername.setText("Hi,"+mUser.getUserName().substring(0,12)+"...");
            }else {
                tvUsername.setText("Hi,"+mUser.getUserName());
            }
            if (mUser.getSlogan() != "null")
                tvUsersay.setText(mUser.getSlogan());
            ibLogout.setBackground(getResources().getDrawable(R.drawable.logout));
        }
    }

    private void setLogoutInfo() {
        myappInfo.getMUser().setState(0);
        tvUsername.setText("Hi,请登录...");
        tvUsersay.setText("Have a good Sleep!");
        ibLogout.setBackground(getResources().getDrawable(R.drawable.login));

        userService.logoutUser();
    }


    private void fmTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMain, fragment);
        fragmentTransaction.commit();
    }
}