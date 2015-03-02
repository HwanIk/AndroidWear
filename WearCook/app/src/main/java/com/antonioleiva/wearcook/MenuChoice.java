package com.antonioleiva.wearcook;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.imageloader.ImageLoader;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MenuChoice extends ActionBarActivity {
    //Notification ID
    static final int RECIPE_NOTIFICATION_ID=1;
    static final int ACTION_NOTIFICATION_ID=2;
    final int REQ_CODE_SELECT_IMAGE=100; //이미지 로드 버튼을 구분하기 위한 상수

    //이미지 로더를 사용해서 여러가지 이미지 출력해보기
    ImageView ad1,ad2,ad3;
    List<ParseObject> ob;

    private ImageView imgs[] = new ImageView[3];

    HorizontalScrollView horizontalScrollView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_choice);
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        ad1=(ImageView) findViewById(R.id.image_0);
        ad2=(ImageView) findViewById(R.id.image_1);
        ad3=(ImageView) findViewById(R.id.image_2);
        imgs[0] = ad1;
        imgs[1] = ad2;
        imgs[2] = ad3;

        /*
        for (int i = 0; i < 3; i++) {
            imgs[i] = (ImageView) findViewById(R.id.image + i);
        }*/


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
    //이미지의 경로와 이름을 받아오는 함수
    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    //이미지를 불러와서 출력, 서버에 저장하는 함수
    public void load_image(View view) {
        //갤러리를 불러오는 과정
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }
    //load_image함수에서 갤러리를 호출한 후 그 결과 작업에 따른 반환 값을 onActivityResult에서 가진다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        if(requestCode == REQ_CODE_SELECT_IMAGE){ //해당 작업이 이미지 로드라면
            if(resultCode== Activity.RESULT_OK){
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.image);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();

                    //parse에 이미지 업로드 시키기, 이미지 파일을 byte형태로 변환
                    ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();
                    image_bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray2); //png포맷의 품질을 100(%)으로 byteArray2에 저장.
                    byte[] image_to_byte = byteArray2.toByteArray();

                    ParseFile Imagefile = new ParseFile(name_Str,image_to_byte);       //(업로드할 파일,byte[])

                    Imagefile.saveInBackground();

                    ParseObject imageApplication = new ParseObject("ImageSaveLoad"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.
                    imageApplication.put("imagename", name_Str);
                    imageApplication.put("FileName", Imagefile);
                    imageApplication.saveInBackground();

                    Toast.makeText(this,"이미지 업로드 완료",Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    //주의!!!!!!!! 에러 : 이미지뷰의 배열의 수와 parse에 업로드 된 수의 싱크를 잘 맞춰야한다.
    public void multiple_image(View view) {
        int i=0;
        //imgl=new ImageLoader((java.net.URLStreamHandlerFactory) getApplication());
        //imgl=new ImageLoader((java.net.URLStreamHandlerFactory) getApplicationContext());

        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ImageSaveLoad");
        ob=new ArrayList<ParseObject>();
        //query.orderByDescending("updatedAt");
        try {
            ob.addAll(query.find());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        for (ParseObject upload_img : ob) {

            ParseFile image = (ParseFile) upload_img.get("FileName");

            Picasso.with(view.getContext())
                    .load(image.getUrl())
                    .into(imgs[i]);
            i=i+1;
            //System.out.println("the urls are"+image.getUrl());
        }

    }
}