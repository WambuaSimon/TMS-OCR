package com.wizag.ocrproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wizag.ocrproject.pojo.Worker;

import java.util.ArrayList;
import java.util.List;

import static com.wizag.ocrproject.pojo.Worker.TABLE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "worker_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Worker.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }


    public long insertWorker(Worker worker) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Worker.COLUMN_NAME, worker.getName());
        values.put(Worker.COLUMN_ID_NO, worker.getId_no());
        values.put(Worker.COLUMN_LOCATION, worker.getLocation());
        values.put(Worker.COLUMN_TIME_IN, worker.getTime_in());
        values.put(Worker.COLUMN_TIME_OUT, worker.getTime_out());
        values.put(Worker.COLUMN_IMAGE, worker.getImage());


        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Worker getWorker(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{Worker.COLUMN_ID, Worker.COLUMN_NAME, Worker.COLUMN_ID_NO, Worker.COLUMN_LOCATION,
                        Worker.COLUMN_TIME_IN, Worker.COLUMN_TIME_OUT, Worker.COLUMN_IMAGE},
                Worker.COLUMN_ID_NO + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Worker worker = new Worker(
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID_NO)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME_IN)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME_OUT)),
                cursor.getBlob(cursor.getColumnIndex(Worker.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();

        return worker;
    }


    public List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();

        /*nb: to display image, convert it to bitmap [then to base 64 to send it to the server]*/

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                Worker.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Worker worker = new Worker();
                worker.setId(cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID)));
                worker.setId_no(cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID_NO)));
                worker.setName(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_NAME)));
                worker.setLocation(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)));
                worker.setTime_in(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME_IN)));
                worker.setTime_in(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME_OUT)));
                worker.setImage(cursor.getBlob(cursor.getColumnIndex(Worker.COLUMN_IMAGE)));

                workers.add(worker);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return workers;
    }


    public boolean rowIdExists(String idNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id_no from " + TABLE_NAME
                + " where id_no=?", new String[]{idNo});
        boolean exists = (cursor.getCount() > 0);
    /*cursor.close();
    db.close();*/
        return exists;
    }


    public int getWorkersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    boolean isAdded(int id_no) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + Worker.COLUMN_ID_NO + " =? ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public int updateWorker(Worker worker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Worker.COLUMN_TIME_OUT, worker.getTime_out());

        // updating row
        return db.update(Worker.TABLE_NAME, values, Worker.COLUMN_ID_NO + " = ?",
                new String[]{String.valueOf(worker.getId_no())});
    }

    public void deleteNote(Worker worker) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Worker.COLUMN_ID + " = ?",
                new String[]{String.valueOf(worker.getId())});
        db.close();
    }


}