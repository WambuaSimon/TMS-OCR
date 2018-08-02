/*
package com.wizag.ocrproject;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.microblink.entities.recognizers.Recognizer;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkid.mrtd.MrtdRecognizer;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.BarcodeUISettings;
import com.microblink.uisettings.BaseScanUISettings;
import com.microblink.uisettings.DocumentUISettings;
import com.microblink.uisettings.DocumentVerificationUISettings;
import com.microblink.uisettings.FieldByFieldUISettings;
import com.microblink.uisettings.UISettings;
import com.microblink.uisettings.options.BeepSoundUIOptions;
import com.microblink.uisettings.options.HelpIntentUIOptions;
import com.microblink.uisettings.options.ShowOcrResultMode;
import com.microblink.uisettings.options.ShowOcrResultUIOptions;

import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import java.util.ArrayList;
import java.util.List;

public class test extends AppCompatActivity {

    public static final int MY_BLINKID_REQUEST_CODE = 123;

    MrtdRecognizer mRecognizer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // in case of problems with the SDK (crashes or ANRs, uncomment following line to enable
        // verbose logging that can help developers track down the problem)
        //Log.setLogLevel(Log.LogLevel.LOG_VERBOSE);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // check if BlinkID is supported on the device
        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkID is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }
    }

    */
/**
     * This method is invoked after returning from scan activity. You can obtain
     * scan results here
     *//*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // onActivityResult is called whenever we are returned from activity started
        // with startActivityForResult. We need to check request code to determine
        // that we have really returned from BlinkID activity.

        if (requestCode == MY_BLINKID_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {



            startResultActivity(data);


        } else {

            // if BlinkID activity did not return result, user has probably
            // pressed Back button and cancelled scanning
            Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startResultActivity(Intent data) {
        // set intent's component to Details_Activity and pass its contents
        // to Details_Activity. Details_Activity will show how to extract
        // data from result.
        data.setComponent(new ComponentName(getApplicationContext(), com.microblink.result.Details_Activity.class));

//        Toast.makeText(this, ""+data, Toast.LENGTH_SHORT).show();
        startActivity(data);
    }

//    @Override
//    protected List<MenuListItem> createMenuListItems() {
//        List<MenuListItem> items = new ArrayList<>();
//
//        // ID document list entries
//        items.add(buildMrtdElement());
      */
/*  items.add(buildAustrianIDElement());
        items.add(buildAustrianIDCombinedElement());
        items.add(buildAustrianPassportElement());
        items.add(buildColombiaIDElement());
        items.add(buildCroatianIDElement());
        items.add(buildCroatianIDCombinedElement());
        items.add(buildCzechIDElement());
        items.add(buildCzechIDCombinedElement());
        items.add(buildEgyptIDFrontElement());
        items.add(buildGermanIDElement());
        items.add(buildGermanPassportElement());
        items.add(buildGermanIDCombinedElement());
        items.add(buildHongKongIDFrontElement());
        items.add(buildIndonesianIdElement());
        items.add(buildJordanIdElement());
        items.add(buildJordanIdCombinedElement());
        items.add(buildMyKadElement());
        items.add(buildIKadElement());
        items.add(buildMyTenteraElement());
        items.add(buildPolishIdElement());
        items.add(buildPolishIdCombinedElement());
        items.add(buildRomanianElement());
        items.add(buildSingaporeIDElement());
        items.add(buildSingaporeIDCombinedElement());
        items.add(buildSerbianIDElement());
        items.add(buildSerbianIDCombinedElement());
        items.add(buildSlovakIDElement());
        items.add(buildSlovakIDCombinedElement());
        items.add(buildSlovenianIDElement());
        items.add(buildSlovenianIDCombinedElement());
        items.add(buildSwissIDElement());
        items.add(buildSwissPassportElement());
        items.add(buildUnitedArabEmiratesIdElement());

        // DL list entries
        items.add(buildAustrianDLElement());
        items.add(buildAustralianDLElement());
        items.add(buildMalaysianDLElement());
        items.add(buildNewZealandDLElement());
        items.add(buildGermanDLElement());
        items.add(buildSwedenDlElement());
        items.add(buildUKDLElement());
        items.add(buildUsdlElement());
        items.add(buildUsdlCombinedElement());

        // barcode list entries
        items.add(buildPDF417Element());
        items.add(buildBarcodeElement());
        items.add(buildSimNumberElement());
        items.add(buildVinElement());

        // Field by field entries
        items.add(buildGenericFieldByFieldElement());
        items.add(buildVehicleFieldByFieldElement());
*//*

//        return items;
//    }

    */
/**
     * Starts scan activity. Activity that will be used is determined by the passed activity settings.
     * UI options are configured inside this method.
     * @param activitySettings activity settings that will be used for scanning, only recognizers are
     *                         important, UI options will be configured inside this method.
     * @param helpIntent help intent that can be launched if scan activity supports that
     *//*

    private void scanAction(@NonNull UISettings activitySettings, @Nullable Intent helpIntent) {
        setupActivitySettings(activitySettings, helpIntent);
        ActivityRunner.startActivityForResult(this, MY_BLINKID_REQUEST_CODE, activitySettings);
    }

    */
/**
     * Starts scan activity. Activity that will be used is determined by the passed activity settings.
     * UI options are configured inside this method.
     * @param activitySettings activity settings that will be used for scanning, only recognizers are
     *                         important, UI options will be configured inside this method.
     *//*

    private void scanAction(@NonNull UISettings activitySettings) {
        scanAction(activitySettings, null);
    }

    */
/**
     * Starts {@link com.microblink.activity.DocumentVerificationActivity} with given recognizer.
     * @param combinedRecognizer recognizer that will be used.
     *//*

    private void combinedRecognitionAction(Recognizer combinedRecognizer) {
        DocumentVerificationUISettings uiSettings = new DocumentVerificationUISettings(new RecognizerBundle(combinedRecognizer));
        uiSettings.setBeepSoundResourceID(R.raw.beep);

        ActivityRunner.startActivityForResult(this, MY_BLINKID_REQUEST_CODE, uiSettings);
    }

    private void setupActivitySettings(@NonNull UISettings settings, @Nullable Intent helpIntent) {
        if (settings instanceof BeepSoundUIOptions) {
            // optionally, if you want the beep sound to be played after a scan
            // add a sound resource id
            ((BeepSoundUIOptions) settings).setBeepSoundResourceID(R.raw.beep);
        }
        if (helpIntent != null && settings instanceof HelpIntentUIOptions) {
            // if we have help intent, we can pass it to scan activity so it can invoke
            // it if user taps the help button. If we do not set the help intent,
            // scan activity will hide the help button.
            ((HelpIntentUIOptions) settings).setHelpIntent(helpIntent);
        }
        if (settings instanceof ShowOcrResultUIOptions) {
            // If you want, you can disable drawing of OCR results on scan activity. Drawing OCR results can be visually
            // appealing and might entertain the user while waiting for scan to complete, but might introduce a small
            // performance penalty.
            // ((ShowOcrResultUIOptions) settings).setShowOcrResult(false);

            // Enable showing of OCR results as animated dots. This does not have effect if non-OCR recognizer like
            // barcode recognizer is active.
            ((ShowOcrResultUIOptions) settings).setShowOcrResultMode(ShowOcrResultMode.ANIMATED_DOTS);
        }
        if (settings instanceof BaseScanUISettings) {
            // If you want you can have scan activity display the focus rectangle whenever camera
            // attempts to focus, similarly to various camera app's touch to focus effect.
            // By default this is off, and you can turn this on by setting EXTRAS_SHOW_FOCUS_RECTANGLE
            // extra to true.
            // ((BaseScanUISettings) settings).setShowingFocusRectangle(true);

            // If you want, you can enable the pinch to zoom feature of scan activity.
            // By enabling this you allow the user to use the pinch gesture to zoom the camera.
            // By default this is off
            ((BaseScanUISettings) settings).setPinchToZoomAllowed(true);
        }
    }






    private RecognizerBundle prepareRecognizerBundle(@NonNull Recognizer<?,?>... recognizers ) {
        return new RecognizerBundle(recognizers);
    }

}*/
