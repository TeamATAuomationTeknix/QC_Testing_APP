package com.example.qctestingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MyDbHelper extends SQLiteOpenHelper {
    String tb_question="question_data";
    String tb_part="part_table";
    String tb_answer="answer_table";
    String tb_temp_ans="temp_table_ans";
    String tb_remaining_parts="remaining_parts";
    String tb_master="master_images";
    ArrayList<String> pnames;
    ArrayList<Questions_main> questionsList;
    SQLiteDatabase mydatabase;

    public static final String DB_NAME="my_database";

    public MyDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+tb_answer+" (id Integer , question text,answer Integer, Highlight Integer, partname varchar, qr varchar, user varchar)");
       // id,qid,partname,qrcode,operator,answer,partTime,TimeStamp,fullTime,qr_code

        db.execSQL("create table "+tb_temp_ans+"(id Integer ,qid Integer, partname text,qrcode Text, operator Text, answer varchar, partTime varchar, TimeStamp varchar)");
        db.execSQL("create table "+tb_part+"(id Integer,part_name varchar)");
        db.execSQL("create table "+tb_question+"(id Integer primary key,question varchar, Highlight Integer, part_name String)");
        db.execSQL("create table "+tb_remaining_parts+"(id Integer,part_name varchar,qr_code varchar, fullTime Integer)");
        db.execSQL("create table "+tb_master+" (id integer primary key, model_name text, image blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert_data( ArrayList<Questions_main> questions,String partname,String qr,String user){

        mydatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        for(Questions_main q:questions) {

            values.put("id",q.getId());
            values.put("question",q.getQuestion());
            values.put("answer",q.getAnswer());
            values.put("Highlight",q.isHighlighted());
            values.put("qr",qr);
            values.put("user",user);
            values.put("partname",partname);
            mydatabase.insert(tb_answer,null,values);

        }

    }
    //******************************
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
                int highlight=cursor.getInt(3);
                boolean flag=highlight==1?true:false;
                Questions_main q=new Questions_main(id,question,flag);
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
        String[] resultColumns = {"id", "question", "answer","Highlight","partname","qr","user"};
        String where="qr=? and partname=?";
        Cursor cursor= mydatabase.query(tb_answer, resultColumns, where, new String[]{qr_res, partname}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String question = cursor.getString(1);
                String answer = cursor.getString(2);
                int highlight=cursor.getInt(3);
                boolean flag=highlight==1?true:false;
                Questions_main q=new Questions_main(id,question,flag);
                q.setAnswer(answer);
                questionsList.add(q);
                Log.e("from database", id + " " + question);
                Log.e("from database", answer);

            } while (cursor.moveToNext());
        }
        return questionsList;
    }
    //add partnames***************************
    public void addPartNames(List<String> partnames){
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        mydatabase.execSQL("delete from " +tb_part);
        ContentValues values=new ContentValues();
        for(String q:partnames) {
            values.put("part_name",q);
            mydatabase.insert(tb_part,null,values);

        }

    }
    //get Partnames*****************************
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
    //**********************add questions
    public void addQuestions(ArrayList<Questions_main> questions,String partname){
        Log.e("MyDbHelper","size:  "+questions.size()+" "+partname);
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        for(Questions_main q:questions) {
            Log.e("MyDbHelper","adding "+q.getQuestion());
            values.put("id",q.getId());
            values.put("question",q.getQuestion());
            int h=q.isHighlighted?1:0;
            values.put("Highlight",h);
            values.put("part_name",partname);
            mydatabase.insert(tb_question,null,values);

        }
        Log.e("MyDbHelper","questions added to local db");
    }
    //get Qeestions***********************************************
    public ArrayList<Questions_main> getQuestions(String partname){
        Log.e("Mydbhelper","reading partnames");
        questionsList=new ArrayList<>();
        String[] resultColumns = {"id", "question", "Highlight"};
        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String where="part_name=?";

        Cursor cursor = mydatabase.query( tb_question, resultColumns, where, new String[]{partname}, null, null, null, null);
        Log.e("Mydbhelper","reading questions");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String question = cursor.getString(1);
                int highlight = cursor.getInt(2);
                boolean h=highlight==1?true:false;
                questionsList.add(new Questions_main(id,question,h));
                Log.e("from database", id + " " + question);


            } while (cursor.moveToNext());
        }
        return questionsList;
    }

    public void submitTempAnswers( int qid, String partname, String qrcode, String operator,int answer,String partTime, String TimeStamp) {
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
            values.put("TimeStamp", String.valueOf(TimeStamp));
            mydatabase.insert(tb_temp_ans,null,values);


    }
    public  JSONArray getTempAnswers(){
        ArrayList<Questions_main> q=new ArrayList<>();
        SQLiteDatabase mydatabase=this.getWritableDatabase();
        //id,qid,partname,qrcode,operator,answer,partTime,TimeStamp
        String[] resultColumns = { "qid","partname","qrcode","operator","answer","partTime","TimeStamp"};
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
                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    jsonObjet.put("currentTime",formatter.format(new Date()));
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

    public long addImage(int id, String model_nm , byte[] img)
    {
//(id integer primary key, model_name text, image blob)

        SQLiteDatabase mydatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("model_name", model_nm);
        contentValues.put("image", img);
        //Toast.makeText(context,"Image Inserted",Toast.LENGTH_SHORT).show();
        return mydatabase.insert(tb_master, null, contentValues);
    }
    public Cursor getMaxImageRegResult(){
        SQLiteDatabase mydatabase = this.getWritableDatabase();
        return mydatabase.query(tb_master, new String[] {"id"}, null, null, null, null, null);
    }

    public Cursor getAllImagesOfSpecificModel(String model_nm) {
        SQLiteDatabase mydatabase = this.getReadableDatabase();

        Cursor cursor = mydatabase.query(tb_master, new String[] {"id", "image"}, "model_name"+" = '"+model_nm+"' ", null, null, null, null);;
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

    public boolean duplicateQr(String qr_result) {

        SQLiteDatabase mydatabase = this.getReadableDatabase();
        String where="qr=?";
        Cursor cursor;
        if(qr_result!=null) {
            cursor = mydatabase.query(tb_answer, new String[]{"qr"}, where, new String[]{qr_result}, null, null, null, null);

            if (cursor.moveToFirst()) {
                Log.e("Mydbhelper", "qr code matched");
                return true;
            }
            else return false;
        }
        else {

            return false;
        }
    }

   public void deletePrimaryData() {

        deleteAllImages();
        deletAllParts();
        deleteAllQuestions();
    }
}
