package com.wizag.ocrproject.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.wizag.ocrproject.R;

public class Activity_Dashboard extends AppCompatActivity {
    CardView attendance, add_staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        attendance = (CardView) findViewById(R.id.attendance);
        add_staff = (CardView) findViewById(R.id.add_staff);
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_Scan.class));
            }
        });

        add_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_New_Staff.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        killActivity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void killActivity() {
        finish();
    }

}
