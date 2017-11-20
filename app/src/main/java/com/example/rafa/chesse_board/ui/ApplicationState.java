package com.example.rafa.chesse_board.ui;

import android.app.Application;

/**
 * Created by henri on 11/19/2017.
 */

public class ApplicationState extends Application {

    protected static ApplicationState obj;

    @Override
    public void onCreate() {
        super.onCreate();
        //read();
    }

    public static void saveSerializableObject(){
        //FileOutputStream fos = obj.openFileOutput("")
    }
}
