package com.wizag.ocrproject.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wizag.ocrproject.BuildConfig;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.helper.SessionManager;
import com.wizag.ocrproject.network.APIClient;
import com.wizag.ocrproject.network.ApiInterface;
import com.wizag.ocrproject.network.MySingleton;
import com.wizag.ocrproject.pojo.AuthUser;
import com.wizag.ocrproject.pojo.SpinnerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Login extends AppCompatActivity {
    Button login, cancel;
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
    String access_token, refresh_token, token_type;
    //String username = "admin@admin.com";
    String username, password;
    //String password = "password";
    //SessionManager session;

    EditText enter_username, enter_password;
    SharedPreferences prefs;

    Spinner site;
    ArrayList<String> SiteName;
    HashMap<String, String> site_values;
    String id_site;
    String URL = "http://timetrax.wizag.biz/api/v1/sitesapi";
    Button checkin;
    private static final String SHARED_PREF_NAME = "site_name";
    ArrayList<SpinnerModel> spinnerModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        site = findViewById(R.id.site);
        site_values = new HashMap<String, String>();
        checkin = findViewById(R.id.checkin);
        SiteName = new ArrayList<>();
        spinnerModels = new ArrayList<SpinnerModel>();
        isNetworkConnectionAvailable();
        loadSpinnerData(URL);


        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
                finish();
            }
        });

        // Session Manager
       /* session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), Activity_Dashboard.class));
            finish();
        }
        HashMap<String, String> user = session.getUserDetails();
*/

      /*  cancel = (Button) findViewById(R.id.cancel);
        login = (Button) findViewById(R.id.login);


        enter_username = findViewById(R.id.username);
        enter_password = findViewById(R.id.password);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = enter_username.getText().toString();
                password = enter_password.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Activity_Login.this, "Ensure all fields are filled", Toast.LENGTH_LONG).show();
                } else {

                    *//*store user in sqlite*//*
                    loginUser();



                }


            }
        });*/

//        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

  /*  private void loginUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);
        // Call<AuthUser> call = service.loginUser("admin@cosand.com", "Qwerty123!","password", "2", "GEf81B8TnpPDibW4NKygaatvBG3RmbYSaJf8SZTA");
        Call<AuthUser> call = service.loginUser(username, password, "password", "2", "76lIuQb2Z8LvrYgYMq8VKc00VHr2G0dmqEMLPH1Y");
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

                        //  String token = prefs.getString("access_token", ACCESS_TOKEN);

                        //session.createLoginSession(username, password, access_token);

                        //Toast.makeText(FirstActivity.this, "" + access_token, Toast.LENGTH_LONG).show();
                        // Toast.makeText(FirstActivity.this, "" + refresh_token, Toast.LENGTH_LONG).show();
                        // Toast.makeText(FirstActivity.this, "" + token_type, Toast.LENGTH_LONG).show();
                        Intent go_to_menu = new Intent(Activity_Login.this, Activity_Dashboard.class);
                        go_to_menu.putExtra("Name", "Susan");
                        startActivity(go_to_menu);
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
    }*/

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

/*

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }
*/


    private void loadSpinnerData(String url) {
        final SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor = sp.edit();

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

                                spinnerModels.add(spinnerModel);

                               /* String site_name = site_items.getString("name");
                                String site_id = site_items.getString("id");
*//*
                                editor.putString("login_site_id", site_id);
                                editor.apply();
*/
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


                    site.setAdapter(new ArrayAdapter<String>(Activity_Login.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    SiteName));

                    site.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                            String site_id = spinnerModels.get(position).getId();
                            editor.putString("site_id", site_id);
                            editor.commit();

//                            Toast.makeText(Activity_Login.this, "" + site_id, Toast.LENGTH_SHORT).show();
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


    }
}
