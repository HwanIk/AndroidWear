package com.example.hwanik.materialtest;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Typeface;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import me.relex.circleindicator.CircleIndicator;

public class Detail extends ActionBarActivity implements SensorEventListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener{

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

    //Wear
    GoogleApiClient googleClient;
    byte[] bitmapdata;
    // 데이터 전송 횟수이다.
    // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
    private int sendCount = 0;
    private TextView toolbarTitle;

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
        setTitle("");
        toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);
        toolbarTitle.setText("레시피");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        //wear Initialize
        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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

        if (id == android.R.id.home){
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.sendToWear) {
            onSendDataString();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 앱을 실행시키고 데이터를 보낸다!
    public void onSendDataString() {

        // 시계로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/STRING_DATA_PATH");

        //◎05-04 이 소스를 이용해Drawable을 받음. Drawable배열은 웨어로 넘겨줘 메인화면에 띄우게 한다.
        try {
            for(int i=0;i<count;i++){


                //NetworkOnMainThreadException 에러!
                //URL로부터 이미지를 불러오는 코드가 안먹히는 이유는 네트워크 작업이 UI 쓰레드(메인스레드)에서 동작하기 때문이다.
                // 안드로이드 3.0 이후버젼부터는 UI쓰레드에 부담이 가는 것을 막기 위해 별도의 쓰레드에서 처리하도록 원천적으로 막아놨다.
                // 해결방법은
//                 if(android.os.Build.VERSION.SDK_INT > 9) {
//
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//                StrictMode.setThreadPolicy(policy);
//
//                }
                //를 추가하는 것이다.

                if(android.os.Build.VERSION.SDK_INT > 9) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);

                    URL url = new URL(imgUrl[i]);
                    InputStream is = url.openStream();
                    Drawable drawable = Drawable.createFromStream(is, "none");
                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                    bitmap=resizeBitmapImageFn(bitmap,320);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    bitmapdata = stream.toByteArray();
                    //이미지의 용량이 큰경우 전송이 안되니 주의요망.
                    dataMap.getDataMap().putByteArray("stepImage"+i,bitmapdata);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 전송할 텍스트&이미지를 지정한다.

        int currentPage = viewPager.getCurrentItem();
        dataMap.getDataMap().putStringArray("content",content); // step string 배열을 dataMap에 삽입해주기.
        dataMap.getDataMap().putInt("stepCount",count); // step의 수를 dataMap에 삽입.
        dataMap.getDataMap().putInt("currentPage",currentPage);
        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount++);
        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(googleClient, request).setResultCallback(resultCallback);
    }
    public Bitmap resizeBitmapImageFn(
            Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        //이미지의 가로 세로 비율에 맞게 조절
        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(
                bmpSource, newWidth, newHeight, true);
    }
    // 시계로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {

            String resultString = "Sending Result : " + result.getStatus();

            Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.like_btn:
                like();
                break;
            case R.id.comment_btn:
                Intent intent=new Intent(this,Comment.class);
                intent.putExtra("objectId",objectId);
                startActivity(intent);
//                if(check_btn[1]==false) {
//                    commentCnt++;
//                    check_btn[1]=true;
//                }else{
//                    commentCnt--;
//                    check_btn[1]=false;
//                }
//                commentTv.setText(String.valueOf(commentCnt));
                break;
            case R.id.share_btn:


                break;

        }
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

    public void movePager(final int pagerPosition){
        // 페어링 기기들을 지칭하는 노드를 가져온다.
        Wearable.NodeApi.getConnectedNodes(googleClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {

                    // 노드를 가져온 후 실행된다.
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult
                                                 getConnectedNodesResult) {

                        // 노드를 순회하며 메시지를 전송한다.
                        for (final Node node : getConnectedNodesResult.getNodes()) {

                            // 전송할 메시지 텍스트 생성
                            String message = Integer.toString(pagerPosition);
                            byte[] bytes = message.getBytes();

                            // 메시지 전송 및 전송 후 실행 될 콜백 함수 지정
                            Wearable.MessageApi.sendMessage(googleClient,
                                    node.getId(), "/MESSAGE_PATH_MOVE_W", bytes)
                                    .setResultCallback(resultCallback);
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.NodeApi.addListener(googleClient, this);
        Wearable.MessageApi.addListener(googleClient, this);
        Wearable.DataApi.addListener(googleClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/MESSAGE_PATH_MOVE")){
            String msg = new String(messageEvent.getData(), 0, messageEvent.getData().length);
            final int pagerPosition = Integer.parseInt(msg);
            // UI 스레드를 실행하여 텍스트 뷰의 값을 수정한다.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(pagerPosition+2);
                }
            });
        }
    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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