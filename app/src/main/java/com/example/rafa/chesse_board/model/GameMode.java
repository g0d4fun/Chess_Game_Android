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

    public static GameMode builder(String mode){
        mode = mode.trim();

        if(GameMode.SINGLE_PLAYER.toString().equalsIgnoreCase(mode))
            return GameMode.SINGLE_PLAYER;
        else if(GameMode.MULTIPLAYER.toString().equalsIgnoreCase(mode))
            return GameMode.MULTIPLAYER;
        else if(GameMode.ONLINE.toString().equalsIgnoreCase(mode))
            return GameMode.ONLINE;
        return null;
    }
}
