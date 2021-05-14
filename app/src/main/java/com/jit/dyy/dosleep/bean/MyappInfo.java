package com.jit.dyy.dosleep.bean;


import android.app.Application;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MyappInfo extends Application{

    private static final User MUSER  = new User(0,"-1","-1",0);

    private User mUser;

    private Boolean loginFlag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mUser = MUSER;
    }
}
