package com.example.rafa.chesse_board.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageButton;

import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by henri on 11/19/2017.
 */

public class ApplicationState extends Application {

    protected static ApplicationState obj;
    protected Wrapper wrapper;

    private static class Wrapper{
        public ImageButton[] tiles;
        public Model model;
        public Tile sourceTile, destinationTile;
        public Piece pieceToBeMoved;
    }

    public ApplicationState(){
        obj = this;
        obj.wrapper = new Wrapper();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        read();
    }

    public void save(ImageButton[] tiles, Model model, Tile sourceTile, Tile destinationTile, Piece pieceToBeMoved){
        obj.wrapper.sourceTile = sourceTile;
        obj.wrapper.destinationTile = destinationTile;
        obj.wrapper.model = model;
        obj.wrapper.pieceToBeMoved = pieceToBeMoved;
        obj.wrapper.tiles = tiles;
        write();
    }

    public static void write(){
        try {
            FileOutputStream fos = obj.openFileOutput("chess_game.dat",MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setAllNull(){
        obj.wrapper = null;
    }

    public static void read(){
        setAllNull();
        try{
            FileInputStream fis = obj.openFileInput("chess_game.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj.wrapper = (Wrapper) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(obj.wrapper == null)
            obj.wrapper = new Wrapper();
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

}


