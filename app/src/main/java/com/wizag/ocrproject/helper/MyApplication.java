package com.wizag.ocrproject.helper;

import android.app.Application;

import com.microblink.MicroblinkSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
      //  MicroblinkSDK.setLicenseFile();
        MicroblinkSDK.setLicenseFile("MB_com.wizag.ocrproject_BlinkID_Android_2018-11-15.mblic", this);
    }
}