package com.example.hwanik.materialtest;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwanik on 2015. 5. 6..
 */
public class ThirdPage extends Fragment {
    ListView listView;
    private MyAdapter myAdapter;
    private List<Category> list = new ArrayList<Category>();
    private Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumBarunGothic.otf");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view=(LinearLayout)inflater.inflate(R.layout.thirdpage, container, false);

        listView = (ListView)view.findViewById(R.id.category);
        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);

        return view;
    }
    class Category{
        public String title;
        public Category(String title){
            this.title=title;
        }
    }
    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.category_item;
        private Context context;
        List<Category> list;

        public MyAdapter(Context context, List<Category> list) {
            super();
            this.context = context;
            this.list = list;
            this.list.add(new Category("한식"));
            this.list.add(new Category("양식"));
            this.list.add(new Category("중식/일식"));
            this.list.add(new Category("분식류/간식"));
            this.list.add(new Category("베이커리"));
            this.list.add(new Category("디저트"));
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Category getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            Category category = getItem(position);

            if (v == null) {

                v = getActivity().getLayoutInflater().inflate(resId, null);
                //TextView tv = (TextView)v.findViewById(R.id.category_title);
            }

            TextView tv = (TextView)v.findViewById(R.id.category_title);
            if(position%2==0) {
                tv.setBackgroundColor(0xfff0f0f0);
            }
            tv.setTypeface(typeface);
            tv.setTag(category);

            tv.setText(category.title);

            return v;
        }
    }
}
