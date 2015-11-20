package com.example.hwanik.Yosee;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.hwanik.materialtest.R;

import java.util.zip.Inflater;

public class main_step extends Fragment {
    byte[] byteToBitmap;
    Bitmap postTitle_image;
    String postTitle_title;
    String postTitle_subTitle;

    ImageView mainStep_image;
    TextView mainStep_title;
    TextView mainStep_subTitle;

    private String TYPEFACE_NAME;
    private Typeface typeface;
    private TextView toolbarTitle;

    public main_step(){

    }

    @SuppressLint("ValidFragment")
    public main_step(byte[] byteToBitmap, String postTitle_title, String postTitle_subTitle){
        this.byteToBitmap=byteToBitmap;
        this.postTitle_title=postTitle_title;
        this.postTitle_subTitle=postTitle_subTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
        typeface = Typeface.createFromAsset(getActivity().getAssets(),TYPEFACE_NAME);

        LinearLayout linearLayout= (LinearLayout)inflater.inflate(R.layout.fragment_main_step,container,false);

        getActivity().setTitle("");

        mainStep_image = (ImageView)linearLayout.findViewById(R.id.mainStep_image);
        mainStep_title = (TextView)linearLayout.findViewById(R.id.mainStep_title);
        mainStep_subTitle = (TextView)linearLayout.findViewById(R.id.mainStep_subTitle);

        mainStep_title.setTypeface(typeface);
        mainStep_subTitle.setTypeface(typeface);

        postTitle_image = BitmapFactory.decodeByteArray(byteToBitmap,0,byteToBitmap.length);

        mainStep_image.setImageBitmap(postTitle_image);
        mainStep_title.setText(postTitle_title);
        mainStep_subTitle.setText(postTitle_subTitle);

        return linearLayout;
    }

}
