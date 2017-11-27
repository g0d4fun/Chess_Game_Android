package com.example.rafa.chesse_board.model.sqlite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henri on 11/26/2017.
 */

public class Profile {

    private int id;
    private String nickName;
    private List<GameScore> gameScores;
    private String imagePath;

    public Profile(String nickName, List<GameScore> gameScores, String imagePath) {
        this.id = 1;
        this.nickName = nickName;
        this.gameScores = gameScores;
        this.imagePath = imagePath;
    }

    public Profile(int id, String nickName, List<GameScore> gameScores, String imagePath) {
        this.id = id;
        this.nickName = nickName;
        this.gameScores = gameScores;
        this.imagePath = imagePath;
    }

    public Profile(String nickName, String imagePath) {
        this.id = 1;
        this.nickName = nickName;
        this.gameScores = new ArrayList<>();
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<GameScore> getGameScores() {
        return gameScores;
    }

    public void addGameScore(GameScore gameScore){
        gameScores.add(gameScore);
    }

    public void setGameScores(List<GameScore> gameScores) {
        this.gameScores = gameScores;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
