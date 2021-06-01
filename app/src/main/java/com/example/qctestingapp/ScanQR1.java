package com.example.qctestingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQR1 extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private String page;
    public static TextView qu;
    private String partname;
    ZXingScannerView scannerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(ScanQR1.this);
        setContentView(scannerView);

        qu = (TextView) findViewById(R.id.txtView);
        Log.e("tag", "scan qr activity started");
        //Getting data from ScanQR
        page = getIntent().getStringExtra("calling_page");
        partname = getIntent().getStringExtra("partname");
        // Add Back Arrow to Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            switch (page){
                case "Questions":
                    Intent qu = new Intent(ScanQR1.this, Battery.class);
                    qu.putExtra("partname",partname);
                    qu.putExtra("qrScanned","no");
                    startActivity(qu);
                    finish();
                    break;
                default:
                    Toast.makeText(ScanQR1.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result rawResult) {
        Pattern p = Pattern.compile("\\d{3}:\\d{4}\\S{2}\\d{5}\\S{1}:\\S{2}\\d{3}:\\S{6}:\\d{2}:\\d{5}");
        //067:1401AA00762N:DA040:140421:02:06163
        if (p.matcher(rawResult.getText()).matches()) {
            //Toast.makeText(ScanQR.this, "Done", Toast.LENGTH_SHORT).show();
            switch (page){
                case "Questions":
                    Intent qu = new Intent(ScanQR1.this, Battery.class);
                    qu.putExtra("partname",partname);
                    qu.putExtra("qrScanned","scanned");
                    qu.putExtra("qr_result", rawResult.getText());
                    startActivity(qu);
                    finish();
                    break;
                default:
                    Toast.makeText(ScanQR1.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    break;
            }


        } else {
            Toast.makeText(ScanQR1.this, "Invalid QR Code...!", Toast.LENGTH_LONG).show();
            switch (page){
                case "Questions":
                    Intent qu = new Intent(ScanQR1.this, Battery.class);
                    qu.putExtra("partname",partname);
                    qu.putExtra("qrScanned","o");
                    startActivity(qu);
                    finish();
                    break;
                default:
                    Toast.makeText(ScanQR1.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }

    @Override
    public void onBackPressed() {

        switch (page) {
            case "Questions":
                Intent qu = new Intent(ScanQR1.this, Battery.class);
                qu.putExtra("partname", "partname");
                qu.putExtra("qrScanned", "no");
                startActivity(qu);
                finish();
                break;
            default:
                Toast.makeText(ScanQR1.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String battery_no(){
        String  qr_code = qu.getText().toString().trim();
        String[] a = qr_code.split(":");
        String model_name = a[4].charAt(1)+""+a[4].charAt(2)+""+a[4].charAt(3)+""+a[4].charAt(4)+""+a[4].charAt(5)+""+a[4].charAt(6);
        return model_name.trim();
    }

    private void checking() {
        String txtQR = qu.getText().toString().trim();

        if (txtQR.equals(battery_no())){
            qu.setText("Okay");
        }
        else {
            qu.setText("Not Okay");
        }
    }
}
