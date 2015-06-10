package com.example.hwanik.materialtest;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends ActionBarActivity implements MaterialTabListener {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;
    private MaterialTabHost mTabHost;
    private String[] pagerTitle;
    private ImageView topBtn;
    private Typeface typeface;
    private List<Fragment> Flist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      setContentView(R.layout.activity_main_appbar); 이 코드는 툴바를 넘지 않는 drawerlayout을 적용시키는 코드.

        //edittext검색하려고 키보드 띄우면 하단탭이 올라오는 현상을 막아주는 코드드
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");
//        ParseFacebookUtils.initialize("810766125683106");

        setTitle("");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.otf");
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);


        //Home버튼 생성
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Navigation Drawer Bar
        NavigationDrawerFragment drawerFragment=(NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout),toolbar);
        */
        Flist=new ArrayList<Fragment>();

        pagerTitle=new String[4];
        pagerTitle[0]="오늘의 레시피";
        pagerTitle[1]="카테고리";
        pagerTitle[2]="요리 검색";
        pagerTitle[3]="내 정보";

        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        mPager=(ViewPager)findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mPager.setOffscreenPageLimit(4);
        //Viewpager - The specified child already has a parent. 에러를 발생시키는 것에 대한 해결 방법

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);
                topBtn.setVisibility(View.GONE);
                toolbarTitle.setText(pagerTitle[position]);
            }
        });
        //Add all the Tabs to the TabHost
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setIcon(mAdapter.getIcon(i))
                            .setTabListener(this));
        }

        topBtn=(ImageView)findViewById(R.id.go_top);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstPage tmp=(FirstPage)Flist.get(0);
                tmp.goTop();
                Flist.set(0,tmp);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.post_Btn){
            /*
            ParseUser.logOut();
            Intent intent=new Intent(this,SignIn.class);
            startActivity(intent);
            finish();*/
            Intent intent=new Intent(this,UploadPage.class);
            startActivity(intent);
        }

        if(id==R.id.logout){
            ParseUser.logOut();
            finish();
            Intent intent=new Intent(this, SignIn.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        mPager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }



    /*  FragmentPagerAdapter의 경우, 사용자가 ViewPager에서 좌/우로 스크롤(플링)하여 화면 전환을 하여 다음 Fragment가 표시되면
        이전 Fragment를 메모리 상에 저장해 만일 사용자가 화면을 반대로 이동하면 메모리 상에 저장되어있는 Fragment를 사용하게된다.*/
    public class MyPagerAdapter extends FragmentPagerAdapter{
        int icons[]={R.drawable.cook_icon,R.drawable.menu_icon,
                R.drawable.search_icon,R.drawable.man_icon};
        String[] tabText=getResources().getStringArray(R.array.tabs);
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
//          strings.xml에 저장되어있는 string형 array값을 받아온다.
            tabText=getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
//            MyFragment myFragment=MyFragment.getInstance(position);
//            return myFragment;
            Fragment current=new Fragment();
            switch (position) {
                case 0:
                    current = new FirstPage();
                    Flist.add(current);
                    break;
                case 1:
                    current = new ThirdPage();
                    Flist.add(current);
                    break;
                case 2:
                    current = new SecondPage();
                    Flist.add(current);
                    break;
                case 3:
                    current = new myPage();
                    Flist.add(current);
                    break;
            }
            return current;
        }

        @Override
        public int getCount() { return 4; }

        //      String.xml에 저장해놓은 값을 PageTitle로 가져온다.
        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable=getResources().getDrawable(icons[position]);
            drawable.setBounds(0,0,36,36);
            ImageSpan imageSpan=new ImageSpan(drawable);
            SpannableString spannableString=new SpannableString(" ");
            spannableString.setSpan(imageSpan,0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }

    }
}
