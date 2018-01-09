package com.example.rafa.chesse_board.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.model.GameOnlineMode;
import com.example.rafa.chesse_board.model.GameResult;
import com.example.rafa.chesse_board.model.sqlite.DatabaseHandler;
import com.example.rafa.chesse_board.model.sqlite.Profile;
import com.example.rafa.chesse_board.ui.profile.ProfileInfoActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class StartActivity extends AppCompatActivity {

    private Profile profile;
    private TextView nickname;
    private DatabaseHandler db;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = new DatabaseHandler(this);
        if (db.getProfilesCount() == 0)
            db.addProfile(new Profile(getString(R.string.no_profile), null));

        profile = db.getPlayerProfile();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_start);

        nickname = findViewById(R.id.start_profile_nickname);
        nickname.setText(profile.getNickName());

        updateView();
    }

    protected void onClickProfile(View v) {
        Intent intent = new Intent(this, ProfileInfoActivity.class);
        Log.i("chess_game", "Profile Edit");
        startActivity(intent);
    }

    protected void onClickSinglePlayer(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.SINGLE_PLAYER.toString());
        startActivity(intent);
    }

    protected void onClickMultiplayer(View v) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.MULTIPLAYER.toString());

        getOpponentNameIntentDialog();
    }

    protected void getOpponentNameIntentDialog() {
        final EditText txtUrl = new EditText(this);
        txtUrl.setHint(getString(R.string.dialog_hint_opponent_name));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_opponent_name))
                .setView(txtUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        if (url.length() <= 2) {
                            getOpponentNameIntentDialog();
                            return;
                        }
                        intent.putExtra("opponent_name", url);
                        hasCountDownDialog();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent = null;
                    }
                })
                .show();
    }

    protected void hasCountDownDialog() {
        final EditText txtUrl = new EditText(this);
        txtUrl.setHint("10-120 " + getString(R.string.minutes));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.timer))
                .setView(txtUrl)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        try {
                            int timer = Integer.parseInt(url);
                            if (timer < 10 || timer > 120) {
                                intent = null;
                                Toast.makeText(StartActivity.this,
                                        getString(R.string.out_of_range_time), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            intent.putExtra("timer", timer);
                            startActivity(intent);
                        } catch (Exception e) {
                            intent = null;
                            Toast.makeText(StartActivity.this,
                                    getString(R.string.time_not_valid), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent = null;
                    }
                })
                .show();
    }

    protected void onClickLaunchCredits(View v){
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    protected void onClickOnline(View v) {

    }

    protected void onClickCreateGame(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.ONLINE.toString());
        intent.putExtra("online_mode", GameOnlineMode.SERVER.toString());
        startActivity(intent);
    }

    protected void onClickJoinGame(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.ONLINE.toString());
        intent.putExtra("online_mode", GameOnlineMode.CLIENT.toString());
        startActivity(intent);
    }

    public void updateView() {
        profile = db.getPlayerProfile();
        if (profile == null || nickname == null) {
            nickname.setText("");
            return;
        }
        nickname.setText(profile.getNickName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateView();
    }
}
