package com.example.rafa.chesse_board;

import android.app.Application;
import android.widget.ImageButton;

import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

/**
 * Created by henri on 11/19/2017.
 */

public class ApplicationState extends Application {

    private static ApplicationState instance;

    public ImageButton[] tiles;
    private Model model;
    private Tile sourceTile, destinationTile;
    private Piece pieceToBeMoved;

    @Override
    public void onCreate() {
        super.onCreate();
        //read();
    }

    public ApplicationState() {
        instance = this;
    }

    public static void setMainActivityState(ImageButton[] tiles, Model model, Tile sourceTile, Tile destinationTile, Piece pieceToBeMoved) {
        instance.tiles = tiles;
        instance.model = model;
        instance.sourceTile = sourceTile;
        instance.destinationTile = destinationTile;
        instance.pieceToBeMoved = pieceToBeMoved;
    }

    public static ImageButton[] getTiles() {
        return instance.tiles;
    }

    public static Model getModel() {
        return instance.model;
    }

    public static Tile getSourceTile() {
        return instance.sourceTile;
    }

    public static Tile getDestinationTile() {
        return instance.destinationTile;
    }

    public static Piece getPieceToBeMoved() {
        return instance.pieceToBeMoved;
    }


}
