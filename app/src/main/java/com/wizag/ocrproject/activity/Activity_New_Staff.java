package com.wizag.ocrproject.activity;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkid.mrtd.MrtdRecognizer;
import com.microblink.entities.recognizers.blinkid.mrtd.MrzResult;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.DocumentUISettings;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.R;

public class Activity_New_Staff extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 100;
    private MrtdRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    private MrtdRecognizer mMRTDRecognizer;
    GPSLocation gps;
    String location;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staff);


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


    }

    public void onScanNewStaffClick(View view) {
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
        String scannedDob = String.valueOf(mrzResult.getDateOfBirth());


        String scanned_id = mrzResult.getOpt2().replaceAll("[^0-9]", "");

        Intent result = new Intent(getApplicationContext(), Activity_Results.class);
        result.putExtra("Name", scannedPrimaryId+scannedSecondaryId);
        result.putExtra("Dob", scannedDob);
        result.putExtra("Id", scanned_id);
        result.putExtra("Location", location);
//        Toast.makeText(this, "Scanned primary id: " + scanned_id, Toast.LENGTH_LONG).show();
        startActivity(result);


    }

    private void onScanCanceled() {
        Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
    }


}
