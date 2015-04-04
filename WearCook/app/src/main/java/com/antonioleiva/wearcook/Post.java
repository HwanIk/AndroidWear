
package com.antonioleiva.wearcook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.changer.polypicker.ImagePickerActivity;


public class Post extends ActionBarActivity {

    private ListView listView;
    private List<Item> list = new ArrayList<Item>();
    private MyAdapter myAdapter;
    private final int INTENT_REQUEST_GET_N_IMAGES=13;
    private static final String TAG = MainActivity.class.getSimpleName();
    public String[] name_str;      //★1
    byte[] image_to_byte;
    Bitmap[] bitmap;
    ParseFile Imagefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        listView = (ListView)findViewById(R.id.listView);
        myAdapter=new MyAdapter(this,list);
        listView.setAdapter(myAdapter);
        image_to_byte=null;
        name_str=null;
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
        Intent intent = new Intent(Post.this, ImagePickerActivity.class);

        // limit image pick count to only 3 images.
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }
    //갤러리에서 이미지를 가져온다. image_load 함수에서 갤러리를 호출한 후 그 결과 작업에 따른 반환 값을 onActivityResult에서 가진다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if(parcelableUris ==null) {
                    return;
                }
                Uri[] uris = new Uri[parcelableUris.length];
                name_str = new String[parcelableUris.length];//★9
                bitmap=new Bitmap[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                if(uris != null) {
                    int i=0;
                    for (Uri uri : uris) {
                        name_str[i]=getImageNameToUri(uri);
                        uri=Uri.parse("file://" + uri);
                        Log.i(TAG, " uri: " + uri);
                        //
                        try {
                            bitmap[i] = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            int height = bitmap[i].getHeight();
                            int width = bitmap[i].getWidth();
                            if (width > 600){
                                if (width >= height) {
                                    bitmap[i] = Bitmap.createScaledBitmap(bitmap[i], 600, height / (width / 600), true);
                                } else {
                                    bitmap[i] = Bitmap.createScaledBitmap(bitmap[i], width / (height / 600), 600, true);
                                }
                            }
                            list.add(new Item(bitmap[i], "" + i));
                            i++; //★12
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    myAdapter.notifyDataSetChanged();
                }
                // show images using uris returned.
            }
        }
    }
    //이미지의 경로와 이름을 받아오는 함수
    public String getImageNameToUri(Uri data) {
        String imgPath = data.toString();
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    private class Item {
        public Bitmap img;
        public String edit;
        public Item(Bitmap img, String edit) {
            this.img = img;
            this.edit = edit;
        }
    }
    private class MyWatcher implements TextWatcher {
        private EditText edit;
        private Item item;
        public MyWatcher(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TAG", "onTextChanged: " + s);
            this.item = (Item)edit.getTag();
            if (item != null) {
                item.edit = s.toString();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.list_item;
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
                ImageView iv=(ImageView)v.findViewById(R.id.image);
                EditText et = (EditText)v.findViewById(R.id.edit);
                et.addTextChangedListener(new MyWatcher(et));
            }
            ImageView iv=(ImageView)v.findViewById(R.id.image);
            EditText et = (EditText)v.findViewById(R.id.edit);
            et.setTag(item);
            iv.setTag(item);

            et.setText(item.edit);
            iv.setImageBitmap(item.img);
            return v;
        }
    }

    //parse에 이미지 업로드 시키기
    public void post(View view) {
        ParseObject editTextValues = new ParseObject("hi"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.
        ParseUser user = ParseUser.getCurrentUser();
        for (int i = 0; i < list.size(); i++) {

            ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();//★4
            bitmap[i].compress(Bitmap.CompressFormat.JPEG, 70, byteArray2); //jpg포맷의 품질을 70(%)으로 byteArray2에 저장.//★5
            byte[] image_to_byte = byteArray2.toByteArray();//★6
            Imagefile = new ParseFile(name_str[i], image_to_byte);//★7 "image"+1부분에 name_str[i]를 넣으면 업로드가 안되요.

            Log.d(TAG, "" + list.get(i).edit);
            editTextValues.put("step" + String.valueOf(i) + "image", Imagefile);//★8
            editTextValues.put("content" + String.valueOf(i), list.get(i).edit);
            editTextValues.put("user",user);
        }
        editTextValues.saveInBackground(); //Parse Cloud에 데이터를 저장하는 함수
        Toast.makeText(this, "텍스트 업로드 완료", Toast.LENGTH_SHORT).show();
        finish();
    }
}