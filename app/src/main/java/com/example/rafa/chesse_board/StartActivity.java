package com.example.rafa.chesse_board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_start);

    }

    protected void onClickProfile(View v){
        Intent intent = new Intent(this,MainActivity.class);
        Toast.makeText(this, "Profile Edit", Toast.LENGTH_SHORT).show();
    }

    protected void onClickSinglePlayer(View v){
        Intent intent = new Intent(this,MainActivity.class);
        Toast.makeText(this, "Single Player Game Started", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    protected void onClickMultiplayer(View v){
        Toast.makeText(this, "Multiplayer Game ", Toast.LENGTH_SHORT).show();
    }

    protected void onClickOnline(View v){
        Toast.makeText(this, "Online Game ", Toast.LENGTH_SHORT).show();

    }
}
