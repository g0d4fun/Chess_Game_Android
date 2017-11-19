package com.example.rafa.chesse_board;

import android.app.Application;
import android.widget.ImageButton;

import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by henri on 11/19/2017.
 */

public class MainActivityState extends Application {

    private class Wrapper {
        public ImageButton[] tiles;
        public Model model;
        public Tile sourceTile, destinationTile;
        public Piece pieceToBeMoved;
    }
    private Wrapper wrapper;
    private static MainActivityState obj;

    @Override
    public void onCreate() {
        super.onCreate();
        readSerializedState();
    }

    public MainActivityState() {
        wrapper = new Wrapper();
        obj = this;
    }

    public static MainActivityState getInstance(){
        return getInstance();
    }

    public static void setMainActivityState(ImageButton[] tiles, Model model, Tile sourceTile, Tile destinationTile, Piece pieceToBeMoved) {

        obj.wrapper.tiles = tiles;
        obj.wrapper.model = model;
        obj.wrapper.sourceTile = sourceTile;
        obj.wrapper.destinationTile = destinationTile;
        obj.wrapper.pieceToBeMoved = pieceToBeMoved;
        writeSerializedState();
    }

    public static ImageButton[] getTiles() {
        return obj.wrapper.tiles;
    }

    public static Model getModel() {
        return obj.wrapper.model;
    }

    public static Tile getSourceTile() {
        return obj.wrapper.sourceTile;
    }

    public static Tile getDestinationTile() {
        return obj.wrapper.destinationTile;
    }

    public static Piece getPieceToBeMoved() {
        return obj.wrapper.pieceToBeMoved;
    }

    private static void writeSerializedState() {
        try {
            FileOutputStream fos = obj.openFileOutput("MainActivityState.dat", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj.wrapper);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readSerializedState() {
        obj.wrapper = null;
        try {
            FileInputStream fis = obj.openFileInput("MainActivityState.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj.wrapper = (Wrapper) ois.readObject();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
