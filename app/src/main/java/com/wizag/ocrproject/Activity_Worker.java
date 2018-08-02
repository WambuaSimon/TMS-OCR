package com.wizag.ocrproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_Worker extends AppCompatActivity {
    private WorkerAdapter mAdapter;
    private List<Worker> workersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        recyclerView = findViewById(R.id.recycler_view);


        db = new DatabaseHelper(this);

        workersList.addAll(db.getAllWorkers());



        mAdapter = new WorkerAdapter(this, workersList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);



        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */

    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createWorker(String name,int id_no, String location, String time_in) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertWorker(name,id_no,location,time_in);

        // get the newly inserted note from db
        Worker n = db.getWorker(id);

        if (n != null) {
            // adding new note to array list at 0 position
            workersList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
   /* private void updateWorker(String note, int position) {
        Note n = workersList.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        workersList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }*/

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(workersList.get(position));

        // removing the note from the list
        workersList.remove(position);
        mAdapter.notifyItemRemoved(position);

    }





}