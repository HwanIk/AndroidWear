package com.example.hwanik.materialtest;

import android.content.Context;
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

public class DetailMain extends Fragment {

    private String titleImg;
    private String titleT;
    private String titleSub;
    private Context context;

    private Typeface typeface;

    public DetailMain(Context context, String titleImg, String titleT, String titleSub) {
        // Required empty public constructor
        this.titleImg=titleImg;
        this.titleT=titleT;
        this.titleSub=titleSub;
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumBarunGothic.otf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view= (LinearLayout) inflater.inflate(R.layout.fragment_detail_main,container,false);
        setGlobalFont(view);

        ImageView detailImg=(ImageView)view.findViewById(R.id.detailImg);
        TextView detailTitle=(TextView)view.findViewById(R.id.detailTitle);
        TextView detailSub=(TextView)view.findViewById(R.id.detailSubTitle);

        Picasso.with(context)
                .load(titleImg)
                .into(detailImg);
        detailTitle.setText(titleT);
        detailSub.setText(titleSub);

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
}
