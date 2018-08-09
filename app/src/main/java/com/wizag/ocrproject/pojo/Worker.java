package com.wizag.ocrproject;

public class Worker {
    public static final String TABLE_NAME = "worker";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ID_NO = "id_no";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TIME_IN = "time_in";
    public static final String COLUMN_TIME_OUT = "time_out";
    public static final String COLUMN_IMAGE = "image";


    private int id,id_no;
    private String name,location,time_in,time_out;
    private byte[] image;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_LOCATION + " TEXT,"
                    + COLUMN_TIME_IN + " TEXT,"
                    + COLUMN_TIME_OUT + " TEXT,"
                    + COLUMN_ID_NO + " TEXT,"
                    + COLUMN_IMAGE + " BLOB"
                    + ")";

    public Worker() {
    }

    public Worker(int id, int id_no, String name, String location, String time_in, String time_out, byte[] image) {
        this.id = id;
        this.id_no = id_no;
        this.name = name;
        this.location = location;
        this.time_in = time_in;
        this.time_out = time_out;
        this.image = image;
    }


    public Worker(int id_no, String name, String location, String time_in, String time_out, byte[] image) {
        this.id_no = id_no;
        this.name = name;
        this.location = location;
        this.time_in = time_in;
        this.time_out = time_out;
        this.image = image;
    }

    public Worker(int id_no, String name, String location, String time_in, byte[] image) {
        this.id_no = id_no;
        this.name = name;
        this.location = location;
        this.time_in = time_in;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_no() {
        return id_no;
    }

    public void setId_no(int id_no) {
        this.id_no = id_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
