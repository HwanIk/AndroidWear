package com.example.hwanik.materialtest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;


public class Detail extends ActionBarActivity implements SensorEventListener {
    private String objectId;
    private String[] imgUrl; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    private String[] content;//§ 2

    private int count=0;//§
    private int current_page;
    private int accel_state=0;

    private Toolbar toolbar;

    private ViewPager viewPager;

    // 센서 관련 객체
    SensorManager m_sensor_manager;
    Sensor m_accelerometer;
    // 실수의 출력 자리수를 지정하는 포맷 객체
    DecimalFormat m_format;

    // 데이터를 저장할 변수들
    float[] m_gravity_data = new float[3];
    float[] m_accel_data = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent=getIntent();
        objectId=intent.getStringExtra("objectId");//§

        count = intent.getIntExtra("count",-1);//§
        imgUrl=new String[count];
        content=new String[count];
        imgUrl=intent.getStringArrayExtra("imgUrlArray");//§
        content=intent.getStringArrayExtra("contentArray");//§

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.swipe_pager);
        CustomAdapter adapter = new CustomAdapter(this);
        viewPager.setAdapter(adapter);

        //센서 부분
        // 포맷 객체를 생성한다.
        m_format = new DecimalFormat();
        // 소수점 두자리까지 출력될 수 있는 형식을 지정한다.
        m_format.applyLocalizedPattern("0.##");
        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // SensorManager 를 이용해서 가속센서 객체를 얻는다.
        m_accelerometer = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

    // 해당 액티비티가 포커스를 얻으면 가속 데이터를 얻을 수 있도록 리스너를 등록한다.
    protected void onResume()
    {
        super.onResume();
        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록한다.
        m_sensor_manager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    // 해당 액티비티가 포커스를 잃으면 가속 데이터를 얻어도 소용이 없으므로 리스너를 해제한다.
    protected void onPause()
    {
        super.onPause();
        // 센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        m_sensor_manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 가속 센서가 전달한 데이터인 경우
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // 중력 데이터를 구하기 위해서 저속 통과 필터를 적용할 때 사용하는 비율 데이터.
            // t : 저속 통과 필터의 시정수. 시정수란 센서가 가속도의 63% 를 인지하는데 걸리는 시간
            // dT : 이벤트 전송율 혹은 이벤트 전송속도.
            // alpha = t / (t + Dt)
            final float alpha = (float)0.8;

            // 저속 통과 필터를 적용한 중력 데이터를 구한다.
            // 직전 중력 값에 alpha 를 곱하고, 현재 데이터에 0.2 를 곱하여 두 값을 더한다.
            m_gravity_data[0] = alpha * m_gravity_data[0] + (1 - alpha) * event.values[0];
            m_gravity_data[1] = alpha * m_gravity_data[1] + (1 - alpha) * event.values[1];
            m_gravity_data[2] = alpha * m_gravity_data[2] + (1 - alpha) * event.values[2];

            // 현재 값에 중력 데이터를 빼서 가속도를 계산한다.
            m_accel_data[0] = event.values[0] - m_gravity_data[0];
            m_accel_data[1] = event.values[1] - m_gravity_data[1];
            m_accel_data[2] = event.values[2] - m_gravity_data[2];

            if(accel_state==0 && m_accel_data[0]>13){
                current_page=viewPager.getCurrentItem();
                viewPager.setCurrentItem(current_page+1);
                accel_state=1;
            }
            else if(m_accel_data[0]>-3 && m_accel_data[0]<3){
                accel_state=0;
            }
            else if(accel_state==0 && m_accel_data[0]<-13){
                current_page=viewPager.getCurrentItem();
                viewPager.setCurrentItem(current_page-1);
                accel_state=1;
            }
            else if(accel_state==0 && m_accel_data[2]>5){
                finish();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class CustomAdapter extends PagerAdapter {
        private Context context;
        LayoutInflater inflater;

        ImageView imageView;
        TextView textView;
        TextView step;
        String step_num;

        public CustomAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            return count;
        }//§
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.swipe_item, container,false);

            imageView=(ImageView)itemView.findViewById(R.id.swipe_img);
            textView=(TextView)itemView.findViewById(R.id.swipe_txt);
            step=(TextView)itemView.findViewById(R.id.step);

            step_num=String.valueOf(position+1)+" 단계";
            step.setText(step_num);
            textView.setText(content[position]);
            Picasso.with(getApplicationContext())
                    .load(imgUrl[position])
                    .into(imageView);
            ((ViewPager) container).addView(itemView);
            return itemView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((LinearLayout) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }
    }
}
