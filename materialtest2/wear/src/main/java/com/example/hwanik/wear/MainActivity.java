package com.example.hwanik.wear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.DecimalFormat;


public class MainActivity extends Activity implements SensorEventListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NodeApi.NodeListener,
        MessageApi.MessageListener,DataApi.DataListener {
    // 센서 관련 객체
    SensorManager m_sensor_manager;
    Sensor m_accelerometer;

    // 출력용 텍스트뷰
    TextView m_gravity_view;
    TextView m_accel_view;

    // 실수의 출력 자리수를 지정하는 포맷 객체
    DecimalFormat m_format;

    // 데이터를 저장할 변수들
    float[] m_gravity_data = new float[3];
    float[] m_accel_data = new float[3];

    final GridViewPager pager = null;
    GridPagerAdapter page;
    int accel_state = 0;


    TextView mTextView;

    TextView fragmentTextView;

    String noti=null;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////    메시지 송수신 변수들      //////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GoogleApiClient mGoogleApiClient; // 구글 플레이 서비스 API 객체

    // intent변수들
    int receiveCount=1;         // step 갯수
    String[] receiveStringArray;// step i content
    //★ Intent로 받은 byte[]를->Bitmap->Drawable로 변환한다. 한번에 변환하는걸 찾아보고 싶으나 일단 급하니 아는 방법으로 진행했음.
    byte[] receiveImage;
    Bitmap changeImageToBitmap;
    Drawable[] backgroundImage;
    int currentPage=0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////    타이머 관련 변수들      //////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    CountDownTimer timer;//★Timer Test !
    int [] recipeTimer;//★Timer Test !!
    Vibrator vibe;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 구글 플레이 서비스 객체를 시계 설정으로 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        // res와 pager 변수를 상수로 지정한다.
        final Resources res = getResources();

        // 포맷 객체를 생성한다.
        m_format = new DecimalFormat();
        // 소수점 두자리까지 출력될 수 있는 형식을 지정한다.
        m_format.applyLocalizedPattern("0.##");


        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // SensorManager 를 이용해서 가속센서 객체를 얻는다.
        m_accelerometer = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        // 페이저에 윈도우 틀을 적용하는 이벤트를 지정한다.
        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {

            // 윈도우 틀을 지정할 때 실행되는 메소드이다.
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {

                // 시계가 원형인지 판단한다.
                final boolean round = insets.isRound();

                // 페이지 행의 여백
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);

                // 페이지 열의 여백
                // 원형 시계라면 더 많은 여백을 준다.
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round :
                        R.dimen.page_column_margin);

                // 페이저의 여백을 지정한다.
                pager.setPageMargins(rowMargin, colMargin);


                // 파라미터로 넘어온 변수를 그대로 반환한다.
                return insets;
            }
        });

        fragmentTextView = (TextView) findViewById(R.id.fragmentContent);

        Intent intent = getIntent();
        int receiveCount = intent.getIntExtra("receiveCount", 1024);
        if (receiveCount != 1024) {
            receiveStringArray = intent.getStringArrayExtra("receiveString");
            backgroundImage = new Drawable[receiveCount];
            currentPage = intent.getIntExtra("currentPage", 0);
            noti = "Step";
            recipeTimer = new int[receiveCount]; //★Timer Test !!
            recipeTimer[1] = 3;//★Timer Test !!
            for (int i = 0; i < receiveCount; i++) {
                receiveImage = intent.getByteArrayExtra("receiveImage" + i);
                changeImageToBitmap = BitmapFactory.decodeByteArray(receiveImage, 0, receiveImage.length);
                backgroundImage[i] = new BitmapDrawable(changeImageToBitmap);
            }
        } else {
            receiveCount = 1;

            noti = "알림";
            receiveStringArray = new String[receiveCount];
            backgroundImage = new Drawable[receiveCount];
            receiveStringArray[0] = "스마트폰을 통해 앱을 실행시켜주십시오.";
            backgroundImage[0] = getResources().getDrawable(R.drawable.common_signin_btn_icon_focus_light);
        }
        // 페이저의 속성과 페이지 리스트를 담고 있는 어답터를 추가한다.
        page = new gridPagerAdapter(this, getFragmentManager());
        pager.setAdapter(page);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//★Timer Test !!

        pager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {//★Timer Test !!
            @Override
//            onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels)
//            This method will be invoked when the current page is scrolled, either as part of a programmatically initiated smooth scroll or a user initiated touch scroll.
            public void onPageScrolled(int i, int i2, float v, float v2, int i3, int i4) {

            }

            @Override
//            onPageSelected(int row, int column)
//            This method is called when a new page becomes selected.
            //여기만 조작하면 될듯
            public void onPageSelected(int i, int i2) {
                if(recipeTimer[i2]>0){
                    TimerStart(recipeTimer[i2]);
                }
                movePager(i2);
            }

            @Override
//            onPageScrollStateChanged(int state)
//            Called when the scroll state changes.
            public void onPageScrollStateChanged(int i) {

            }
        });


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }
    //★Timer Test !!
    public void TimerStart(int Time){
        //타이머 설정 CountDownTimer(long millisInFuture,long countDownInterval) 단위는 1/1000초.
        //첫번째 인수 long millisInFuture : 카운트 다운을 할 총 시간
        //두번째 인수 long countDownInterval : 한 번 카운트 할 주기.
        timer = new CountDownTimer(Time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                vibe.vibrate(1000);
            }
        };

        //타이머 시작
        timer.start();
    }
    // 해당 액티비티가 포커스를 얻으면 가속 데이터를 얻을 수 있도록 리스너를 등록한다.
    protected void onResume() {
        super.onResume();
        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록한다.
        m_sensor_manager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    // 해당 액티비티가 포커스를 잃으면 가속 데이터를 얻어도 소용이 없으므로 리스너를 해제한다.
    protected void onPause() {
        super.onPause();
        // 센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        m_sensor_manager.unregisterListener(this);
    }

    // 정확도 변경시 호출되는 메소드. 센서의 경우 걋?호출되지 않는다.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // 측정한 값을 전달해주는 메소드.
    public void onSensorChanged(SensorEvent event) {
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        // 가속 센서가 전달한 데이터인 경우
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if(currentPage!=0){
                //pager.setCurrentItem(0,currentPage);
                pager.setCurrentItem(0,0);
                page.notifyDataSetChanged();
                currentPage=0;
            }

            // 중력 데이터를 구하기 위해서 저속 통과 필터를 적용할 때 사용하는 비율 데이터.
            // t : 저속 통과 필터의 시정수. 시정수란 센서가 가속도의 63% 를 인지하는데 걸리는 시간
            // dT : 이벤트 전송율 혹은 이벤트 전송속도.
            // alpha = t / (t + Dt)
            final float alpha = (float) 0.8;

            // 저속 통과 필터를 적용한 중력 데이터를 구한다.
            // 직전 중력 값에 alpha 를 곱하고, 현재 데이터에 0.2 를 곱하여 두 값을 더한다.
            m_gravity_data[0] = alpha * m_gravity_data[0] + (1 - alpha) * event.values[0];
            m_gravity_data[1] = alpha * m_gravity_data[1] + (1 - alpha) * event.values[1];
            m_gravity_data[2] = alpha * m_gravity_data[2] + (1 - alpha) * event.values[2];

            // 현재 값에 중력 데이터를 빼서 가속도를 계산한다.
            m_accel_data[0] = event.values[0] - m_gravity_data[0];
            m_accel_data[1] = event.values[1] - m_gravity_data[1];
            m_accel_data[2] = event.values[2] - m_gravity_data[2];

            //★ 이부분 ★ //
            //담스텝 (Next)
            if (m_accel_data[1] < -7 && m_accel_data[2]<-5 && accel_state>5) {
                accel_state = 0;
                Point cur = pager.getCurrentItem();
                if (cur.x < page.getColumnCount(0)) {
                    pager.setCurrentItem(0, cur.x + 1);
                    page.notifyDataSetChanged();
                }
            }
            //이전스텝
            else if (m_accel_data[1] >10 && accel_state>5) {
                Point cur = pager.getCurrentItem();
                accel_state = 0;
                if (cur.x > 0) {
                    pager.setCurrentItem(0, cur.x - 1);

                    page.notifyDataSetChanged();
                }
            }else if(m_accel_data[1]>-2 && m_accel_data[1]<2){
                accel_state++;
            }
        }
//                else{
//                        accel_state=0;
//                    }
        // }
    }


    public void movePager(final int pagerPosition){

        // 페어링 기기들을 지칭하는 노드를 가져온다.
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
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
                            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                    node.getId(), "/MESSAGE_PATH_MOVE", bytes)
                                    .setResultCallback(resultCallback);
                        }
                    }
                });
    }
    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {

            String resultString = "Sending Result : " + result.getStatus().isSuccess();

//            Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////↓///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 액티비티가 시작할 때 실행
    @Override // Activity
    protected void onStart() {
        super.onStart();

        // 구글 플레이 서비스에 접속돼 있지 않다면 접속한다.
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    // 액티비티가 종료될 때 실행
    @Override // Activity
    protected void onStop() {
        // 구글 플레이 서비스 접속 해제
        mGoogleApiClient.disconnect();

        super.onStop();
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
//        finishFromChild(this);
//        ListenerService listen = null;
//        listen.onDataChanged(dataEvents);


        finish();
        // 데이터 이벤트 횟수별로 동작한다.
        for (DataEvent event : dataEvents) {

            // 데이터 변경 이벤트일 때 실행된다.
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // 동작을 구분할 패스를 가져온다.
                String path = event.getDataItem().getUri().getPath();

                // 패스가 문자 데이터 일 때
                if (path.equals("/STRING_DATA_PATH")) {
                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());


                    //앱을 실행시킨다.
                    Intent startIntent = new Intent(this, MainActivity.class);

                    // 데이터맵으로부터 수신한 문자열을 가져온다.
                    receiveCount = dataMapItem.getDataMap().getInt("stepCount");
                    currentPage = dataMapItem.getDataMap().getInt("currentPage");
                    receiveStringArray = new String[receiveCount];
                    final String[] receiveStringArray = dataMapItem.getDataMap().getStringArray("content");

                    byte[] receiveByte;
                    for(int i=0;i<receiveCount;i++){
                        receiveByte = dataMapItem.getDataMap().getByteArray("stepImage"+i);
                        startIntent.putExtra("receiveImage"+i,receiveByte);
                    }

                    startIntent.putExtra("currentPage",currentPage);
                    startIntent.putExtra("receiveCount",receiveCount);
                    startIntent.putExtra("receiveString",receiveStringArray);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startIntent);

                }
                // 데이터 삭제 이벤트일 때 실행된다.
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 데이터가 삭제됐을 때 수행할 동작
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        // 노드, 메시지, 데이터 이벤트를 활용할 수 있도록 이벤트 리스너 지정
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        if(messageEvent.getPath().equals("/MESSAGE_PATH_MOVE_W")){
            String msg = new String(messageEvent.getData(), 0, messageEvent.getData().length);
            final int pagerPosition = Integer.parseInt(msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pager.setCurrentItem(0, pagerPosition);

                    page.notifyDataSetChanged();
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
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

        // 노드, 메시지, 데이터 이벤트 리스너 해제
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    //↑////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class gridPagerAdapter extends FragmentGridPagerAdapter {


        private final Context mContext;
        String imgUrl;

        LayoutInflater inflater;
        TextView textView;
        private Context context;
        ViewGroup container;

        // 그리드 페이저 어답터의 생성자이다.
        public gridPagerAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;
        }

        // 페이지의 속성 클래스
        private class Page {
            String imgUrl;
            String content;
            // 생성자
            public Page(String content, String imgUrl) {
                this.content = content;
                this.imgUrl = imgUrl;
            }
        }
        public class ExampleFragment extends Fragment{
            String content;
            String title;
            public ExampleFragment(String title,String content){
                this.title=title;
                this.content=content;
            }
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
                View view = inflater.inflate(R.layout.fragmentlayout,container,false);
                TextView tv=(TextView)view.findViewById(R.id.fragmentContent);
                tv.setText(title);
                TextView tv2=(TextView)view.findViewById(R.id.fragmentTitle);
                tv2.setText(content);
                return view;
            }

        }
        @Override
        public Fragment getFragment(int row, int col) {
            // 열과 행에 해당하는 페이지를 가져와 page 객체로 생성한다.
            Page page = new Page(noti+(col+1),receiveStringArray[col]);
            // 카드 타이틀
            String title =  receiveStringArray[col] != null ?receiveStringArray[col] : null; // 1번째 문장이 트루면 2번째 문장 리턴 펄스면 3번째 리턴
            // 카드 텍스트
            String text =page.content != null ? page.content : null;
            ExampleFragment fragment = new ExampleFragment(title,text);
            // 카드 조각 반환
            return fragment;
        }
        // 배경 이미지 반환한다.
        public Drawable getBackgroundForPage(int row, int column) {
            Drawable drawable = null;
            drawable = backgroundImage[column];
            return drawable;
        }

        // 페이지의 행 개수를 반환한다.
        @Override
        public int getRowCount() {

            return 1;
        }

        // 페이지의 행 당 열 개수를 반환한다.
        @Override
        public int getColumnCount(int rowNum) {

            Intent intent = getIntent();
            int receiveCount = intent.getIntExtra("receiveCount",1024);
            if(receiveCount==1024) receiveCount=1;
            return receiveCount;
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            // Display message in UI
            mTextView.setText(message);
        }
    }
}
