package com.wizag.ocrproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Activity_Results extends AppCompatActivity {
    TextView name, time_in;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        db = new DatabaseHelper(this);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        final String date_str = df.format(cal.getTime());
        mAdapter = new WorkerAdapter(this, workersList);

        name = findViewById(R.id.name);
        time_in = findViewById(R.id.time_in);
        id_no = findViewById(R.id.id_no);
        id_image = findViewById(R.id.id_image);

        confirm = (Button) findViewById(R.id.submit);
        discard = (Button) findViewById(R.id.discard);


        /*get scanned data*/
        Intent intent = new Intent();
        final String scanned_name = getIntent().getStringExtra("Name");
        final String scanned_id_no = getIntent().getStringExtra("Id");
        final String current_location = getIntent().getStringExtra("Location");

        name.setText(scanned_name);
        id_no.setText(scanned_id_no);
        time_in.setText(date_str);

        /*add user to local db*/

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int scanned_id_to_int = Integer.parseInt(scanned_id_no);
                createWorker(scanned_name, scanned_id_to_int, current_location, date_str);
                startActivity(new Intent(getApplicationContext(), Activity_Worker.class));
//                finish();
            }
        });
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
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            id_image.setImageBitmap(photo);
        }
    }

    private void createWorker(String name, int id_no, String location, String time_in) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertWorker(name, id_no, location, time_in);

        // get the newly inserted note from db
        Worker n = db.getWorker(id);

        if (n != null) {
            // adding new note to array list at 0 position
            workersList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

        }
    }
}
