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
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.GameMode;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.MULTIPLAYER.toString());
        startActivity(intent);
    }

    protected void onClickOnline(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("game_mode", GameMode.ONLINE.toString());
        startActivity(intent);
    }

    protected void onClickCreateGame(View v){

    }

    protected void onClickJoinGame(View v){

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
        if (profile == null || nickname != null) {
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
