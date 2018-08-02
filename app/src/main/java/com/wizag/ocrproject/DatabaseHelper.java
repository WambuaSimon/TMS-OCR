package com.wizag.ocrproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


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
        db.execSQL("DROP TABLE IF EXISTS " + Worker.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertWorker(String name,int id_no, String location, String time_out) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Worker.COLUMN_NAME, name);
        values.put(Worker.COLUMN_ID_NO, id_no);
        values.put(Worker.COLUMN_LOCATION, location);
        values.put(Worker.COLUMN_TIME_IN, time_out);


        // insert row
        long id = db.insert(Worker.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Worker getWorker(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Worker.TABLE_NAME,
                new String[]{Worker.COLUMN_ID, Worker.COLUMN_NAME, Worker.COLUMN_ID_NO, Worker.COLUMN_LOCATION,
                        Worker.COLUMN_TIME_IN},
                Worker.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Worker worker = new Worker(
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID_NO)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME_IN)));

        // close the db connection
        cursor.close();

        return worker;
    }

    public List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Worker.TABLE_NAME + " ORDER BY " +
                Worker.COLUMN_TIME_IN + " DESC";

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

                workers.add(worker);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return workers;
    }

    public int getWorkersCount() {
        String countQuery = "SELECT  * FROM " + Worker.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

   /* public int updateNote(Worker worker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Worker.COLUMN_NOTE, worker.getName());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }*/

    public void deleteNote(Worker worker) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Worker.TABLE_NAME, Worker.COLUMN_ID + " = ?",
                new String[]{String.valueOf(worker.getId())});
        db.close();
    }
}