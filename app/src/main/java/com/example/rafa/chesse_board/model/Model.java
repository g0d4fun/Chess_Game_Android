/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rafa.chesse_board.model;

import android.util.Log;

import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.MoveStatus;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;
import com.example.rafa.chesse_board.ui.MainActivity;

import java.util.Collection;

/**
 * @author henri
 */
public class Model {

    private InnerModel model;
    private OnlineCommunication communication;

    public Model() {
        model = new InnerModel();
    }

    // Iteration Methods----------------------------------------------------------------------------
    public Board startNewGame(GameMode gameMode, String opponentName, String username) {
        return model.startNewGame(gameMode,opponentName, username);
    }

    public Board startNewGame(GameMode gameMode, String username) {
        return model.startNewGame(gameMode, username);
    }

    public boolean changeGameModeMultiToSingle(){
        return model.changeGameModeMultiToSingle();
    };

    /**
     * @param sourceTile
     * @param destinationTile
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(Tile sourceTile, Tile destinationTile) {
        return model.makeMove(sourceTile,destinationTile);
    }

    /**
     * @param sourceTileId
     * @param destinationTileId
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(int sourceTileId, int destinationTileId) {
        return model.makeMove(sourceTileId, destinationTileId);
    }

    public Collection<Move> pieceLegalMoves(Piece pieceToMove) {
        return model.pieceLegalMoves(pieceToMove);
    }

    public void setOpponentName(String name){
        model.setOpponentName(name);
    }

    // Retrieve Data
    public String getUsername(){return model.getUsername();}

    public Board getBoard() {
        return model.getBoard();
    }

    public Tile getTile(int tileId) {
        return model.getTile(tileId);
    }

    public Alliance getCurrentPlayerAlliance() {
        return model.getCurrentPlayerAlliance();
    }

    public Alliance getAlliancePieceByTileId(int tileId) {
        return model.getAlliancePieceByTileId(tileId);
    }

    public MoveLog getMoveLog() {
        return model.getMoveLog();
    }

    public Alliance getCurrentPlayer() {
        return model.getCurrentPlayer();
    }

    public GameMode getGameMode() {
        return model.getGameMode();
    }

    public String getOpponentName() {
        return model.getOpponentName();
    }

    public int getMillisecondsToFinish() {
        return model.getMillisecondsToFinish();
    }

    public void setMillisecondsToFinish(int millisecondsToFinish) {
        model.setMillisecondsToFinish(millisecondsToFinish);
    }

    public int getMillisecondsToFinishOpponent() {
        return model.getMillisecondsToFinishOpponent();
    }

    public void setMillisecondsToFinishOpponent(int millisecondsToFinishOpponent) {
        model.setMillisecondsToFinishOpponent(millisecondsToFinishOpponent);
    }

    public boolean isWhiteCountDownTimer() {
        return model.isWhiteCountDownTimer();
    }

    public void setWhiteCountDownTimer(boolean whiteCountDownTimer) {
        model.setWhiteCountDownTimer(whiteCountDownTimer);
    }

    // Communication for online game

    public void setUpCommunication(GameOnlineMode mode, MainActivity mainActivity){
        communication = new OnlineCommunication(mode,mainActivity, this);
    }

    public OnlineCommunication getComm() {
        if(communication == null)
            throw new RuntimeException("Should call setUpCommunication First");
        return communication;
    }

    public void onPauseCommunication(){
        communication.onPause();
    }

    public void onResumeCommunication(){
        communication.onResume();
    }

    public boolean commSettedUp(){
        if(communication == null)
            return false;
        return true;
    }

    public void makeMoveClient(int sourceTile,int destinationTile){
        communication.makeMove(sourceTile,destinationTile);
    }

    public boolean amILight(){
        // Server is white
        if(communication.isServer()) {
            return true;
        }
        return false;
    }

    public boolean amIDark(){
        if(communication.isServer()){
            return false;
        }
        return true;
    }
}
