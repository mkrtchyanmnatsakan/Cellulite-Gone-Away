package com.wildcard.cellulite.app;

import android.app.Application;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by mno on 4/10/18.
 */

public class CelluliteGoneAwayApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
