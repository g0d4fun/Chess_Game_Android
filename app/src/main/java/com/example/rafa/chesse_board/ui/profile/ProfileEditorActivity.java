package com.example.rafa.chesse_board.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.sqlite.DatabaseHandler;
import com.example.rafa.chesse_board.model.sqlite.Profile;
import com.example.rafa.chesse_board.ui.ApplicationState;
import com.example.rafa.chesse_board.ui.CameraActivity;
import com.example.rafa.chesse_board.ui.StartActivity;
import com.example.rafa.chesse_board.ui.UIConstants;
import com.example.rafa.chesse_board.ui.UIUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProfileEditorActivity extends AppCompatActivity {

    private Profile profile;
    private DatabaseHandler db;

    private String imageFilePath;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        if (db.getProfilesCount() == 0)
            db.addProfile(new Profile("No Profile", null));

        profile = db.getAllProfiles().get(0);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_editor);

        profilePicture = findViewById(R.id.profile_picture);
        updateView();
        Toast.makeText(this, imageFilePath, Toast.LENGTH_LONG).show();
    }

    public void updateView() {
        profile = db.getPlayerProfile();
        imageFilePath = profile.getImagePath();

        try {
            UIUtils.setPic(profilePicture, imageFilePath, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not Set Picture.", Toast.LENGTH_SHORT).show();
            profilePicture.setImageResource(R.drawable.chess_sporting);
        }

        setTitle(profile.getNickName() + "'s Profile");
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
        Intent takePhotoIntent = new Intent(this, CameraActivity.class);
        startActivity(takePhotoIntent);
    }

    protected void onClickSubmit(View v) {
        EditText nickname = (EditText) findViewById(R.id.nickname_editor);

        String str = nickname.getText().toString();
        if (str.length() >= 10) {
            Toast.makeText(this, "Nickname Can't be larger than 10 letters", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile newProfile = new Profile(str.trim(), profile.getImagePath());
        db.updateProfile(newProfile);
        nickname.setText("");
        updateView();

        Toast.makeText(this, "Nickname Successfully changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 20 && resultCode == RESULT_OK) {
            UIUtils.setPic(profilePicture, imageFilePath);

            Profile newProfile = new Profile(profile.getNickName().trim(), imageFilePath);
            db.updateProfile(newProfile);
            updateView();

            Toast.makeText(this, imageFilePath, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateView();
    }
}
