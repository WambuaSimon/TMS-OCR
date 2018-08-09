package com.wizag.ocrproject.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizag.ocrproject.database.DatabaseHelper;
import com.wizag.ocrproject.helper.GPSLocation;
import com.wizag.ocrproject.R;
import com.wizag.ocrproject.pojo.Worker;
import com.wizag.ocrproject.adapter.WorkerAdapter;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_Results extends AppCompatActivity {
    TextView name;
    TextView time_in;
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
    String date_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        db = new DatabaseHelper(this);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date_str = df.format(cal.getTime());
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
        scanned_id_no = getIntent().getStringExtra("Id");
        final String current_location = getIntent().getStringExtra("Location");


        name.setText(scanned_name);
        id_no.setText(scanned_id_no);
        time_in.setText(date_str);

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*add user to local db*/

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int scanned_id_to_int = Integer.parseInt(scanned_id_no);




                /*convert image bitmap to byte[]*/
                bitmap = ((BitmapDrawable) id_image.getDrawable()).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                id_photo = outputStream.toByteArray();


                if (db.rowIdExists(scanned_id_no)) {
                    showChangeLangDialog();
                  /*  Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));
                    int existing_no = existing_worker.getId_no();
                    String existing_name = existing_worker.getName();*/
//                    Toast.makeText(getApplicationContext(), "User Exists", Toast.LENGTH_SHORT).show();

//                    Toast.makeText(getApplicationContext(), "Name: " + existing_name + " No: " + existing_no, Toast.LENGTH_SHORT).show();

                } else {
                    createWorker(scanned_name, scanned_id_to_int, current_location, date_str, id_photo);
                    startActivity(new Intent(getApplicationContext(), Activity_Worker.class));
                    Toast.makeText(getApplicationContext(), "Data Submitted Successfully", Toast.LENGTH_SHORT).show();
//                finish();


                }


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

    private void createWorker(String name, int id_no, String location, String time_in, byte[] id_photo) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertWorker(new Worker(id_no, name, location, time_in, id_photo));

        // get the newly inserted note from db
       /* Worker n = db.getWorker(id);

        if (n != null) {
            // adding new note to array list at 0 position
            workersList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

        }*/
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.time_out_layout, null);
        dialogBuilder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.time_out_name);
        final TextView id_no = dialogView.findViewById(R.id.time_out_id_no);
        final TextView time_out = dialogView.findViewById(R.id.time_out);

        dialogBuilder.setTitle("Check Out Staff");
        final String time_out_txt = time_out.getText().toString();

        Worker existing_worker = db.getWorker(Long.parseLong(scanned_id_no));
        int existing_no = existing_worker.getId_no();
        String existing_name = existing_worker.getName();
        name.setText(existing_name);
        id_no.setText(String.valueOf(existing_no));
        time_out.setText(date_str);
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /*confirm column*/
                updateNote(time_out_txt);
                Toast.makeText(Activity_Results.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
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

    private void updateNote(String time_out) {
        Worker worker = new Worker();
        // updating note text
        worker.setTime_out(time_out);

        // updating note in db
        db.updateWorker(worker);

    }


}
