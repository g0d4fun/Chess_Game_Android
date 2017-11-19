/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rafa.chesse_board.model;

import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.MoveStatus;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.util.Collection;

/**
 *
 * @author henri
 */
public class Model {

    protected ModelChess modelChess;

    public Model() {
        modelChess = new ModelChess();
    }

    // Interation Methods
    public Board startNewGame() {
        return modelChess.startNewGame();
    }

    /**
     *
     * @param sourceTile
     * @param destinationTile
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(Tile sourceTile, Tile destinationTile) {
        return modelChess.makeMove(sourceTile, destinationTile);
    }

    /**
     *
     * @param sourceTileId
     * @param destinationTileId
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(int sourceTileId, int destinationTileId) {
        return modelChess.makeMove(sourceTileId, destinationTileId);
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

    public Alliance getCurrentPlayer(){
        return modelChess.getCurrentPlayer();
    }

}
