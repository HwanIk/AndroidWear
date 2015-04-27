package com.example.hwanik.materialtest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.List;


public class Detail extends ActionBarActivity {

    String objectId;
    public String[] imgUrl;
    public String[] content;
    int count=0;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");


        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*
        Intent intent=getIntent();
        objectId=intent.getStringExtra("objectId");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("hi");
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for(int i=0;i<parseObjects.size();i++ ){
                    Log.d("object size", String.valueOf(parseObjects.size()));
                    int j=0;
                    while(parseObjects.get(i).get("step" + String.valueOf(j) + "image")!=null){
                        ParseFile image=(ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "image");

                        imgUrl[count]=image.getUrl();
                        count++;

                        j++;
                        if(j>3) break;
                    }
                }
            }
        });*/

        ViewPager viewPager = (ViewPager) findViewById(R.id.swipe_pager);
        CustomAdapter adapter = new CustomAdapter(this);
        viewPager.setAdapter(adapter);


    }
    public void load_from_parse(){

        // Locate the class table named "Footer" in Parse.com

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

        private int [] images={
                R.drawable.splash,
                R.drawable.splash_cook,
                R.drawable.ic_launcher
        };


        public CustomAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            ImageView imageView;
            TextView textView;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.swipe_item, container,
                    false);
            imageView=(ImageView)itemView.findViewById(R.id.swipe_img);
            textView=(TextView)itemView.findViewById(R.id.swipe_txt);

            textView.setText("Test");
            Picasso.with(getApplicationContext())
                    .load(images[position])
                    .into(imageView);
            ((ViewPager) container).addView(itemView);
            /*
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(images[position]);
            ((ViewPager) container).addView(imageView, 0);
            */
            return itemView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }
    }
}
