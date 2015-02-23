package com.antonioleiva.wearcook;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.List;


public class MenuChoice extends ActionBarActivity {
    //Notification ID
    static final int RECIPE_NOTIFICATION_ID=1;
    static final int ACTION_NOTIFICATION_ID=2;
    //리소스로부터 사진을 가져온
    private final static int ACT_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_choice);

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

}