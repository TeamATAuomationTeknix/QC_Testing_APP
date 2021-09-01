package com.example.MVMcR_MA_QCR;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TextViewAdapter extends RecyclerView.Adapter<TextViewAdapter.TextViewHolder> {
    ArrayList<Questions_main> list;
    Context context;
    public TextViewAdapter(Context context,ArrayList list) {
        this.list = list;
        this.context=context;
    }

    public ArrayList getList() {
        return list;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TextViewHolder(new TextView(context));
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull TextViewHolder holder, int position) {
        holder.textView.setText(position+1+". "+list.get(position).getQuestion());
        if(list.get(position).getHighlight().equals("INTERNAL"))
            holder.textView.setBackgroundColor(context.getResources().getColor(R.color.BlueColor));
        if(list.get(position).getHighlight().equals("EXTERNAL"))
            holder.textView.setBackgroundColor(context.getResources().getColor(R.color.highlighit));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public TextViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView=(TextView) itemView;
           // textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
           // textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setPadding(70,0,0,0);
            //textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            //textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }
}
