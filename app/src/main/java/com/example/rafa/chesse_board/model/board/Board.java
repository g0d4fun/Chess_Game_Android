package com.example.rafa.chesse_board.model.board;


import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.board.Move.MoveFactory;
import com.example.rafa.chesse_board.model.pieces.Bishop;
import com.example.rafa.chesse_board.model.pieces.King;
import com.example.rafa.chesse_board.model.pieces.Knight;
import com.example.rafa.chesse_board.model.pieces.Pawn;
import com.example.rafa.chesse_board.model.pieces.Piece;
import com.example.rafa.chesse_board.model.pieces.Queen;
import com.example.rafa.chesse_board.model.pieces.Rook;
import com.example.rafa.chesse_board.model.player.BlackPlayer;
import com.example.rafa.chesse_board.model.player.Player;
import com.example.rafa.chesse_board.model.player.WhitePlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Board implements Serializable {

    private final List<Tile> gameBoard;
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
    private final Move transitionMove;

    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final List<Move> whiteStandardMoves = calculateLegalMoves(this.whitePieces);
        final List<Move> blackStandardMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteStandardMoves, blackStandardMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardMoves, blackStandardMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
        this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = prettyPrint(this.gameBoard.get(i));
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % 8 == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private static String prettyPrint(final Tile tile) {
        if (tile.isTileOccupied()) {
            return tile.getPiece().getPieceAllegiance().isBlack()
                    ? tile.toString().toLowerCase() : tile.toString();
        }
        return tile.toString();
    }

    public List<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public List<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Iterable<Piece> getAllPieces() {
        List<Piece> allPiecesList = new ArrayList<Piece>(this.whitePieces);
        allPiecesList.addAll(this.blackPieces);

        return allPiecesList;
    }

    public Iterable<Move> getAllLegalMoves() {
        List<Move> allMovesList = new ArrayList<Move>(this.whitePlayer.getLegalMoves());
        allMovesList.addAll(this.blackPlayer.getLegalMoves());

        return allMovesList;
    }

    public WhitePlayer whitePlayer() {
        return this.whitePlayer;
    }

    public BlackPlayer blackPlayer() {
        return this.blackPlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Tile getTile(final int coordinate) {
        return this.gameBoard.get(coordinate);
    }

    public List<Tile> getGameBoard() {
        return this.gameBoard;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Move getTransitionMove() {
        return this.transitionMove;
    }

    public static Board createStandardBoard() {
        return STANDARD_BOARD;
    }

    private static Board createStandardBoardImpl() {
        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        // White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        //white to move
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        return builder.build();
    }

    private static List<Tile> createGameBoard(final Builder boardBuilder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, boardBuilder.boardConfig.get(i));
        }
        return Arrays.asList(tiles);
    }

    private List<Move> calculateLegalMoves(final List<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>(35);
        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return legalMoves;
    }

    private static List<Piece> calculateActivePieces(final Builder builder,
            final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>(16);
        for (final Piece piece : builder.boardConfig.values()) {
            if (piece.getPieceAllegiance() == alliance) {
                activePieces.add(piece);
            }
        }
        return activePieces;
    }

    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        public Builder() {
            this.boardConfig = new HashMap<>(33, 1.0f);
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Builder setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

    }

}
