package com.example.hwanik.Yosee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hwanik.materialtest.R;
import com.squareup.picasso.Picasso;
import android.graphics.Typeface;

import org.w3c.dom.Text;

import me.relex.circleindicator.CircleIndicator;

public class DetailStep extends Fragment {

    private String imgUrl; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    private String content;//§ 2
    private int position;
    private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
    private Typeface typeface = null;

    public DetailStep(){

    }

    @SuppressLint("ValidFragment")
    public DetailStep(String imgUrl, String content, int position){
        this.imgUrl=imgUrl;
        this.content=content;
        this.position=position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface=Typeface.createFromAsset(getActivity().getAssets(),TYPEFACE_NAME);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view=(LinearLayout)inflater.inflate(R.layout.swipe_item,container,false);
        ImageView iv=(ImageView)view.findViewById(R.id.swipe_img);
        TextView title=(TextView)view.findViewById(R.id.step);
        TextView sub=(TextView)view.findViewById(R.id.swipe_txt);
        if(imgUrl==null){
            iv.setImageResource(R.drawable.search);
        }
        else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(imgUrl)
                    .into(iv);
        }
        title.setText("STEP " + String.valueOf(position));
        title.setTypeface(typeface);
        sub.setText(content);
        sub.setTypeface(typeface);

        return view;
    }
}
