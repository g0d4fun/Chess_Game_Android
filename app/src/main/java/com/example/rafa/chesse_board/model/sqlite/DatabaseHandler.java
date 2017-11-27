package com.example.rafa.chesse_board.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rafa.chesse_board.model.GameMode;
import com.example.rafa.chesse_board.model.GameResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henri on 11/26/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_SCORES = "scores";
    // Table Columns names
    private static final String SCORES_KEY_ID = "id";
    private static final String SCORES_KEY_GAME_MODE = "game_mode";
    private static final String SCORES_KEY_RESULT = "game_result";
    private static final String SCORES_KEY_NICKNAME = "opponent_nickname";

    private static final String TABLE_PROFILES = "profiles";
    private static final String PROFILES_KEY_ID = "id";
    private static final String PROFILES_KEY_NICKNAME = "nickname";
    private static final String PROFILES_KEY_IMAGE_PATH = "image_path";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_SCORES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SCORES + "("
                + SCORES_KEY_ID + " INTEGER PRIMARY KEY," + SCORES_KEY_GAME_MODE + " TEXT,"
                + SCORES_KEY_RESULT + " TEXT," + SCORES_KEY_NICKNAME + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_SCORES_TABLE);
        String CREATE_PROFILES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILES + "("
                + PROFILES_KEY_ID + " INTEGER PRIMARY KEY," + PROFILES_KEY_NICKNAME + " TEXT,"
                + PROFILES_KEY_IMAGE_PATH + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_PROFILES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Score Table
     **/
    public void addScore(GameScore score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SCORES_KEY_GAME_MODE, score.getMode().toString());
        values.put(SCORES_KEY_RESULT, score.getResult().toString());
        values.put(SCORES_KEY_NICKNAME, score.getOpponentNickName());

        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    public GameScore getScore(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SCORES, new String[]{SCORES_KEY_ID,
                        SCORES_KEY_GAME_MODE, SCORES_KEY_RESULT, SCORES_KEY_NICKNAME}, SCORES_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        GameScore score = new GameScore(
                GameMode.valueOf(cursor.getString(1)),
                GameResult.valueOf(cursor.getString(2)),
                cursor.getString(3));
        return score;
    }

    public List<GameScore> getAllScores() {
        List<GameScore> scores = new ArrayList<GameScore>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCORES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GameScore score = new GameScore(
                        GameMode.valueOf(cursor.getString(1)),
                        GameResult.valueOf(cursor.getString(2)),
                        cursor.getString(3));
                // Adding contact to list
                scores.add(score);
            } while (cursor.moveToNext());
        }

        // return scores list
        return scores;
    }

    public int getScoresCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SCORES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updateScore(GameScore score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SCORES_KEY_GAME_MODE, score.getMode().toString());
        values.put(SCORES_KEY_RESULT, score.getResult().toString());
        values.put(SCORES_KEY_NICKNAME, score.getOpponentNickName());

        // updating row
        return db.update(TABLE_SCORES, values, SCORES_KEY_ID + " = ?",
                new String[]{String.valueOf(score.getId())});
    }

    public void deleteScore(GameScore score) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORES, SCORES_KEY_ID + " = ?",
                new String[]{String.valueOf(score.getId())});
        db.close();
    }

    /**
     * Profiles Table
     **/
    public void addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILES_KEY_NICKNAME, profile.getNickName());
        values.put(PROFILES_KEY_IMAGE_PATH, profile.getImagePath());

        db.insert(TABLE_PROFILES, null, values);
        db.close();
    }

    public Profile getProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROFILES, new String[]{PROFILES_KEY_ID,
                        PROFILES_KEY_NICKNAME, PROFILES_KEY_IMAGE_PATH}, PROFILES_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Profile profile = new Profile(cursor.getString(1),getAllScores(), cursor.getString(2));
        return profile;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<Profile>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Profile profile = new Profile(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        getAllScores(),
                        cursor.getString(2));
                // Adding contact to list
                profiles.add(profile);
            } while (cursor.moveToNext());
        }

        // return scores list
        return profiles;
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROFILES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getCount();
            }
            return count;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int updateProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILES_KEY_NICKNAME, profile.getNickName());
        values.put(PROFILES_KEY_IMAGE_PATH, profile.getImagePath());

        // updating row
        return db.update(TABLE_PROFILES, values, PROFILES_KEY_ID + " = ?",
                new String[]{String.valueOf(profile.getId())});
    }

    public void deleteProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILES, PROFILES_KEY_ID + " = ?",
                new String[]{String.valueOf(profile.getId())});
        db.close();
    }
}
