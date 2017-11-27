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
}
