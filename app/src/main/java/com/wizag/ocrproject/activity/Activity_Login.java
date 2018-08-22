package com.wizag.ocrproject.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wizag.ocrproject.BuildConfig;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.helper.PrefKeys;
import com.wizag.ocrproject.helper.PrefUtils;
import com.wizag.ocrproject.network.ApiInterface;
import com.wizag.ocrproject.pojo.AuthUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.wizag.ocrproject.network.APIClient.BASE_URL;

public class Activity_Login extends AppCompatActivity {

    String Sites_Url = "http://timetrax.wizag.biz/api/v1/sitesapi";
    String search_sites_url = "http://timetrax.wizag.biz/api/v1/get_distance";
    Button checkin;
    TextView site, description;
    String site_lt, site_ld, site_name, site_description;
    EditText username_txt, password_txt;
    String login_username, login_password;
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
    String access_token, refresh_token, token_type;
    //String username = "admin@admin.com";

    SharedPreferences prefs;
    GPSLocation gps;
    //String password = "password";
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkin = findViewById(R.id.checkin);
        site = findViewById(R.id.site);
        description = findViewById(R.id.description);



        gps = new GPSLocation(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            if(latitude == 0.0 && longitude==0.0){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please turn on Location services to continue");
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
//                Toast.makeText(getApplicationContext(), "Ensure GPS is turned on!", Toast.LENGTH_SHORT).show();
            }
//            location = latitude + "," + longitude;
//            Toast.makeText(getApplicationContext(), "" + location, Toast.LENGTH_SHORT).show();

        } else {
            gps.showSettingsAlert();
        }

        isNetworkConnectionAvailable();
//        loadSpinnerData(URL);

        SearchSites();


        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String site_txt = site.getText().toString();

                if (site_txt.matches("")) {
                    Toast.makeText(getApplicationContext(), "No site found", Toast.LENGTH_SHORT).show();

                } else {
                    startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
                    finish();
                }


            }
        });
        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

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


    private void loginToAddSite() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_layout, null);
        username_txt = dialogView.findViewById(R.id.username);
        password_txt = dialogView.findViewById(R.id.password);
        final Button cancel = dialogView.findViewById(R.id.cancel);
        final Button login = dialogView.findViewById(R.id.login);


        builder.setView(dialogView);
        builder.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login_username = username_txt.getText().toString();
                login_password = password_txt.getText().toString();
                if (validateUserName(login_username) && validatePassword(login_password)) {
                    loginUser();
//                    Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        builder.show();


    }

    private void loginUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);
        // Call<AuthUser> call = service.loginUser("admin@cosand.com", "Qwerty123!","password", "2", "GEf81B8TnpPDibW4NKygaatvBG3RmbYSaJf8SZTA");
        Call<AuthUser> call = service.loginUser(login_username, login_password, "password", "2", "76lIuQb2Z8LvrYgYMq8VKc00VHr2G0dmqEMLPH1Y");
        call.enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        AuthUser authUser = response.body();
                        access_token = authUser.getAccessToken();
                        refresh_token = authUser.getRefreshToken();
                        token_type = authUser.getTokenType();

                        prefs.edit().putBoolean("oauth.loggedin", true).apply();
                        prefs.edit().putString(ACCESS_TOKEN, access_token).apply();
                        prefs.edit().putString(REFRESH_TOKEN, refresh_token).apply();
                        prefs.edit().putString(TOKEN_TYPE, token_type).apply();

                        /*redirect to add site activity*/
                        startActivity(new Intent(getApplicationContext(), Activity_Add_Site.class));
                        finish();

                    }

                } else if (response.code() >= 400 && response.code() < 599) {

                    Toast.makeText(Activity_Login.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Activity_Login.this, "Something Went Wrong", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<AuthUser> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Activity_Login.this, "" + fetchErrorMessage(t), Toast.LENGTH_LONG).show();


            }
        });
    }

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnectionAvailable()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }


    private boolean validateUserName(String username) {
        if (username.length() == 0) {
            username_txt.requestFocus();
            username_txt.setError("Username cannot be empty");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() == 0) {
            username_txt.requestFocus();
            username_txt.setError("Password cannot be empty");
            return false;
        }
        return true;
    }


    private void SearchSites() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Login.this);
        final ProgressDialog pDialog = new ProgressDialog(Activity_Login.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
//        pDialog.setIndeterminate(false);
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, search_sites_url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();
                            JSONObject data = jsonObject.getJSONObject("data");
                            String success_message = data.getString("message");

                            if (success_message.equalsIgnoreCase("true")) {

                                JSONObject siteObject = data.getJSONObject("site");
                                String site_name = siteObject.getString("name");
                                String site_description = siteObject.getString("description");
                                int site_id= siteObject.getInt("id");

                                prefs.edit().putInt("site_id", site_id).apply();

                                site.setText(site_name);
                                description.setText(site_description);


//                                Toast.makeText(getApplicationContext(), success_message, Toast.LENGTH_SHORT).show();


                            } else if (success_message.equalsIgnoreCase("false")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                                builder.setTitle("Site does not exist!").setMessage("Login to add site").setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //*show login dialog: if login is successful: show add site dialog*//*
                                                loginToAddSite();


                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User cancelled the dialog
                                            }
                                        });

                                builder.show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Buy.this, "", Toast.LENGTH_LONG).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(Activity_Login.this, "An Error Occurred" + error, Toast.LENGTH_LONG).show();
//                finish();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put("lt", String.valueOf(latitude));
                params.put("ld", String.valueOf(longitude));


                //params.put("code", "blst786");
                //  params.put("")
                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



}