package com.example.hwanik.materialtest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.changer.polypicker.ImagePickerActivity;


public class UploadPage extends ActionBarActivity {
    ImageView postTitle_image;
    EditText postTitle_title;
    EditText postTitle_subTitle;
    Button postTitle_nextButton;

    //★onActivityResult에서 쓰는 변수들 시작
    private final int INTENT_REQUEST_GET_N_IMAGES=1;
    Bitmap bitmap=null;
    Uri uri=null;
    String bitmapName = null;
    //☆onActivityResult에서 쓰는 변수들 끝

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("표지 만들기");

        postTitle_image = (ImageView)findViewById(R.id.postTitle_image);
        postTitle_title = (EditText)findViewById(R.id.postTitle_title);
        postTitle_subTitle = (EditText)findViewById(R.id.postTitle_subTitle);
        postTitle_nextButton = (Button)findViewById(R.id.postTitle_nextButton);
    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if(parcelableUris ==null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if(uris != null) {
                    uris[0]=Uri.parse("file://" + uris[0]);
                    uri=uris[0];
                    bitmapName = getImageNameToUri(uri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        int height=bitmap.getHeight();
                        int width=bitmap.getWidth();
                        if(width>=600){
                            if(width>=height){
                                bitmap = Bitmap.createScaledBitmap(bitmap, 600, height/(width/600), true);
                            } else{
                                bitmap = Bitmap.createScaledBitmap(bitmap, width/(height/600),600, true);
                            }}
                        else{
                            //아무것도안해도됨. 비트맵 있는그대로 놔두기.
                        }
                        postTitle_image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getImageNameToUri(Uri data) {
        String imgPath = data.toString();
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    public void postTitle_nextButton(View view) {
        Intent intent = new Intent(UploadPage.this,makeStep.class);
        //젤리빈 이하 버전에서는 100KB이상의 데이터 intent가 불가하기 때문에 byte로 변환해 보낸 후 byte를 다시 bitmap으로 변환해줘야한다.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapToByte = stream.toByteArray();
        intent.putExtra("postTitle_image", bitmapToByte);
        intent.putExtra("postTitle_title",postTitle_title.getText().toString());
        intent.putExtra("postTitle_subTitle", postTitle_subTitle.getText().toString());
        startActivity(intent);
        finish();
    }

    //표지이미지 선택시 polyPicker 호출 -> 1개의 사진 받아오기.
    public void postTitle_image(View view) {
        Intent intent = new Intent(UploadPage.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 1);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_page, menu);
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
