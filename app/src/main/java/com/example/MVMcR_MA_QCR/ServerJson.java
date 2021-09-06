package com.example.MVMcR_MA_QCR;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteTransactionListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;
import com.example.MVMcR_MA_QCR.DataClass.ProductionReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ServerJson {
    ArrayList<String> partnames;
    ArrayList<MyDbHelper.Parts> partnames1;
    Context context;
    ArrayList<Questions_main> qlist;
    RecyclerView recyclerView;
    ProgressDialog p;
    ProgressDialog p1;
    String partname;
    MyDbHelper myDbHelper;
    AlertDialog.Builder builder;
    CommonMethods commonMethods;
    OnResponseInterface onResponseInterface;
    public ServerJson(Context context, ArrayList<Questions_main> qlist, RecyclerView recyclerView, String partname) {
        this.context=context;
        this.qlist=qlist;
        this.recyclerView= recyclerView;
        this.partname=partname;
        commonMethods=new CommonMethods();
        Log.e("getting from partname: ",partname);
    }

    public ServerJson(Context context, ArrayList<String> pnames) {
        this.context = context;
        partnames=pnames;
        commonMethods=new CommonMethods();
    }
    public ServerJson(Context context) {
        this.context = context;
        commonMethods=new CommonMethods();
    }

    public OnResponseInterface getOnResponseInterface() {
        return onResponseInterface;
    }

    public void setOnResponseInterface(OnResponseInterface onResponseInterface) {
        this.onResponseInterface = onResponseInterface;
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

    //********************todo get answers**********************
    public ArrayList<Questions_main> getAnswers(String qr, RecyclerView recyclerViewQCheck,QCheck qCheck) {
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
        String url=Main_page.IP_ADDRESS+"/GetAnswers.php/?partname="+partname+"&qr="+qr;
        Log.e("url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    String user="";
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            // (id Integer , question text,answer Integer, Highlight Integer, partname varchar, qr varchar, user varcha,remark
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                //user=jsonObject.getString("operator");
                                Questions_main q= new Questions_main(jsonObject.getInt("id"),jsonObject.getString("question"),jsonObject.getString("answer"),
                                        "NOHIGHLIGHT",qr);
                                q.setRemark(jsonObject.getString("remark"));
                                Log.e("answer from server",q.toString());
                                qqlist.add(q);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(qqlist.size()>0) {

                            MyDbHelper myDbHelper = new MyDbHelper(dialog.getContext(), MyDbHelper.DB_NAME, null, 1);
                            myDbHelper.insert_data(qqlist, ServerJson.this.getPartname().replace("%20"," "), qr, "user");
                           qCheck.addFragments(qqlist,true);
//                            QCheckAdapter qCheckAdapter = new QCheckAdapter(dialog.getContext(), qqlist);
//                            recyclerViewQCheck.setAdapter(qCheckAdapter);
                        }
                        else{
                            qCheck.removeFragment();
                            qCheck.recyclerViewQCheck.setVisibility(View.INVISIBLE);
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
                });
         MySingleton.getInstance(context).addToRequestQue(stringRequest);
        //requestQueue.add(stringRequest);
        return qqlist;
    }


    //*********************************todo access questions**************************************
    public void volleyRequest(String platform, String varient){
        //partname=partname.replace(" ","%20");
        p=new ProgressDialog(context);
        p.setMessage("Please wait... Questions are downloading");
        p.setIndeterminate(false);
        p.setCancelable(false);
        Log.e("tag","Showing progress dialog");
        p.show();
        MySingleton m=MySingleton.getInstance(context);

        RequestQueue requestQueue= m.getRequestQueue();
        Log.e("getting from partname: ",partname);
        //String url=Main_page.IP_ADDRESS + "/GetQuestionsByModel.php?partname=" + partname + "&model_name=" + platform;
        String url=Main_page.IP_ADDRESS + "/GetQuestionsByModel.php";
        StringRequest jsonObjectRequest=new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("question response",response);
                        p.hide();
                        Log.e("length of ressponse",response.length()+"");
                        try {
                            JSONArray jsonArrays=new JSONArray(response);
                            for(int i=0;i<response.length();i++) {
                                JSONObject obj = jsonArrays.getJSONObject(i);
                                JSONArray array=obj.getJSONArray("remarks");
                                Log.e("json question", obj.getInt("id") + " " + obj.getString("question") );
                                Log.e("json remarks","remarks");
                                for(int j=0;j<array.length();j++){
                                    JSONObject remark=array.getJSONObject(j);
                                    Log.e("remark ",remark.toString());
                                }
                                String highlight = obj.getString("Highlight");
                                Questions_main question=new Questions_main(obj.getInt("id"), obj.getString("question"), highlight);
                                question.setPartname(partname.replace("%20", " "));
                                question.setPlatform(platform);
                                question.setVarient(varient);
                                qlist.add(question);
                                //qlist.add(new Questions_main(obj.getString("question"), flag));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            if(partname!=null) {
                                String pn = partname.replace("%20", " ");
                                myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                                if (qlist != null && pn != null) {

                                    myDbHelper.addQuestions(qlist);
                                }
                                QuestionsAdapter adapter = new QuestionsAdapter(qlist,context);
                                if (recyclerView != null) {
                                    Questions.partcount=Questions.devidedparts/qlist.size();
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        }

                    }
                },
                new ErrorListner()
        )
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hm=new HashMap<>();
                hm.put("partname",partname);
                hm.put("model_name",platform);
                hm.put("varient",varient);
                return hm;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void getAllQuestions() {

        String url=Main_page.IP_ADDRESS+"/GetAllQuestions.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            String platform="";
            String varient="";
            ArrayList<Questions_main> qlist;
            @Override
            public void onResponse(String response) {
                qlist=new ArrayList<>();
                Log.e("question response",response);

                Log.e("length of ressponse",response.length()+"");
                try {
                    JSONArray jsonArrays=new JSONArray(response);
                    for(int i=0;i<response.length();i++) {
                        JSONObject obj = jsonArrays.getJSONObject(i);
                        JSONArray array=obj.getJSONArray("remarks");
                        Log.e("json question", obj.getInt("id") + " " + obj.getString("question") );
                        Log.e("json remarks","remarks");
                        platform=obj.getString("platform");
                        varient=obj.getString("varient");
                        partname=obj.getString("partname");
                        for(int j=0;j<array.length();j++){
                            JSONObject remark=array.getJSONObject(j);
                            Log.e("remark ",remark.toString());
                        }
                        String highlight = obj.getString("Highlight");
                        Questions_main question=new Questions_main(obj.getInt("id"), obj.getString("question"), highlight);
                        question.setPlatform(platform);
                        question.setVarient(varient);
                        question.setPartname(partname);
                        qlist.add(question);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    if(partname!=null) {
                        String pn = partname.replace("%20", " ");
                        myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                        if (qlist != null && pn != null)
                            myDbHelper.addQuestions(qlist);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getQuestions(String platform, String varient, String partname, RecyclerView recyclerView){
       qlist=new ArrayList<>();
        p=new ProgressDialog(context);
        p.setMessage("Please wait... Questions are downloading");
        p.setIndeterminate(false);
        p.setCancelable(false);
        Log.e("tag","Showing progress dialog");
        p.show();
        MySingleton m=MySingleton.getInstance(context);
        RequestQueue requestQueue= m.getRequestQueue();
        Log.e("getting from partname: ",partname);
        String url=Main_page.IP_ADDRESS + "/GetQuestionsByModel.php";
        StringRequest jsonObjectRequest=new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("question response",response);
                        p.hide();
                        Log.e("length of ressponse",response.length()+"");
                        try {
                            JSONArray jsonArrays=new JSONArray(response);
                            for(int i=0;i<response.length();i++) {
                                JSONObject obj = jsonArrays.getJSONObject(i);
                                JSONArray array=obj.getJSONArray("remarks");
                                Log.e("json question", obj.getInt("id") + " " + obj.getString("question") );
                                Log.e("json remarks","remarks");
                                for(int j=0;j<array.length();j++){
                                    JSONObject remark=array.getJSONObject(j);
                                    Log.e("remark ",remark.toString());
                                }
                                String highlight = obj.getString("Highlight");
                                Questions_main question=new Questions_main(obj.getInt("id"), obj.getString("question"), highlight);
                                question.setPartname(partname.replace("%20", " "));
                                question.setPlatform(platform);
                                question.setVarient(varient);
                                qlist.add(question);
                                //qlist.add(new Questions_main(obj.getString("question"), flag));
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            ArrayList<String> questionlist=new ArrayList<>();
                            if(partname!=null) {
                                String pn = partname.replace("%20", " ");
                                myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                                if (qlist != null && pn != null) {
                                    myDbHelper.addQuestions(qlist);
                                    for(Questions_main q:qlist){
                                        questionlist.add(q.getQuestion());
                                    }
                                    TextViewAdapter t=new TextViewAdapter(context,questionlist);
                                    recyclerView.setAdapter(t);
                                }
                            }
                        }

                    }
                },
                new ErrorListner()
        )
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hm=new HashMap<>();
                hm.put("partname",partname);
                hm.put("model_name",platform);
                hm.put("varient",varient);
                return hm;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    class ErrorListner implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            p.hide();

            Log.e("json ERROR",error.toString());
        }
    }

    //**************************************************todo get partname*************************************
    public void getPartName(){

        p1=new ProgressDialog(context);
        p1.setMessage("Please wait... getting parts");
        p1.setIndeterminate(false);
        p1.setCancelable(false);
        Log.e("tag","showing progress dialog");
        p1.show();
        MySingleton m=MySingleton.getInstance(context);
        RequestQueue requestQueue= m.getRequestQueue();
        String url=Main_page.IP_ADDRESS + "/GetPartNames.php";
        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(
                Request.Method.GET,
                url,
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
        partnames1=new ArrayList<>();
            try {
                for(int i=0;i<response.length();i++) {
                    JSONObject obj = response.getJSONObject(i);
                    // TODO: 31-07-2021  skip duplicate parts
//                    if(!partnames.contains(obj.getString("partname"))) {
                        partnames.add(obj.getString("partname"));
                        partnames1.add(new MyDbHelper.Parts(obj.getString("partname"), obj.getString("appname"),obj.getString("varient"),obj.getInt("part_id")));
                   // }
                    Log.e("json tag", obj.getString("partname"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                MyDbHelper myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                myDbHelper.addPartNames(partnames1);
            }
         }
    }
//****************************************todo Submit answers*********************************
    public void submitAnswer(ArrayList<Questions_main> answerList, String partname, String partTime, String fullTime, String qr_res,String user){
        builder=new AlertDialog.Builder(context);
        String url=Main_page.IP_ADDRESS+"/InsertAnswer1.php";
        Log.e("ip",url);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        builder.setTitle("Done");
                         //builder.setMessage("message: "+response);
                         Log.e("submit answer",response);
                        builder.setMessage("Records submitted successfully");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
  //                      AlertDialog alertDialog=builder.create();
 //                       alertDialog.show();
                        Log.e("hehe","hehe");
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
                            int ans=q.getAnswer().equals("OK")?1:0;
                            //( int qid, String partname, String qrcode, String operator,String answer,String partTime, Date TimeStamp)
                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            myDbHelper.submitTempAnswers(q.getId(),partname,qr_res,user,ans,partTime,formatter.format(new Date()),q.getRemark(),q.getNokImage());

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
                        jsonObjet.put("operator", user);
                        int ans=q.getAnswer().equals("OK")?1:0;
                        Log.e("ans",q.getAnswer());
                        jsonObjet.put("answer", ans);
                        jsonObjet.put("partTime",partTime);
                        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        jsonObjet.put("currentTime",formatter.format(q.getSubmissionTime()));
                        jsonObjet.put("remark",q.getRemark());
                        if(ans==0) {
                            byte[] bb=q.getNokImage();
                            if(bb==null)
                            jsonObjet.put("nokImage", 00);
                            else
                                jsonObjet.put("nokImage", commonMethods.byteArrayToBase64(bb));
                        }
                            else
                            jsonObjet.put("nokImage",0);
                        jsonObjet.put("platform",commonMethods.getPlatform(qr_res));
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
                Main_page.IP_ADDRESS+"/InsertAnswer1.php",
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
// todo get images from server**************************************

    public void fetchImagesFromServer(){

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=Main_page.IP_ADDRESS + "/GetMasterImageByModel.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            MyDbHelper db = new MyDbHelper(context,MyDbHelper.DB_NAME,null,1);
                            db.deleteAllImages();
                            Log.e("response for images",response);
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.e("adding image",i+"");
                                int id = jsonObject.getInt("id");
                                String part_name = jsonObject.getString("img_name");
                                String image = jsonObject.getString("img");
                                String model=jsonObject.getString("model_name");
                                String varient=jsonObject.getString("varient");
                                String imageDescription=jsonObject.getString("descr");
                                Log.e("model is",jsonObject.getString("model_name"));
                                 //db.addImage(id, model_nm, image.getBytes());
                                //db.addImage(id, model_nm, Base64.decode(image.substring(23), Base64.DEFAULT));
                                db.addImage( part_name,model,varient, Base64.decode(image, Base64.DEFAULT),imageDescription);

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
       // String url= Main_page.IP_ADDRESS + "/InsertTotalTime.php";
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
//****************** Get app name****************************
    public void getAppName(Context context, ArrayList<String> appNames){
        //ArrayList<String> appNames=appnames;
        p1=new ProgressDialog(context);
        p1.setMessage("Please wait...");
        p1.setIndeterminate(false);
        p1.setCancelable(false);
        Log.e("tag","showing progress dialog");
        p1.show();
        MySingleton m=MySingleton.getInstance(context);
        RequestQueue requestQueue= m.getRequestQueue();
        String url=Main_page.IP_ADDRESS + "/GetAppName.php";
        Log.e("url",url);
        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for(int i=0;i<response.length();i++) {
                                JSONObject obj = response.getJSONObject(i);
                                appNames.add(obj.getString("appname"));
                                Log.e("appname", obj.getString("appname"));
                            }

                        } catch (JSONException e) {
                            Log.e("getting appname",e.toString());
                            e.printStackTrace();
                        }
                        finally{
                            MyDbHelper myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                            myDbHelper.addAppNames(appNames);
                            //context.initparts(appNames);
                            p1.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p1.dismiss();
                        Toast.makeText(context, "Not connected to network", Toast.LENGTH_SHORT).show();
                        Log.e("json ERROR", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    public void getEmpInfo(String token){
        String tk="";
        ProgressDialog pg=new ProgressDialog(context);
        pg.setMessage("Please wait checking token...");
        pg.setTitle("Wait");
        pg.setIndeterminate(false);
        pg.setCancelable(false);
        pg.show();
        String url=Main_page.IP_ADDRESS+"/GetToken.php?token="+token;
        Log.e("token url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pg.dismiss();
                try {
                    JSONArray jsonArray=new JSONArray(response);

                        JSONObject jsonObject=  jsonArray.getJSONObject(0);
                        Log.e("emp token",jsonObject.getString("token"));
                        Log.e("emp name",jsonObject.getString("emp_name"));

                        if(token.equals(jsonObject.getString("token"))){
                            String empname=jsonObject.getString("emp_name");
                            SharedPreferences preferences=context.getSharedPreferences("userpref",MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("user",empname);
                            editor.apply();
                            MyDbHelper myDbHelper=new MyDbHelper(context,MyDbHelper.DB_NAME,null,1);
                            myDbHelper.addEmployee(jsonObject.getInt("token"),jsonObject.getString("emp_name"));
                            Toast.makeText(context, "Successfully login", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context, Main_page.class);
                            context.startActivity(i);
                        }
                        else{
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pg.dismiss();
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                Log.e("emp name error",error.toString());
            }
        });
//        {
//            @Nullable
//            @org.jetbrains.annotations.Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String,String> hashMap=new HashMap<>();
//                hashMap.put("token",token);
//                return hashMap;
//            }
//        };
        MySingleton singleton=MySingleton.getInstance(context);
        singleton.addToRequestQue(stringRequest);
    }
    public void insertBatteryStatus(HashMap<String,String> batteryInfo){
        builder=new AlertDialog.Builder(context);
        String url=Main_page.IP_ADDRESS+"/InsertBatteryInfo.php";
        Log.e("battery ip",url);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        builder.setTitle("Done");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        // builder.setMessage("message: "+response);
                        if(response.equals("success"))
                        builder.setMessage("Records submitted successfully");
                        else {
                           // builder.setMessage("Server problem:" + response);
                            Log.e("error in battery",response);
                        }
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
                            myDbHelper.batteryStatusTemp(batteryInfo);
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return batteryInfo;
            }

        };
        // MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    // TODO: 25-06-2021 fetch all cars report
    public void getReport(CurrentDataReport currentDataReport,RecyclerView recyclerView, Date date){
        List<Bundle> modelList=new ArrayList<>();
        p1=new ProgressDialog(context);
        p1.setMessage("Please wait...");
        p1.setIndeterminate(false);
        p1.setCancelable(false);
        Log.e("tag","showing progress dialog");
        p1.show();
        MySingleton m=MySingleton.getInstance(context);
        RequestQueue requestQueue= m.getRequestQueue();
        if(date.getHours()<7){
            date=getYesterday(date);
        }
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd 07:00:00");
        String url=Main_page.IP_ADDRESS + "/GetModelwiseDetails.php?date="+formatter.format(date);
        Log.e("report url",url);
        Date finalDate = date;
        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    int okcount=0,nokcount=0;
                        try {

                            for(int i=0;i<response.length();i++) {
                                Bundle bundle=new Bundle();
                                JSONObject obj = response.getJSONObject(i);
                                String model_name=obj.getString("model_name");
                                String total=obj.getString("total");
                                String ok=obj.getString("ok");
                                okcount=okcount+Integer.parseInt(ok);
                                String not_ok=obj.getString("notOk");
                                nokcount=nokcount+Integer.parseInt(not_ok);
                                bundle.putString("model_name",model_name);
                                bundle.putString("total",total);
                                bundle.putString("ok",ok);
                                bundle.putString("notOk",not_ok);
                                modelList.add(bundle);
                                Log.e("model name", obj.getString("model_name"));
                            }
                        } catch (JSONException e) {
                            Log.e("getting model details",e.toString());
                            e.printStackTrace();
                            p1.dismiss();
                        }
                        finally {
                            currentDataReport.addPieChart(okcount,nokcount);
                            ReportAdapter adapter=new ReportAdapter();
                            adapter.setData(modelList);
                            recyclerView.setAdapter(adapter);
                            p1.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyDbHelper myDbHelper=new MyDbHelper(context);

                        ArrayList<ProductionReport> report=myDbHelper.getCurrentDataReport(finalDate);
                        int okcount=0,not_okcount=0;
                        for(ProductionReport p:report){
                            Bundle bundle=new Bundle();
                            String model_name=p.getModel();
                            String total=Integer.toString(p.getTotal());
                            String ok=Integer.toString(p.getOk());
                            okcount=okcount+Integer.parseInt(ok);
                            String not_ok=Integer.toString(p.getNok());
                            not_okcount=not_okcount+Integer.parseInt(not_ok);
                            bundle.putString("model_name",model_name);
                            bundle.putString("total",total);
                            bundle.putString("ok",ok);
                            bundle.putString("notOk",not_ok);
                            modelList.add(bundle);
                            currentDataReport.addPieChart(okcount,not_okcount);
                            ReportAdapter adapter=new ReportAdapter();
                            adapter.setData(modelList);
                            recyclerView.setAdapter(adapter);
                            Log.e("model name", p.getModel());
                        }

                        p1.dismiss();
                        Toast.makeText(context, "Not connected to network", Toast.LENGTH_SHORT).show();

                        Log.e("Json ERROR", error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
    public Date getYesterday(Date date){
        Log.e("minus 24 hours",new Date(date.getTime()-24*60*60*1000).toString());
        return new Date(date.getTime()-24*60*60*1000);
    }

    // TODO: 12-07-2021 get remarks from server
    public void getRemarks(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Main_page.IP_ADDRESS+"/GetRemarks.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyDbHelper myDbHelper=new MyDbHelper(context);
                ArrayList<Bundle> list=new ArrayList<>();
                JSONArray jsonArray;
                JSONObject jsonObject;
                try {
                    jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        jsonObject=jsonArray.getJSONObject(i);
                        Bundle bundle=new Bundle();
                        bundle.putInt("question_id",jsonObject.getInt("question_id"));
                        bundle.putString("remark",jsonObject.getString("remark"));
                        Log.e(jsonObject.getInt("question_id")+"",jsonObject.getString("remark"));
                        list.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myDbHelper.insertRemarks(list);
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                Log.e("emp name error",error.toString());
            }
        });
        MySingleton singleton=MySingleton.getInstance(context);
        singleton.addToRequestQue(stringRequest);
    }

    public void getAnswers(String qr){
        ProgressDialog dialog=new ProgressDialog(context);
        dialog.setMessage("Please Wait Getting Information From Server");
        dialog.setTitle("Wait");
        dialog.show();
        String url=Main_page.IP_ADDRESS+"/GetAnswers1.php/?qr="+qr;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyDbHelper myDbHelper = new MyDbHelper(context, MyDbHelper.DB_NAME, null, 1);
                ArrayList<Questions_main> list=new ArrayList<>();
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()>0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Questions_main questions_main = new Questions_main
                                    (jsonObject.getInt("id"), jsonObject.getString("checkpoint"), jsonObject.getString("answer"), "NOHIGHLIGHT", qr
                                    );
                            questions_main.setUser(jsonObject.getString("user"));
                            Log.e("tag",questions_main.toString());
                            String img=jsonObject.getString("remarkImage");
                            String dateTime=jsonObject.getString("timestamp");
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d= simpleDateFormat.parse(dateTime);
                            questions_main.setSubmissionTime(d);

                            if(img.length()>2)
                            questions_main.setNokImage(Base64.decode(jsonObject.getString("remarkImage"), Base64.DEFAULT));
                            else
                                questions_main.setNokImage(new byte[]{0});
                            list.add(questions_main);
                        }

                        //myDbHelper.insert_data(list, ServerJson.this.getPartname().replace("%20", " "), qr,"unknown");
                        myDbHelper.insert_data(list, "na", qr,"unknown");
                        if(onResponseInterface!=null){
                            onResponseInterface.onResponse(list);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    public interface OnResponseInterface{
        public void onResponse(ArrayList<Questions_main> list);
    }

}


