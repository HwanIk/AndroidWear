package com.example.hwanik.Yosee;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hwanik.materialtest.R;

import java.util.List;


public class categoryDialog extends Dialog {

    private ListView lv;
    private MyAdapter myAdapter;
    private List<categoryItem> list;

    public categoryDialog(){
        super(null);
    }
    public categoryDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_dialog);

        lv=(ListView)findViewById(R.id.choiceCategory);
        myAdapter=new MyAdapter(getContext(),list);
        lv.setAdapter(myAdapter);

        list.add(new categoryItem(R.drawable.koreanfood,"한식"));
        list.add(new categoryItem(R.drawable.westernfood,"한식"));
        list.add(new categoryItem(R.drawable.chinesefood,"한식"));
        myAdapter.notifyDataSetChanged();
    }

    public class categoryItem{
        int resId;
        String cat;
        public categoryItem(int resId, String cat){
            this.resId=resId;
            this.cat=cat;
        }
    }
    public class MyAdapter extends BaseAdapter{
        Context context;
        List<categoryItem> list;
        public MyAdapter(Context context, List<categoryItem> list){
            this.context=context;
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public categoryItem getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View v;

            if(convertView==null){
                LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_dialog_item, parent, false);
            }
            ImageView iv=(ImageView)convertView.findViewById(R.id.categoryImg);
            TextView tv=(TextView)convertView.findViewById(R.id.categoryTitle);

            iv.setImageResource(getItem(i).resId);
            tv.setText(getItem(i).cat);

            return convertView;
        }
    }
}
