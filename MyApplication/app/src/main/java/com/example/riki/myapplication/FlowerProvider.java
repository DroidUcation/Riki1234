package com.example.riki.myapplication;

/**
 * Created by Riki on 03/05/2016.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class FlowerProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.riki.myapplication";
    static final String URL = "content://" + PROVIDER_NAME + "/flowers";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";
    static final String COLOR = "color";
    static final String NUM_OF_LEAVES = "numOfLeaves";
    static final String AT_NIGHT = "atNight";


    private static HashMap<String, String> FLOWERS_PROJECTION_MAP;

    static final int FLOWERS = 1;
    static final int FLOWERS_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "flowers", FLOWERS);
        uriMatcher.addURI(PROVIDER_NAME, "flowers/#", FLOWERS_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "myApp";
    static final String FLOWERS_TABLE_NAME = "flowers";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            "CREATE TABLE  "+FLOWERS_TABLE_NAME+
            "("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
             " "+NAME+" TEXT NOT NULL,"+
             " "+COLOR+" TEXT NOT NULL,"+
             " "+NUM_OF_LEAVES+" int,"+
             " "+AT_NIGHT+" bool)";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  FLOWERS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(	FLOWERS_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FLOWERS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case FLOWERS:
                qb.setProjectionMap(FLOWERS_PROJECTION_MAP);
                break;

            case FLOWERS_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){

            sortOrder = NAME;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case FLOWERS:
                count = db.delete(FLOWERS_TABLE_NAME, selection, selectionArgs);
                break;

            case FLOWERS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( FLOWERS_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case FLOWERS:
                count = db.update(FLOWERS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case FLOWERS_ID:
                count = db.update(FLOWERS_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){

            case FLOWERS:
                return "vnd.android.cursor.dir/vnd.example.flowers";


            case FLOWERS_ID:
                return "vnd.android.cursor.item/vnd.example.flowers";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}