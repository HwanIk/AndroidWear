package com.antonioleiva.wearcook;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoViewAttacher;


public class Full_Image extends ActionBarActivity {

    static String imgUrl;
    ImageView full_img;

    PhotoViewAttacher mAttacher; //PhotoView Library - Zoom In/Out 기능
    // build.gradle(app)에 다음을 추가해야한다. compile 'com.github.chrisbanes.photoview:library:1.2.3'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full__image);

        Intent intent=getIntent();
        imgUrl=intent.getExtras().getString("imgUrl");
        full_img=(ImageView)findViewById(R.id.full_image);

        Picasso.with(this)   //피카소 클래스를 이용하여 이미지 URL을 view에 출력한다.
                .load(imgUrl)
                .into(full_img);

        mAttacher=new PhotoViewAttacher(full_img); //zoom in/out
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full__image, menu);
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
}
