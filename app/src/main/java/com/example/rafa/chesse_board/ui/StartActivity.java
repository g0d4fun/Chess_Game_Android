package com.example.rafa.chesse_board.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.ui.profile.ProfileInfoActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_start);

    }

    protected void onClickProfile(View v){
        Intent intent = new Intent(this,ProfileInfoActivity.class);
        Toast.makeText(this, "Profile Edit", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    protected void onClickSinglePlayer(View v){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("game_mode",GameMode.SINGLE_PLAYER.toString());
        startActivity(intent);
    }

    protected void onClickMultiplayer(View v){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("game_mode",GameMode.MULTIPLAYER.toString());
        startActivity(intent);
    }

    protected void onClickOnline(View v){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("game_mode",GameMode.ONLINE.toString());
        startActivity(intent);
    }
}
