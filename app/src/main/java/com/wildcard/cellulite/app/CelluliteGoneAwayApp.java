package com.wildcard.cellulite.app;

import android.app.Application;
import android.content.ContextWrapper;

import com.google.android.gms.ads.MobileAds;
import com.pixplicity.easyprefs.library.Prefs;
import com.wildcard.cellulite.R;

/**
 * Created by mno on 4/10/18.
 */

public class CelluliteGoneAwayApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
