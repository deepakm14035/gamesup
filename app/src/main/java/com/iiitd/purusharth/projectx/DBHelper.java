package com.iiitd.purusharth.projectx;

/**
 * Created by Purusharth on 28-10-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Purusharth on 02-10-2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "EventStore";

    // Table Names
    private static final String DB_TABLE = "table_event";

    // column names
    private static final String KEY_ID = "eventId";
    private static final String KEY_UID = "userId";
    private static final String KEY_TIME = "timestamp";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_NAME = "author_name";
    private static final String KEY_VENUE = "venue";
    private static final String KEY_VENUE_NAME = "venue_name";
    private static final String KEY_DESC = "description";


    // Table create statement
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + "(" +
            KEY_ID + " TEXT," +
            KEY_UID + " TEXT," +
            KEY_TIME + " TEXT," +
            KEY_NAME + " TEXT," +
            KEY_VENUE + " TEXT," +
            KEY_ACTIVITY + " TEXT," +
            KEY_DESC + " TEXT," +
            KEY_VENUE_NAME + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        // create new table
        onCreate(db);
    }

    public void addEntry(EventActivity event) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, event.getActivityID());
        cv.put(KEY_UID, event.getUserID());
        cv.put(KEY_TIME, event.getPostedDate());
        cv.put(KEY_NAME, event.getName());
        cv.put(KEY_VENUE, event.getVenue());
        cv.put(KEY_ACTIVITY, event.getActivityName());
        cv.put(KEY_DESC, event.getDesc());
        cv.put(KEY_VENUE_NAME, event.getVenue_name());
        database.insert(DB_TABLE, null, cv);
        database.close();
    }

    public List<EventActivity> getEvents() {
        List<EventActivity> e = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "select * from " + DB_TABLE + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                EventActivity event = new EventActivity();
                event.setActivityID(cursor.getString(0));
                event.setUserID(cursor.getString(1));
                event.setPostedDate(cursor.getString(2));
                event.setName(cursor.getString(3));
                event.setVenue(cursor.getString(4));
                event.setActivityName(cursor.getString(5));
                event.setDesc(cursor.getString(6));
                event.setVenue_name(cursor.getString(7));
                e.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return e;
    }
}