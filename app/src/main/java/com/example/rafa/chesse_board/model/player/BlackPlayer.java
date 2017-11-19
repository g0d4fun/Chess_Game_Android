package com.example.rafa.chesse_board.model.player;

import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.BoardUtils;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.KingSideCastleMove;
import com.example.rafa.chesse_board.model.board.Move.QueenSideCastleMove;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;
import com.example.rafa.chesse_board.model.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public final class BlackPlayer extends Player {

    public BlackPlayer(final Board board,
                       final List<Move> whiteStandardLegals,
                       final List<Move> blackStandardLegals) {
        super(board, blackStandardLegals, whiteStandardLegals);
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {
        
        if (this.isInCheck() || this.isCastled() || !(this.isKingSideCastleCapable() || this.isQueenSideCastleCapable())) {
            return null;
        }

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck) {
            //blacks king side castle
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
                        kingCastles.add(
                                new KingSideCastleMove(this.board, this.playerKing, 6, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));

                    }
                }
            }
            //blacks queen side castle
            if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                        Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
                        kingCastles.add(
                                new QueenSideCastleMove(this.board, this.playerKing, 2, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                    }
                }
            }
        }
        return kingCastles;
    }

    @Override
    public WhitePlayer getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public String toString() {
        return Alliance.BLACK.toString();
    }

}
