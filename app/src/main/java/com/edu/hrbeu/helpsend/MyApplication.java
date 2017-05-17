package com.edu.hrbeu.helpsend;


import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.edu.hrbeu.helpsend.utils.DebugUtil;
import com.edu.hrbeu.rx.HttpUtils;

public class MyApplication extends Application {

    private static MyApplication myApplication;

    public static MyApplication getInstance() {
        return myApplication;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        HttpUtils.getInstance().init(this, DebugUtil.DEBUG);

        initTextSize();
    }

    /**
     * 使其系统更改字体大小无效
     */
    private void initTextSize() {
        Resources res = getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

}
