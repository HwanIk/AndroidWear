package com.example.hwanik.materialtest;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailMat extends Fragment {
    private String[] Mats;
    private String mainImg;
    private String titleT;
    private ArrayList<Materials> matList;
    private Typeface typeface;


    private TextView dTitle;
    private TextView dTime;
    private TextView dMan;
    private TextView dTip;
    public TextView dMat;

    public DetailMat(String titleT, String mainImg, String []Mats, ArrayList<Materials> matList){
        this.Mats=Mats;
        this.mainImg=mainImg;
        this.titleT=titleT;
        this.matList=matList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumBarunGothic.otf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view=(LinearLayout)inflater.inflate(R.layout.fragment_detail_mat,container,false);
        setGlobalFont(view);

        ImageView mainIv=(ImageView)view.findViewById(R.id.Detail_image);

        dTitle=(TextView)view.findViewById(R.id.Detail_title);
        dTime=(TextView)view.findViewById(R.id.dTime);
        dMan=(TextView)view.findViewById(R.id.dMan);
        dTip=(TextView)view.findViewById(R.id.tipT);
        dMat=(TextView)view.findViewById(R.id.matN);

        dTitle.setText(titleT);
        dTime.setText(Mats[0]);
        dMan.setText(Mats[1]);
        dTip.setText(Mats[2]);


        for(int i=0;i<matList.size();i++){
                dMat.append(matList.get(i).mName + " " + matList.get(i).mNum + matList.get(i).mUnit);
                if (i != matList.size() - 1)
                    dMat.append(", ");
        }

        Picasso.with(getActivity().getApplicationContext())
                .load(mainImg)
                .into(mainIv);
        return view;
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dMat.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dMat.setText("");
    }
}
