package com.example.rafa.chesse_board.ui.profile;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.sqlite.DatabaseHandler;
import com.example.rafa.chesse_board.model.sqlite.Profile;

public class ProfileEditorActivity extends AppCompatActivity {

    private Profile profile;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        if (db.getProfilesCount() == 0)
            db.addProfile(new Profile("No Profile", null));

        profile = db.getAllProfiles().get(0);
        setTitle(profile.getNickName() + "'s Profile");

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_editor);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    protected void onClickTakePicture(View v) {
        Toast.makeText(this, "Take Picture", Toast.LENGTH_SHORT).show();
    }

    protected void onClickSubmit(View v) {
        EditText nickname = (EditText) findViewById(R.id.nickname_editor);

        String str = nickname.getText().toString();
        if(str.length() >= 10){
            Toast.makeText(this, "Nickname Can't be larger than 10 letters", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile newProfile = new Profile(str.trim(),profile.getImagePath());
        db.updateProfile(newProfile);
        Toast.makeText(this, "Nickname Successfully changed", Toast.LENGTH_SHORT).show();
    }
}
