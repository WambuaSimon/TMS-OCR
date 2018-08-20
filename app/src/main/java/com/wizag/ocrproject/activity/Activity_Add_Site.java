package com.wizag.ocrproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.helper.GPSLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Site extends AppCompatActivity {
    EditText description, site;
    Button checkin;
    String site_txt, description_txt;
    String Post_Material = "http://timetrax.wizag.biz/api/v1/sitesapi";
    GPSLocation gps;
    String latitude_txt, longitude_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);

        gps = new GPSLocation(this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            latitude_txt = String.valueOf(latitude);
            longitude_txt = String.valueOf(longitude);
//            location = latitude + "," + longitude;
//            Toast.makeText(getApplicationContext(), "" + location, Toast.LENGTH_SHORT).show();

        } else {
            gps.showSettingsAlert();
        }
        site = findViewById(R.id.site);
        description = findViewById(R.id.description);

        checkin = findViewById(R.id.checkin);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                site_txt = site.getText().toString();
                description_txt = description.getText().toString();

                if (validateSiteName(site_txt)) {
                    /*post site to db*/

                    AddSite();
                }
            }
        });

    }

    private boolean validateSiteName(String site_name) {
        if (site_name.length() == 0) {
            site.requestFocus();
            site.setError("Site name cannot be empty");
            return false;
        }
        return true;
    }

    public void AddSite() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Add_Site.this);
        final ProgressDialog pDialog = new ProgressDialog(Activity_Add_Site.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
//        pDialog.setIndeterminate(false);
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Post_Material,
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
//                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_LONG).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(Activity_Add_Site.this, "An Error Occurred", Toast.LENGTH_LONG).show();
                finish();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("site", site_txt);
                params.put("description", description_txt);
                params.put("lt", latitude_txt);
                params.put("ld", longitude_txt);

                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
