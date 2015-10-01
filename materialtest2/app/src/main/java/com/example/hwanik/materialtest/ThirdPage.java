package com.example.hwanik.materialtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Color;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
        listView.setSelector(R.drawable.bg_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "레시피 불러오는 중", "잠시만 기다려주세요", true, true);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
                Category test=new Category();
                test=(Category)listView.getItemAtPosition(position);
                final String category=test.title;
                query.whereEqualTo("category",category);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        Intent intent = new Intent(getActivity().getApplicationContext(),CategoryDetail.class);
                        intent.putExtra("category",category);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }
    class Category{
        public int title_img;
        public String title;
        public Category(){

        }
        public Category(String title, int title_img){
            this.title_img=title_img;
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
            this.list.add(new Category("한식",R.drawable.korean_foods));
            this.list.add(new Category("양식",R.drawable.american_foods));
            this.list.add(new Category("중식/일식",R.drawable.sea_foods));
            this.list.add(new Category("분식류/간식",0));
            this.list.add(new Category("베이커리",R.drawable.bakery_foods));
            this.list.add(new Category("디저트",R.drawable.dessert));
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
            ImageView iv = (ImageView)v.findViewById(R.id.category_title_img);
            tv.setTypeface(typeface);
            tv.setTag(category);

            tv.setText(category.title);
            iv.setImageResource(category.title_img);
            iv.setTag(category);
            return v;
        }
    }
}
