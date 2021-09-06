package com.example.MVMcR_MA_QCR;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.DataHolder> {
    List<Bundle> data;

    @NonNull
    @NotNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        LinearLayout layout=(LinearLayout) inflater.inflate(R.layout.current_data_report_row,null);
        return new DataHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataHolder holder, int position) {
       holder.model_name.setText(data.get(position).getString("model_name"));
        holder.total.setText(data.get(position).getString("total"));
        holder.ok.setText(data.get(position).getString("ok"));
        holder.not_ok.setText(data.get(position).getString("notOk"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public List<Bundle> getData() {
        return data;
    }

    public void setData(List<Bundle> data) {
        this.data = data;
    }

    class DataHolder extends RecyclerView.ViewHolder{
        TextView model_name;
        TextView total;
        TextView ok;
        TextView not_ok;
        public DataHolder(@NonNull @NotNull View itemView) {
            super(itemView);
             model_name=itemView.findViewById(R.id.modelName);
             total=itemView.findViewById(R.id.totalCars);
             total.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
             total.setPadding(0,0,20,0);
             ok=itemView.findViewById(R.id.report_ok);
             ok.setPadding(0,0,20,0);
             ok.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
             not_ok=itemView.findViewById(R.id.report_not_ok);
             not_ok.setPadding(0,0,20,0);
            not_ok.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }
}
