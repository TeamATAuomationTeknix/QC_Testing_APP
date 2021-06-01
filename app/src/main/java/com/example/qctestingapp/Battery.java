package com.example.qctestingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class Battery extends AppCompatActivity {

    public static TextView qr, txt;
    private ImageButton scanqr, scanqr1;
    private String qr_res;
    String model_name;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        textView = findViewById(R.id.txtView);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        qr = (TextView) findViewById(R.id.txt_qr);
        scanqr = (ImageButton) findViewById(R.id.btn_scan_qr);
        scanqr1 = (ImageButton) findViewById(R.id.btn_scan_qr2);

        qr_res = getIntent().getStringExtra("qr_result");
        qr.setText(qr_res);
        if(qr_res!=null){
            try {
                checking();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ScanQR.class));
                Intent i = new Intent(Battery.this, ScanQR.class);
                i.putExtra("calling_page", "Battery");
                startActivity(i);
                finish();
            }
        });

        scanqr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ScanQR.class));
                Intent i = new Intent(Battery.this, ScanQR1.class);
                i.putExtra("calling_page", "Battery");
                startActivity(i);
                finish();
            }
        });


        qr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputQRCode();
                return false;
            }
        });

    }

    private String getModel(){
        if(model_name==null) {
            String  qr_code = qr.getText().toString().trim();
            String[] a = qr_code.split("_");
            String model_name = a[1].charAt(1)+""+a[1].charAt(2);
        }
        return model_name;
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
                String qr_code = inputQR.getText().toString();//toUpperCase().trim().replaceAll("\\s+", "").replaceAll("\n","");
                Pattern p = Pattern.compile("\\S{2}\\d{1}\\S{2}\\d{1}\\S{4}\\d{1}\\S{1}\\d{5}\\S{3}\\d{1}\\S{4}\\d{1}\\S{2}\\d{2}\\S{2}\\d{1}\\S{2}");
                if (!qr_code.equals("") && p.matcher(qr_code).matches()) {
                    qr_code = qr_code.substring(0, 17) + "_" + qr_code.substring(17, qr_code.length())+"_";
                    Intent i = new Intent(Battery.this, Battery.class);
                    i.putExtra("qr_result", qr_code);
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
    private void checking() throws ParseException {
        String txtQR = qr.getText().toString();
        String[] arr=txtQR.split(":");

        Date date = new Date();
        long today = date.getTime();
        Log.e("date",new SimpleDateFormat("dd/MM/yyyy").format(date));
        Date prev = new SimpleDateFormat("ddMMyy").parse(arr[3]);
        Log.e("3rd array",arr[3]);
        Log.e("date",new SimpleDateFormat("dd/MM/yyyy").format(prev));
        long pretime = prev.getTime();
        long milliseconds = today - pretime;
        Log.e("milliseconds",milliseconds+"");
        int days = (int) (milliseconds / (60*60*24*1000));
        Log.e("days", days + "");
        if (days <= 45) {
            textView.setText("Okay");
            textView.setBackgroundColor(getResources().getColor(R.color.color_ok));
        } else {
            textView.setText("Not Okay");
            textView.setBackgroundColor(getResources().getColor(R.color.color_not_ok));
        }
    }

}