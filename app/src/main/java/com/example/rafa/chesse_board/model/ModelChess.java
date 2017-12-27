/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.rafa.chesse_board.model;

import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.MoveFactory;
import com.example.rafa.chesse_board.model.board.Move.MoveStatus;
import com.example.rafa.chesse_board.model.board.MoveTransition;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;


/**
 *
 * @author rafa
 */
public class ModelChess implements Serializable {

    protected Board chessBoard;
    protected MoveTransition transition;
    protected MoveLog moveLog;

    public ModelChess() {
        moveLog = new MoveLog();
    }

    // Interation Methods
    public Board startNewGame() {
        chessBoard = Board.createStandardBoard();
        return chessBoard;
    }

    /**
     *
     * @param sourceTile
     * @param destinationTile
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public MoveStatus makeMove(Tile sourceTile, Tile destinationTile) {
        Move move = MoveFactory.createMove(chessBoard,
                sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());

        MoveTransition transition = chessBoard.currentPlayer().makeMove(move);

        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getToBoard();
            // if move was valid, add to MoveLog
            moveLog.addMoves(move);
        }

        return transition.getMoveStatus();
    }

    /**
     *
     * @param sourceTileId
     * @param destinationTileId
     * @return DONE, ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK
     */
    public Move.MoveStatus makeMove(int sourceTileId, int destinationTileId) {
        if (sourceTileId >= 64 || destinationTileId >= 64
                || sourceTileId < 0 || destinationTileId < 0) {
            return Move.MoveStatus.ILLEGAL_MOVE;
        }

        return makeMove(getTile(sourceTileId), getTile(destinationTileId));
    }

    public Collection<Move> pieceLegalMoves(Piece pieceToMove) {

        if (pieceToMove != null && pieceToMove.getPieceAllegiance()
                == chessBoard.currentPlayer().getAlliance()) {
            return pieceToMove.calculateLegalMoves(chessBoard);

        }
        return Collections.emptyList();
    }

    public boolean isGameDraw(){
        return false;
    }

    // Retrieve Data
    public Board getBoard() {
        return chessBoard;
    }

    public Tile getTile(int tileId) {
        return chessBoard.getTile(tileId);
    }

    public Alliance getCurrentPlayerAlliance() {
        return chessBoard.currentPlayer().getAlliance();
    }

    public Alliance getAlliancePieceByTileId(int tileId) {
        return chessBoard.getTile(tileId).getPiece().getPieceAllegiance();
    }

    public MoveLog getMoveLog() {
        return moveLog;
    }

    public Alliance getCurrentPlayer() {
        return chessBoard.currentPlayer().getAlliance();
    }
}
