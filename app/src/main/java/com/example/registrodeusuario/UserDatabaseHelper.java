// UserDatabaseHelper.java
package com.example.registrodeusuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 8; // Incremented version

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_ESTADO_VOTACION = "estado_votacion";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT, " +
                    COLUMN_ESTADO_VOTACION + " INTEGER DEFAULT 0);";

    public static final String TABLE_PROPOSALS = "propuestas";
    public static final String COLUMN_PROPOSAL_ID = "_id";
    public static final String COLUMN_PROPOSAL_TITLE = "title";
    public static final String COLUMN_PROPOSAL_DESCRIPTION = "description";
    public static final String COLUMN_PROPOSAL_LOCALITY = "locality";
    public static final String COLUMN_PROPOSAL_DURATION = "duration";
    public static final String COLUMN_PROPOSAL_USERNAME = "username";
    public static final String COLUMN_PROPOSAL_FIRST_NAME = "first_name";
    public static final String COLUMN_PROPOSAL_LAST_NAME = "last_name";
    public static final String COLUMN_PROPOSAL_VOTOS = "votos";
    public static final String COLUMN_PROPOSAL_STATUS = "proposal_status";

    private static final String TABLE_CREATE_PROPOSALS =
            "CREATE TABLE " + TABLE_PROPOSALS + " (" +
                    COLUMN_PROPOSAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROPOSAL_TITLE + " TEXT, " +
                    COLUMN_PROPOSAL_DESCRIPTION + " TEXT, " +
                    COLUMN_PROPOSAL_LOCALITY + " TEXT, " +
                    COLUMN_PROPOSAL_DURATION + " TEXT, " +
                    COLUMN_PROPOSAL_USERNAME + " TEXT, " +
                    COLUMN_PROPOSAL_FIRST_NAME + " TEXT, " +
                    COLUMN_PROPOSAL_LAST_NAME + " TEXT, " +
                    COLUMN_PROPOSAL_VOTOS + " INTEGER DEFAULT 0, " + // Nueva columna
                    COLUMN_PROPOSAL_STATUS + " BOOLEAN DEFAULT 1);";

    private Context context;

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_PROPOSALS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE " + TABLE_PROPOSALS + " ADD COLUMN " + COLUMN_PROPOSAL_STATUS + " BOOLEAN DEFAULT 1;");
        }
    }

    public boolean updateProposal(String proposalId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(TABLE_PROPOSALS, values, COLUMN_PROPOSAL_ID + " = ?", new String[]{proposalId});
        return rowsAffected > 0;
    }

    public boolean updateUserRole(int userId, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE, newRole);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsDeleted > 0;
    }

    public boolean hasUserVoted(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ESTADO_VOTACION},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        boolean hasVoted = false;
        if (cursor != null && cursor.moveToFirst()) {
            hasVoted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_VOTACION)) == 1;
            cursor.close();
        }
        return hasVoted;
    }

    public void saveUserVote(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO_VOTACION, 1);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
    }

    public Cursor getAllProposals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_PROPOSALS,
                new String[]{COLUMN_PROPOSAL_TITLE, COLUMN_PROPOSAL_STATUS, COLUMN_PROPOSAL_DURATION, COLUMN_PROPOSAL_VOTOS}, // Incluye la nueva columna
                null, null, null, null, null
        );
    }

    public boolean resetAllProposals() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROPOSAL_STATUS, 0);
        int rowsAffected = db.update(TABLE_PROPOSALS, values, null, null);
        return rowsAffected > 0;
    }

    public boolean resetAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO_VOTACION, 0);
        int rowsAffected = db.update(TABLE_USERS, values, null, null);
        return rowsAffected > 0;
    }

    public Cursor getProposalsWithStatus(int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_PROPOSALS,
                new String[]{COLUMN_PROPOSAL_TITLE, COLUMN_PROPOSAL_STATUS, COLUMN_PROPOSAL_DURATION, COLUMN_PROPOSAL_VOTOS},
                COLUMN_PROPOSAL_STATUS + " = ?",
                new String[]{String.valueOf(status)},
                null, null, null
        );
    }

    public boolean deleteProposal(String proposalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PROPOSALS, COLUMN_PROPOSAL_ID + " = ?", new String[]{proposalId});
        return rowsDeleted > 0;
    }

    public Cursor getProposalById(String proposalId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_PROPOSALS,
                null,
                COLUMN_PROPOSAL_ID + " = ?",
                new String[]{proposalId},
                null, null, null
        );
    }

    public Map<String, Integer> getLocalitiesWithStatus(int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROPOSAL_LOCALITY + ", COUNT(*) as count FROM " + TABLE_PROPOSALS + " WHERE " + COLUMN_PROPOSAL_STATUS + " = ? GROUP BY " + COLUMN_PROPOSAL_LOCALITY, new String[]{String.valueOf(status)});

        Map<String, Integer> localities = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String locality = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROPOSAL_LOCALITY));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
                localities.put(locality, count);
            }
            cursor.close();
        }
        return localities;
    }



}