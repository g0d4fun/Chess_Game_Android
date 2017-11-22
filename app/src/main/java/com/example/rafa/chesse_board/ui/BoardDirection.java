package com.example.rafa.chesse_board.ui;

import com.example.rafa.chesse_board.model.board.Tile;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by henri on 11/22/2017.
 */
public enum BoardDirection {

    NORMAL{
        @Override
        public List<Tile> traverse(List<Tile> boardTiles) {
            return boardTiles;
        }

        @Override
        public BoardDirection opposite() {
            return FLIPPED;
        }

    },
    FLIPPED{
        @Override
        public List<Tile> traverse(List<Tile> boardTiles) {
            return Lists.reverse(boardTiles);
        }

        @Override
        public BoardDirection opposite() {
            return NORMAL;
        }

    };

    public abstract List<Tile> traverse(List<Tile> boardTiles);
    public abstract BoardDirection opposite();
}
