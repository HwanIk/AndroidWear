package com.example.hwanik.materialtest;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class makeStep extends ActionBarActivity {

    private ViewPager mPager;
    private StepPagerAdapter mAdapter;
    private Toolbar toolbar;

    byte[] byteToBitmap;
    Bitmap postTitle_image;
    String postTitle_title;
    String postTitle_subTitle;
    private int curPage;
    private ImageView preBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_step);

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        byteToBitmap = intent.getByteArrayExtra("postTitle_image");
        postTitle_image = BitmapFactory.decodeByteArray(byteToBitmap, 0, byteToBitmap.length);
        postTitle_title = intent.getStringExtra("postTitle_title");
        postTitle_subTitle = intent.getStringExtra("postTitle_subTitle");

        mPager= (ViewPager) findViewById(R.id.step_page);
        mAdapter=new StepPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(3);

        preBtn=(ImageView)findViewById(R.id.pre_btn);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        preBtn.setVisibility(View.GONE);
                        break;
                    case 1:
                        preBtn.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        preBtn.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_step, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.next) {
            mAdapter.addFragment(new n_step());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void next_btn(View view) {
        curPage=curPage+1;
        mPager.setCurrentItem(curPage);
    }

    public void pre_btn(View view) {
        curPage=curPage-1;
        mPager.setCurrentItem(curPage);
    }

    public class StepPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mItems;

        public StepPagerAdapter(FragmentManager fm) {
            super(fm);
            mItems=new ArrayList<Fragment>();
            init();
        }
        public void init(){
            mItems.add(new main_step(byteToBitmap, postTitle_title, postTitle_subTitle));
            mItems.add(new mat_step(byteToBitmap,postTitle_title));
            mItems.add(new n_step());
        }

        public void addFragment(Fragment f){
            mItems.add(f);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            /*
            Fragment current=new Fragment();
            switch (position){
                case 0 :
                    current= new main_step(byteToBitmap, postTitle_title, postTitle_subTitle);
                    break;
                case 1:
                    current=new mat_step(byteToBitmap,postTitle_title);
                    break;
                case 2:
                    current=new n_step();
                    break;
            }*/
            return mItems.get(position);
        }

        @Override
        public int getCount() {
            Log.d("zz", String.valueOf(mItems.size()));
            return mItems.size();
        }
        @Override
        public int getItemPosition(Object object){

            return POSITION_NONE;
        }
    }
}
