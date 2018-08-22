package com.wizag.ocrproject.pojo;

public class Worker {
    public static final String TABLE_NAME = "worker";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "f_name";
    public static final String COLUMN_LAST_NAME = "l_name";
    public static final String COLUMN_ID_NO = "id_no";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SITE = "site";
    public static final String COLUMN_WAGE = "wage";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_FLAG = "flag";


    private int id,id_no, site,flag;
    private String f_name, l_name, location, time, date, wage;
    private byte[] image;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID_NO + " TEXT,"
                    + COLUMN_FIRST_NAME + " TEXT,"
                    + COLUMN_LAST_NAME + " TEXT,"
                    + COLUMN_LOCATION + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_SITE + " TEXT,"
                    + COLUMN_WAGE + " TEXT,"
                    + COLUMN_IMAGE + " BLOB,"
                    + COLUMN_FLAG + " INTEGER"
                    + ")";

    public Worker() {
    }

    public Worker(int id, int id_no, int flag, String f_name, String l_name, String location, String time, String date, int site, String wage, byte[] image) {
        this.id = id;
        this.id_no = id_no;
        this.flag = flag;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time = time;
        this.date = date;
        this.site = site;
        this.wage = wage;
        this.image = image;
    }

    public Worker(int id_no, int flag, String f_name, String l_name, String location, String time, String date, int site, byte[] image) {
        this.id_no = id_no;
        this.flag = flag;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time = time;
        this.date = date;
        this.site = site;
        this.image = image;
    }

    public Worker(int id_no, int flag, String f_name, String l_name, String location, String time, String date, int site, String wage, byte[] image) {
        this.id_no = id_no;
        this.flag = flag;
        this.f_name = f_name;
        this.l_name = l_name;
        this.location = location;
        this.time = time;
        this.date = date;
        this.site = site;
        this.wage = wage;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
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
