package com.example.MVMc_MA_QCR;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MasterImages extends AppCompatActivity {

    RecyclerView masterRecyclerView;
    RecyclerView.Adapter adapter;

    ArrayList<Bitmap> listImages;
    ArrayList<String> listModel;
    ArrayList<Integer> listClipNo;

    ArrayList<String> listAllModels;
    ArrayList<String> listUniqueModels;
    Button homeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_master_images);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        masterRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_master);

        listImages = new ArrayList<>();
        listModel = new ArrayList<>();
        listClipNo = new ArrayList<>();
        listAllModels = new ArrayList<>();
        listUniqueModels = new ArrayList<>();

        // The number of Columns
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        masterRecyclerView.setLayoutManager(layoutManager);

        fetchModels();

        // Add Back Arrow to Toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Handle Back Arrow operation Here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchModels(){
Log.e("hi","fetch models");
        listAllModels.clear();
        MyDbHelper myDbHelper=new MyDbHelper(getBaseContext(),MyDbHelper.DB_NAME,null,1);

       listAllModels= myDbHelper.getPartnames();
       for(String m:listAllModels)
           Log.e("moedel",m);
        fetchImages();
    }

    private void fetchImages(){

        listImages.clear();
        listModel.clear();
        listClipNo.clear();

        Set setAllModels = new HashSet<String>(listAllModels);
        listUniqueModels = new ArrayList<>(setAllModels);
        Collections.sort(listUniqueModels);

        for (int i = 0; i < listUniqueModels.size(); i++){

            try {
                MyDbHelper db = new MyDbHelper(MasterImages.this,MyDbHelper.DB_NAME,null,1);
                Cursor c1 = db.getAllImagesOfSpecificModel(listUniqueModels.get(i));

                Log.e("tag", "got image to specific model");
                //Toast.makeText(getApplicationContext(), "Hiii "+listAllModels.get(i), Toast.LENGTH_SHORT).show();
                if (c1 != null && c1.getCount() != 0) {
                    Log.e("tag","c1 is not null");
                    if (c1.moveToFirst()) {
                        Log.e("c1 first","move to first");
                        int count = 1;
                        do {
                            int id = c1.getInt(0);
                            String model = c1.getString(2);
                            byte[] image = c1.getBlob(1);
                            Log.e("model",model);
                            listImages.add(byteArrayToBitmap(image));
                            listModel.add(model+"-"+listUniqueModels.get(i));
                            listClipNo.add(count);
                            count++;

                        } while (c1.moveToNext());
                    }
                }
                c1.close();
                db.close();
            }
            catch (Exception e){

            }

        }

        adapter = new MasterImagesAdapter(MasterImages.this, listImages, listModel, listClipNo);
        masterRecyclerView.setAdapter(adapter);

    }

    private static Bitmap byteArrayToBitmap(byte[] byteimg){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteimg, 0, byteimg.length);
        return bitmap;
    }
    //todo add home icon to tolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }


}
