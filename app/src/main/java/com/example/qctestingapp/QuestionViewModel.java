package com.example.qctestingapp;

import androidx.lifecycle.ViewModel;

import com.example.qctestingapp.Questions_main;

import java.util.ArrayList;
import java.util.List;

public class QuestionViewModel  extends ViewModel {
public ArrayList<Questions_main> list=new ArrayList<>();
public float count=0;
public float partcount=0;
public float devidedparts=0;

//    public ArrayList<Questions_main> getList() {
//        return list;
//    }
//
//    public void setList(ArrayList<Questions_main> list) {
//        this.list = list;
//    }
}
