package com.example.hwanik.materialtest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Typeface;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class Detail extends ActionBarActivity{

    private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
    private Typeface typeface = null;


    private String objectId;
    private String[] imgUrl; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    private String[] content;//§ 2
    private String[] Mats;
    private String titleImg;
    private String titleT;
    private String titleSub;
    private ArrayList<Materials> matList;

    private int count=0;//§
    private int current_page;

    private int likeCnt=0;
    private int commentCnt=0;
    private int shareCnt=0;
    private boolean[] check_btn;
    private TextView likeTv;
    private TextView commentTv;
    private TextView shareTv;
    private ImageView likeImg;
    private Toolbar toolbar;

    private ViewPager viewPager;
    private CircleIndicator customIndicator;

    private int countInt1;
    private String user;
    ParseObject cntLike = new ParseObject("Like");

    ParseQuery<ParseObject> innerQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        countInt1=0;
        user=ParseUser.getCurrentUser().getUsername();

        typeface=Typeface.createFromAsset(getAssets(),TYPEFACE_NAME);

        Intent intent=getIntent();
        objectId=intent.getStringExtra("objectId");//§

        countLike();

        count = intent.getIntExtra("count",-1);//§
        imgUrl=new String[count];
        content=new String[count];
        imgUrl=intent.getStringArrayExtra("imgUrlArray");//§
        content=intent.getStringArrayExtra("contentArray");//§
        titleImg=intent.getStringExtra("TitleImg");
        titleT=intent.getStringExtra("Title");
        titleSub=intent.getStringExtra("SUB");
        Mats=intent.getStringArrayExtra("mats");
        matList=intent.getParcelableArrayListExtra("materials");

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        likeTv=(TextView)findViewById(R.id.like_ctn);
        commentTv=(TextView)findViewById(R.id.comment_ctn);
        shareTv=(TextView)findViewById(R.id.share_ctn);
        likeImg=(ImageView)findViewById(R.id.like_img);

        check_btn=new boolean[3];

        viewPager = (ViewPager) findViewById(R.id.swipe_pager);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        for(int i=0;i<count;i++){
            adapter.add(new DetailStep(imgUrl[i],content[i],i+1));
            adapter.notifyDataSetChanged();
        }

        //circleIndicator 뷰페이저를 먼저 set Adapter한 후에 연결시켜줘야 한다.
        customIndicator=(CircleIndicator)findViewById(R.id.indicator_default);
        customIndicator.setViewPager(viewPager);


    }

    private void countLike() {
        innerQuery = ParseQuery.getQuery("test1");
        innerQuery.whereEqualTo("objectId", objectId);
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Like");
        query2.whereMatchesQuery("foodObjectId",innerQuery);
        query2.whereEqualTo("LikeState",true);
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> LikeList, ParseException e) {
                if(LikeList.isEmpty()){
                    likeCnt=0;
                }
                else {

                    for(int i=0;i<LikeList.size();i++) {
                        Log.d("user",user);
                        Log.d("server",LikeList.get(i).getString("userId"));
                        String tmp=LikeList.get(i).getString("userId");
                        if (user.equals(tmp) ){
                            likeImg.setImageResource(R.drawable.like_active_icon);
                        }
                    }
                    likeCnt = LikeList.size();
                    likeTv.setText(String.valueOf(likeCnt));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.like_btn:
                like();
                break;
            case R.id.comment_btn:
                if(check_btn[1]==false) {
                    commentCnt++;
                    check_btn[1]=true;
                }else{
                    commentCnt--;
                    check_btn[1]=false;
                }
                commentTv.setText(String.valueOf(commentCnt));
                break;
            case R.id.share_btn:
                if(check_btn[2]==false) {
                    shareCnt++;
                   check_btn[2]=true;
                }
                else{
                    shareCnt--;
                    check_btn[2]=false;
                }
                shareTv.setText(String.valueOf(shareCnt));
                break;

        }
    }
    public void like(){
        //버튼을 누르면->서버에서 현재유저가 개체(objectId)를 좋아요 했는지 쿼리
        // 했다면 숫자증감 및 버튼변화 상태업로드
        // 하지 않았다면 생성 후 숫자 증가 및 버튼변화, 상태업로드


        // Create the comment
        final ParseObject Like = new ParseObject("Like");

        final String user = ParseUser.getCurrentUser().getUsername();

        // Assume ParseObject myPost was previously created.
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Like");
        query.whereMatchesQuery("foodObjectId",innerQuery);  //디테일에서 intent로 받아오는 오브젝트아이디로 바꿔줘야해요.
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> LikeList, ParseException e) {
                // LikeList now has the comments for myPost
                // foodObjectId의 쿼리를 성공한 상황. userId가 있다면,없다면의 경우를 여기서 코딩하겠음.
                if (e == null) {
                    query.whereEqualTo("userId", user);     //asb를 user.toString으로 바꿔주세요.
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                if (parseObjects.size() > 0) {  // 좋아요를 눌러서 유저 id가 "Like" ParseObject에 있는 경우
                                    if (parseObjects.get(0).getBoolean("LikeState")) {    //LikeState가 true인 경우(좋아요가 눌린 경우) - 그니까 좋아요 취소하는 경우
                                        query.getInBackground(parseObjects.get(0).getObjectId(), new GetCallback<ParseObject>() {
                                            public void done(ParseObject parseObject2, com.parse.ParseException e) {
                                                if (e == null) {
                                                    // Now let's update it with some new data. In this case, only cheatMode and score
                                                    // will get sent to the Parse Cloud. playerName hasn't changed.
                                                    parseObject2.deleteInBackground();
                                                }
                                            }
                                        });
                                        //onoff1.setText(onoffString1);
                                        likeCnt=likeCnt-1;
                                        likeTv.setText(Integer.toString(likeCnt));
                                        likeImg.setImageResource(R.drawable.like_icon);
                                    }
                                } else { //좋아요를 단 한번도 누르지 않은 경우.
                                    Like.put("userId", user);//TestId 나중에 user로 바꿔주세요.
                                    Like.put("foodObjectId", ParseObject.createWithoutData("test1", objectId));
                                    Like.put("LikeState", true);
                                    Like.saveInBackground();
                                    //onoff1.setText("true");
                                    likeCnt=likeCnt+1;
                                    likeTv.setText(Integer.toString(likeCnt));
                                    likeImg.setImageResource(R.drawable.like_active_icon);
                                }
                            }
                        }
                    });
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    public class DetailPagerAdapter extends FragmentPagerAdapter {
        LayoutInflater inflater;
        ArrayList<Fragment> StepList=new ArrayList<>();

        public DetailPagerAdapter(FragmentManager fm){
            super(fm);
            StepList.add(new DetailMain(getApplicationContext(),titleImg,titleT,titleSub));
            StepList.add(new DetailMat(titleT,titleImg,Mats,matList));
        }
        public void add(Fragment f){
            StepList.add(f);
        }

        @Override
        public int getCount() {
            return StepList.size();
        }//§

        @Override
        public Fragment getItem(int position) {
            return StepList.get(position);
        }
    }
}
