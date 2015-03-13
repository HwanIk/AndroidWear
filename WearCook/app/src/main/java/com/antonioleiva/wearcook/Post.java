package com.antonioleiva.wearcook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Post extends ActionBarActivity {

    private static final int REQ_CODE_SELECT_IMAGE =1;
    Bitmap image_bitmap;
    String name_Str;
    EditText title;
    EditText content;
    String titleTxt;
    String contentTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        name_Str="";
        title=(EditText)findViewById(R.id.title);
        content=(EditText)findViewById(R.id.content);
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
        /*
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
        */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQ_CODE_SELECT_IMAGE);
    }
    //갤러리에서 이미지를 가져온다. image_load 함수에서 갤러리를 호출한 후 그 결과 작업에 따른 반환 값을 onActivityResult에서 가진다.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (Build.VERSION.SDK_INT >= 18 && null == data.getData()) {
            ClipData clipdata = data.getClipData();
            for (int i=0; i<clipdata.getItemCount();i++)
            {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                    //DO something
                    String pkg = getPackageName();

                    int id = getResources().getIdentifier("imageView"+(i), "id", pkg);
                    ImageView image=(ImageView) findViewById(id);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }*/

        if (requestCode == REQ_CODE_SELECT_IMAGE) { //해당 작업이 이미지 로드라면
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.post_image);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(getBaseContext(), "이미지 로드 완료 : " + resultCode, Toast.LENGTH_SHORT).show();
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
        titleTxt=title.getText().toString();
        contentTxt=content.getText().toString();


        //이미지 파일을 byte형태로 변환
        ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();
        image_bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray2); //png포맷의 품질을 100(%)으로 byteArray2에 저장.
        byte[] image_to_byte = byteArray2.toByteArray();

        ParseUser user= ParseUser.getCurrentUser();
        String userName=user.getUsername();

        ParseFile Imagefile = new ParseFile(name_Str, image_to_byte);       //(업로드할 파일,byte[])
        Imagefile.saveInBackground();
        ParseObject imageApplication = new ParseObject("post"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.

        imageApplication.put("title",titleTxt);
        imageApplication.put("content",contentTxt);
        imageApplication.put("FileName", Imagefile);
        imageApplication.put("userName",userName);
        //imageApplication.put("열이름",데이터 벨류");
        imageApplication.saveInBackground(); //Parse Cloud에 데이터를 저장하는 함수

        Toast.makeText(this, "이미지 업로드 완료", Toast.LENGTH_SHORT).show();
        finish();
    }
}
