package com.example.MVMcR_MA_QCR;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.MVMcR_MA_QCR.DataClass.CommonMethods;
import com.example.MVMcR_MA_QCR.DataClass.PartInfo;
import com.example.MVMcR_MA_QCR.DataClass.ProductionReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyDbHelper extends SQLiteOpenHelper {
    String tb_question="question_data";
    String tb_part="part_table";
    String tb_answer="answer_table";
    String tb_temp_ans="temp_table_ans";
    String tb_remaining_parts="remaining_parts";
    String tb_master="master_images";
    String tb_appName="app_names";
    String tb_employee="emp_table";
    String tb_tmp_battery="tmp_battery";
    String tb_ip_adress="ip_adress_tbl";
    String tb_remark="remark_tbl";
    String tb_backpress="backpress_tbl";
    String tb_report_tbl="data_report_tbl";
    ArrayList<String> pnames;
    ArrayList<Questions_main> questionsList;
    SQLiteDatabase mydatabase;
    Context context;
    CommonMethods methods;
    public static final String DB_NAME="my_database";


    public MyDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
        methods=new CommonMethods();
    }
    public MyDbHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.context=context;
        methods=new CommonMethods();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+tb_answer+" (id Integer , question text,answer Integer, Highlight varchar, partname varchar, qr varchar, user varchar,TimeStamp datetime,remark varchar,nokImage blob,vehiclePlatform varchar)");
        // id,qid,partname,qrcode,operator,answer,partTime,TimeStamp,fullTime,qr_code
        db.execSQL("create table "+tb_temp_ans+"(id Integer ,qid Integer, partname text,qrcode Text, operator Text, answer varchar, partTime varchar, TimeStamp datetime,remark varchar,nokImage blob,vehiclePlatform varchar)");
        db.execSQL("create table "+tb_part+"(id Integer primary key autoincrement,part_name varchar,app_name varchar,varient varchar)");
        db.execSQL("create table "+tb_question+"(id Integer primary key,question varchar, Highlight varchar, part_name varchar, platform varchar,varient varchar)");
        db.execSQL("create table "+tb_remaining_parts+"(id Integer,part_name varchar,qr_code varchar, fullTime Integer)");
        db.execSQL("create table "+tb_master+" (id integer primary key autoincrement,part_name text, platform text, image blob,varient varchar,description varchar)");
        db.execSQL("create table "+tb_appName+"(id Integer,app_name varchar)");
        db.execSQL("create table "+tb_employee+"(id Integer primary key autoincrement,token_no Integer, name varchar)");
        db.execSQL("create table "+tb_tmp_battery+"(id Integer primary key autoincrement,mainqr varchar, batteryqr varchar, status varchar)");
        db.execSQL("create table "+tb_ip_adress+"(ip_address varchar)");
        db.execSQL("insert into "+tb_ip_adress+"(ip_address) values ('192.168.0.13')");
        db.execSQL("create table "+tb_remark+"(id Integer primary key autoincrement,question_id Integer,remark varchar)");
        db.execSQL("create table "+tb_backpress+"(id int,answer varchar,qr varchar,partname varchar," +
                "remark varchar)");
        db.execSQL("create table "+tb_report_tbl+"(id Integer primary key autoincrement,qr_code varchar,timestamp datetime,vehiclePlatform varchar,answer varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // TODO: 01-09-2021  submit answer
    public void insert_data( ArrayList<Questions_main> questions,String partname,String qr,String user){
        mydatabase=this.getWritableDatabase();
        String ans="OK";
        ContentValues values=new ContentValues();
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Questions_main q:questions) {
            if(q.getAnswer().equals("NOK"))
                ans="NOK";
                values.put("id", q.getId());
                values.put("question", q.getQuestion());
                values.put("answer", q.getAnswer());
                values.put("Highlight", q.getHighlight());
                values.put("qr", qr);
                values.put("user", user);
                values.put("partname", partname);
                values.put("remark",q.getRemark());
                values.put("nokImage",q.getNokImage());
                values.put("timestamp",formatter.format(q.getSubmissionTime()));
                values.put("vehiclePlatform",methods.getPlatform(qr));
                mydatabase.insert(tb_answer,null,values);
        }
        if(questions.size()>0) {
            Questions_main q=questions.get(0);
            addReportData(qr,formatter.format(q.getSubmissionTime()),methods.getPlatform(qr),ans);
            Toast.makeText(context, "Records Submitted", Toast.LENGTH_SHORT).show();
        }
    }
    //todo get all answers******************************
    public ArrayList<Questions_main> getAllAnswers() {
        // (id Integer , question text,answer Integer, Highlight Integer, partname varchar, qr varchar, user varchar)
        questionsList=new ArrayList<>();
        String[] resultColumns = {"id", "question", "answer","Highlight","partname","qr","user"};
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        Cursor cursor = mydatabase.query(false, tb_answer, resultColumns, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String question = cursor.getString(1);
                String answer = cursor.getString(2);
                String highlight=cursor.getString(3);
                Questions_main q=new Questions_main(id,question,highlight);
                q.setAnswer(answer);
                questionsList.add(q);
                Log.e("from database", id + " " + question);
                Log.e("from database", answer);

            } while (cursor.moveToNext());
        }
        return questionsList;
    }
    public ArrayList<Questions_main> getAnswersBydata(String qr_res, String partname) {
        Log.e(qr_res,partname);
        questionsList=new ArrayList<>();
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String query="select max(TimeStamp) as d from "+tb_answer+" where qr='"+qr_res+"'";
        Log.e("questy",query);
        Cursor c=mydatabase.rawQuery(query,null);
        String date="";
        if(c.moveToFirst())
            date=c.getString(0);
        Log.e("date",date);
        String[] resultColumns = {"id", "question", "answer","Highlight","partname","qr","user","remark","nokImage"};
        String where="qr=? and partname=? and TimeStamp=?";
        Cursor cursor= mydatabase.query(tb_answer, resultColumns, where, new String[]{qr_res, partname,date}, null, null, null, null);
        if (cursor.moveToFirst()) {
            Log.e("tag","update operation");
            do {
                int id = cursor.getInt(0);
                String question = cursor.getString(1);
                String answer = cursor.getString(2);
                String highlight=cursor.getString(3);

                Questions_main q=new Questions_main(id,question,highlight);
                q.setAnswer(answer);
                q.setRemark(cursor.getString(7));
                q.setNokImage(cursor.getBlob(8));
                questionsList.add(q);
                Log.e("from database", id + " " + question);
                Log.e("from database", answer);

            } while (cursor.moveToNext());
        }
        return questionsList;
    }
    //todo add partnames***************************
    public void addPartNames(List<MyDbHelper.Parts> partnames){
        Log.e("tag","insert");
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_part);
        ContentValues values=new ContentValues();
        for(Parts q:partnames) {
            values.put("part_name",q.getPartname());
            values.put("app_name",q.getAppname());
            values.put("varient",q.getVarient());
            values.put("id",q.getPart_id());
            mydatabase.insert(tb_part,null,values);

        }

    }
    //todo get Partnames*****************************
    public ArrayList<String> getPartnames(){
        pnames=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","part_name"};
        Cursor cursor=db.query(tb_part,cols,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                pnames.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return pnames;
    }

    public ArrayList<String> getPartnamesByApp(String appname){
        pnames=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","part_name"};
        String where="app_name=?";
        Cursor cursor=db.query(tb_part,cols,where,new String[]{appname},null,null,null);
        if(cursor.moveToFirst()){
            do {
                pnames.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return pnames;
    }

    public ArrayList<String> getPartnameByVarient(String varient){
        pnames=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","part_name"};
        String where="varient=?";
        Cursor cursor=db.query(tb_part,cols,where,new String[]{varient},null,null,null);
        if(cursor.moveToFirst()){
            do {
                pnames.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return pnames;
    }
    //********************** todo add questions
    public void addQuestions(ArrayList<Questions_main> questions){
        Log.e("MyDbHelper","size:  "+questions.size());
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        for(Questions_main q:questions) {
            Log.e("MyDbHelper","adding "+q.getQuestion());
            values.put("id",q.getId());
            values.put("question",q.getQuestion());
            String h=q.highlight;
            values.put("Highlight",h);
            values.put("part_name",q.getPartname());
            values.put("platform",q.getPlatform());
            values.put("varient",q.getVarient());
            mydatabase.insert(tb_question,null,values);
        }
        Log.e("MyDbHelper","questions added to local db");
    }
    //todo get Qeestions***********************************************
    public ArrayList<Questions_main> getQuestions( String partname, String platform,String varient ){
        Log.e("parameters",partname+" "+platform+" "+varient);
        if(partname==null)partname="";
        Log.e("Mydbhelper","reading partnames");
        questionsList=new ArrayList<>();
        String[] resultColumns = {"id", "question", "Highlight"};
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String where="part_name=? and platform=? and varient=?";

        Cursor cursor = mydatabase.query( tb_question, resultColumns, where, new String[]{partname,platform,varient}, null, null, null, null);

        Log.e("Mydbhelper","reading questions");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String question = cursor.getString(1);
                String highlight = cursor.getString(2);
                questionsList.add(new Questions_main(id,question,highlight));
                Log.e("from database", id + " " + question);

            } while (cursor.moveToNext());
        }
        return questionsList;
    }

    public void submitTempAnswers( int qid, String partname, String qrcode, String operator,int answer,String partTime, String TimeStamp, String remark,byte[] nokImage) {
        // (id Integer ,qid Text, question text,answer Integer, Highlight Integer, partname varchar, qr varchar, user varchar)
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        //id,qid,partname,qrcode,operator,answer,partTime,TimeStamp
        //  values.put("id",id);
        values.put("qid",qid);
        values.put("partname",partname);
        values.put("qrcode",qrcode);
        values.put("operator",operator);
        values.put("answer",answer);
        values.put("partTime",partTime);
        values.put("remark",remark);
        values.put("TimeStamp", TimeStamp);
        values.put("nokImage",nokImage);
        mydatabase.insert(tb_temp_ans,null,values);
    }
    public  JSONArray getTempAnswers(){
        ArrayList<Questions_main> q=new ArrayList<>();
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        //id,qid,partname,qrcode,operator,answer,partTime,TimeStamp
        String[] resultColumns = { "qid","partname","qrcode","operator","answer","partTime","TimeStamp","remark","nokImage"};
        Cursor cursor = mydatabase.query(tb_temp_ans,resultColumns,null,null,null,null,null);
        JSONObject jsonObjet=new JSONObject();
        JSONArray jsonArray=null;
        if(cursor.moveToFirst()){
            jsonArray=new JSONArray();
            do{
                try {
                    jsonObjet= new JSONObject();
                    jsonObjet.put("id_fk_lhs_all_prt_que_tbl", cursor.getInt(0));
                    jsonObjet.put("partname", cursor.getString(1));
                    String qr_res=cursor.getString(2);
                    if(qr_res!=null)
                        jsonObjet.put("qr_code", qr_res);
                    else
                        jsonObjet.put("qr_code", "100");
                    jsonObjet.put("operator", cursor.getString(3));

                    jsonObjet.put("answer", cursor.getInt(4));
                    jsonObjet.put("partTime",cursor.getString(5));
                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    jsonObjet.put("currentTime",formatter.format(new Date()));
                    jsonObjet.put("remark",cursor.getString(7));
                    byte[] byteArray=cursor.getBlob(8);
                    if(byteArray!=null&&byteArray.length>2) {
                        String nokimage = methods.bitmapToBase64(methods.byteArrayToBitmap(byteArray));
                        jsonObjet.put("nokImage", nokimage);
                    }
                    jsonObjet.put("nokImage",new byte[]{0});
                    jsonArray.put(jsonObjet);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }while(cursor.moveToNext());
        }
        return jsonArray;
    }

    public void deleteTempAnswers(){
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_temp_ans);
        mydatabase.close();
    }

    public void setRemainingParts(LinkedList<String> parts, long fullTime, String qr_code){
        int id=1;
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_remaining_parts);
        for(String partname:parts) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("part_name", partname);
            values.put("fullTime", fullTime);
            values.put("qr_code", qr_code);
            mydatabase.insert(tb_remaining_parts,null,values);
            id++;
        }
    }
    public void deletAllParts() {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from " +tb_part);
    }

    public Cursor getRemainingParts(){
        String[] resultColumns = { "part_name","fullTime","qr_code"};
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        //ArrayList<Questions_main> pnames=new ArrayList<>();
        Cursor cursor = mydatabase.query(tb_remaining_parts,resultColumns,null,null,null,null,null);

        return cursor;
    }
    public void deleteRemainingParts(){
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        mydatabase.execSQL("delete from " +tb_remaining_parts);
    }

    public long addImage(String partname, String platform, String varient , byte[] img,String imageDescription)
    {
//(id integer primary key, platform text, image blob)

        SQLiteDatabase mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("platform", platform);
        contentValues.put("part_name",partname);
        contentValues.put("image", img);
        contentValues.put("varient",varient);
        contentValues.put("description",imageDescription);
        Log.e("tag","add image");
        //Toast.makeText(context,"Image Inserted",Toast.LENGTH_SHORT).show();
        return mydatabase.insert(tb_master, null, contentValues);
    }
    public Cursor getMaxImageRegResult(){
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        return mydatabase.query(tb_master, new String[] {"id"}, null, null, null, null, null);
    }

    public Cursor getAllImagesOfSpecificModel(String platform,String part_name, String varient) {
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String where="platform=? and part_name=? and varient=?";
        Cursor cursor = mydatabase.query(tb_master, new String[] {"id", "image","description"}, where, new String[]{platform,part_name,varient}, null, null, null);;
        //mydatabase.close();
        return cursor;
    }
    public Cursor getAllImagesOfSpecificModel(String part_name) {
        Log.e("geting model",part_name);
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String where="part_name=?";
        Cursor cursor = mydatabase.query(tb_master, new String[] {"id", "image","platform","varient"}, where, new String[]{part_name}, null, null, null);;
        //mydatabase.close();
        return cursor;
    }

    public void deleteAllImages(){
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from "+tb_master);
        mydatabase.close();
    }
    public void deleteAllQuestions(){
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_question);
        mydatabase.close();
    }



    public void deletePrimaryData() {
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from "+tb_master);
        mydatabase.execSQL("delete from " +tb_question);
        mydatabase.execSQL("delete from " +tb_part);
        mydatabase.execSQL("delete from " +tb_appName);
        mydatabase.execSQL("delete from "+tb_remark);
//        deleteAllImages();
//        deletAllParts();
//        deleteAllQuestions();
    }

    public void addAppNames(ArrayList<String> appNames) {
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_appName);
        ContentValues values=new ContentValues();
        for(String q:appNames) {
            values.put("app_name",q);
            mydatabase.insert(tb_appName,null,values);
        }
    }
    public ArrayList<String> getAppNames(){
        pnames=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","app_name"};
        Cursor cursor=db.query(tb_appName,cols,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                pnames.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return pnames;
    }
    //****************** Insert Employee******************
    public void addEmployee(int token,String name){
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token_no", token);
        contentValues.put("name", name);
        mydatabase.insert(tb_employee,null,contentValues);
        Log.e("insert emp","employee added");
    }

    public String getEmployee(String token){
        String empname="";
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"token_no","name"};
        String where="token_no=?";
        Cursor cursor=db.query(tb_employee,cols,where,new String[]{token},null,null,null);
        if(cursor.moveToFirst()){
              empname= cursor.getString(1);
        }
        return empname;
    }

    // TODO: 22-06-2021 change ip adress
    public void changeIp(String ip_adress) {
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ip_address", ip_adress);
        mydatabase.update(tb_ip_adress,contentValues,null,null);
    }

    // TODO: 23-06-2021 get ip adress 
    public String getIpAdress() {
        String ip="";
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        Cursor cursor=mydatabase.query(tb_ip_adress,new String[]{"ip_address"},null,null,null,null,null);
        if(cursor.moveToFirst())
        ip=cursor.getString(0);
        Log.e("ip: ",ip);
        return ip;
    }

    public ArrayList<Questions_main> getPreviousAnswers(String qr_code) {
        ArrayList<Questions_main> list=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String query="select max(TimeStamp) as d from "+tb_answer+" where qr='"+qr_code+"'";
        Log.e("questy",query);
        Cursor c=db.rawQuery(query,null);
        String date="";
        if(c.moveToFirst())
            date=c.getString(0);
        if(date==null) date="55";
        Log.e("date",date);
        String[] cols=new String[]{"id","question","answer","qr","user","TimeStamp","nokImage"};
        String where="qr=? and TimeStamp=?";
        Cursor cursor=db.query(tb_answer,cols,where,new String[]{qr_code,date},null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            do {
                Questions_main q = new Questions_main(cursor.getInt(0), cursor.getString(1), cursor.getString(2), "NOHIGHLIGHT", cursor.getString(3));
                q.setNokImage(cursor.getBlob(6));
                list.add(q);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<ProductionReport> getCurrentDataReport(Date date) {
        ArrayList<ProductionReport> reportList=new ArrayList<>();
        Date nextDay=methods.getNextDay(date);
        SQLiteDatabase db=this.getReadableDatabase();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd 07:00:00");
        Cursor platforms=db.rawQuery("select distinct vehiclePlatform from "+tb_report_tbl,null);
        if(platforms.moveToFirst()){
            int total=0,ok=0,nok=0;
            do{
                String platform=platforms.getString(0);
                Log.e("model",platform);
                String query="select count(*) from "+tb_report_tbl+ " where vehiclePlatform='"+platform+"' and answer='OK' and timestamp BETWEEN '"+formatter.format(date)+"' AND '" +formatter.format(nextDay)+"'";
                Log.e("query",query);
                Cursor okCursor=db.rawQuery(query,null);
                if(okCursor.moveToFirst()) {
                    ok=Integer.parseInt(okCursor.getString(0));
                    Log.e("ok count is:", ok+"");
                    total+=ok;
                }
                Cursor nOkCursor=db.rawQuery("select count(*) from "+tb_answer+ " where vehiclePlatform='"+platform+"' and answer='NOK' and timestamp BETWEEN '"+formatter.format(date)+"' AND '" +formatter.format(nextDay)+"'",null);

                if(nOkCursor.moveToFirst()) {
                    nok=Integer.parseInt(nOkCursor.getString(0));
                    total+=nok;
                    Log.e("nok count is:", nok + "");
                }
                reportList.add(new ProductionReport(ok,nok,total,platform));
                Log.e("total is:", total + "");
            }while(platforms.moveToNext());
        }
        return reportList;
    }

    // TODO: 16-08-2021 check number of questions 
    public int getQuestionLength() {
        int len=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(tb_question,null,null,null,null,null,null);
        len=cursor.getCount();
        return len;
    }

    public static class Parts{
        int part_id;
        String partname;
        String appname;
        String varient;


        public Parts(String partname, String appname) {
            this.partname = partname;
            this.appname = appname;
        }

        public Parts(String partname, String appname, String varient,int id) {
            this.part_id=id;
            this.partname = partname;
            this.appname = appname;
            this.varient = varient;
        }

        public int getPart_id() {
            return part_id;
        }

        public void setPart_id(int part_id) {
            this.part_id = part_id;
        }

        public String getVarient() {
            return varient;
        }

        public void setVarient(String varient) {
            this.varient = varient;
        }

        public String getPartname() {
            return partname;
        }

        public void setPartname(String partname) {
            this.partname = partname;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }
    }
    public void batteryStatusTemp(HashMap<String, String> batteryInfo){
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mainqr", batteryInfo.get("mainqr"));
        contentValues.put("batteryqr", batteryInfo.get("batteryqr"));
        contentValues.put("status", batteryInfo.get("status"));
        mydatabase.insert(tb_tmp_battery,null,contentValues);
    }
    public List<HashMap> getBatteryTemp(){

        ArrayList<HashMap> batteryinfo=new ArrayList<>();
        SQLiteDatabase db=this.getWritableDatabase();
        String[] cols={"mainqr","batteryqr","status"};
        Cursor cursor=db.query(tb_tmp_battery,cols,null,null,null,null,null);

       if(cursor.moveToFirst())
        do{
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("mainqr",cursor.getString(0));
            hashMap.put("batteryqr",cursor.getString(1));
            hashMap.put("status",cursor.getString(2));
            batteryinfo.add(hashMap);
            Log.e("battery info",hashMap.toString());
        }while(cursor.moveToNext());
        db.execSQL("delete from " +tb_tmp_battery);

        return batteryinfo;
    }

    // TODO: 12-07-2021 add remarks
    public void insertRemarks(List<Bundle> remarkList){
        //id Integer,question_id Integer,remark varchar
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        for(Bundle b:remarkList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("question_id", b.getInt("question_id"));
            contentValues.put("remark", b.getString("remark"));
            mydatabase.insert(tb_remark, null, contentValues);
        }
    }
    public Cursor getRemarks(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursorRemark=db.query(tb_remark,new String[]{"question_id,remark"},null,null,null,null,null);
        return cursorRemark;
    }
    public ArrayList<String> getRemarksByQID(int question_id){
        ArrayList<String> remarks=new ArrayList<>();
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        String where="question_id=?";
        Cursor cursor=mydatabase.query(tb_remark,new String[]{"remark"},where,new String[]{question_id+""},null,null,null);
        if(cursor.moveToFirst()){
           do{
                remarks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return remarks;
    }
    public ArrayList<PartInfo> getParts(String varient){
        ArrayList<PartInfo> pnames=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","part_name"};
        String where="varient=?";
        Cursor cursor=db.query(tb_part,cols,where,new String[]{varient},null,null,null);
        if(cursor.moveToFirst()){
            do {
               pnames.add(new PartInfo(cursor.getInt(0),cursor.getString(1),"na"));
            } while (cursor.moveToNext());
        }
        return pnames;
    }
    public void addBackpressData(List<PartInfo> questionList, String partname, String qr_code) {
        mydatabase=this.getWritableDatabase();
        String query="create table "+tb_backpress+"(id int,answer varchar,qr varchar,partname varchar" +
                "remark varchar)";
        ContentValues values=new ContentValues();
        for(PartInfo q:questionList) {
            values.put("id", q.getPartId());
            values.put("partname", q.getPartname());
            values.put("answer", q.getAnswer());
            values.put("qr", qr_code);
            values.put("remark", q.getConcern());
            mydatabase.insert(tb_backpress, null, values);
        }
    }
    public String backPressQr(){
        String qr="";
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","answer","qr","partname","remark"};
        Cursor cursor=db.query(tb_backpress,cols,null,null,null,null,null);
        if(cursor.moveToFirst())
        qr=cursor.getString(2);
        //db.execSQL("delete from "+tb_backpress);
        return qr;
    }
    public List<PartInfo> getBackPressData(String qr_code){
        List<PartInfo> list=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String[] cols={"id","answer","qr","partname","remark"};
        String where="qr=?";
        Cursor cursor=db.query(tb_backpress,cols,where,new String[]{qr_code},null,null,null);
        if(cursor.moveToFirst()){
            do{
               PartInfo partInfo=new PartInfo(cursor.getInt(0),cursor.getString(3),cursor.getString(1));
               partInfo.setConcern(cursor.getString(4));
               list.add(partInfo);
            }while(cursor.moveToNext());
        }
        db.delete(tb_backpress,null,null);
        return list;
    }

    public void addReportData(String qr_code,String timestamp,String vehiclePlatform,String answer){
        //qr_code ,timestamp ,vehiclePlatform ,answer
        mydatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("qr_code",qr_code);
        //values.put("prt_time",prt_time);
        values.put("timestamp",timestamp);
        values.put("vehiclePlatform",vehiclePlatform);
        values.put("answer",answer);
        mydatabase.insert(tb_report_tbl,null,values);
    }
}
