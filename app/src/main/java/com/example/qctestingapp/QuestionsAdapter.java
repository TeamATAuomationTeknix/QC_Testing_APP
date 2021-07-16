package com.example.qctestingapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.VHQuestions> {
        PartFragmentViewModel partFragmentViewModel;
        ArrayList<Questions_main> questionsList;
        int id=1;
        Context context;
    public QuestionsAdapter(ArrayList<Questions_main> questionsList, Context context) {
        partFragmentViewModel=new PartFragmentViewModel();
            this.questionsList = questionsList;
            Log.e("tag","adapter initialized");
            id=1;
            this.context=context;

            }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override

    public VHQuestions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            LinearLayout layout=(LinearLayout) inflater.inflate(R.layout.questions,null);
            Log.e("tag","on create view holder");
            return new VHQuestions(layout);
            }

    @Override
    public void onBindViewHolder(@NonNull VHQuestions holder, int position) {
            holder.qNo.setText((position+1) +". ");

            id++;
            if(questionsList.get(position).highlight.equals("EXTERNAL")){
            holder.question.setBackgroundColor(holder.question.getResources().getColor(R.color.highlighit));
            }
        if(questionsList.get(position).highlight.equals("INTERNAL")){
            holder.question.setBackgroundColor(holder.question.getResources().getColor(R.color.BlueColor));
        }
            if(questionsList.get(position).getAnswer()==null) {
                holder.increase=true;
            holder.btnOk.setBackgroundColor(holder.btnOk.getResources().getColor(R.color.white));
            holder.btnNotOk.setBackgroundColor(holder.btnNotOk.getResources().getColor(R.color.white));
            }else{
            switch (questionsList.get(position).getAnswer()){
            case Questions_main.OK:
            holder.btnOk.setBackgroundColor(holder.btnOk.getResources().getColor(R.color.color_ok));
            holder.btnNotOk.setBackgroundColor(holder.btnNotOk.getResources().getColor(R.color.white));
            break;
            case Questions_main.NOT_OK:
            holder.btnNotOk.setBackgroundColor(holder.btnNotOk.getResources().getColor(R.color.color_not_ok));
            holder.btnOk.setBackgroundColor(holder.btnOk.getResources().getColor(R.color.white));
            }
            }

            holder.question.setText(questionsList.get(position).getQuestion());
            Log.e("tag","On bind view");
            }

            @Override
            public int getItemCount() {
                    return questionsList.size();
            }

        class VHQuestions extends RecyclerView.ViewHolder {
            TextView qNo,question;
            Button btnOk,btnNotOk;
            boolean increase=true;
            @RequiresApi(api = Build.VERSION_CODES.M)
            public VHQuestions(@NonNull View itemView) {
                super(itemView);

                qNo=itemView.findViewById(R.id.txtQNo);
                question=itemView.findViewById(R.id.txtQuestion);
                btnOk=itemView.findViewById(R.id.btnOk);
                btnNotOk=itemView.findViewById(R.id.btnNotOk);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("tag","ok button clicked");
                        questionsList.get(getAdapterPosition()).setAnswer(Questions_main.OK);
                        questionsList.get(getAdapterPosition()).setRemark("no remark");
                        Log.e("tag",questionsList.get(getAdapterPosition()).getAnswer());
                        btnOk.setBackgroundColor(v.getResources().getColor(R.color.color_ok));
                        btnNotOk.setBackgroundColor(v.getResources().getColor(R.color.white));
                        if(increase){
                            increase=false;
                            Questions.count=Questions.count+Questions.partcount;
                            Questions.progressBar.setProgress((int) Questions.count);
                        }
                    }
                });

                btnNotOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("tag","ok button clicked");

                        Questions.notOkPosition=getAdapterPosition();
                        // TODO: 14-07-2021 set default remark
                        int qid=questionsList.get(getAdapterPosition()).getId();

                        List remarks=new MyDbHelper(context).getRemarksByQID(qid);
                        if(remarks.size()>0)
                            questionsList.get(getAdapterPosition()).setRemark((String) remarks.get(0));
                        //end of set defaullt remark
                        questionsList.get(getAdapterPosition()).setAnswer(Questions_main.NOT_OK);
                        Log.e("tag",questionsList.get(getAdapterPosition()).getAnswer());

                        btnNotOk.setBackgroundColor(v.getResources().getColor(R.color.color_not_ok));
                        btnOk.setBackgroundColor(v.getResources().getColor(R.color.white));
                        if(increase){
                            increase=false;
                            Questions.count=Questions.count+Questions.partcount;
                            Questions.progressBar.setProgress((int) Questions.count);
                        }
                        partFragmentViewModel.saveListState(questionsList);
                        ImagePicker.with((Activity) context).maxResultSize(512,512)
                                .start(103);
                    }
                });

                // TODO: 01-07-2021 register not ok btn for context menu
//                btnNotOk.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                    @Override
//                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//                        questionsList.get(getAdapterPosition()).setAnswer(Questions_main.NOT_OK);
//                        Log.e("tag",questionsList.get(getAdapterPosition()).getAnswer());
//
//                        btnNotOk.setBackgroundColor(v.getResources().getColor(R.color.color_not_ok));
//                        btnOk.setBackgroundColor(v.getResources().getColor(R.color.white));
//                        if(increase){
//                            increase=false;
//                            Questions.count=Questions.count+Questions.partcount;
//                            Questions.progressBar.setProgress((int) Questions.count);
//                        }
//                        int id=questionsList.get(getAdapterPosition()).getId();
//                        MyDbHelper myDbHelper=new MyDbHelper(context);
//                        ArrayList<String> list=  myDbHelper.getRemarksByQID(id);
//                        if(list!=null){
//                            for(String s:list){
//                                menu.add(0,id,getAdapterPosition(),s);
//                            }
//                        }
//                       // menu.add(0, id+2, 0, "Another problem");
//                    }
//                });
        }


        }


}
