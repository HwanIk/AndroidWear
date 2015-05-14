package com.example.hwanik.materialtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwanik on 2015. 5. 6..
 */
public class ThirdPage extends Fragment {
    ListView listView;
    private MyAdapter myAdapter;
    private List<Category> list = new ArrayList<Category>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.thirdpage, container, false);
        LinearLayout thirdPage=(LinearLayout)linearLayout.findViewById(R.id.thirdPage);

        listView = (ListView)thirdPage.findViewById(R.id.category);
        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);




        return thirdPage;
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
            this.list.add(new Category("중식"));
            this.list.add(new Category("분식"));
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
            tv.setTag(category);

            tv.setText(category.title);

            return v;
        }
    }
}
