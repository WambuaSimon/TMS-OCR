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
import com.wizag.ocrproject.database.DatabaseHelper;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.network.MySingleton;
import com.wizag.ocrproject.pojo.Worker;
import com.wizag.ocrproject.adapter.WorkerAdapter;

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
    byte[] id_photo;
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
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        db = new DatabaseHelper(this);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date_str = df.format(cal.getTime());
        String[] divide = date_str.split("\\s");
        date = divide[0]; //2017-03-08
        time = divide[1]; // 13:27:00

//        Toast.makeText(getApplicationContext(), "" + date, Toast.LENGTH_SHORT).show();

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

//        Toast.makeText(getApplicationContext(), "\n" + l_name, Toast.LENGTH_SHORT).show();


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


        SharedPreferences sp = getSharedPreferences("site_name", MODE_PRIVATE);
        site = sp.getString("site_id", "");

//        Toast.makeText(getApplicationContext(), ""+site, Toast.LENGTH_SHORT).show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanned_id_to_int = Integer.parseInt(scanned_id_no);




                /*convert image bitmap to byte[]*/
                bitmap = ((BitmapDrawable) id_image.getDrawable()).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                id_photo = outputStream.toByteArray();


//                Toast.makeText(getApplicationContext(), "User Exists", Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "Name: " + existing_name + " No: " + existing_no, Toast.LENGTH_SHORT).show();

                if (!db.rowIdExists(scanned_id_no)) {
//                     search user in remote db and add to local db
                    searchWorker();

//                    createWorker(scanned_name, scanned_id_to_int, current_location, time, date, site, id_photo);
//                    Toast.makeText(getApplicationContext(), "User not found,search in remote server", Toast.LENGTH_SHORT).show();

                } else {
                   compareDates();
                }
                /*same dates, check out*/
              /*  else if (db.rowIdExists(scanned_id_no) && compareDates()) {


                    //  Toast.makeText(getApplicationContext(), "Check out user", Toast.LENGTH_SHORT).show();

//                    checkOutUserDialog();

                }


//                different dates, check in
                else if (db.rowIdExists(scanned_id_no) && compareUnequalDates()) {
//                    checkinUserDialog();
                    //  Toast.makeText(getApplicationContext(), "Check in user", Toast.LENGTH_SHORT).show();

                }*/


            }
        });
    }

    private void compareDates() {

        Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));
        existing_date_txt = existing_worker.getDate_in();
        int id_no_txt = existing_worker.getId_no();
        if (existing_date_txt.equalsIgnoreCase(date)) {

            checkOutUserDialog();

        } else if (!existing_date_txt.equalsIgnoreCase(date)) {
            checkinUserDialog();
        }


    }

    private boolean compareUnequalDates() {

        Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));
        existing_date_txt = existing_worker.getDate_in();
        int id_no_txt = existing_worker.getId_no();
        if (!existing_date_txt.equalsIgnoreCase(date)) {
            return true;
        } else {
            return false;
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

    private void createWorker(String f_name, String l_name, int id_no, String location, String time_in, String date_in, String site, byte[] id_photo) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertWorker(new Worker(id_no, f_name, l_name, location, time_in, date_in, site, id_photo));

        // get the newly inserted note from db
       /* Worker n = db.getWorker(id);

        if (n != null) {
            // adding new note to array list at 0 position
            workersList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

        }*/
    }

    public void checkOutUserDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.time_out_layout, null);
        dialogBuilder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.time_out_name);
        final TextView id_no = dialogView.findViewById(R.id.time_out_id_no);
        final TextView time_out = dialogView.findViewById(R.id.time_out);
        final TextView date_out = dialogView.findViewById(R.id.date_out);

        dialogBuilder.setTitle("Check Out Staff");
        final String time_out_txt = time_out.getText().toString();
        final String date_out_txt = date_out.getText().toString();

        Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));
        int existing_no = existing_worker.getId_no();
        String existing_fname = existing_worker.getF_name();
        String existing_lname = existing_worker.getL_name();
        existing_date = existing_worker.getDate_in();


        name.setText(existing_fname + existing_lname);
        id_no.setText(String.valueOf(existing_no));
        time_out.setText(time);
        date_out.setText(date);
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /*confirm column*/
                updateNote(time_out_txt, date_out_txt);
                checkoutUser();
                Toast.makeText(Activity_Results.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
                finish();

            }
        });
        dialogBuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        dialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void updateNote(String time_out, String date_out) {
        Worker worker = new Worker();
        // updating note text
        worker.setTime_out(time_out);
        worker.setDate_out(date_out);

        // updating note in db
        db.updateWorker(worker);

    }


    public void checkinUserDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.time_in_user, null);
        dialogBuilder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.time_in_name);
        final TextView id_no = dialogView.findViewById(R.id.time_in_id_no);
        final TextView time_out = dialogView.findViewById(R.id.time_in_txt);
        final TextView date_out = dialogView.findViewById(R.id.date_in_txt);

        dialogBuilder.setTitle("Check in Staff");
        /*final String time_out_txt = time_out.getText().toString();
        final String date_out_txt = date_out.getText().toString();*/

        name.setText(scanned_name);
        id_no.setText(scanned_id_no);
        time_out.setText(time);
        date_out.setText(date);

        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /*confirm column*/
                createWorker(f_name, l_name, scanned_id_to_int, current_location, time, date, site, id_photo);
                checkinUser();
                startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
//                Toast.makeText(getApplicationContext(), "User Checked in Successfully", Toast.LENGTH_SHORT).show();

                finish();


            }
        });
        dialogBuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        dialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void searchWorker() {
        isNetworkConnectionAvailable();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
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

                            createWorker(f_name_remote, l_name_remote, id_no_remote, current_location, time, date, site, id_photo);
                            Toast.makeText(getApplicationContext(), "User found and Added to local database", Toast.LENGTH_LONG).show();
                            checkinUser();


                            startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
                            finish();

                        } else if (exists.equalsIgnoreCase("false")) {
                            Toast.makeText(getApplicationContext(), "User Does not exist in the system", Toast.LENGTH_LONG).show();

                        }
                        /*JSONArray user = data.getJSONArray("user");

                        if (user != null) {
                            for (int i = 0; i < user.length(); i++) {

                                JSONObject available_users = user.getJSONObject(i);
                                String user_id = available_users.getString("id");


                                *//*save user in local db*//*


                            }
                        } else {

                            *//*worker not found, prompt adding new worker*//*


                        }
*/

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
        pDialog = new ProgressDialog(Activity_Results.this);
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
                finish();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_no", scanned_id_no);
                params.put("time_in", time);
                params.put("date_in", date);
                params.put("site_id", site);

                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void checkoutUser() {

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
                            // Snackbar.make(sell_layout, "New Request Created Successfully" , Snackbar.LENGTH_LONG).show();
                            //Snackbar.make(sell_layout, "New request created successfully", Snackbar.LENGTH_LONG).show();

                            Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Activity_Results.this, "An Error Occurred", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_no", scanned_id_no);
                params.put("time_out", time);
                params.put("date_out", date);
                params.put("site_id", site);

                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    /*@Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }*/
}
