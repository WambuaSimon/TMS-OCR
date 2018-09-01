package com.wizag.ocrproject.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wizag.ocrproject.pojo.Worker;

import java.util.ArrayList;
import java.util.List;

import static com.wizag.ocrproject.pojo.Worker.COLUMN_ID;
import static com.wizag.ocrproject.pojo.Worker.COLUMN_LAST_NAME;
import static com.wizag.ocrproject.pojo.Worker.TABLE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "employee_db";


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
        values.put(Worker.COLUMN_FIRST_NAME, worker.getF_name());
        values.put(Worker.COLUMN_LAST_NAME, worker.getL_name());
        values.put(Worker.COLUMN_ID_NO, worker.getId_no());
        values.put(Worker.COLUMN_LOCATION, worker.getLocation());
        values.put(Worker.COLUMN_TIME, worker.getTime());
        values.put(Worker.COLUMN_DATE, worker.getDate());
        values.put(Worker.COLUMN_SITE, worker.getSite());
        values.put(Worker.COLUMN_WAGE, worker.getWage());
        values.put(Worker.COLUMN_IMAGE, worker.getImage());
        values.put(Worker.COLUMN_FLAG, worker.getFlag());


        // insert row
        long id = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public Worker getWorker(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{Worker.COLUMN_ID, Worker.COLUMN_FIRST_NAME, COLUMN_LAST_NAME, Worker.COLUMN_ID_NO, Worker.COLUMN_LOCATION,
                        Worker.COLUMN_TIME, Worker.COLUMN_DATE, Worker.COLUMN_SITE, Worker.COLUMN_WAGE, Worker.COLUMN_IMAGE, Worker.COLUMN_FLAG},
                Worker.COLUMN_ID_NO + "=?"+ " order by " + COLUMN_ID + " DESC limit 1",
                new String[]{String.valueOf(id)}, null, null, null, null);

       /* String query = "SELECT id_no from worker order by id DESC limit 1";
        Cursor cursor = db.rawQuery(query,new String[]{String.valueOf(id)});
*/
        if (cursor != null)
            cursor.moveToFirst();


        // prepare note object
        Worker worker = new Worker(
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID_NO)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_FLAG)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LAST_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_SITE)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_WAGE)),
                cursor.getBlob(cursor.getColumnIndex(Worker.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();

        return worker;
    }


    public Worker getOnlyWorker(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{Worker.COLUMN_ID, Worker.COLUMN_FIRST_NAME, COLUMN_LAST_NAME, Worker.COLUMN_ID_NO, Worker.COLUMN_LOCATION,
                        Worker.COLUMN_TIME, Worker.COLUMN_DATE, Worker.COLUMN_SITE, Worker.COLUMN_WAGE, Worker.COLUMN_IMAGE, Worker.COLUMN_FLAG},
                Worker.COLUMN_ID_NO + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

       /* String query = "SELECT id_no from worker order by id DESC limit 1";
        Cursor cursor = db.rawQuery(query,new String[]{String.valueOf(id)});
*/
        if (cursor != null)
            cursor.moveToFirst();


        // prepare note object
        Worker worker = new Worker(
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_ID_NO)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_FLAG)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LAST_NAME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_SITE)),
                cursor.getString(cursor.getColumnIndex(Worker.COLUMN_WAGE)),
                cursor.getBlob(cursor.getColumnIndex(Worker.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();

        return worker;
    }


    public List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();

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
                worker.setF_name(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_FIRST_NAME)));
                worker.setL_name(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LAST_NAME)));
                worker.setLocation(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_LOCATION)));
                worker.setTime(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_TIME)));
                worker.setDate(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_DATE)));
                worker.setSite(cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_SITE)));
                worker.setWage(cursor.getString(cursor.getColumnIndex(Worker.COLUMN_WAGE)));
                worker.setImage(cursor.getBlob(cursor.getColumnIndex(Worker.COLUMN_IMAGE)));
                worker.setFlag(cursor.getInt(cursor.getColumnIndex(Worker.COLUMN_FLAG)));

                workers.add(worker);


            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return workers;
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


    public void deleteWorker(Worker note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Worker.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
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
}