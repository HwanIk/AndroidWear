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
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class makeStep extends ActionBarActivity {

    public ViewPager mPager;
    public StepPagerAdapter mAdapter;

    public List<Fragment> mItems;

    private Toolbar toolbar;

    byte[] byteToBitmap;
    Bitmap postTitle_image;
    String postTitle_title;
    String postTitle_subTitle;
    public int curPage;
    public int maxPage;
    private ImageView preBtn;
    private ImageView nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_step);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

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
        nextBtn=(ImageView)findViewById(R.id.next_btn);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                curPage=position;
                if(position==0){
                    preBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                }else if(position==maxPage){
                    nextBtn.setVisibility(View.GONE);
                }else{
                    preBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
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
        if (id == R.id.post) {
            postToParse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postToParse() {
        ParseObject Post = new ParseObject("test1"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile mainImageFile = new ParseFile("mainImg", byteToBitmap);
        ParseFile nStepImageFile;
        for(int i=0;i<mItems.size();i++){
            if(i==0){
                Post.put("MAIN_IMAGE",mainImageFile);
                Post.put("MAIN_TITLE",postTitle_title);
                Post.put("SUB_TITLE",postTitle_subTitle);
            }
            if(i==1){
                mat_step tmp=(mat_step)mItems.get(i);
                Post.put("COOK_TIME",tmp.cookT.getText().toString());
                Post.put("COOK_MAN",tmp.cookM.getText().toString());
                for(int j=0;j<tmp.list.size();j++){
                    Post.put("M_NAME_"+j,tmp.list.get(j).matName);
                    Post.put("M_NUM_"+j,tmp.list.get(j).matNum);
                    Post.put("M_UNIT_"+j,tmp.list.get(j).matUnit);
                }
                Post.put("TIP",tmp.tipT.getText().toString());
            }
            if(i>=2){
                n_step tmp=(n_step)mItems.get(i);
                ByteArrayOutputStream byteArray3 = new ByteArrayOutputStream();
                tmp.bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArray3);
                byte[] imageToByte = byteArray3.toByteArray();
                nStepImageFile=new ParseFile("STEP"+(i-1)+"IMAGE",imageToByte);
                String nStepContent=tmp.et.getText().toString();
                Post.put("step"+(i-1)+"Image",nStepImageFile);
                Post.put("step"+(i-1)+"Content",nStepContent);
            }
        }
        Post.saveInBackground();
        Toast.makeText(this,"업로드가 완료되었습니다",Toast.LENGTH_LONG).show();
        finish();
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

        public StepPagerAdapter(FragmentManager fm) {
            super(fm);
            mItems=new ArrayList<Fragment>();
            init();
        }
        public void init(){
            mItems.add(new main_step(byteToBitmap, postTitle_title, postTitle_subTitle));
            mItems.add(new mat_step(byteToBitmap,postTitle_title));
            mItems.add(new n_step());
            maxPage=mItems.size()-1;
        }

        public void addFragment(Fragment f){
            mItems.add(f);
            mAdapter.notifyDataSetChanged();
            maxPage=mItems.size()-1;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0) {
                nextBtn.setVisibility(View.VISIBLE);
            }
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
