/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antonioleiva.wearcook;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class HomeActivity1 extends BaseActivity {

    private DrawerLayout drawer;
    static final int ACTION_NOTIFICATION_ID=1;
    static final int RECIPE_NOTIFICATION_ID=2;
    static final int FINISH_STEP=3;


    @Override
    public void onNewIntent(Intent i)
    {
        CountDownTimer mCountDown = null;

        mCountDown = new CountDownTimer(10000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onFinish() {
                Notification_Vibration();
            }
        }.start();
    }
    public void Notification_Vibration(){
        long[] vibrationPattern={0,500,0,30};
        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.cancel(RECIPE_NOTIFICATION_ID);
        //step6
        Notification fourthPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 6")
                .setContentText("양파를 넣어 조금 더 볶는다.")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER))
                .build();
        NotificationCompat.WearableExtender wearableOptions=
                new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER)
                        .addPage(fourthPage);

        Notification notification1=new NotificationCompat.Builder(this)
                .setContentTitle("타이머 종료")
                .setContentText("다음 단계로 진행하세요")
                .setSmallIcon(R.drawable.ic_launcher)
                .setVibrate(vibrationPattern)
                .extend(wearableOptions)
                .build();

        NotificationManagerCompat.from(this).notify(FINISH_STEP,notification1);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = (String) view.getTag();
                DetailActivity.launch(HomeActivity1.this, view.findViewById(R.id.image), url);

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.cancel(ACTION_NOTIFICATION_ID);

        //두번째 액티비티로 이동하는 과정
        Intent intent1=new Intent(this,HomeActivity1.class);
        PendingIntent viewPendingIntent=PendingIntent.getActivity(this, ACTION_NOTIFICATION_ID, intent1, 0);


        NotificationCompat.WearableExtender secondWearableExtender=
                new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER);
        //step4
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 4")
                .setContentText("중간불로 달군 팬에 식용유와 참기름을 두른다.")
                .extend(secondWearableExtender)
                .build();
        //5번 액션
        NotificationCompat.Action step5=
                new NotificationCompat.Action(R.drawable.ic_launcher,"step5",viewPendingIntent);
        //step5
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 5")
                .setContentText("김치를 넣고 부드러워질 때까지 볶는다.")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER)
                        .setContentAction(0))

                .build();
        //step6
        Notification fourthPage=new NotificationCompat.Builder(this)
                .setContentTitle("Step 6")
                .setContentText("양파를 넣어 조금 더 볶는다.")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER))
                .build();

        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째, 세번째 페이지를 추가한다
        NotificationCompat.WearableExtender wearableOptions=
                new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.CENTER)
                        .addAction(step5)
                        .addPage(secondPage)
                        .addPage(thirdPage)
                        .addPage(fourthPage);

        //웨어러블 옵션 적용한 알림을 생성

        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("김치볶음밥")
                .setContentText("2단계")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .setAutoCancel(true)
                .build();
        //시간체크하는 함수

        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(RECIPE_NOTIFICATION_ID,notification);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class GridViewAdapter extends BaseAdapter {

        @Override public int getCount() {
            return 3;
        }

        @Override public Object getItem(int i) {
            String step []={
                    "중간불로 달군 팬에 식용유(2)와 참기름(0.5)를 두른다.",
                    "김치를 넣고 부드러워질 때까지 볶는다",
                    "양파를 넣어 조금 더 볶는다"
            };
            return String.valueOf(i + 4)+" step : " + step[i];
        }

        @Override public long getItemId(int i) {
            return i;
        }

        @Override public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.grid_item, viewGroup, false);
            }
            String img_url[]= {
                    "http://postfiles14.naver.net/20150210_221/rlaghksdlr_1423539781002i3nK4_PNG/c_4.png?type=w3",
                    "http://postfiles5.naver.net/20150210_148/rlaghksdlr_1423539781523jo87u_PNG/c_5.png?type=w3",
                    "http://postfiles8.naver.net/20150210_151/rlaghksdlr_1423539781938bJnjJ_PNG/c_6.png?type=w3",
            };
            //String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
            view.setTag(img_url[i]);

            ImageView image = (ImageView) view.findViewById(R.id.image);
            Picasso.with(view.getContext())
                    .load(img_url[i])
                    .into(image);

            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(getItem(i).toString());

            return view;
        }
    }
}
