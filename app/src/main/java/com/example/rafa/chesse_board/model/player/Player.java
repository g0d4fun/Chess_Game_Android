package com.example.rafa.chesse_board.model.player;

import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.MoveStatus;
import com.example.rafa.chesse_board.model.board.MoveTransition;
import com.example.rafa.chesse_board.model.pieces.King;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final List<Move> legalMoves;
    protected final boolean isInCheck;

    Player(Board board, List<Move> playerLegals, List<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
        
        //playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
        this.legalMoves = playerLegals;
    }

    private boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! " + this.getAlliance() + " king could not be established!");
    }

    private boolean hasEscapeMoves() {
        for (final Move move : getLegalMoves()) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    static Collection<Move> calculateAttacksOnTile(final int tile,
            final Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (tile == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
                transitionedBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionedBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }

    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
            Collection<Move> opponentLegals);

}
