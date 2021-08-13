package com.example.MVMcR_MA_QCR;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MVMcR_MA_QCR.DataClass.PartInfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.PartsViewHolder> {
    List<PartInfo> partsList;
    Context context;
    String qr_code;
    long timerBase;
    public PartsAdapter(Context context, List<PartInfo> partsList) {
        this.partsList = partsList;
        this.context=context;
    }
    public PartsAdapter(Context context, List<PartInfo> partsList,String qr_code) {
        this.partsList = partsList;
        this.context=context;
        this.qr_code=qr_code;
    }
    public PartsAdapter(Context context, List<PartInfo> partsList,String qr_code,long base) {
        this.partsList = partsList;
        this.context=context;
        this.qr_code=qr_code;
        timerBase=base;
    }
    public List<PartInfo> getPartsList() {
        return partsList;
    }

    public void setPartsList(List<PartInfo> partsList) {
        this.partsList = partsList;
    }
    @NonNull
    @NotNull
    @Override
    public PartsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.part_item,null);
        return new PartsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PartsViewHolder holder, int position) {
        holder.partName.setText(position+1+". "+partsList.get(position).getPartname());
        if(partsList.get(position).getAnswer()!=null){
            String ans=partsList.get(position).getAnswer();
            if(ans.equals("OK")){
                holder.checkBox.setChecked(true);
                holder.checkBox.setBackgroundColor(context.getResources().getColor(R.color.color_ok));
            }
            if(ans.equals("NOK")){
                holder.checkBox.setChecked(false);
                holder.checkBox.setBackgroundColor(context.getResources().getColor(R.color.color_not_ok));
            }
            if(ans.equals("na")){
                holder.checkBox.setChecked(false);
                holder.checkBox.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public int getItemCount() {
        return partsList.size();
    }

    class PartsViewHolder extends RecyclerView.ViewHolder {
        TextView partName;
        CheckBox checkBox;
        ImageButton editCheckPoint;
        public PartsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.checkboxResult);
            editCheckPoint=itemView.findViewById(R.id.editCheckPoint);
            checkBox.setEnabled(false);
            partName=itemView.findViewById(R.id.item_part_name);
            partName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,SinglePartCheckPoints.class);
                    intent.putExtra("qr_result",qr_code);
                    //intent.putExtra("partname",partsList.get(getAdapterPosition()).getPartname());
                    intent.putExtra("partname",partsList.get(0).getPartname());
                    //intent.putExtra("position",getAdapterPosition());
                    intent.putExtra("position",0);
                    intent.putExtra("timerBase",timerBase);
                    ((Activity)context).startActivityForResult(intent,1);
                }
            });
            editCheckPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,SinglePartCheckPoints.class);
                    intent.putExtra("qr_result",qr_code);
                    intent.putExtra("partname",partsList.get(getAdapterPosition()).getPartname());
                    intent.putExtra("position",getAdapterPosition());
                    intent.putExtra("timerBase",timerBase);
                    ((Activity)context).startActivityForResult(intent,1);
                }
            });
        }
    }
}
