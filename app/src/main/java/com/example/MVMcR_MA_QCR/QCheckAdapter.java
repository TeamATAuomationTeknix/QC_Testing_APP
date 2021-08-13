package com.example.MVMcR_MA_QCR;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class QCheckAdapter extends RecyclerView.Adapter<QCheckAdapter.VhQuestions>{

    private Context context;
    ArrayList<Questions_main> list;
    CommonMethods methods;


    public QCheckAdapter(Context context, ArrayList<Questions_main> list) {
        this.list=list;
        this.context = context;
        methods=new CommonMethods();
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
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   AlertDialog.Builder builder=new AlertDialog.Builder(context);
                   //builder.setTitle("Remark");
                   //builder.setMessage(list.get(getAdapterPosition()).getRemark());
                    ImageView imageView=new ImageView(context);
                    imageView.setImageBitmap(methods.byteArrayToBitmap(list.get(getAdapterPosition()).getNokImage()));
                    imageView.setFocusable(true);

                    imageView.setPadding(50,50,50,50);
                    LinearLayout layout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.nok_image_dialog,null);
                    imageView=layout.findViewById(R.id.nokImage);
                    imageView.setImageBitmap(methods.byteArrayToBitmap(list.get(getAdapterPosition()).getNokImage()));
                    builder.setView(layout);
                    AlertDialog alertDialog=builder.create();
                   if(btnOk.getText().toString().equals("NOK"))
                   alertDialog.show();

                }
            });
        }
    }


}