package com.example.MVMcR_MA_QCR;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;

import java.util.List;

public class CarDetails extends AppCompatActivity {
    TextView qr;
    ImageButton scanqr;
    String qr_res="";
    List<Questions_main> list;
    RecyclerView recyclerView;
    CarDetailsAdapter adapter;
    CommonMethods commonMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        commonMethods=new CommonMethods();
        recyclerView=findViewById(R.id.recyclerCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        qr=findViewById(R.id.txt_qr);
        checkRemainingParts();
        qr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputQRCode();
                return false;
            }
        });
        scanqr = (ImageButton) findViewById(R.id.btn_scan_qr);
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ScanQR.class));
                Intent i = new Intent(CarDetails.this, ScanQR.class);
                i.putExtra("calling_page", "CarDetails");
                startActivity(i);
                finish();
            }
        });
        qr_res = getIntent().getStringExtra("qr_result");


        qr.setText(qr_res);
        if(qr_res!=null&&!qr_res.equals("")){
            if(!commonMethods.checkQR(qr_res)){
                Toast.makeText(this,"Invalid QR Code...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(CarDetails.this, com.example.MVMcR_MA_QCR.CheckPoints.class);
            i.putExtra("qr_result", qr_res);
            i.putExtra("qrScanned","scanned");
            startActivityForResult(i,1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        MyDbHelper myDbHelper=new MyDbHelper(this);
//        list=myDbHelper.getAllAnswers();
//        for(Questions_main q:list){
//            Log.e("qr",q.getQr_code());
//        }
//        if(list!=null&&list.size()>0){
//            adapter=new CarDetailsAdapter(list,this);
//            recyclerView.setAdapter(adapter);
//        }
    }

    // TODO: 20-07-2021 Manual qr code
    private void inputQRCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR Code.");
        builder.setMessage("Enter Correct QR Code.");
        // Set up the input
        final EditText inputQR = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputQR.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        int maxLength = 37;
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        inputQR.setFilters(filters);
        builder.setView(inputQR);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String qr_code=inputQR.getText().toString();
                if(qr_code.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Enter QR Code...!", Toast.LENGTH_LONG).show();
                    inputQRCode();
                    return;
                }

               // if(qr_code.length()==37){
                if(commonMethods.checkQR(qr_code)){
                    //parts.setEnabled(false);
                    Main_page.partEnabled=false;
                    // qr_code = qr_code.substring(0, 17) + "_" + qr_code.substring(17, qr_code.length())+"_";
                    Intent i = new Intent(CarDetails.this, com.example.MVMcR_MA_QCR.CheckPoints.class);
                    i.putExtra("qr_result", qr_code);
                    i.putExtra("qrScanned","scanned");

                    startActivityForResult(i,1);

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
    //todo add home icon to tolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
public void checkRemainingParts(){
        String qr="";
        MyDbHelper myDbHelper=new MyDbHelper(this);
        qr=myDbHelper.backPressQr();

    if(qr!=null&&!qr.equals("")){
        Intent i = new Intent(CarDetails.this, com.example.MVMcR_MA_QCR.CheckPoints.class);
        i.putExtra("qr_result", qr);
        i.putExtra("qrScanned","scanned");
        startActivityForResult(i,1);
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            qr.setText("");
            Intent i = new Intent(CarDetails.this, ScanQR.class);
            i.putExtra("calling_page", "CarDetails");
            startActivity(i);
            finish();
        }
    }
}