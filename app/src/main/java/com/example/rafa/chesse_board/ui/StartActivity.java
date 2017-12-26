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
            db.addProfile(new Profile("No Profile", null));

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
        Toast.makeText(this, "Profile Edit", Toast.LENGTH_SHORT).show();
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
        txtUrl.setHint("Opponent Name");
        new AlertDialog.Builder(this)
                .setTitle("Opponent Name")
                .setView(txtUrl)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        if(url.length() <= 2){
                            getOpponentNameIntentDialog();
                            return;
                        }
                        intent.putExtra("opponent_name", url);
                        hasCountDownDialog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent = null;
                    }
                })
                .show();
    }

    protected void hasCountDownDialog() {
        final EditText txtUrl = new EditText(this);
        txtUrl.setHint("10-120 minutes");
        new AlertDialog.Builder(this)
                .setTitle("Timer")
                .setView(txtUrl)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();
                        try {
                            int timer = Integer.parseInt(url);
                            if(timer < 10 || timer > 120) {
                                intent = null;
                                Toast.makeText(StartActivity.this,
                                        "Time Value Out of Range.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            intent.putExtra("timer",timer);
                            startActivity(intent);
                        } catch (Exception e) {
                            intent = null;
                            Toast.makeText(StartActivity.this,
                                    "Time Value not Valid.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent = null;
                    }
                })
                .show();
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

    public void setUpCreateGameDialog() {
//        String myIp = getLocalIpAddress();
//
//        ProgressDialog progDlg = new ProgressDialog(this);
//        progDlg.setTitle("Create an Online Game");
//        progDlg.setMessage("Your opponent must choose to Join Game and write the following number.\n" + myIp);
//        progDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                finish();
//                if(serverSocket != null){
//                    try {
//                        serverSocket.close();
//                    }catch(IOExeption e){}
//                    serverSocket = null;
//                }
//            }
//        });
//        progDlg.show();
//
//        Thread serverThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    serverSocket = new ServerSocket(PORT);
//                    socketGame = serverSocket.accept();
//                    serverSocket.close();
//                    serverSocket = null;
//                    commThread.start();
//                }catch(Exception e){
//                    e.printStackTrace();
//                    socketGame = null;
//                }
//                procMsg.post()
//            }
//        })
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
