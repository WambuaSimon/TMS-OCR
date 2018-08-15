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
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.database.DatabaseHelper;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.network.MySingleton;
import com.wizag.ocrproject.pojo.SpinnerModel;
import com.wizag.ocrproject.pojo.Worker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_New_User_Results extends AppCompatActivity {

    Button cancel, save;

    TextView name, id_no, dob, reg_time, reg_date;
    ImageView id_image;
    Spinner site;
    EditText wage;
    String scanned_name, scanned_id_no, current_location, date_str, date, time, dob_txt;
    DatabaseHelper db;
    Bitmap photo, bitmap;
    byte[] id_photo;
    int scanned_id_to_int;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String wage_txt;
    private List<SpinnerModel> workersList = new ArrayList<>();
    ArrayList<String> worker;
    ArrayList<String> SiteName;
    //    String URL = "http://timetrax.wizag.biz/api/v1/sitesapi";
    String POST_MATERIAL = "http://timetrax.wizag.biz/api/v1/register_employee";
    String names[], f_name, l_name;
    String worker_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_results);


//        check connectivity

        isNetworkConnectionAvailable();


//        loadSpinnerData(URL);

        worker = new ArrayList<>();
        SiteName = new ArrayList<>();

        db = new DatabaseHelper(this);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date_str = df.format(cal.getTime());
        String[] divide = date_str.split("\\s");
        date = divide[0]; //2017-03-08
        time = divide[1];

        scanned_name = getIntent().getStringExtra("Name");
        scanned_id_no = getIntent().getStringExtra("Id");
        current_location = getIntent().getStringExtra("Location");
        dob_txt = getIntent().getStringExtra("Dob");
        /*split names*/
        names = scanned_name.split(" ", 2);
        f_name = names[0];
        l_name = names[1];

        scanned_id_to_int = Integer.parseInt(scanned_id_no);
        name = findViewById(R.id.name);
        id_no = findViewById(R.id.id_no);
        id_image = findViewById(R.id.id_image);

        reg_time = findViewById(R.id.reg_time);
        reg_date = findViewById(R.id.reg_date);


//        site = findViewById(R.id.site);
        wage = findViewById(R.id.wage);

        name.setText(scanned_name);

        id_no.setText(scanned_id_no);
        reg_date.setText(date);
        reg_time.setText(time);


        cancel = (Button) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*save to both remote and local db*/
                wage_txt = wage.getText().toString();
//                String site_name = site.getSelectedItem().toString();

                if (wage_txt.isEmpty()) {
                    Toast.makeText(Activity_New_User_Results.this, "Enter Wage Amount in KSH. to continue", Toast.LENGTH_LONG).show();
                } else if (id_image.getDrawable() == null) {
                    Toast.makeText(Activity_New_User_Results.this, "Capture Image of User to continue", Toast.LENGTH_LONG).show();

                } else {
                    registerUser();
                    createWorker(scanned_id_to_int, scanned_name, current_location, time, date, wage_txt, dob_txt, id_photo);
//                    Toast.makeText(Activity_New_User_Results.this, "User Created", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void createWorker(int id_no, String name, String location, String time_in, String date_in, String wage, String dob, byte[] id_photo) {

        long id = db.insertWorker(new Worker(id_no, name, location, time_in, date_in, wage, dob, id_photo));


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
           /* photo = (Bitmap) data.getExtras().get("data");
            id_image.setImageBitmap(photo);*/

//            bitmap = ((BitmapDrawable) id_image.getDrawable()).getBitmap();
            photo = (Bitmap) data.getExtras().get("data");
            id_image.setImageBitmap(photo);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            id_photo = outputStream.toByteArray();

            /*convert image to base64*/

            worker_image = Base64.encodeToString(id_photo, Base64.DEFAULT);

        }
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

    private void registerUser() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_New_User_Results.this);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_MATERIAL,
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

                            Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_SHORT).show();
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Activity_New_User_Results.this, "An Error Occurred", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("f_name", f_name);
                params.put("l_name", l_name);
                params.put("id_no", scanned_id_no);
                params.put("image", worker_image);
                params.put("wage", wage_txt);
                params.put("location", current_location);
                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }





    /*private void loadSpinnerData(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    pDialog.hide();
                    if (jsonObject != null) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray sites = data.getJSONArray("sites");

                        if (sites != null) {
                            for (int i = 0; i < sites.length(); i++) {

                                JSONObject site_items = sites.getJSONObject(i);

                                SpinnerModel spinnerModel = new SpinnerModel();

                                spinnerModel.setName(site_items.getString("name"));
                                spinnerModel.setId(site_items.getString("id"));

                                workersList.add(spinnerModel);

                               *//* String site_name = site_items.getString("name");
                                String site_id = site_items.getString("id");
*//**//*
                                editor.putString("login_site_id", site_id);
                                editor.apply();
*//*
//                                site_values.put(site_name, site_id);

                                // Toast.makeText(getApplicationContext(), ""+map_values, Toast.LENGTH_SHORT).show();

                                if (site_items != null) {
                                    if (SiteName.contains(sites.getJSONObject(i).getString("name"))) {

                                    } else {


                                        SiteName.add(sites.getJSONObject(i).getString("name"));

                                    }


                                }


                            }
                        }


                    }

                    site
                            .setAdapter(new ArrayAdapter<String>(Activity_New_User_Results.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    SiteName));

                    site.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                            String site_id = workersList.get(position).getId();


//                            Toast.makeText(Activity_Login.this, "" + spinnerModels.get(position).getId(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }

        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }*/


}