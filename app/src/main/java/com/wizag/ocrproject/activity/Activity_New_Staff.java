package com.wizag.ocrproject.activity;

import android.app.Activity;
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
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkid.mrtd.MrtdRecognizer;
import com.microblink.entities.recognizers.blinkid.mrtd.MrzResult;
import com.microblink.results.date.DateResult;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.DocumentUISettings;
import com.wizag.ocrproject.BuildConfig;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.network.APIClient;
import com.wizag.ocrproject.network.ApiInterface;
import com.wizag.ocrproject.pojo.AuthUser;

import java.util.concurrent.TimeoutException;

import javax.xml.validation.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.wizag.ocrproject.network.APIClient.*;

public class Activity_New_Staff extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 100;
    private MrtdRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    private MrtdRecognizer mMRTDRecognizer;
    GPSLocation gps;
    String location;
    CardView scan;
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
    String access_token, refresh_token, token_type;
    //String username = "admin@admin.com";
    String login_username, login_password;
    SharedPreferences prefs;

    //String password = "password";

    EditText username_txt;
    EditText password_txt;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staff);

        scan = findViewById(R.id.scan);


        isNetworkConnectionAvailable();
        gps = new GPSLocation(this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            location = latitude + "," + longitude;
//            Toast.makeText(getApplicationContext(), "" + location, Toast.LENGTH_SHORT).show();

        } else {
            gps.showSettingsAlert();
        }
        // setup views, as you would normally do in onCreate callback

        mRecognizerBundle = new RecognizerBundle();
        mMRTDRecognizer = new MrtdRecognizer();
        mRecognizerBundle = new RecognizerBundle(mMRTDRecognizer);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*login user: if login successful, call scan method*/
                EmployeeLogin();

            }
        });

        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

    }

    public void onScanNewStaffClick() {
        // we'll use Machine Readable Travel Document recognizer
        mMRTDRecognizer = new MrtdRecognizer();

        // put our recognizer in bundle so that it can be sent via intent
        mRecognizerBundle = new RecognizerBundle(mMRTDRecognizer);
        mMRTDRecognizer.setAllowUnverifiedResults(true);
        // use default UI for scanning documents
        DocumentUISettings documentUISettings = new DocumentUISettings(mRecognizerBundle);

        // start scan activity based on UI settings
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, documentUISettings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // onActivityResult is called whenever we returned from activity started with startActivityForResult
        // We need to check request code to determine that we have really returned from BlinkID activity
        if (requestCode != MY_REQUEST_CODE) {
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            // OK result code means scan was successful
            onScanSuccess(data);
        } else {
            // user probably pressed Back button and cancelled scanning
            onScanCanceled();
        }
    }

    private void onScanSuccess(Intent data) {
        // update recognizer results with scanned data
        mRecognizerBundle.loadFromIntent(data);

        // you can now extract any scanned data from result, we'll just get primary id
        MrtdRecognizer.Result mrtdResult = mMRTDRecognizer.getResult();

        MrzResult mrzResult = mrtdResult.getMrzResult();
        String scannedPrimaryId = mrzResult.getPrimaryId();
        String scannedSecondaryId = mrzResult.getSecondaryId();
        DateResult scannedDob = mrzResult.getDateOfBirth();


        String scanned_id = mrzResult.getOpt2().replaceAll("[^0-9]", "");

        Intent result = new Intent(getApplicationContext(), Activity_New_User_Results.class);
        result.putExtra("Name", scannedPrimaryId + scannedSecondaryId);
        result.putExtra("Dob", scannedDob);
        result.putExtra("Id", scanned_id);
        result.putExtra("Location", location);
//        Toast.makeText(this, "Scanned primary id: " + scanned_id, Toast.LENGTH_LONG).show();
        startActivity(result);
        finish();

    }

    private void onScanCanceled() {
        Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
    }

    private void EmployeeLogin() {
        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
        editText.setText("test label");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();*/

        final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_New_Staff.this);

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

                        //  String token = prefs.getString("access_token", ACCESS_TOKEN);

                        //session.createLoginSession(username, password, access_token);

                        //Toast.makeText(FirstActivity.this, "" + access_token, Toast.LENGTH_LONG).show();
                        // Toast.makeText(FirstActivity.this, "" + refresh_token, Toast.LENGTH_LONG).show();
                        // Toast.makeText(FirstActivity.this, "" + token_type, Toast.LENGTH_LONG).show();
                        onScanNewStaffClick();
                    }

                } else if (response.code() >= 400 && response.code() < 599) {

                    Toast.makeText(Activity_New_Staff.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Activity_New_Staff.this, "Something Went Wrong", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<AuthUser> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Activity_New_Staff.this, "" + fetchErrorMessage(t), Toast.LENGTH_LONG).show();


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

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnectionAvailable()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }


}