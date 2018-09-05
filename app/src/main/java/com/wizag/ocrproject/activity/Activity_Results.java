package com.wizag.ocrproject.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wizag.ocrproject.BuildConfig;
import com.wizag.ocrproject.database.DatabaseHelper;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.network.MySingleton;
import com.wizag.ocrproject.pojo.Worker;
import com.wizag.ocrproject.adapter.WorkerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Results extends AppCompatActivity {
    TextView name;
    TextView time_in, date_in;
    TextView id_no;
    Button confirm, discard;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ImageView id_image;
    GPSLocation gps;
    String location;
    private WorkerAdapter mAdapter;
    private List<Worker> workersList = new ArrayList<>();
    protected Context context;
    DatabaseHelper db;
    Bitmap photo, bitmap;
    byte[] id_photo, image_from_db;
    String scanned_id_no;
    String date_str, date, time;
    private static final String SHARED_PREF_SITE = "site_name";
    String scanned_name;
    int scanned_id_to_int;
    String current_location;
    String site;
    String existing_date;
    String existing_date_txt;
    String[] names;
    String f_name, l_name;
    String checkIn_URL = "http://timetrax.wizag.biz/api/v1/checkin_employee";
    String checkout_URL = "http://timetrax.wizag.biz/api/v1/checkout_employee";
    String f_name_remote, l_name_remote;
    int id_no_remote;
    //    ProgressDialog pDialog;
    int flag_checkin = 1;
    int flag_checkout = 0;
    int flag;
    SharedPreferences prefs;
    String encoded_image;
    int site_id;
    String wage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        db = new DatabaseHelper(this);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        date_str = df.format(cal.getTime());
        String[] divide = date_str.split("\\s");
        date = divide[0]; //2017-03-08
        time = divide[1]; // 13:27:00


        mAdapter = new WorkerAdapter(this, workersList);

        name = findViewById(R.id.name);
        time_in = findViewById(R.id.time_in);
        date_in = findViewById(R.id.date_in);
        id_no = findViewById(R.id.id_no);
        id_image = findViewById(R.id.id_image);

        confirm = (Button) findViewById(R.id.submit);
        discard = (Button) findViewById(R.id.discard);


        /*get scanned data*/
        Intent intent = new Intent();
        scanned_name = getIntent().getStringExtra("Name");
        scanned_id_no = getIntent().getStringExtra("Id");
        current_location = getIntent().getStringExtra("Location");

        names = scanned_name.split(" ", 2);
        f_name = names[0];
        l_name = names[1];


        name.setText(scanned_name);
        id_no.setText(scanned_id_no);
        time_in.setText(time);
        date_in.setText(date);

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        site_id = prefs.getInt("site_id", 0);


//        Toast.makeText(getApplicationContext(), "" + site_id, Toast.LENGTH_SHORT).show();
//        Worker existing_worker_new_image = db.getOnlyWorker(Long.parseLong(scanned_id_no));

        if (db.rowIdExists(scanned_id_no)) {
            Worker existing_worker_new = db.getOnlyWorker(Long.parseLong(scanned_id_no));
            image_from_db = existing_worker_new.getImage();
            wage = existing_worker_new.getWage();



            ByteArrayInputStream inputStream = new ByteArrayInputStream(image_from_db);
            Bitmap bitmap_image = BitmapFactory.decodeStream(inputStream);
            id_image.setImageBitmap(bitmap_image);


//            Toast.makeText(getApplicationContext(), ""+wage, Toast.LENGTH_SHORT).show();

        }



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanned_id_to_int = Integer.parseInt(scanned_id_no);






                bitmap = ((BitmapDrawable) id_image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                id_photo = byteArrayOutputStream.toByteArray();

                encoded_image = Base64.encodeToString(id_photo, Base64.DEFAULT);

                if (!db.rowIdExists(scanned_id_no)) {

                    searchWorker();
//                    checkinUser();
                   /* startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
                    finish();*/


                } else if (db.rowIdExists(scanned_id_no)) {


                    Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));

                    existing_date_txt = existing_worker.getDate();
                    flag = existing_worker.getFlag();


//
                    String fname = existing_worker.getF_name();
                    String lname = existing_worker.getL_name();

//                    Toast.makeText(getApplicationContext(), ""+wage, Toast.LENGTH_SHORT).show();


                    if (flag == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Results.this);
                        builder.setTitle("Confirm Action").setMessage("Would you like to check out\n\n" + scanned_name + "?").setCancelable(false)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        createEmployee(scanned_id_to_int, f_name, l_name, current_location, time, date, site_id, id_photo, flag_checkout);
                                        checkoutUser();

                                       /* Toast.makeText(getApplicationContext(), "user checked out successfully", Toast.LENGTH_SHORT).show();
                                        finish();*/
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        builder.show();

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Results.this);
                        builder.setTitle("Confirm Action").setMessage("Would you like to check in\n\n" + scanned_name + "?").setCancelable(false)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        createEmployee(scanned_id_to_int, f_name, l_name, current_location, time, date, site_id, id_photo, flag_checkin);
                                        checkinUser();
                                      /*  Toast.makeText(getApplicationContext(), "user checked in successfully", Toast.LENGTH_SHORT).show();
                                        finish();*/
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                        builder.show();
                    }

                }


            }
        });

    }


    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");

        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showMenu(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }


    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


   /* public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
          return outputStream.toByteArray();
    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");

            id_image.setImageBitmap(photo);
        }
    }

    private void createEmployee(int id_no, String f_name, String l_name, String location, String time, String date, int site, byte[] image, int flag)

    {
        long id = db.insertWorker(new Worker(id_no, flag, f_name, l_name, location, time, date, site, image));
    }


    private void searchWorker() {
        isNetworkConnectionAvailable();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://timetrax.wizag.biz/api/v1/check_employee/" + scanned_id_no, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    pDialog.dismiss();
                    if (jsonObject != null) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String exists = data.getString("exists");
                        if (exists.equalsIgnoreCase("true")) {

                            JSONObject employee = data.getJSONObject("employee");

                            f_name_remote = employee.getString("first_name");
                            l_name_remote = employee.getString("last_name");
                            id_no_remote = employee.getInt("id_number");

                            Toast.makeText(getApplicationContext(), "Employee Found and added to local database", Toast.LENGTH_SHORT).show();
                            /*save them to local db*/
                            createEmployee(id_no_remote, f_name_remote, l_name_remote, current_location, time, date, site_id, id_photo, flag_checkin);

                            checkinUser();
                            /*send their details to server*/


                        } else if (exists.equalsIgnoreCase("false")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Results.this);
                            builder.setTitle("Add Employee").setMessage("Employee:\t" + scanned_name + "\tdoes not exist in the system, please add them").setCancelable(false)
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(getApplicationContext(), Activity_New_Staff.class));
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });

                            builder.show();


//                            Toast.makeText(getApplicationContext(), "User Does not exist in the system", Toast.LENGTH_LONG).show();

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_LONG).show();
                pDialog.hide();
            }


        });


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }

    public void checkinUser() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Results.this);
        final ProgressDialog pDialog = new ProgressDialog(Activity_Results.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
//        pDialog.setIndeterminate(false);
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, checkIn_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();
                            JSONObject data = jsonObject.getJSONObject("data");
                            String success_message = data.getString("message");
                            // Snackbar.make(sell_layout, "New Request Created Successfully" , Snackbar.LENGTH_LONG).show();
                            //Snackbar.make(sell_layout, "New request created successfully", Snackbar.LENGTH_LONG).show();
//                            createEmployee(id_no_remote, f_name_remote, l_name_remote, current_location, time, date, site, id_photo, flag_checkin);
                            Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_LONG).show();
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_LONG).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(Activity_Results.this, "An Error Occurred", Toast.LENGTH_LONG).show();
//                finish();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_no", scanned_id_no);
                params.put("time_in", time);
                params.put("date_in", date);
                params.put("site_id", String.valueOf(site_id));

                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }*/

    /*@Override
    protected void onPause() {
        super.onPause();
        pDialog.dismiss();
    }*/

    public void checkoutUser() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Results.this);
        final ProgressDialog pDialog = new ProgressDialog(Activity_Results.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
//        pDialog.setIndeterminate(false);
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, checkout_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();
                            JSONObject data = jsonObject.getJSONObject("data");
                            String success_message = data.getString("message");
//                            createEmployee(id_no_remote, f_name_remote, l_name_remote, current_location, time, date, site, id_photo, flag_checkout);
//
                            // Snackbar.make(sell_layout, "New Request Created Successfully" , Snackbar.LENGTH_LONG).show();
                            //Snackbar.make(sell_layout, "New request created successfully", Snackbar.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_LONG).show();
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_LONG).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(Activity_Results.this, "An Error Occurred", Toast.LENGTH_LONG).show();
//                finish();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_no", scanned_id_no);
                params.put("time_out", time);
                params.put("date_out", date);
                params.put("site_id", String.valueOf(site_id));

                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
