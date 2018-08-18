package com.wizag.ocrproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wizag.ocrproject.R;
import com.wizag.ocrproject.pojo.Worker;

import java.util.List;


public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.MyViewHolder> {

    private Context context;
    private List<Worker> workerList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id_no;
        public TextView name;
        public TextView time_in;

        public MyViewHolder(View view) {
            super(view);
            id_no = view.findViewById(R.id.id_no);
            name = view.findViewById(R.id.name);
            time_in = view.findViewById(R.id.time_in);
        }
    }


    public WorkerAdapter(Context context, List<Worker> workersList) {
        this.context = context;
        this.workerList = workersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_worker_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Worker worker = workerList.get(position);

        holder.name.setText(worker.getF_name());
        holder.name.setText(worker.getL_name());


        holder.id_no.setText(String.valueOf(worker.getId_no()));

        // Formatting and displaying timestamp
//        holder.time_in.setText(worker.getTime_in());
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

}