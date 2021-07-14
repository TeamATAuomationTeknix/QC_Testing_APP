package com.example.qctestingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.example.qctestingapp.Fragments.PieChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurrentDataReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    RecyclerView recyclerView;
    LinearLayout pieChartLayout;
    PieChart pieChart;
    int countOK=0,countNotOk=0;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_data_report);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        pieChartLayout=findViewById(R.id.currentDataPieChart);
        spinner=findViewById(R.id.reportSpinner);
        List<String> list=new ArrayList();
        list.add("Today");
        list.add("Yesterday");
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
        recyclerView=findViewById(R.id.report_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       ServerJson serverJson=new ServerJson(this);

       serverJson.getReport(this,recyclerView,new Date());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection=parent.getSelectedItem().toString();
        if(selection.equals("Today")){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ServerJson serverJson=new ServerJson(this);
            serverJson.getReport(this,recyclerView,new Date());
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ServerJson serverJson=new ServerJson(this);

            serverJson.getReport(this,recyclerView,getYesterday(new Date()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public Date getYesterday(Date date){
        Log.e("minus 24 hours",new Date(date.getTime()).toString());
        return new Date(date.getTime()-24*60*60*1000);
    }
    //todo add home icon to tolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    // TODO: 26-06-2021 when home button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        if (item.getItemId() == R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public void addPieChart(int okcount, int not_okcount){
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        // pieChart= (PieChart) fragmentManager.findFragmentByTag("pieChart");
        if(pieChart!=null)
            transaction.remove(pieChart);
        Log.e("pie chart values",okcount+" "+not_okcount);
        pieChart=new PieChart(okcount,not_okcount,false);
        if(okcount+not_okcount==0)
            pieChartLayout.setVisibility(View.INVISIBLE);
        else
            pieChartLayout.setVisibility(View.VISIBLE);
        transaction.add(R.id.currentDataPieChart,pieChart);
        transaction.commit();
    }


}