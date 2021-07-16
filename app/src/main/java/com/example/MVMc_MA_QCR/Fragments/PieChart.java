package com.example.MVMc_MA_QCR.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.MVMc_MA_QCR.R;

import org.eazegraph.lib.models.PieModel;


public class PieChart extends Fragment {

org.eazegraph.lib.charts.PieChart pieChart;
TextView txtok,txtNotOk;
boolean flag=true;
    // TODO: Rename and change types of parameters
    private int ok=0;
    private int Not_ok=0;
    public PieChart() {
        // Required empty public constructor
    }

    // TODO: get how much times ok and not ok selected
    public   PieChart(int ok, int Not_ok, boolean flag) {
      this.ok=ok;
      this.Not_ok=Not_ok;
      this.flag=flag;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;

    }

    public int getNot_ok() {
        return Not_ok;

    }

    public void setNot_ok(int not_ok) {
        Not_ok = not_ok;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_pie_chart,container,false);
        txtok=view.findViewById(R.id.txtOk);
        txtNotOk=view.findViewById(R.id.txtNotOk);
        txtNotOk.setText("NOT OK="+Not_ok);
        txtok.setText("OK="+ok);


        pieChart= view.findViewById(R.id.piechart);
        pieChart.addPieSlice(
                new PieModel(
                        "OK",
                        ok,
                        Color.parseColor("#88EA16")));
        pieChart.addPieSlice(
        new PieModel(
                "NOT OK",
                Not_ok,
                Color.parseColor("#FA6B3E")));
        pieChart.startAnimation();

        return view;
    }

//    @Override
//    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if(savedInstanceState!=null&&flag){
//            ok=savedInstanceState.getInt("ok");
//            Not_ok=savedInstanceState.getInt("not_ok");
//            txtok.setText("OK="+ok);
//            txtNotOk.setText("NOT OK="+Not_ok);
//
//            pieChart.addPieSlice(
//                    new PieModel(
//                            "OK",
//                            ok,
//                            Color.parseColor("#88EA16")));
//            pieChart.addPieSlice(
//                    new PieModel(
//                            "NOT OK",
//                            Not_ok,
//                            Color.parseColor("#FA6B3E")));
//            pieChart.startAnimation();
//
//
//            //Log.e("ok count",savedInstanceState.getInt("ok")+"");
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.e("saving instanse in pie",ok+" "+Not_ok);
        outState.putInt("ok", ok);
        outState.putInt("not_ok",Not_ok);
    }

}