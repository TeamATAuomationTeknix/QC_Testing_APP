package com.example.qctestingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class QCheckAdapter extends RecyclerView.Adapter<QCheckAdapter.VhQuestions>{

    private Context context;
    ArrayList<Questions_main> list;


    public QCheckAdapter(Context context, ArrayList<Questions_main> list) {
        this.list=list;
        this.context = context;

    }

    @NonNull
    @NotNull
    @Override
    public VhQuestions onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.qc_check_layout,null);
        return new VhQuestions(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VhQuestions holder, int position) {
        //holder.question.setText(list.get(position).getId()+" "+list.get(position).getQuestion());
        holder.question.setText((position+1)+" "+list.get(position).getQuestion());
        if(list.get(position).highlight.equals("EXTERNAL")){
            holder.question.setBackgroundColor(holder.question.getResources().getColor(R.color.highlighit));
        }
        if(list.get(position).highlight.equals("INTERNAL")){
            holder.question.setBackgroundColor(holder.question.getResources().getColor(R.color.BlueColor));
        }
        String ans=list.get(position).getAnswer();
        holder.btnOk.setText(ans);
        if(ans.equals(Questions_main.OK))
            holder.btnOk.setBackgroundColor(context.getResources().getColor(R.color.color_ok));
        else
            holder.btnOk.setBackgroundColor(context.getResources().getColor(R.color.color_not_ok));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VhQuestions extends RecyclerView.ViewHolder {
        TextView question;
        Button btnOk;
        public VhQuestions(@NonNull View itemView) {
            super(itemView);
            question=itemView.findViewById(R.id.qcCheckQuestion);
            btnOk=itemView.findViewById(R.id.qcCheckBtn);

        }
    }


}