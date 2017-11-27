package com.example.rafa.chesse_board.model;

/**
 * Created by henri on 11/26/2017.
 */

public enum GameResult {
    WIN {
        @Override
        public String toString() {
            return "Win";
        }
    },
    LOSE {
        @Override
        public String toString() {
            return "Lose";
        }
    },
    DRAW {
        @Override
        public String toString() {
            return "Draw";
        }
    };

    @Override
    public abstract String toString();

    public static GameResult builder(String result){
        result = result.trim();

        if(GameResult.WIN.toString().equalsIgnoreCase(result))
            return GameResult.WIN;
        else if(GameResult.LOSE.toString().equalsIgnoreCase(result))
            return GameResult.LOSE;
        else if(GameResult.DRAW.toString().equalsIgnoreCase(result))
            return GameResult.DRAW;
        return null;
    }
}
