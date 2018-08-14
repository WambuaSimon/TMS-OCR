package com.wizag.ocrproject.pojo;

public class Worker {
    public static final String TABLE_NAME = "worker";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "f_name";
    public static final String COLUMN_LAST_NAME = "l_name";
    public static final String COLUMN_ID_NO = "id_no";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TIME_IN = "time_in";
    public static final String COLUMN_DATE_IN = "date_in";
    public static final String COLUMN_DATE_OUT = "date_out";
    public static final String COLUMN_TIME_OUT = "time_out";
    public static final String COLUMN_SITE = "site";
    public static final String COLUMN_WAGE = "wage";
    public static final String COLUMN_IMAGE = "image";


    private int id, id_no;
    private String f_name, l_name, location, time_in, time_out, date_in, date_out, site, wage;
    private byte[] image;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID_NO + " TEXT,"
                    + COLUMN_FIRST_NAME + " TEXT,"
                    + COLUMN_LAST_NAME + " TEXT,"
                    + COLUMN_LOCATION + " TEXT,"
                    + COLUMN_TIME_IN + " TEXT,"
                    + COLUMN_TIME_OUT + " TEXT,"
                    + COLUMN_DATE_IN + " TEXT,"
                    + COLUMN_DATE_OUT + " TEXT,"
                    + COLUMN_SITE + " TEXT,"
                    + COLUMN_WAGE + " TEXT,"
                    + COLUMN_IMAGE + " BLOB"
                    + ")";

    public Worker() {
    }

    public Worker(int id, int id_no, String f_name, String l_name, String location, String time_in, String time_out, String date_in, String date_out, String site, String wage, byte[] image) {
        this.id = id;
        this.id_no = id_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time_in = time_in;
        this.time_out = time_out;
        this.date_in = date_in;
        this.date_out = date_out;
        this.site = site;
        this.wage = wage;
        this.image = image;
    }

    public Worker(int id_no, String f_name, String l_name, String location, String time_in, String time_out, String date_in, String date_out, String site, String wage, byte[] image) {
        this.id_no = id_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time_in = time_in;
        this.time_out = time_out;
        this.date_in = date_in;
        this.date_out = date_out;
        this.site = site;
        this.wage = wage;
        this.image = image;
    }

    public Worker(int id_no, String f_name, String l_name, String location, String time_in, String date_in, String site, String wage, byte[] image) {
        this.id_no = id_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time_in = time_in;
        this.date_in = date_in;
        this.site = site;
        this.wage = wage;
        this.image = image;
    }

    public Worker(int id_no, String f_name, String l_name, String location, String time_in, String date_in, String site, byte[] image) {
        this.id_no = id_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time_in = time_in;
        this.date_in = date_in;
        this.site = site;
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

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
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

    public String getDate_in() {
        return date_in;
    }

    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    public String getDate_out() {
        return date_out;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getWage() {
        return wage;
    }

    public void setWage(String wage) {
        this.wage = wage;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
