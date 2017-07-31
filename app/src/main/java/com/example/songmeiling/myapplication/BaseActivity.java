package com.example.songmeiling.myapplication;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by songmeiling on 2015/12/31.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
