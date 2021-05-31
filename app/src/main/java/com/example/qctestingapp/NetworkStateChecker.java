package com.example.qctestingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            //if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //getting all the unsynced names
               // Toast.makeText(context, "network Connected...", Toast.LENGTH_SHORT).show();
                Questions.isConnected=true;
               submitAnswer();
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
               // Toast.makeText(context, "Mobile network connected", Toast.LENGTH_SHORT).show();
                Questions.isConnected=true;
                submitAnswer();
            }
            else {
                Questions.isConnected=false;
            }

        }
        else{
            Questions.isConnected=false;
        }
    }
    public void submitAnswer(){
        MyDbHelper myDbHelper=new MyDbHelper(context,MyDbHelper.DB_NAME,null,1);
        JSONArray jsonArray=myDbHelper.getTempAnswers();
        if(jsonArray!=null) {
            ServerJson serverJson = new ServerJson(context);
            serverJson.submitTempAnswer(jsonArray);
        }
    }
}