package com.example.qctestingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ServerJson {
    ArrayList<String> partnames;
    Context context;
    ArrayList<Questions_main> qlist;
    RecyclerView recyclerView;
    ProgressDialog p;
    ProgressDialog p1;
    String partname;
    MyDbHelper myDbHelper;
    AlertDialog.Builder builder;

    public ServerJson(Context context, ArrayList<Questions_main> qlist, RecyclerView recyclerView, String partname) {
        this.context=context;
        this.qlist=qlist;
        this.recyclerView= recyclerView;
        this.partname=partname;
        Log.e("getting from partname: ",partname);
    }

    public ServerJson(Context context, ArrayList<String> pnames) {
        this.context = context;
        partnames=pnames;
    }
    public ServerJson(Context context) {
        this.context = context;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    public ArrayList<String> getPartnames() {
        return partnames;
    }

    public void setPartnames(ArrayList<String> partnames) {
        this.partnames = partnames;
    }

    //********************get answers**********************
    public ArrayList<Questions_main> getAnswers(String qr, RecyclerView recyclerViewQCheck) {
        final ProgressDialog dialog=new ProgressDialog(context);
        ArrayList<Questions_main> qqlist=new ArrayList<Questions_main>();
        partname=partname.replace(" ","%20");

        dialog.setMessage("Please wait... Answers checking from server");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        Log.e("tag","showing progress dialog of answers");
        dialog.show();
        MySingleton mySingleton=MySingleton.getInstance(context);
        RequestQueue requestQueue= mySingleton.getRequestQueue();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Main_page.IP_ADDRESS+"/GetAnswers.php/?partname="+partname+"&qr="+qr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray=new JSONArray(response);
                            // (id Integer , question text,answer Integer, Highlight Integer, partname varchar, qr varchar, user varcha
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                Questions_main q= new Questions_main(jsonObject.getInt("id"),jsonObject.getString("question"),jsonObject.getString("answer"),
                                        false,qr);
                                Log.e("answer from server",q.toString());
                                qqlist.add(q);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(qqlist.size()>0) {
                            MyDbHelper myDbHelper = new MyDbHelper(dialog.getContext(), MyDbHelper.DB_NAME, null, 1);
                            myDbHelper.insert_data(qqlist, ServerJson.this.getPartname(), qr, "sukrut");

                            QCheckAdapter qCheckAdapter = new QCheckAdapter(dialog.getContext(), qqlist);
                            recyclerViewQCheck.setAdapter(qCheckAdapter);
                        }
                        dialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.hide();
                        Log.e("temp saving error:",error.toString());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();

                params.put("partname",partname);
                params.put("qr",qr);
                return params;
            }

        };
        // MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);

        requestQueue.add(stringRequest);
        return qqlist;
    }

    //*********************************access questions**************************************
    public void volleyRequest(){
        partname=partname.replace(" ","%20");
        p=new ProgressDialog(context);
        p.setMessage("Please wait... Questions are downloading");
        p.setIndeterminate(false);
        p.setCancelable(false);
        Log.e("tag","showing progress dialog");
        p.show();
        MySingleton m=MySingleton.getInstance(context);

        RequestQueue requestQueue= m.getRequestQueue();
        Log.e("getting from partname: ",partname);
        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(

                Request.Method.POST,
                Main_page.IP_ADDRESS + "/PhpMySql.php?partname="+partname,
                null,
                new JSONObjectResponseListener(),
                new ErrorListner()
        );
//        {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String,String> hm=new HashMap<>();
//                hm.put("partname","Ext(Wheel Rim type)");
//                return hm;
//            }
//        };
        requestQueue.add(jsonObjectRequest);
    }


    class JSONObjectResponseListener implements Response.Listener<JSONArray>{

        @Override
        public void onResponse(JSONArray response) {

            p.hide();
            Log.e("length of ressponse",response.length()+"");
            try {
                for(int i=0;i<response.length();i++) {
                    JSONObject obj = response.getJSONObject(i);
                    Log.e("json tag", obj.getInt("id") + " " + obj.getString("question") + " " + obj.getInt("Highlight"));
                    boolean flag = obj.getInt("Highlight") == 1 ? true : false;
                    qlist.add(new Questions_main(obj.getInt("id"), obj.getString("question"), flag));
                    //qlist.add(new Questions_main(obj.getString("question"), flag));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if(partname!=null) {
                    String pn = partname.replace("%20", " ");
                    myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                    if (qlist != null && pn != null) myDbHelper.addQuestions(qlist, pn);
                    QuestionsAdapter adapter = new QuestionsAdapter(qlist);
                    if (recyclerView != null)
                        recyclerView.setAdapter(adapter);
                }
            }
        }
    }
    class ErrorListner implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            p.hide();

            Log.e("json ERROR",error.toString());
        }
    }

    //**************************************************get partname*************************************
    public void getPartName(){

        p1=new ProgressDialog(context);
        p1.setMessage("Please wait... getting parts");
        p1.setIndeterminate(false);
        p1.setCancelable(false);
        Log.e("tag","showing progress dialog");
        p1.show();
        MySingleton m=MySingleton.getInstance(context);
        RequestQueue requestQueue= m.getRequestQueue();

        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(
                Request.Method.GET,
                Main_page.IP_ADDRESS + "/GetPartNames.php",
                null,
                new GetPartnameListner(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p1.hide();
                        Toast.makeText(context, "Not connected to network", Toast.LENGTH_SHORT).show();
                        Log.e("json ERROR",error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);


    }

    class GetPartnameListner implements Response.Listener<JSONArray>{
        @Override
        public void onResponse(JSONArray response) {
            p1.hide();

            try {
                for(int i=0;i<response.length();i++) {
                    JSONObject obj = response.getJSONObject(i);
                    partnames.add(obj.getString("partname"));
                    Log.e("json tag", obj.getString("partname"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                MyDbHelper myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                myDbHelper.addPartNames(partnames);

            }


        }
    }


    //****************************************Submit answers*********************************
    public void submitAnswer(ArrayList<Questions_main> answerList, String partname, String partTime, String fullTime, String qr_res){
        builder=new AlertDialog.Builder(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                Main_page.IP_ADDRESS+"/InsertAnswer.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        builder.setTitle("Done");
                        // builder.setMessage("message: "+response);
                        builder.setMessage("Records submitted successfully");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Connection problem", Toast.LENGTH_SHORT).show();
                        Log.e("submit answer",error.toString());
                        Toast.makeText(context, "Data is stored locally", Toast.LENGTH_SHORT).show();
                        myDbHelper=new MyDbHelper(context,MyDbHelper.DB_NAME,null,1 );
                        for(Questions_main q:answerList) {
                            int ans=q.getAnswer()=="OK"?1:0;
                            //( int qid, String partname, String qrcode, String operator,String answer,String partTime, Date TimeStamp)
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            myDbHelper.submitTempAnswers(q.getId(),partname,qr_res,"sukrut",ans,partTime,formatter.format(new Date()));
                        }
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                JSONArray jsonArray=new JSONArray();
                JSONObject jsonObjet ;
                String part=partname.replace("%20"," ");
                for(Questions_main q:answerList) {

                    try {
                        jsonObjet= new JSONObject();
                        jsonObjet.put("partname", part);
                        jsonObjet.put("id_fk_lhs_all_prt_que_tbl", q.getId());
                        if(qr_res!=null)
                            jsonObjet.put("qr_code", qr_res);
                        else
                            jsonObjet.put("qr_code", "100");
                        jsonObjet.put("operator", "sukrut");
                        int ans=q.getAnswer()=="OK"?1:0;
                        jsonObjet.put("answer", ans);
                        jsonObjet.put("partTime",partTime);
                        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        jsonObjet.put("currentTime",formatter.format(new Date()));
                        jsonArray.put(jsonObjet);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String op=jsonArray.toString();
                Log.e("json array",op);
                params.put("value",op);

                return params;
            }

        };
        // MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    public void submitTempAnswer(JSONArray jsonArray){
        builder=new AlertDialog.Builder(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                Main_page.IP_ADDRESS+"/InsertAnswer.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyDbHelper myDbHelper=new MyDbHelper(context,MyDbHelper.DB_NAME,null,1);
                        myDbHelper.deleteTempAnswers();
                        Log.e("temp answers: " ,"submitted and deleted");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("temp saving error:",error.toString());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();

                String op=jsonArray.toString();
                Log.e("json array temp: ",op);
                params.put("value",op);

                return params;
            }

        };
        // MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
// add images**************************************

    public void fetchImagesFromServer(){

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ImageRegistration.FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            MyDbHelper db = new MyDbHelper(context,MyDbHelper.DB_NAME,null,1);
                            db.deleteAllImages();

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String model_nm = jsonObject.getString("img_name");
                                String image = jsonObject.getString("img");

                                // db.addImage(id, model_nm, image.getBytes());
                                db.addImage(id, model_nm, Base64.decode(image.substring(23), Base64.DEFAULT));

                            }

                            progressDialog.dismiss();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                //Toast.makeText(getApplicationContext(),"Sorry"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }


    public void insertTotalTime(Context context,String qr,String operator,String time){
        MySingleton mySingleton=MySingleton.getInstance(context);
        RequestQueue requestQueue= mySingleton.getRequestQueue();
        String url= Main_page.IP_ADDRESS + "/InsertTotalTime.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("insertTotalTime: ",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error: ",qr+" "+operator+" "+time+"//"+error.toString());
            }
        }){
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("qr",qr);
                hashMap.put("operator",operator);
                hashMap.put("total_time",time);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

}


