package com.phuchaihuynh.sdnextbus.realgtfs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BusStopsDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "BusStopsDatabaseHelper";

    private static final int DATABASE_VERSION = 1;

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.phuchaihuynh.sdnextbus.app/databases/";
    private static String DB_NAME = "SanDiegoBusStops.sqlite";
    private static String MY_DATABASE = DB_PATH + DB_NAME;
    private final Context myContext;

    private static final String TABLE_FAVORITE = "favorites";
    private static final String KEY_ID = "_id";
    private static final String KEY_BUS_STOP_ID = "bus_stop_id";
    private static final String KEY_TROLLEY_STOP_ID = "trolley_stop_id";
    private static final String CREATE_TABLE_FAVORITE = "CREATE TABLE "
            + TABLE_FAVORITE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_BUS_STOP_ID + " INTEGER,"
            + KEY_TROLLEY_STOP_ID + " INTEGER" + ")";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     */
    public BusStopsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
            Log.d(TAG, "THE DATABASE EXISTS");
        }
        else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            try {
                copyDataBase();
                db.execSQL(CREATE_TABLE_FAVORITE);
                db.close();
            }
            catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB;
        try{
            checkDB = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e){
            //database does't exist yet.
            Log.d(TAG, "THE DATABASE DOES NOT EXIST");
            return false;
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        Log.d(TAG, "COPYING DATABASE FROM ASSETS FOLDER");
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(MY_DATABASE);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READWRITE);
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // -------------"Favorites Table Methods"---------------------//
    public long createFavorite(long bus_stop_id, long trolley_stop_id) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put(KEY_BUS_STOP_ID, bus_stop_id);
        values.put(KEY_TROLLEY_STOP_ID, trolley_stop_id);
        long id = db.insert(TABLE_FAVORITE, null, values);
        db.close();
        return id;
    }

    public void deleteFavorite(long id) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READWRITE);
        db.delete(TABLE_FAVORITE, KEY_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }

    public List<Long> getAllFavorites() {
        List<Long> favors = new ArrayList<Long>();
        String selectQuery = "SELECT _id FROM " + TABLE_FAVORITE;
        Log.d(TAG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                favors.add(c.getLong(c.getColumnIndex(KEY_ID)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return favors;
    }

    // -------------"Transports Table Methods"-------------------//
    public List<String> getTransportDirection(String transportTable, String transportRoute) {
        List<String> directions = new ArrayList<String>();
        String[] columns = {"trip_headsign"};
        String selection = "route = " + "'" + transportRoute + "'";
        String query = SQLiteQueryBuilder.buildQueryString(true, transportTable, columns, selection, null, null, null, null);
        Log.d(TAG, "Get transport direction query: " + query);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery(query, null, null);
        if (c.moveToFirst()) {
            do {
                directions.add(c.getString(c.getColumnIndex("trip_headsign")));
            }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        return directions;
    }

    public List<String> getTransportStops(String transportTable, String transportRoute, String transportDirection) {
        List<String> stops = new ArrayList<String>();
        String[] columns = {"stop_name"};
        String selection = "route = " + "'" + transportRoute + "' and trip_headsign = " + "'" + transportDirection + "'";
        String query = SQLiteQueryBuilder.buildQueryString(false,transportTable,columns,selection,null,null,null,null);
        Log.d(TAG, "Get transport stops query: " + query);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery(query, null, null);
        if (c.moveToFirst()) {
            do {
                stops.add(c.getString(c.getColumnIndex("stop_name")));
            }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        return stops;
    }

    public String getTransportStopId(String transportTable, String transportRoute, String transportDirection, String transportStop) {
        String[] columes = {"stop_id"};
        String selection = "route = " + "'" + transportRoute
                + "' and trip_headsign = " + "'" + transportDirection
                + "' and stop_name = " + "'" + transportStop + "'";
        String query = SQLiteQueryBuilder.buildQueryString(false,transportTable,columes,selection,null,null,null,null);
        Log.d(TAG, "Get transport stop id: " + query);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery(query, null, null);
        String stop_id = "";
        if (c.moveToFirst()) {
            stop_id = c.getString(c.getColumnIndex("stop_id"));
        }
        c.close();
        db.close();
        return stop_id;
    }

    public long getId(String transportTable, String stop_id) {
        String[] columes = {"_id"};
        String selection = "stop_id = " + "'" + stop_id + "'";
        String query = SQLiteQueryBuilder.buildQueryString(false,transportTable,columes,selection,null,null,null,null);
        Log.d(TAG, "Get transport row id: " + query);
        long id = -1;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MY_DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery(query, null, null);
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(KEY_ID));
        }
        c.close();
        db.close();
        return id;
    }
}
