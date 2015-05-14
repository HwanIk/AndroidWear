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
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

public class Detail extends ActionBarActivity{

    private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
    private Typeface typeface = null;


    private String objectId;
    private String[] imgUrl; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    private String[] content;//§ 2

    private int count=0;//§
    private int current_page;

    private Toolbar toolbar;

    private ViewPager viewPager;
    private CircleIndicator customIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        typeface=Typeface.createFromAsset(getAssets(),TYPEFACE_NAME);

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

        //circleIndicator 뷰페이저를 먼저 set Adapter한 후에 연결시켜줘야 한다.
        customIndicator=(CircleIndicator)findViewById(R.id.indicator_default);
        customIndicator.setViewPager(viewPager);

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

    public class CustomAdapter extends PagerAdapter {
        private Context context;
        LayoutInflater inflater;

        ImageView imageView;
        TextView title;
        TextView subTitle;
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
            title=(TextView)itemView.findViewById(R.id.swipe_txt);
            subTitle=(TextView)itemView.findViewById(R.id.step);
            title.setTypeface(typeface);
            subTitle.setTypeface(typeface);

            step_num="STEP "+String.valueOf(position+1);
            subTitle.setText(step_num);
            title.setText(content[position]);
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
