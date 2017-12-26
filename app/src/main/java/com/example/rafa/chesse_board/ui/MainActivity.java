package com.example.rafa.chesse_board.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.Alliance;
import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.model.GameOnlineMode;
import com.example.rafa.chesse_board.model.GameResult;
import com.example.rafa.chesse_board.model.Model;
import com.example.rafa.chesse_board.model.board.Move;
import com.example.rafa.chesse_board.model.board.Tile;
import com.example.rafa.chesse_board.model.pieces.Piece;
import com.example.rafa.chesse_board.model.sqlite.DatabaseHandler;
import com.example.rafa.chesse_board.model.sqlite.GameScore;
import com.example.rafa.chesse_board.model.sqlite.Profile;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.rafa.chesse_board.model.board.BoardUtils.NUM_TILES;

public class MainActivity extends AppCompatActivity implements UIConstants {

    private ImageButton[] tiles;
    private Model model;
    private Tile sourceTile, destinationTile;
    private Piece pieceToBeMoved;

    protected BoardDirection direction;

    private Profile profile;
    private DatabaseHandler db;

    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        db = new DatabaseHandler(this);
        if (db.getProfilesCount() == 0)
            db.addProfile(new Profile("No Profile", null));

        profile = db.getPlayerProfile();

        setContentView(R.layout.activity_main);
        direction = BoardDirection.NORMAL;

        TextView nicknamePlayer1 = findViewById(R.id.Player1);
        nicknamePlayer1.setText(profile.getNickName());

        LinearLayout chessBoardLayout = findViewById(R.id.chess_board);
        int boardWidth = chessBoardLayout.getWidth();
        chessBoardLayout.setMinimumHeight(boardWidth);

        if (savedInstanceState != null && savedInstanceState.getBoolean("Save")) {
            savedInstanceState(savedInstanceState);

        } else {
            // New Game
            model = new Model();
            String temp = getIntent().getStringExtra("game_mode");
            if (temp.equalsIgnoreCase(GameMode.SINGLE_PLAYER.toString())) {
                ((TextView) findViewById(R.id.countdown)).setVisibility(View.INVISIBLE);
                model.startNewGame(GameMode.SINGLE_PLAYER);
            } else if (temp.equalsIgnoreCase(GameMode.MULTIPLAYER.toString())) {
                String opponentName = getIntent().getStringExtra("opponent_name");
                int timer = getIntent().getIntExtra("timer", 30);
                model.setMillisecondsToFinish(timer * 60000);
                model.setMillisecondsToFinishOpponent(timer * 60000);
                model.startNewGame(GameMode.MULTIPLAYER, opponentName);
                setUpCountDown(model.getMillisecondsToFinish());

            } else if (temp.equalsIgnoreCase(GameMode.ONLINE.toString())) {
                model.startNewGame(GameMode.ONLINE);
            }
            setTitle(model.getGameMode().toString() + " Game");
            Toast.makeText(this, "Mode: " + model.getGameMode(), Toast.LENGTH_SHORT).show();


        }
        if (model.getOpponentName() != null)
            ((TextView) findViewById(R.id.Player2)).setText(model.getOpponentName());

        tiles = setUpButtons();
        setUpListeners();
        setUpDialog();
        renderGame();
        updatePlayerProfile();

        if (model.getGameMode().equals(GameMode.ONLINE))
            setUpOnlineGame(getIntent());
    }

    /**
     * Count Down Timer used in Multiplayer and Online Game, which only
     * need to be called once per game.
     * When player is changed, automatically changes the count down time.
     * After one of the count down time reaches the end, the opponent
     * (not current player) wins the game.
     *
     * @param milliseconds
     */
    private void setUpCountDown(final long milliseconds) {

        new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                long minutesLeft = (millisUntilFinished / 1000) / 60;
                long secondsLeft = (millisUntilFinished / 1000) % 60;
                if (model.isWhiteCountDownTimer()) {
                    if (model.getCurrentPlayer().isWhite()) {
                        ((TextView) (findViewById(R.id.countdown))).setTextColor(getResources().getColor(R.color.LIGHT_TILE_COLOR_HEX));
                        ((TextView) (findViewById(R.id.countdown))).setText(minutesLeft + ":" + secondsLeft);
                        model.setMillisecondsToFinish((int) millisUntilFinished);
                    } else {
                        setUpCountDown(model.getMillisecondsToFinishOpponent());
                        model.setWhiteCountDownTimer(false);
                        cancel();
                    }
                } else {
                    if (model.getCurrentPlayer().isBlack()) {
                        ((TextView) (findViewById(R.id.countdown))).setTextColor(getResources().getColor(R.color.DARK_TILE_COLOR_HEX));
                        ((TextView) (findViewById(R.id.countdown))).setText(minutesLeft + ":" + secondsLeft);
                        model.setMillisecondsToFinishOpponent((int) millisUntilFinished);
                    } else {

                        setUpCountDown(model.getMillisecondsToFinish());
                        model.setWhiteCountDownTimer(true);
                        cancel();
                    }
                }
            }

            public void onFinish() {
                ((TextView) (findViewById(R.id.countdown))).setText("Time's Up");
                String opponentName = model.getOpponentName();
                if (opponentName == null)
                    opponentName = "Bot";
                // Player 1 - Profile owner
                if (model.getCurrentPlayer().isWhite())
                    alertDialogEndGame("Your time is over, you Lose!", opponentName, GameResult.LOSE);
                    // Player 2
                else
                    alertDialogEndGame("Your opponent time is over, you Won!", opponentName, GameResult.WIN);
            }
        }.start();
    }

    /**
     * This method display a Alert Dialog on End Game, before
     * return to Start Menu. Automatically add score to database and
     * ends activity
     *
     * @param message      How game ended
     * @param opponentName Opponent Name (to save on mysql local database)
     * @param result       Game Result (win, lose or draw to save on mysql local database)
     */
    protected void alertDialogEndGame(final String message, final String opponentName,
                                      final GameResult result) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Game Over");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                addGameScore(model.getGameMode(), result, opponentName);
            }
        });

        alertDialog.show();
    }

    // chessBoardLayout-----------------------------------------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((ApplicationState) getApplication()).save(tiles, model, sourceTile, destinationTile, pieceToBeMoved);
        outState.putBoolean("Save", true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_flip_board:
                Toast.makeText(this, "Flip Board", Toast.LENGTH_SHORT).show();
                direction = direction.opposite();
                renderGame();
                break;
            case R.id.item_give_up:
                Toast.makeText(this, "Given Up", Toast.LENGTH_SHORT).show();
                alert.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_game, menu);

        return super.onCreateOptionsMenu(menu);
    }

    protected void savedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("Save")) {
            //From Application
            ApplicationState state = (ApplicationState) getApplication();
            model = state.getModel();
            tiles = state.getTiles();
            sourceTile = state.getSourceTile();
            destinationTile = state.getDestinationTile();
            pieceToBeMoved = state.getPieceToBeMoved();
            Toast.makeText(state, "Get Saved Instance", Toast.LENGTH_SHORT).show();
            if (model.getCurrentPlayer().isWhite())
                setUpCountDown(model.getMillisecondsToFinish());
            else
                setUpCountDown(model.getMillisecondsToFinishOpponent());
        } else {
            //From intent
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        alert.show();
    }

    /**
     * @return ImageButton initialized with id game cells
     * ready for changes
     */
    public ImageButton[] setUpButtons() {

        ImageButton[] tiles = new ImageButton[NUM_TILES];

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

    /**
     * Render game
     */
    public void renderGame() {
        // Draw Tiles
        for (int i = 0; i < NUM_TILES; i++) {
            int column = i / 8;
            int row = i - column * 8;

            if ((row % 2) == (column % 2)) // Light Color
                tiles[i].setBackgroundColor(getResources().getColor(R.color.LIGHT_TILE_COLOR_HEX));
            else // Dark Color
                tiles[i].setBackgroundColor(getResources().getColor(R.color.DARK_TILE_COLOR_HEX));

            if (sourceTile == direction.traverse(model.getBoard().getGameBoard()).get(i))
                if ((row % 2) == (column % 2)) // Light Color
                    tiles[i].setBackgroundColor(getResources().getColor(R.color.LIGHT_TILE_COLOR_HEX_ON_CLICK));
                else  // Dark Color
                    tiles[i].setBackgroundColor(getResources().getColor(R.color.DARK_TILE_COLOR_HEX_ON_CLICK));

            Tile tile = direction.traverse(model.getBoard().getGameBoard()).get(i);
            for (Move move : model.pieceLegalMoves(pieceToBeMoved)) {
                if (move.getDestinationCoordinate() == tile.getTileCoordinate()) {
                    if (tile.getPiece() != null)
                        tiles[i].setBackgroundColor(Color.RED);
                }
            }

            tiles[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


        List<Tile> tilesReversed = direction.traverse(model.getBoard().getGameBoard());
        // Draw Pieces
        for (int i = 0; i < NUM_TILES; i++) {
            Integer drawableValue = getDrawableByPieceName(tilesReversed.get(i));
            if (drawableValue != null)
                tiles[i].setImageResource(drawableValue);
            else
                tiles[i].setImageResource(android.R.color.transparent);
        }

        // Current Player Viewer
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

    public void updatePiecesCaptured() {
        int lightRooks = 2;
        int lightKnights = 2;
        int lightBishops = 2;
        int lightQueen = 1;
        int darkRooks = 2;
        int darkKnights = 2;
        int darkBishops = 2;
        int darkQueen = 1;

        //How many pieces captures
        Iterable<Piece> pieces = model.getBoard().getAllPieces();
        for (Piece piece : pieces) {
            switch (piece.getPieceType()) {
                case ROOK:
                    if (piece.getPieceAllegiance().isBlack())
                        darkRooks--;
                    else
                        lightRooks--;
                    break;
                case KNIGHT:
                    if (piece.getPieceAllegiance().isBlack())
                        darkKnights--;
                    else
                        lightKnights--;
                    break;
                case BISHOP:
                    if (piece.getPieceAllegiance().isBlack())
                        darkBishops--;
                    else
                        lightBishops--;
                    break;
                case QUEEN:
                    if (piece.getPieceAllegiance().isBlack())
                        darkQueen--;
                    else
                        lightQueen--;
                    break;
            }
        }
        // Light Captured
        editTextViewText(R.id.light_queen_captured, String.valueOf(lightQueen));
        editTextViewText(R.id.light_bishops_captured, String.valueOf(lightBishops));
        editTextViewText(R.id.light_knights_captured, String.valueOf(lightKnights));
        editTextViewText(R.id.light_rooks_captured, String.valueOf(lightRooks));
        capturedPiecesOpacity(R.id.light_queen_captured_picture, lightQueen);
        capturedPiecesOpacity(R.id.light_bishops_captured_picture, lightBishops);
        capturedPiecesOpacity(R.id.light_knights_captured_picture, lightKnights);
        capturedPiecesOpacity(R.id.light_rooks_captured_picture, lightRooks);

        // Dark Captured
        editTextViewText(R.id.dark_queen_captured, String.valueOf(darkQueen));
        editTextViewText(R.id.dark_bishops_captured, String.valueOf(darkBishops));
        editTextViewText(R.id.dark_knights_captured, String.valueOf(darkKnights));
        editTextViewText(R.id.dark_rooks_captured, String.valueOf(darkRooks));
        capturedPiecesOpacity(R.id.dark_queen_captured_picture, darkQueen);
        capturedPiecesOpacity(R.id.dark_bishops_captured_picture, darkBishops);
        capturedPiecesOpacity(R.id.dark_knights_captured_picture, darkKnights);
        capturedPiecesOpacity(R.id.dark_rooks_captured_picture, darkRooks);

    }

    public void editTextViewText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    public void capturedPiecesOpacity(int id, int numberCaptured) {
        if (numberCaptured > 0)
            ((ImageView) findViewById(id)).setImageAlpha(255);
        else
            ((ImageView) findViewById(id)).setImageAlpha(80);
    }

    public void setUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure about exit?");
        builder.setMessage("You lose the game if you exit.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String opponentName = model.getOpponentName();
                if (opponentName == null)
                    opponentName = "Bot";
                addGameScore(model.getGameMode(), GameResult.LOSE, opponentName);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        alert = builder.create();
    }

    private void addGameScore(GameMode mode, GameResult result, String opponentNickName) {
        GameScore gameScore = new GameScore(mode, result, opponentNickName);

        DatabaseHandler db = new DatabaseHandler(this);
        db.addScore(gameScore);

        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    /**
     * Update player profile picture
     */
    private void updatePlayerProfile() {
        profile = db.getPlayerProfile();
        String imageFilePath = profile.getImagePath();
        ImageView profilePicture = findViewById(R.id.player_picture);
        try {
            UIUtils.setPic(profilePicture, imageFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not Set Picture.", Toast.LENGTH_SHORT).show();
            profilePicture.setImageResource(R.drawable.chess_sporting);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (model.commSettedUp()) {
            model.onResumeCommunication();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (model.commSettedUp()) {
            model.onPauseCommunication();
        }
    }
    //Communication -------------------------------------------------------------------------

    protected void setUpOnlineGame(Intent intent) {
        String mode = "";
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "Network Connection Error.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (intent != null)
            mode = intent.getStringExtra("online_mode");
        if (mode == null) {
            Toast.makeText(this, "Game Mode is empty.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mode.equalsIgnoreCase(GameOnlineMode.CLIENT.toString()) &&
                !mode.equalsIgnoreCase(GameOnlineMode.SERVER.toString())) {
            Toast.makeText(this, "Game Mode is wrong.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        GameOnlineMode gameOnlineMode = GameOnlineMode.valueOf(mode);

        model.setUpCommunication(gameOnlineMode, this);
    }
}
