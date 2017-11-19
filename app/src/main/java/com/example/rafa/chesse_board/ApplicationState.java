package com.example.rafa.chesse_board;

import android.app.Application;
import android.widget.ImageButton;

import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.io.FileOutputStream;

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
