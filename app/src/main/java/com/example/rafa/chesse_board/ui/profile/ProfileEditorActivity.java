package com.example.rafa.chesse_board.ui.profile;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;

public class ProfileEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Rafa's Profile");
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_editor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    protected void onClickTakePicture(View v){
        Toast.makeText(this, "Take Picture", Toast.LENGTH_SHORT).show();
    }

    protected void onClickSubmit(View v){
        Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
    }
}
