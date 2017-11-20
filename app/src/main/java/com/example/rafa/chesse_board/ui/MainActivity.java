package com.example.rafa.chesse_board.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.BoardUtils;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements UIConstants {

    private static int hh = 0;
    private ImageButton[] tiles;
    private Model model;
    private Tile sourceTile, destinationTile;
    private Piece pieceToBeMoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout chessBoardLayout = findViewById(R.id.chess_board);
        int boardWidth = chessBoardLayout.getWidth();
        chessBoardLayout.setMinimumHeight(boardWidth);

        if (savedInstanceState != null && savedInstanceState.getBoolean("Save"))
            savedInstanceState(savedInstanceState);
        else {
            model = new Model();
            model.startNewGame();
            tiles = setUpButtons();
        }
        setUpListeners();
        renderGame();
    }
    //chessBoardLayout


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //((MainActivityState) getApplication()).
        //MainActivityState.getInstance().
        //        setMainActivityState(tiles, model, sourceTile, destinationTile, pieceToBeMoved);
        //outState.putBoolean("Save", true);
    }

    protected void savedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("Save")) {
            //From Application
            MainActivityState state = MainActivityState.getInstance();
            model = state.getModel();
            tiles = state.getTiles();
            sourceTile = state.getSourceTile();
            destinationTile = state.getDestinationTile();
            pieceToBeMoved = state.getPieceToBeMoved();
        } else {
            //From intent
        }
    }

    /**
     * @return ImageButton initialized with id game cells
     * ready for changes
     */
    public ImageButton[] setUpButtons() {

        ImageButton[] tiles = new ImageButton[BoardUtils.NUM_TILES];

        // First Row
        tiles[0] = findViewById(R.id.cell_a8);
        tiles[1] = findViewById(R.id.cell_b8);
        tiles[2] = findViewById(R.id.cell_c8);
        tiles[3] = findViewById(R.id.cell_d8);
        tiles[4] = findViewById(R.id.cell_e8);
        tiles[5] = findViewById(R.id.cell_f8);
        tiles[6] = findViewById(R.id.cell_g8);
        tiles[7] = findViewById(R.id.cell_h8);

        //Second Row
        tiles[8] = findViewById(R.id.cell_a7);
        tiles[9] = findViewById(R.id.cell_b7);
        tiles[10] = findViewById(R.id.cell_c7);
        tiles[11] = findViewById(R.id.cell_d7);
        tiles[12] = findViewById(R.id.cell_e7);
        tiles[13] = findViewById(R.id.cell_f7);
        tiles[14] = findViewById(R.id.cell_g7);
        tiles[15] = findViewById(R.id.cell_h7);

        //Third Row
        tiles[16] = findViewById(R.id.cell_a6);
        tiles[17] = findViewById(R.id.cell_b6);
        tiles[18] = findViewById(R.id.cell_c6);
        tiles[19] = findViewById(R.id.cell_d6);
        tiles[20] = findViewById(R.id.cell_e6);
        tiles[21] = findViewById(R.id.cell_f6);
        tiles[22] = findViewById(R.id.cell_g6);
        tiles[23] = findViewById(R.id.cell_h6);

        //Fourth Row
        tiles[24] = findViewById(R.id.cell_a5);
        tiles[25] = findViewById(R.id.cell_b5);
        tiles[26] = findViewById(R.id.cell_c5);
        tiles[27] = findViewById(R.id.cell_d5);
        tiles[28] = findViewById(R.id.cell_e5);
        tiles[29] = findViewById(R.id.cell_f5);
        tiles[30] = findViewById(R.id.cell_g5);
        tiles[31] = findViewById(R.id.cell_h5);

        //Fifth Row
        tiles[32] = findViewById(R.id.cell_a4);
        tiles[33] = findViewById(R.id.cell_b4);
        tiles[34] = findViewById(R.id.cell_c4);
        tiles[35] = findViewById(R.id.cell_d4);
        tiles[36] = findViewById(R.id.cell_e4);
        tiles[37] = findViewById(R.id.cell_f4);
        tiles[38] = findViewById(R.id.cell_g4);
        tiles[39] = findViewById(R.id.cell_h4);

        //Sixth Row
        tiles[40] = findViewById(R.id.cell_a3);
        tiles[41] = findViewById(R.id.cell_b3);
        tiles[42] = findViewById(R.id.cell_c3);
        tiles[43] = findViewById(R.id.cell_d3);
        tiles[44] = findViewById(R.id.cell_e3);
        tiles[45] = findViewById(R.id.cell_f3);
        tiles[46] = findViewById(R.id.cell_g3);
        tiles[47] = findViewById(R.id.cell_h3);

        //Seventh Row
        tiles[48] = findViewById(R.id.cell_a2);
        tiles[49] = findViewById(R.id.cell_b2);
        tiles[50] = findViewById(R.id.cell_c2);
        tiles[51] = findViewById(R.id.cell_d2);
        tiles[52] = findViewById(R.id.cell_e2);
        tiles[53] = findViewById(R.id.cell_f2);
        tiles[54] = findViewById(R.id.cell_g2);
        tiles[55] = findViewById(R.id.cell_h2);

        //Eighth Row
        tiles[56] = findViewById(R.id.cell_a1);
        tiles[57] = findViewById(R.id.cell_b1);
        tiles[58] = findViewById(R.id.cell_c1);
        tiles[59] = findViewById(R.id.cell_d1);
        tiles[60] = findViewById(R.id.cell_e1);
        tiles[61] = findViewById(R.id.cell_f1);
        tiles[62] = findViewById(R.id.cell_g1);
        tiles[63] = findViewById(R.id.cell_h1);

        return tiles;
    }

    public void setUpListeners() {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sourceTile = null;
                    //destinationTile = null;
                    //humanMovedPiece = null;
                    int tileId = Arrays.asList(tiles).indexOf((ImageButton) v);
                    if (sourceTile == null) {
                        // firstClick
                        sourceTile = model.getTile(tileId);
                        pieceToBeMoved = sourceTile.getPiece();
                        if (pieceToBeMoved == null || pieceToBeMoved.getPieceAllegiance() != model.getCurrentPlayerAlliance()) {
                            sourceTile = null;
                        }
                    } else {
                        // secondClick
                        destinationTile = model.getTile(tileId);
                        Move.MoveStatus moveStatus = model.makeMove(sourceTile, destinationTile);
                        if (!moveStatus.isDone())
                            Toast.makeText(getApplicationContext(), moveStatus.toString(), Toast.LENGTH_SHORT).show();

                        sourceTile = null;
                        destinationTile = null;
                        pieceToBeMoved = null;
                    }
                    renderGame();
                }
            });
        }
    }

    public void renderGame() {
        // Draw Tiles
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            int column = i / 8;
            int row = i - column * 8;

            if ((row % 2) == (column % 2))// Light Color
                tiles[i].setBackgroundColor(getResources().getColor(R.color.LIGHT_TILE_COLOR_HEX));
            else // White Color
                tiles[i].setBackgroundColor(getResources().getColor(R.color.DARK_TILE_COLOR_HEX));

            if (sourceTile == model.getBoard().getTile(i))
                if ((row % 2) == (column % 2))
                    tiles[i].setBackgroundColor(getResources().getColor(R.color.LIGHT_TILE_COLOR_HEX_ON_CLICK));
                else
                    tiles[i].setBackgroundColor(getResources().getColor(R.color.DARK_TILE_COLOR_HEX_ON_CLICK));

            Tile tile = model.getBoard().getTile(i);
            for (Move move : model.pieceLegalMoves(pieceToBeMoved)) {
                if (move.getDestinationCoordinate() == tile.getTileCoordinate()) {
                    if (tile.getPiece() != null)
                        tiles[i].setBackgroundColor(Color.RED);
                }
            }

            tiles[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


        // Draw Pieces
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            Integer drawableValue = getDrawableByPieceName(model.getBoard().getTile(i));
            if (drawableValue != null)
                tiles[i].setImageResource(drawableValue);
            else
                tiles[i].setImageResource(android.R.color.transparent);
        }

        TextView player1 = findViewById(R.id.Player1);
        TextView player2 = findViewById(R.id.Player2);
        if (model.getCurrentPlayer().isWhite()) {
            player1.setTextColor(getResources().getColor(R.color.CURRENT_PLAYER));
            player2.setTextColor(Color.BLACK);
        } else {
            player1.setTextColor(Color.BLACK);
            player2.setTextColor(getResources().getColor(R.color.CURRENT_PLAYER));

        }

        updatePiecesCaptured();
    }

    /**
     * @param tile
     * @return if return is null, there is nothing to draw
     */
    public Integer getDrawableByPieceName(Tile tile) {
        if (tile.getPiece() == null) {
            for (Move move : model.pieceLegalMoves(pieceToBeMoved)) {
                if (move.getDestinationCoordinate() == tile.getTileCoordinate()) {
                    return R.drawable.chess_aim;
                }
            }
            return null;
        } else if (tile.getPiece().getPieceAllegiance() == Alliance.WHITE) {
            switch (tile.getPiece().getPieceType()) {
                case KING:
                    return R.drawable.chess_klt60;
                case QUEEN:
                    return R.drawable.chess_qlt60;
                case ROOK:
                    return R.drawable.chess_rlt60;
                case KNIGHT:
                    return R.drawable.chess_nlt60;
                case BISHOP:
                    return R.drawable.chess_blt60;
                case PAWN:
                    return R.drawable.chess_plt60;
            }
        } else if (tile.getPiece().getPieceAllegiance() == Alliance.BLACK) {
            switch (tile.getPiece().getPieceType()) {
                case KING:
                    return R.drawable.chess_kdt60;
                case QUEEN:
                    return R.drawable.chess_qdt60;
                case ROOK:
                    return R.drawable.chess_rdt60;
                case KNIGHT:
                    return R.drawable.chess_ndt60;
                case BISHOP:
                    return R.drawable.chess_bdt60;
                case PAWN:
                    return R.drawable.chess_pdt60;
            }
        }
        return null;
    }


    public void updatePiecesCaptured(){
        int lightRooks = 2;
        int lightKnights = 2;
        int lightBishops = 2;
        int lightQueen = 1;
        int darkRooks = 2;
        int darkKnights = 2;
        int darkBishops = 2;
        int darkQueen = 1;
        hh++;
        //How many pieces captures
        Iterable<Piece> pieces = model.getBoard().getAllPieces();
        for(Piece piece : pieces){
            Log.i("Raven",piece.getPieceType().toString() + " " + piece.getPieceAllegiance().toString() + "hh:" + hh);
            switch (piece.getPieceType()){
                case ROOK:
                    if(piece.getPieceAllegiance().isBlack())
                        darkRooks--;
                    else
                        lightRooks--;
                    break;
                case KNIGHT:
                    if(piece.getPieceAllegiance().isBlack())
                        darkKnights--;
                    else
                        lightKnights--;
                    break;
                case BISHOP:
                    if(piece.getPieceAllegiance().isBlack())
                        darkBishops--;
                    else
                        lightBishops--;
                    break;
                case QUEEN:
                    if(piece.getPieceAllegiance().isBlack())
                        darkQueen--;
                    else
                        lightQueen--;
                    break;
            }
        }
        // Light Captured
        editTextViewText(R.id.light_queen_captured,String.valueOf(lightQueen));
        editTextViewText(R.id.light_bishops_captured,String.valueOf(lightBishops));
        editTextViewText(R.id.light_knights_captured,String.valueOf(lightKnights));
        editTextViewText(R.id.light_rooks_captured,String.valueOf(lightRooks));
        capturedPiecesOpacity(R.id.light_queen_captured_picture,lightQueen);
        capturedPiecesOpacity(R.id.light_bishops_captured_picture,lightBishops);
        capturedPiecesOpacity(R.id.light_knights_captured_picture,lightKnights);
        capturedPiecesOpacity(R.id.light_rooks_captured_picture,lightRooks);

        // Dark Captured
        editTextViewText(R.id.dark_queen_captured,String.valueOf(darkQueen));
        editTextViewText(R.id.dark_bishops_captured,String.valueOf(darkBishops));
        editTextViewText(R.id.dark_knights_captured,String.valueOf(darkKnights));
        editTextViewText(R.id.dark_rooks_captured,String.valueOf(darkRooks));
        capturedPiecesOpacity(R.id.dark_queen_captured_picture,darkQueen);
        capturedPiecesOpacity(R.id.dark_bishops_captured_picture,darkBishops);
        capturedPiecesOpacity(R.id.dark_knights_captured_picture,darkKnights);
        capturedPiecesOpacity(R.id.dark_rooks_captured_picture,darkRooks);

    }

    public void editTextViewText(int id,String text){
        ((TextView)findViewById(id)).setText(text);
    }

    public void capturedPiecesOpacity(int id, int numberCaptured){
        if(numberCaptured > 0)
            ((ImageView)findViewById(id)).setImageAlpha(255);
        else
            ((ImageView)findViewById(id)).setImageAlpha(80);
    }
}
