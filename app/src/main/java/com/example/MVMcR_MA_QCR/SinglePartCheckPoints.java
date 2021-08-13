package com.example.MVMcR_MA_QCR;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class SinglePartCheckPoints extends AppCompatActivity {
    CommonMethods methods;
    TextView txtVin,txtModel,imageDescr;
    Button btnOk,btnNotOk;
    ImageView imageView;
    String qr_code="";
    String vin="";
    String partname="";
    ArrayList<Questions_main> questionsList;
    ArrayList<String> stringList;
    public RecyclerView recyclerView;
    Chronometer timer;
    String imageDescription;
    int position;
    ArrayList<String> remarkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_part_check_points);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        Intent intent=getIntent();
        imageDescription="";
        imageDescr=findViewById(R.id.imageDescr);
        timer=findViewById(R.id.timer);
        long b=intent.getLongExtra("timerBase",00);
        timer.setBase(b);
        timer.start();

        remarkList=new ArrayList<String>();
        methods=new CommonMethods();
        btnOk=findViewById(R.id.btnOk);
        btnNotOk=findViewById(R.id.btnNotOk);
        txtVin=findViewById(R.id.textViewQr);
        txtModel=findViewById(R.id.textViewModel);
        recyclerView=findViewById(R.id.recyclerCarDetails);
        imageView=findViewById(R.id.partImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stringList=new ArrayList<>();

        position=intent.getIntExtra("position",1);
        partname=intent.getStringExtra("partname");
        qr_code=intent.getStringExtra("qr_result");
        String[] arr=qr_code.split("_");
        vin=arr[0];
        txtVin.setText("VIN: "+vin);
        txtModel.setText("Model: "+methods.getPlatform(qr_code)+" "+methods.getVarient(qr_code));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.putExtra("result","OK");
                intent1.putExtra("position",position);
                setResult(1,intent1);
                finish();
            }
        });
        btnNotOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(SinglePartCheckPoints.this).maxResultSize(512,512).cameraOnly()
                        .start(103);
//                if(remarkList.size()==0){
//                    Intent intent1=new Intent();
//                    intent1.putExtra("result","NOK");
//                    intent1.putExtra("position",position);
//                    intent1.putExtra("concern","not ok");
//                    setResult(1,intent1);
//                    finish();
//                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bitmap bmImg;
        questionsList=new ArrayList<>();
        MyDbHelper myDbHelper=new MyDbHelper(this);
        questionsList=myDbHelper.getQuestions(partname,methods.getPlatform(qr_code),methods.getVarient(qr_code));
        Cursor c1=myDbHelper.getAllImagesOfSpecificModel(methods.getPlatform(qr_code),partname,methods.getVarient(qr_code));
        imageDescr.setText("Part: "+partname);
        if(c1.moveToFirst()){
            byte[] arrImg= c1.getBlob(1);
            imageDescription=c1.getString(2);
            if(imageDescription!=null)
               // imageDescr.setText(imageDescription);
                imageDescr.setText("Part: "+partname);
            // is= arrImg;
            bmImg= BitmapFactory.decodeByteArray(arrImg,0,arrImg.length);
            imageView.setImageBitmap(bmImg);

        }
        else{
            //Toast.makeText(this, "Please add image for: "+partname, Toast.LENGTH_SHORT).show();
        }
        for(Questions_main q:questionsList){
            stringList.add(q.getQuestion());
            int id=q.getId();
            ArrayList<String> remarksByQID=myDbHelper.getRemarksByQID(id);
            remarkList.addAll(remarksByQID);
        }
        if (stringList != null && stringList.size() > 0) {
            TextViewAdapter t=new TextViewAdapter(this,stringList);
            recyclerView.setAdapter(t);
        }
        // TODO: 29-07-2021 if questions list is empty load data from server
        if(questionsList!=null&&questionsList.size()==0){
            Log.e("data:",methods.getPlatform(qr_code)+" "+methods.getVarient(qr_code));
            ServerJson serverJson=new ServerJson(SinglePartCheckPoints.this);
            serverJson.getQuestions(methods.getPlatform(qr_code),methods.getVarient(qr_code),partname,recyclerView );

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==103){
            Uri uri=data.getData();
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(uri);
                byte[] imageBitmap = methods.getBytes(iStream);
                Intent intent1=new Intent();
                intent1.putExtra("result","NOK");
                intent1.putExtra("position",position);
                intent1.putExtra("imageBitmap",imageBitmap);
                setResult(2,intent1);
                finish();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}