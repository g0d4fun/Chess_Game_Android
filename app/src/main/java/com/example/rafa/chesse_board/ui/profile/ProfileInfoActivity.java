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

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileInfoActivity extends AppCompatActivity {

    ArrayList<HashMap<String, Object>> profileData;

    void addProfile(String name, String imagePath) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("nickname", name);
        hm.put("imagePath", imagePath);
        hm.put("isCurrentProfile", Boolean.TRUE);
        profileData.add(hm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Rafa's Info");
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_info);

        profileData = new ArrayList<>();
        addProfile("Bot", null);
        addProfile("Bot",null);

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
        toast("Profile Editor");
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
            return profileData.size();
        }

        @Override
        public Object getItem(int i) {
            return profileData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.item_profile_list, null);
            final int index = i;
            String nickname = (String) profileData.get(i).get("nickname");
            String imagePath = (String) profileData.get(i).get("imagePath");
            Boolean isCurrentProfile = (Boolean) profileData.get(i).get("isCurrentProfile");

            ((ImageView) layout.findViewById(R.id.item_profile_picture)).setImageResource(R.drawable.chess_bot);
            ((TextView) layout.findViewById(R.id.item_profile_nickname)).setText(nickname);

            notifyDataSetChanged();
            return layout;
        }
    }
}
