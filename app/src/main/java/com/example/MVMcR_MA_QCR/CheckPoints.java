package com.example.MVMcR_MA_QCR;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;
import com.example.MVMcR_MA_QCR.DataClass.PartInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckPoints extends AppCompatActivity {
    CommonMethods methods;
    TextView txtVin,txtModel;

    Button btnSubmit;
    RecyclerView recyclerParts;
    String qr_code="";
    String vin="";
    List<String> list;
    List<PartInfo> partsList;
    String result;
    CheckBox checkBox;
    PartsAdapter partsAdapter;
    Chronometer fullTimer;
    ArrayList<Questions_main> questionList;
    CheckPointsViewModel viewModel;
    ProgressBar progressCheckpoints;
    int position;
    double progressValue=0;
    public static Double progress= Double.valueOf(0);
    int size=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_points);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        viewModel=new ViewModelProvider(this).get(CheckPointsViewModel.class);

        progressCheckpoints=findViewById(R.id.progressCheckpoints);
        fullTimer=findViewById(R.id.fullTimer);
        fullTimer.start();
        partsList=new ArrayList<>();
        questionList=new ArrayList<>();
        methods=new CommonMethods();
        txtVin=findViewById(R.id.textViewQr);
        txtModel=findViewById(R.id.textViewModel);
        btnSubmit=findViewById(R.id.btnSubmit);
        recyclerParts=findViewById(R.id.recycler_parts);
        recyclerParts.setLayoutManager(new LinearLayoutManager(this));
        Intent intent=getIntent();
        qr_code=intent.getStringExtra("qr_result");

        String[] arr=qr_code.split("_");
        vin=arr[0];
        txtVin.setText("VIN: "+vin);
        if(arr.length>1)
        txtModel.setText("Model: "+methods.getPlatform(qr_code)+" "+methods.getVarient(qr_code));
        checkBackPress(qr_code);
        if(questionList.size()==0)
        checkAlreadyComplited(qr_code);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date submissionTime=new Date();
                // TODO: 29-07-2021 check any answer is remaining
                for(int i=0;i<partsList.size();i++){
                    if(partsList.get(i).getAnswer().equals("na")){
                        Toast.makeText(CheckPoints.this, "Please Check Part: "+partsList.get(i).getPartname(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(partsList.size()>0){
                    questionList=new ArrayList<>();

                    SharedPreferences preferences=getSharedPreferences("userpref",MODE_PRIVATE);
                    String user=preferences.getString("user","unknown");
                    for(PartInfo p:partsList) {
                       Questions_main questions_main=new Questions_main(p.getPartId(), p.getPartname(), p.getAnswer(),"NOHIGHLIGHT",qr_code);
                        if(p.getConcern()!=null){
                            questions_main.setRemark(p.getConcern());
                        }
                        questions_main.setNokImage(p.getNokImage());
                        questions_main.setSubmissionTime(submissionTime);
                        questionList.add(questions_main);
                        progress+=progressValue;
                        progressCheckpoints.setProgress(progress.intValue());
                        Log.e("question",questions_main.toString());
                    }
                    MyDbHelper myDbHelper=new MyDbHelper(CheckPoints.this);
                    myDbHelper.insert_data(questionList,"na",qr_code,user);
                    Log.e("size",questionList.size()+"");
                    ServerJson serverJson=new ServerJson(CheckPoints.this);
                    serverJson.submitAnswer(questionList,"na",fullTimer.getText().toString(),fullTimer.getText().toString(),qr_code,user);
                    progress= Double.valueOf(0);
                    progressValue=0;
                    Intent intent=new Intent();
                    intent.putExtra("value","submitted");
                    setResult(1,intent);
                    finish();
                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        MyDbHelper myDbHelper=new MyDbHelper(this);
        String qr=myDbHelper.backPressQr();
        if(qr!=null&&!qr_code.equals(qr)&&questionList.size()==0) {
            list = myDbHelper.getPartnames();
            if (partsList.size() == 0) {
                partsList = myDbHelper.getParts(methods.getVarient(qr_code));
                size=partsList.size();
//            for (String s : list) {
//                partsList.add(new PartInfo(s, "na"));
//            }
            }
            partsAdapter = new PartsAdapter(this, partsList, qr_code, fullTimer.getBase());
            recyclerParts.setAdapter(partsAdapter);
            progressValue=100/size;
            progress+=progressValue;
            progressCheckpoints.setProgress(progress.intValue());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        size=partsList.size();
        if(resultCode==1){
            result=data.getStringExtra("result");
            position=data.getIntExtra("position",0);
            String concern=data.getStringExtra("concern");
            if(concern!=null)
            partsList.get(position).setConcern(concern);
            partsList.get(position).setAnswer(result);
            Log.e("answer:",partsList.get(position).getAnswer());
            partsAdapter.setPartsList(partsList);
            recyclerParts.setAdapter(partsAdapter);
            position++;
            if(position < size) {
                if (partsList.get(position).getAnswer().equals("na")) {
                    Intent intent = new Intent(this, SinglePartCheckPoints.class);
                    String partname = partsList.get(position).getPartname();
                    intent.putExtra("qr_result", qr_code);
                    intent.putExtra("partname", partname);
                    intent.putExtra("position", position);
                    intent.putExtra("timerBase",fullTimer.getBase());
                    startActivityForResult(intent, 1);
                }
            }

        }
        if(resultCode==2){
            result=data.getStringExtra("result");
            position=data.getIntExtra("position",0);
            String concern=data.getStringExtra("concern");
            if(concern!=null)
                partsList.get(position).setConcern(concern);
            partsList.get(position).setAnswer(result);
            partsList.get(position).setNokImage(data.getByteArrayExtra("imageBitmap"));
            partsAdapter.setPartsList(partsList);
            recyclerParts.setAdapter(partsAdapter);
            position++;
            if(position < size) {
                if (partsList.get(position).getAnswer().equals("na")) {
                    Intent intent = new Intent(this, SinglePartCheckPoints.class);
                    String partname = partsList.get(position).getPartname();
                    intent.putExtra("qr_result", qr_code);
                    intent.putExtra("partname", partname);
                    intent.putExtra("position", position);
                    intent.putExtra("timerBase",fullTimer.getBase());
                    startActivityForResult(intent, 1);
                }
            }

        }

    }
    //todo add home icon to tolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyDbHelper myDbHelper=new MyDbHelper(this);
        myDbHelper.addBackpressData(partsList,"na",qr_code);
    }
    public void checkBackPress(String qr_code){
        MyDbHelper myDbHelper=new MyDbHelper(this);
        String qr=myDbHelper.backPressQr();
        Log.e("qr",qr);
        Log.e("qr_code",qr_code);
       if(qr!=null &&qr_code.equals(qr)){
           partsList=myDbHelper.getBackPressData(qr_code);
           partsAdapter=new PartsAdapter(this,partsList,qr);
           recyclerParts.setAdapter(partsAdapter);
           size=partsList.size();
           progressValue=size/100;
           for(PartInfo partInfo:partsList)
           {
               if(!partInfo.getAnswer().equals("na")){
                   progress+=progressValue;
               }
           }
           return;
       }
    }
    public void checkAlreadyComplited(String qr_code){
        MyDbHelper myDbHelper=new MyDbHelper(CheckPoints.this);
        questionList=myDbHelper.getPreviousAnswers(qr_code);
        if(questionList!=null&&questionList.size()>0){
           for(Questions_main q:questionList){
               PartInfo partInfo=new PartInfo(q.getId(),q.getQuestion(),q.getAnswer());
               partInfo.setNokImage(q.getNokImage());
               partsList.add(partInfo);
           }
            partsAdapter=new PartsAdapter(this,partsList,qr_code);
            recyclerParts.setAdapter(partsAdapter);
            return;
        }
        else if(questionList!=null&&questionList.size()==0){
        ServerJson serverJson=new ServerJson(CheckPoints.this);
        serverJson.setOnResponseInterface(new ServerJson.OnResponseInterface() {
            PartsAdapter partsAdapter;
            @Override
            public void onResponse(ArrayList<Questions_main> list) {
                partsList.clear();
                questionList=list;
                if(list!=null&&list.size()>0){
                    for(Questions_main q:list){
                        PartInfo partInfo=new PartInfo(q.getId(),q.getQuestion(),q.getAnswer());
                        partInfo.setNokImage(q.getNokImage());
                        partsList.add(partInfo);
                    }
                    partsAdapter=new PartsAdapter(CheckPoints.this,partsList,qr_code);
                    recyclerParts.setAdapter(partsAdapter);
                    return;
                }
            }
        });
        serverJson.getAnswers(qr_code);
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.partsList=partsList;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        partsList=viewModel.partsList;
        partsAdapter=new PartsAdapter(this,partsList,qr_code);
        recyclerParts.setAdapter(partsAdapter);
    }
}