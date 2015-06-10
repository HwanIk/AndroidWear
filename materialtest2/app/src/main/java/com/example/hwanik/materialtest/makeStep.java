package com.example.hwanik.materialtest;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


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

    private Menu currentMenu;
    private Typeface typeface;
    private TextView toolbarTitle;

    ParseObject Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_step);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.otf");
        toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);
        toolbarTitle.setText("레시피 작성");
        setTitle("");

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
                Log.d("current position", String.valueOf(position));
                Log.d("maxpage", String.valueOf(maxPage));
                curPage=position;
                if(position==0){
                    preBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                    currentMenu.findItem(R.id.post).setVisible(false);
                    currentMenu.findItem(R.id.remove).setVisible(false);
                }else if(position==2 && position==maxPage-1){
                    currentMenu.findItem(R.id.post).setVisible(true);
                    nextBtn.setVisibility(View.GONE);
                }
                else if(position>2){
                    if(position==maxPage-1){
                        nextBtn.setVisibility(View.GONE);
                        currentMenu.findItem(R.id.remove).setVisible(true);
                        currentMenu.findItem(R.id.post).setVisible(true);
                    }else {
                        currentMenu.findItem(R.id.remove).setVisible(true);
                        currentMenu.findItem(R.id.post).setVisible(false);
                    }
                }else{
                    preBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    currentMenu.findItem(R.id.post).setVisible(false);
                    currentMenu.findItem(R.id.remove).setVisible(false);
                }
            }
        });

        Post = new ParseObject("test1");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_step, menu);
        currentMenu=menu;
        currentMenu.findItem(R.id.post).setVisible(false);
        currentMenu.findItem(R.id.remove).setVisible(false);
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
        if (id == R.id.remove){
            mAdapter.removeFragment(mPager.getCurrentItem()-1);
            maxPage--;
            mAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postToParse() {
         //parse에 만들어진 해당 클래스로 데이터를 업로드한다.
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile mainImageFile = new ParseFile("mainImg", byteToBitmap);
        ParseFile nStepImageFile;
        byte[] imageToByte;
        JSONArray mArray=new JSONArray();
        for(int i=0;i<mItems.size();i++){
            if(i==0){
                Post.put("MAIN_IMAGE",mainImageFile);
                Post.put("MAIN_TITLE",postTitle_title);
                Post.put("SUB_TITLE",postTitle_subTitle);
                Post.put("username",user.getUsername());
                Post.put("likeCnt",0);
            }
            if(i==1){
                mat_step tmp=(mat_step)mItems.get(i);
                Post.put("COOK_TIME",tmp.cookT.getText().toString());
                Post.put("COOK_MAN",tmp.cookM.getText().toString());

                for(int j=0;j<tmp.list.size();j++){
                    mArray.put(tmp.list.get(j).matName);
                    Post.put("M_NAME_"+j,tmp.list.get(j).matName);
                    Post.put("M_NUM_"+j,tmp.list.get(j).matNum);
                    Post.put("M_UNIT_"+j,tmp.list.get(j).matUnit);
                }
                Post.put("M_ARRAY",mArray);
                Post.put("TIP",tmp.tipT.getText().toString());
            }
            if(i>=2){
                n_step tmp=(n_step)mItems.get(i);
                if(!tmp.et.getText().toString().equals("")) {
                    ByteArrayOutputStream byteArray3 = new ByteArrayOutputStream();
                    if (tmp.bitmap != null) {
                        tmp.bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArray3);
                        imageToByte = byteArray3.toByteArray();
                    }else {
                        imageToByte = null;
                    }
                    if(imageToByte==null){
                        nStepImageFile=null;
                    }
                    else {
                        nStepImageFile = new ParseFile("STEP" + (i - 1) + "IMAGE", imageToByte);
                        String nStepContent = tmp.et.getText().toString();
                        Post.put("step" + (i - 1) + "Image", nStepImageFile);
                        Post.put("step" + (i - 1) + "Content", nStepContent);
                    }
                }else{
                    Toast.makeText(this,"내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    mPager.setCurrentItem(i);
                    return;
                }
            }
        }
        makeDialog();
        Post.saveInBackground();
    }

    private void makeDialog() {
        List<categoryItem> list=new ArrayList<>();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(makeStep.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.activity_category_dialog, null);
        alertDialog.setView(convertView);


        TextView tv=new TextView(this);
        tv.setTypeface(typeface);
        //tv.setText("카테고리 선택");
        //alertDialog.setCustomTitle(tv);
        ListView lv1 = (ListView) convertView.findViewById(R.id.choiceCategory);
        final catAdapter mAdapter=new catAdapter(this,list);
        lv1.setAdapter(mAdapter);
        list.add(new categoryItem(R.drawable.koreanfood, "한식"));
        list.add(new categoryItem(R.drawable.westernfood,"양식"));
        list.add(new categoryItem(R.drawable.chinesefood,"중식/일식"));
        list.add(new categoryItem(R.drawable.bunsik,"분식"));
        list.add(new categoryItem(R.drawable.bakery,"베이커리"));
        list.add(new categoryItem(R.drawable.desert,"디저트"));
        mAdapter.notifyDataSetChanged();

        Dialog dialog = alertDialog.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 600;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post.put("category",mAdapter.getItem(i).cat);
                Post.saveInBackground();
                Toast.makeText(makeStep.this,"업로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //alertDialog.show();
    }
    public class categoryItem{
        int resId;
        String cat;
        public categoryItem(int resId, String cat){
            this.resId=resId;
            this.cat=cat;
        }
    }
    public class catAdapter extends BaseAdapter {
        Context context;
        List<categoryItem> list;
        public catAdapter(Context context, List<categoryItem> list){
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
            tv.setTypeface(typeface);
            tv.setText(getItem(i).cat);

            return convertView;
        }
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
            maxPage=mItems.size();
        }

        public void addFragment(Fragment f){
            mItems.add(f);
            mAdapter.notifyDataSetChanged();
            maxPage=mItems.size();
        }
        public void removeFragment(int position){
            mItems.remove(position);
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
            return mItems.size();
        }
        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }
}
