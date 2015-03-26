package com.antonioleiva.wearcook;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MenuChoice extends ActionBarActivity {
    //Notification ID
    static final int RECIPE_NOTIFICATION_ID=1;
    static final int ACTION_NOTIFICATION_ID=2;
    final int REQ_CODE_SELECT_IMAGE=100; //이미지 로드 버튼을 구분하기 위한 상수

    //서버로부터 받은 다수 이미지를 출력하기 위한 변수들
    List<ParseObject> ob;
    private ImageView imgs[] = new ImageView[3];
    TextView title[]=new TextView[3];
    TextView content[]=new TextView[3];
    static String imgUrl;
    String titleTxt;
    String contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_choice);

        //Parse에서 부여한 고유의 key 값, Application Id, Clieny Key 값
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        imgs[0] = (ImageView) findViewById(R.id.image_0);
        imgs[1] = (ImageView) findViewById(R.id.image_1);
        imgs[2] = (ImageView) findViewById(R.id.image_2);

        title[0]=(TextView)findViewById(R.id.title_0);
        title[1]=(TextView)findViewById(R.id.title_1);
        title[2]=(TextView)findViewById(R.id.title_2);

        content[0]=(TextView)findViewById(R.id.content_0);
        content[1]=(TextView)findViewById(R.id.content_1);
        content[2]=(TextView)findViewById(R.id.content_2);
        imgUrl="";
        titleTxt="";
        contentTxt="";

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_choice, menu);
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

    public void menu1(View view) {
        //리소스부터 그림파일을 가져온다.
        Bitmap background1=BitmapFactory.decodeResource(getResources(),R.drawable.c_1);

        //최초 액티비티 실
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);

        //두번째 액티비티로 이동하는 과정
        Intent intent1=new Intent(this,HomeActivity1.class);
        PendingIntent viewPendingIntent=PendingIntent.getActivity(this, ACTION_NOTIFICATION_ID, intent1, 0);


        NotificationCompat.WearableExtender secondWearableExtender=
                new NotificationCompat.WearableExtender()
                .setContentIcon(R.drawable.ic_launcher)
                .setContentIconGravity(Gravity.CENTER);
        //두번째 페이지 작성
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 1")
                .setContentText("김치를 작게 썬다")
                .extend(secondWearableExtender)
                .build();
        //세번째 페이지 작성
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 2")
                .setContentText("양파는 굵게 다진다.")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER))
                .build();
        //네번째 페이지 작성
        Notification fourthPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 3")
                .setContentText("양념장을 고루 섞는다")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER))
                .build();

        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째, 세번째 페이지를 추가한다
        NotificationCompat.WearableExtender wearableOptions=
                new NotificationCompat.WearableExtender()
                    .setBackground(background1)
                    .setContentIcon(R.drawable.ic_launcher)
                    .setContentIconGravity(Gravity.CENTER)
                    .addPage(secondPage)
                    .addPage(thirdPage)
                    .addPage(fourthPage)
                    .setHintHideIcon(true);

        //웨어러블 옵션 적용한 알림을 생성
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("요리 스타트!")
                .setContentText("김치볶음밥 만들기")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .addAction(R.drawable.ic_launcher,"NextStep",viewPendingIntent)
                .setAutoCancel(true)
                .build();

        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(RECIPE_NOTIFICATION_ID,notification);

        //NotificationManagerCompat.from(this).cancel("cancel",RECIPE_NOTIFICATION_ID);

    }

    //카메라 버튼을 누르면 기존에 있는 카메라 어플을 선택하여 이미지를 촬영
    public void camera(View view) {
        //안드로이드에서 지원하는 인텐트를 통해서 카메라 액티비티?로 접근한다.
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        }

    //이미지를 불러와서 출력, 서버에 저장하는 함수
    public void post(View view) {
        //post액티비티 호출
        Intent intent=new Intent(this,Post.class);
        startActivity(intent);
    }
    //주의!!!!!!!! 에러 : 이미지뷰의 배열의 수와 parse에 업로드 된 수의 싱크를 잘 맞춰야한다.
    public void multiple_image(View view) {
        int i=0;

        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("post");
        ob=new ArrayList<ParseObject>();
        //query.orderByDescending("updatedAt");
        try {
            ob.addAll(query.find());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        for (ParseObject upload_img : ob) {

            ParseFile image = (ParseFile) upload_img.get("FileName");
            imgUrl=image.getUrl();

            Picasso.with(view.getContext())
                    .load(imgUrl)
                    .into(imgs[i]);
            titleTxt=upload_img.getString("title");
            contentTxt=upload_img.getString("content");

            title[i].setText(titleTxt);
            content[i].setText(contentTxt);
            i=i+1;
            if(i>2)
                break;
            //System.out.println("the urls are"+image.getUrl());
        }
    }

    public void full_image(View view) {
        Intent intent=new Intent(this,Full_Image.class);
        intent.putExtra("imgUrl",imgUrl);
        startActivity(intent);
    }
    public void logout_btn(View view) {
        ParseUser.logOut();
        Intent intent=new Intent(this,signIn.class);
        startActivity(intent);
        finish();
    }
}