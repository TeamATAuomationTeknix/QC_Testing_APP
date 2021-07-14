package com.example.qctestingapp;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PartFragmentViewModel extends ViewModel {
    ArrayList<Questions_main> list=new ArrayList<>();
    public void saveListState( ArrayList<Questions_main> list1){
        list=list1;
    }
    public ArrayList<Questions_main> getList() {
        return list;
    }
}
