package com.example.rafa.chesse_board.ui.profile;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.model.GameResult;
import com.example.rafa.chesse_board.model.sqlite.DatabaseHandler;
import com.example.rafa.chesse_board.model.sqlite.GameScore;
import com.example.rafa.chesse_board.model.sqlite.Profile;
import com.example.rafa.chesse_board.ui.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileInfoActivity extends AppCompatActivity {

    private Profile profile;
    private DatabaseHandler db;

    ArrayList<HashMap<String, Object>> gameScores;

    void addProfile(String name, GameMode gameMode, GameResult result, String imagePath) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("nickname", name);
        hm.put("game_mode",gameMode.toString());
        hm.put("result",result.toString());
        hm.put("image_path", imagePath);
        gameScores.add(hm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        if (db.getProfilesCount() == 0)
            db.addProfile(new Profile("No Profile", null));

        profile = db.getPlayerProfile();

        setTitle(profile.getNickName() + "'s Profile");
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_info);

        TextView nickname = findViewById(R.id.profile_info_nickname);
        nickname.setText(profile.getNickName());

        gameScores = new ArrayList<>();
        List<GameScore> scores = db.getAllScores();
        for(GameScore score : scores)
            addProfile(score.getOpponentNickName(),score.getMode(),score.getResult(),null);

        ListView lv = (ListView) this.findViewById(R.id.profiles_list);
        lv.setAdapter(new ProfileListAdapter());

        setUpListeners();
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

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void launchEditorActivity(View v) {
        Intent intent = new Intent(this, ProfileEditorActivity.class);
        startActivity(intent);
    }

    private void setUpListeners(){
        ImageButton edit = (ImageButton) findViewById(R.id.item_profile_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchEditorActivity(view);
            }
        });
    }

    class ProfileListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return gameScores.size();
        }

        @Override
        public Object getItem(int i) {
            return gameScores.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.item_profile_list, null);
            final int index = i;
            String nickname = (String) gameScores.get(i).get("nickname");
            String gameMode = (String) gameScores.get(i).get("game_mode");
            String result = (String) gameScores.get(i).get("result");
            String imagePath = (String) gameScores.get(i).get("imagePath");

            ((ImageView) layout.findViewById(R.id.item_profile_picture)).setImageResource(R.drawable.chess_bot);
            ((TextView) layout.findViewById(R.id.item_profile_nickname)).setText(nickname);
            ((TextView) layout.findViewById(R.id.item_profile_game_mode)).setText(gameMode);
            ((TextView) layout.findViewById(R.id.item_game_result)).setText(result);

            notifyDataSetChanged();
            return layout;
        }
    }

    public void updateView(){
        profile = db.getPlayerProfile();
        String imageFilePath = profile.getImagePath();
        ImageView profilePicture = findViewById(R.id.info_profile_picture);
        try {
            UIUtils.setPic(profilePicture, imageFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not Set Picture.", Toast.LENGTH_SHORT).show();
            profilePicture.setImageResource(R.drawable.chess_sporting);
        }

        TextView profile_info_nickname = findViewById(R.id.profile_info_nickname);
        profile_info_nickname.setText(profile.getNickName());
        setTitle(profile.getNickName() + "'s Profile");
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateView();
    }
}
