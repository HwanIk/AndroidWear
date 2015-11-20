package com.example.hwanik.Yosee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hwanik.materialtest.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class settingPage extends ActionBarActivity {
    private ListView lv;
    private SettingAdapter mAdapter;
    private List<String> list=new ArrayList<>();
    private Toolbar toolbar;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        setTitle("");
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.otf");
        TextView toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);
        toolbarTitle.setText("설정");

        lv=(ListView)findViewById(R.id.setting_lv);
        mAdapter=new SettingAdapter(this,list);
        lv.setAdapter(mAdapter);

        list.add("공지사항");
        list.add("버전");
        list.add("로그아웃");
        list.add("도움말");
        list.add("앱 정보");
        list.add("알림설정");
        mAdapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==2){
                    ParseUser.logOut();
                    Intent intent=new Intent(getApplicationContext(),SignIn.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            finish();
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class SettingAdapter extends BaseAdapter{
        private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
        private Typeface typeface = Typeface.createFromAsset(getAssets(),TYPEFACE_NAME);

        private Context context;
        private List<String> list;
        private int resId=R.layout.setting_item_list;
        public SettingAdapter(Context context, List<String> list){
            super();
            this.context=context;
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            String str=getItem(position);

            if (v == null) {

                v = getLayoutInflater().inflate(resId, parent,false);
                //ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
                //TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            }
            TextView tv = (TextView)v.findViewById(R.id.settingTitle);

            if(position==1) {
                TextView content = (TextView) v.findViewById(R.id.settingContent);
                content.setTypeface(typeface);
                content.setText("v1.0");
            }
            //tv.setTypeface(typeface);

            tv.setTag(str);
            tv.setTypeface(typeface);
            tv.setText(str);

            return v;
        }
    }
}
