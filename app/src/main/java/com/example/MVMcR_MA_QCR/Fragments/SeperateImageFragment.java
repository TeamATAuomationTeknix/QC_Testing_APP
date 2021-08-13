package com.example.MVMcR_MA_QCR.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.MVMcR_MA_QCR.R;

import org.jetbrains.annotations.NotNull;

public class SeperateImageFragment extends Fragment {
ImageView imageView;
Button button;
Uri uri;
    public SeperateImageFragment(Uri uri) {
        this.uri=uri;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View fragment=inflater.inflate(R.layout.seperate_image, container, false);
        imageView=fragment.findViewById(R.id.notOkImage);
        button=fragment.findViewById(R.id.btnNotOkImg);
        imageView.setImageURI(uri);
        return fragment;

    }
}
