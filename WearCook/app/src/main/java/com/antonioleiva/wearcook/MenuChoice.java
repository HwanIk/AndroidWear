package com.antonioleiva.wearcook;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
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
    static String imgUrl;
    String titleTxt;
    String contentTxt;
    private ListView listView;
    private List<Item> list = new ArrayList<Item>();
    private MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_choice);

        //Parse에서 부여한 고유의 key 값, Application Id, Clieny Key 값
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        imgUrl="";
        titleTxt="";
        contentTxt="";

        listView = (ListView)findViewById(R.id.recent_lv);
        myAdapter=new MyAdapter(this,list);
        listView.setAdapter(myAdapter);

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

    //이미지를 불러와서 출력, 서버에 저장하는 함수
    public void post(View view) {
        //post액티비티 호출
        Intent intent=new Intent(this,Post.class);
        startActivity(intent);
    }
    //주의!!!!!!!! 에러 : 이미지뷰의 배열의 수와 parse에 업로드 된 수의 싱크를 잘 맞춰야한다.
    public void multiple_image(final View view) {

        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("hi");

        //query.orderByDescending("updatedAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        int j=0;
                        while(parseObjects.get(i).get("step" + String.valueOf(j) + "image")!=null){
                            ParseFile image=(ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "image");

                            imgUrl=image.getUrl();
                            list.add(new Item(imgUrl,parseObjects.get(i).getString("content"+String.valueOf(j))));
                            j++;
                            if(j>3) break;
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }else{
                    Log.d("error",e.getMessage());
                }
            }
        });
    }
    private class Item {
        public String img_url;
        public String txt;
        public Item(String img_url, String txt) {
            this.img_url = img_url;
            this.txt = txt;
        }
    }
    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.recent_post_list_item;
        private Context context;
        List<Item> list;

        public MyAdapter(Context context, List<Item> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Item getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            Item item = getItem(position);

            if (v == null) {

                v = getLayoutInflater().inflate(resId, null);
                ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
                TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            }
            ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
            TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            tv.setTag(item);
            iv.setTag(item);

            tv.setText(item.txt);
            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
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