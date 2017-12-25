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
import com.example.rafa.chesse_board.ui.BoardDirection;
import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author henri
 */
public class InnerModel {

    protected ModelChess modelChess;
    protected GameMode gameMode;
    protected Alliance myAllianceOnline;
    protected String opponentName;
    protected int millisecondsToFinish;
    protected int millisecondsToFinishOpponent;

    protected boolean isWhiteCountDownTimer;

    public InnerModel() {
        modelChess = new ModelChess();
        this.millisecondsToFinish = 0;
        this.millisecondsToFinishOpponent = 0;
        this.isWhiteCountDownTimer = true;
    }

    // Iteration Methods----------------------------------------------------------------------------
    public Board startNewGame(GameMode gameMode, String opponentName) {
        this.opponentName = opponentName;
        return startNewGame(gameMode);
    }

    public Board startNewGame(GameMode gameMode) {
        switch (gameMode) {
            case SINGLE_PLAYER:
                this.gameMode = GameMode.SINGLE_PLAYER;
                break;
            case MULTIPLAYER:
                this.gameMode = GameMode.MULTIPLAYER;
                break;
            case ONLINE:
                this.gameMode = GameMode.ONLINE;
                break;
        }
        return modelChess.startNewGame();
    }

    /**
     * @param sourceTile
     * @param destinationTile
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(Tile sourceTile, Tile destinationTile) {
        MoveStatus moveStatus;
        switch (gameMode) {
            case SINGLE_PLAYER:
                if (getCurrentPlayer().isWhite()) {
                    moveStatus = modelChess.makeMove(sourceTile, destinationTile);
                    if(moveStatus.equals(MoveStatus.DONE))
                        makeBotMove();
                    return moveStatus;
                }
                else {
                    //TODO: Bot Turn
                }
                break;
            case MULTIPLAYER:
                return modelChess.makeMove(sourceTile, destinationTile);
            case ONLINE:
                if(myAllianceOnline.equals(getCurrentPlayerAlliance())){
                    return modelChess.makeMove(sourceTile,destinationTile);
                }
                else{
                    //TODO: Opponent Turn
                }
                break;
        }
        return MoveStatus.ILLEGAL_MOVE;
    }

    private MoveStatus makeBotMove(){
        if(!gameMode.equals(GameMode.SINGLE_PLAYER))
            return MoveStatus.ILLEGAL_MOVE;

        MoveStatus moveStatus = MoveStatus.ILLEGAL_MOVE;
        Tile sourceTile, destinationTile;
        do{
            List<Piece> blackPieces = getBoard().getBlackPieces();
            int random = (int)(Math.random() * blackPieces.size());
            Piece piece = blackPieces.get(random);
            sourceTile = getBoard().getTile(piece.getPiecePosition());

            List<Move> moves = new ArrayList<>(piece.calculateLegalMoves(getBoard()));
            if(moves.isEmpty())
                continue;
            int random2 = (int)(Math.random() * moves.size());
            destinationTile = getBoard().getTile(moves.get(random2).getDestinationCoordinate());
            moveStatus = modelChess.makeMove(sourceTile,destinationTile);
        }while(!moveStatus.equals(MoveStatus.DONE));

        return moveStatus;
    }

    /**
     * @param sourceTileId
     * @param destinationTileId
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(int sourceTileId, int destinationTileId) {
        if (sourceTileId >= 64 || destinationTileId >= 64
                || sourceTileId < 0 || destinationTileId < 0) {
            return Move.MoveStatus.ILLEGAL_MOVE;
        }
        return makeMove(getBoard().getTile(sourceTileId), getBoard().getTile(destinationTileId));
    }

    public Collection<Move> pieceLegalMoves(Piece pieceToMove) {
        return modelChess.pieceLegalMoves(pieceToMove);
    }

    // Retrieve Data
    public Board getBoard() {
        return modelChess.getBoard();
    }

    public Tile getTile(int tileId) {
        return modelChess.getTile(tileId);
    }

    public Alliance getCurrentPlayerAlliance() {
        return modelChess.getCurrentPlayerAlliance();
    }

    public Alliance getAlliancePieceByTileId(int tileId) {
        return modelChess.getAlliancePieceByTileId(tileId);
    }

    public MoveLog getMoveLog() {
        return modelChess.getMoveLog();
    }

    public Alliance getCurrentPlayer() {
        return modelChess.getCurrentPlayer();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public int getMillisecondsToFinish() {
        return millisecondsToFinish;
    }

    public void setMillisecondsToFinish(int millisecondsToFinish) {
        this.millisecondsToFinish = millisecondsToFinish;
    }

    public int getMillisecondsToFinishOpponent() {
        return millisecondsToFinishOpponent;
    }

    public void setMillisecondsToFinishOpponent(int millisecondsToFinishOpponent) {
        this.millisecondsToFinishOpponent = millisecondsToFinishOpponent;
    }

    public boolean isWhiteCountDownTimer() {
        return isWhiteCountDownTimer;
    }

    public void setWhiteCountDownTimer(boolean whiteCountDownTimer) {
        isWhiteCountDownTimer = whiteCountDownTimer;
    }
}
