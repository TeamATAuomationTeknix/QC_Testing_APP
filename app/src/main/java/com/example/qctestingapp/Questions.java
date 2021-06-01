package com.example.qctestingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qctestingapp.Fragments.PartFragment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Questions extends AppCompatActivity {
    ProgressDialog p;
    ArrayList<Questions_main> list;
    ArrayList<String> pnames;
    PartFragment partFragment;
    Button btnNext;
    private ImageButton scanqr;
    String partname;
    public TextView qr;
    private String qr_res;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    String qrScanned="no";
    Chronometer timer;
    Chronometer fullTimer;
    Boolean noRemaining=true;
    LinearLayout partTimeLayout,fullTimeLayout;
    String firstPart;


    public static boolean isConnected=true;
    Runnable questionTimer;
    Handler handler;
    boolean active=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        handler=new Handler();
        questionTimer=new Runnable() {
            @Override
            public void run() {
                active=false;
                finish();
                Toast.makeText(Questions.this, "user is inactive", Toast.LENGTH_SHORT).show();
            }
        };
        startHandler();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        scanqr = (ImageButton) findViewById(R.id.btn_scan_qr);
        qr = (TextView) findViewById(R.id.txt_qr);
        timer=findViewById(R.id.timer);
        fullTimer=findViewById(R.id.fullTimer);
        partTimeLayout=findViewById(R.id.parttimeLayout);
        fullTimeLayout=findViewById(R.id.fulltimelayout);
        //Getting data from ScanQR
        qr_res = getIntent().getStringExtra("qr_result");
        qr.setText(qr_res);

        qr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputQRCode();
                return false;
            }
        });


        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ScanQR.class));
                Intent i = new Intent(Questions.this, ScanQR.class);
                i.putExtra("calling_page", "Questions");
                i.putExtra("partname",partname);
                startActivity(i);
                finish();
            }
        });

        init();
    }

    private void init(){
        list = new ArrayList<>();
        pnames = new ArrayList<>();


        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new OnBtnNextClickListner());
        //visibility**************
        btnNext.setVisibility(View.INVISIBLE);
        partTimeLayout.setVisibility(View.INVISIBLE);
        fullTimeLayout.setVisibility(View.INVISIBLE);
    }
     protected void onStart() {
         super.onStart();
         Questions_main.qNo = 1;



         firstPart = getIntent().getStringExtra("partname");
         partname = getIntent().getStringExtra("partname");
         qrScanned = getIntent().getStringExtra("qrScanned");
         MyDbHelper myDbHelper = new MyDbHelper(this, MyDbHelper.DB_NAME, null, 1);
         // Cursor remainingParts=new ArrayList<>();
         Cursor remainingParts = myDbHelper.getRemainingParts();
         if (remainingParts != null) {
             if (remainingParts.moveToFirst()) {
                 noRemaining = false;

                 long t;
                 do {
                     //"part_name","fullTime","qr_code"
                     pnames.add(remainingParts.getString(0));
                     t = remainingParts.getLong(1);
                     Log.e("time remaing part: ", t + "");

                     qr_res = remainingParts.getString(2);
                     Log.e("qr res remaing part: ", qr_res);
                 } while (remainingParts.moveToNext());
                 myDbHelper.deleteRemainingParts();
                 //  pnames = remainingParts;
                 fullTimer.setBase(t);

                 getIntent().putExtra("qr_result", qr_res);
                 //qr.setText(qr_res);
                 partname = pnames.get(0);
                 qrScanned = "scanned";

             }
             else{
                 noRemaining = true;

             }
         }
         else{
             noRemaining = true;

         }

         if (qrScanned != null) {
             Log.e("tag", qrScanned);
             if (qrScanned.equals("scanned")) {

                 ServerJson serverJson = new ServerJson(this);



                 qr.setText(getIntent().getStringExtra("qr_result"));

                 if (noRemaining) {
                     MyDbHelper dbHelper = new MyDbHelper(this, MyDbHelper.DB_NAME, null, 1);
                     pnames = dbHelper.getPartnames();
                    if(pnames!=null) {
                        if (pnames.size() == 0) {
                            serverJson = new ServerJson(this, pnames);
                            serverJson.getPartName();
                            pnames = serverJson.getPartnames();
                        }
                    }
                 }//no remaining
                 if(partname!=null)
addFragment();
             }
         }
     }
     public void addFragment(){


        if(pnames.size()>0) {
            btnNext.setVisibility(View.VISIBLE);
            partTimeLayout.setVisibility(View.VISIBLE);
            fullTimeLayout.setVisibility(View.VISIBLE);
            timer.start();
            fullTimer.start();
        }
         partFragment = new PartFragment(list, partname);
         fragmentManager = getSupportFragmentManager();
         fragmentTransaction = fragmentManager.beginTransaction();
         fragmentTransaction.add(R.id.layoutQuestions, partFragment).commit();

     }

    private void inputQRCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR Code.");
        builder.setMessage("Enter Correct QR Code.");

        // Set up the input
        final EditText inputQR = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputQR.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        int maxLength = 36;
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        inputQR.setFilters(filters);
        builder.setView(inputQR);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String qr_code = inputQR.getText().toString().toUpperCase().trim().replaceAll("\\s+", "").replaceAll("\n","");
//                Pattern p = Pattern.compile("\\S{2}\\d{1}\\S{2}\\d{1}\\S{4}\\d{1}\\S{1}\\d{5}\\S{3}\\d{1}\\S{4}\\d{1}\\S{2}\\d{2}\\S{2}\\d{1}\\S{2}");
//                if (!qr_code.equals("") && p.matcher(qr_code).matches()) {
                String qr_code=inputQR.getText().toString();


                        if(qr_code.length()==36){
                            // qr_code = qr_code.substring(0, 17) + "_" + qr_code.substring(17, qr_code.length())+"_";
                            Intent i = new Intent(Questions.this, Questions.class);
                            i.putExtra("qr_result", qr_code);
                            i.putExtra("qrScanned","scanned");
                            i.putExtra("partname",partname);
                            startActivity(i);
                            finish();
                            //qr.setText(qr_code);
                            //isModelNameExist();
                        }else {
                            Toast.makeText(getApplicationContext(), "Invalid QR Code...!", Toast.LENGTH_LONG).show();
                            inputQRCode();
                        }




            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    class OnBtnNextClickListner implements View.OnClickListener{

        MyDbHelper helper;
        @Override
        public void onClick(View v) {
            ServerJson serverJson=new ServerJson(Questions.this);

            int i=1,ok=0,not_ok=0;
            list=partFragment.getList();
            for(Questions_main q:list){
                if(q.getAnswer()==null) {
                    Toast.makeText(Questions.this, "please check question no: " + i, Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Log.e("tag",q.getId()+" "+q.getQuestion());
                    Log.e("tag",q.getAnswer());
                    if(q.getAnswer().equals(Questions_main.OK))
                        ok++;
                    else
                        not_ok++;
                    i++;
                }

            }
            if(i-1==list.size()&&i!=0) {
                //one activity completed

                helper = new MyDbHelper(Questions.this, MyDbHelper.DB_NAME, null, 1);
                helper.insert_data(list,partname,qr.getText().toString(),"sukrut");
                serverJson=new ServerJson(Questions.this);

                serverJson.submitAnswer(list,partname,timer.getText().toString(),fullTimer.getText().toString(),qr.getText().toString());
                // Toast.makeText(Questions.this, "Records submitted successfully", Toast.LENGTH_SHORT).show();
                Toast.makeText(Questions.this, "Ok: " + ok + " times & not ok: " + not_ok + "times", Toast.LENGTH_SHORT).show();
                Log.e("part time",fullTimer.getText().toString());

                helper.getAllAnswers();
                timer.setBase(SystemClock.elapsedRealtime());
            }
            if(pnames.size()>0&&pnames.get(0).equals(partname)){
                pnames.remove(0);
            }

            if(pnames.size()==0){
                //all part questios are completed
                qrScanned="";
                timer.stop();
                fullTimer.stop();
                serverJson=new ServerJson(Questions.this);
                serverJson.insertTotalTime(Questions.this,qr_res,"sukrut",fullTimer.getText().toString());
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.remove(partFragment);
                fragmentTransaction.commit();
                TextView t=new TextView(Questions.this);
                CompleteDialog completeDialog=new CompleteDialog(Questions.this);
                completeDialog.show();
                Log.e("submit time",fullTimer.getText().toString());
                btnNext.setVisibility(View.INVISIBLE);
                partTimeLayout.setVisibility(View.INVISIBLE);
                fullTimeLayout.setVisibility(View.INVISIBLE);
                qr.setText("");
                return;
            }



            //add fragments
            list=new ArrayList<>();
            if(pnames.size()>0) {

                partname = pnames.get(0);
                pnames.remove(0);

                Questions_main.qNo=1;
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.remove(partFragment);
                partFragment = new PartFragment(list, partname);
                fragmentTransaction.add(R.id.layoutQuestions, partFragment).commit();
            }
            else{
                Toast.makeText(Questions.this, "all questions completed", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();//stop first and then start
        startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(questionTimer);
    }
    public void startHandler() {
        handler.postDelayed(questionTimer, 15*60*1000); //for 15 minutes
    }
    @Override
    protected void onPause() {
        super.onPause();
        LinkedList<String> ppnames=new LinkedList<>(pnames);
        long fullTime= fullTimer.getBase();
        String qr_code=qr.getText().toString();

        if(ppnames.size()>0 &&active){
        if(partname!=firstPart)
            ppnames.addFirst(partname);
            MyDbHelper myDbHelper=new MyDbHelper(this,MyDbHelper.DB_NAME,null,1);
            myDbHelper.setRemainingParts(ppnames,fullTime,qr_code);
        }
    }
}