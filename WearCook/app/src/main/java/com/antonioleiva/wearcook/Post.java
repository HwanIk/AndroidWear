
package com.antonioleiva.wearcook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import nl.changer.polypicker.ImagePickerActivity;


public class Post extends ActionBarActivity {

    private static final int REQ_CODE_SELECT_IMAGE =1;
    private static final int INTENT_REQUEST_GET_IMAGES=13;
    private static final int INTENT_REQUEST_GET_N_IMAGES=14;
    Bitmap image_bitmap;
    String name_Str;
    EditText title;
    EditText content;
    String titleTxt;
    String contentTxt;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        name_Str="";
        title=(EditText)findViewById(R.id.title);
        //content=(EditText)findViewById(R.id.content);
        mContext=Post.this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
    //갤러리를 호출
    public void image_load(View view) {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);

        // limit image pick count to only 3 images.
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }
    //갤러리에서 이미지를 가져온다. image_load 함수에서 갤러리를 호출한 후 그 결과 작업에 따른 반환 값을 onActivityResult에서 가진다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if(parcelableUris ==null) {
                    return;
                }

                // show images using uris returned.
            }
        }
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

    //parse에 이미지 업로드 시키기
    public void post(View view) {
        //titleTxt=title.getText().toString();
        //contentTxt=content.getText().toString();


        //이미지 파일을 byte형태로 변환
        ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();
        image_bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray2); //png포맷의 품질을 100(%)으로 byteArray2에 저장.
        byte[] image_to_byte = byteArray2.toByteArray();

        ParseUser user= ParseUser.getCurrentUser();
        String userName=user.getUsername();

        ParseFile Imagefile = new ParseFile(name_Str, image_to_byte);       //(업로드할 파일,byte[])
        //Imagefile.saveInBackground();
        ParseObject imageApplication = new ParseObject("post1"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.

        //imageApplication.put("title",titleTxt);
        //imageApplication.put("content",contentTxt);
        imageApplication.put("FileName", Imagefile);
        imageApplication.put("userName",userName);
        //imageApplication.put("열이름",데이터 벨류");
        imageApplication.saveInBackground(); //Parse Cloud에 데이터를 저장하는 함수

        Toast.makeText(this, "이미지 업로드 완료", Toast.LENGTH_SHORT).show();
        finish();
    }
}