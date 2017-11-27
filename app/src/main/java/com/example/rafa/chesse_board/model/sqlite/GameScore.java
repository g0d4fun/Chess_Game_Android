package com.example.rafa.chesse_board.model.sqlite;

import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.model.GameResult;

/**
 * Created by henri on 11/26/2017.
 */

public class GameScore {
    private int id;
    private GameMode mode;
    private String opponentNickName;
    private GameResult result;

    public GameScore(int id, GameMode mode, GameResult result, String opponentNickName) {
        this.id = id;
        this.mode = mode;
        this.opponentNickName = opponentNickName;
        this.result = result;
    }

    public GameScore(GameMode mode, GameResult result, String opponentNickName) {
        this.mode = mode;
        this.opponentNickName = opponentNickName;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameMode getMode() {
        return mode;
    }

    public String getOpponentNickName() {
        return opponentNickName;
    }

    public GameResult getResult() {
        return result;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public void setOpponentNickName(String opponentNickName) {
        this.opponentNickName = opponentNickName;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
}
