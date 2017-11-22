package com.example.rafa.chesse_board.model;

/**
 * Created by henri on 11/21/2017.
 */

public enum GameMode {
    SINGLE_PLAYER {
        @Override
        public String toString() {
            return "Single Player";
        }
    },
    MULTIPLAYER {
        @Override
        public String toString() {
            return "Multiplayer";
        }
    },
    ONLINE {
        @Override
        public String toString() {
            return "Online";
        }
    };

    @Override
    public abstract String toString();
}
