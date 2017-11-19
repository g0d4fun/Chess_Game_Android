package com.example.rafa.chesse_board.model.pieces;

import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.board.Board;
import com.example.rafa.chesse_board.model.board.BoardUtils;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Move.MajorAttackMove;
import com.example.rafa.chesse_board.model.board.Move.MajorMove;
import com.example.rafa.chesse_board.model.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class Bishop extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -7, 7, 9};

    public Bishop(final Alliance alliance,
                  final int piecePosition) {
         super(PieceType.BISHOP, alliance, piecePosition, true);
    }

    public Bishop(final Alliance alliance,
                  final int piecePosition,
                   final boolean isFirstMove) {
        super(PieceType.BISHOP, alliance, piecePosition, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate) ||
                    isEighthColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
                    break;
                }
                candidateDestinationCoordinate += currentCandidateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                    else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAllegiance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.bishopBonus(this.piecePosition);
    }

    @Override
    public Bishop movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedBishop(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentCandidate,
                                                  final int candidateDestinationCoordinate) {
        return (BoardUtils.INSTANCE.FIRST_COLUMN.get(candidateDestinationCoordinate) &&
                ((currentCandidate == -9) || (currentCandidate == 7)));
    }

    private static boolean isEighthColumnExclusion(final int currentCandidate,
                                                   final int candidateDestinationCoordinate) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidateDestinationCoordinate) &&
                        ((currentCandidate == -7) || (currentCandidate == 9));
    }

}