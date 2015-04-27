package com.example.hwanik.materialtest;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.changer.polypicker.ImagePickerActivity;

/**
 * Created by jang on 2015-04-12.
 */
public class AddStep extends Activity {

    //★ postTitle에서 intent로 받아오는 data 시작
    byte[] byteToBitmap;
    Bitmap postTitle_image;
    String postTitle_title;
    String postTitle_subTitle;
    byte[] bitmapToByte;
    //☆ postTitle에서 intent로 받아오는 data 끝
    ImageView addStep_image;
    TextView addStep_title;
    TextView addstep_subTitle;


    //

    private ListView listView;
    private List<Item1> list = new ArrayList<Item1>();
    private MyAdapter myAdapter;
    private final int INTENT_REQUEST_GET_N_IMAGES=13;
    private static final String TAG = MainActivity.class.getSimpleName();
    public String[] name_str;      //★1
    byte[] image_to_byte;
    Bitmap[] bitmap=null;
    Bitmap bitmap2=null;
    ParseFile Imagefile;

    //
    private ListView listView2;
    private List<MatItem> list2 = new ArrayList<MatItem>();
    private MatAdapter matAdapter;

    public ByteArrayOutputStream byteArray4;
    public byte[] image_to_byte2;


    //＠ 커스텀 다이얼로그
    final static int DIALOG_1=0;
    LayoutInflater dialogInflater;
    EditText MatnameEt;
    EditText MatcountEt;
    EditText MatunitEt;
    String MatnameStr=null;
    String MatcountStr;
    String MatunitStr=null;

    protected void onCreate(Bundle saveInstanceState) {
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_add_step);

        Intent intent = getIntent();
        byteToBitmap = intent.getByteArrayExtra("postTitle_image");

        postTitle_image = BitmapFactory.decodeByteArray(byteToBitmap,0,byteToBitmap.length);
        postTitle_title = intent.getStringExtra("postTitle_title");
        postTitle_subTitle = intent.getStringExtra("postTitle_subTitle");

        addStep_image = (ImageView)findViewById(R.id.addStep_postTitle_image);
        addStep_title = (TextView)findViewById(R.id.addStep_postTitle_title);
        addstep_subTitle = (TextView)findViewById(R.id.addStep_postTitle_subTitle);

        addStep_image.setImageBitmap(postTitle_image);
        addStep_title.setText(postTitle_title);
        addstep_subTitle.setText(postTitle_subTitle);




        listView = (ListView)findViewById(R.id.addStep_addStepListView);
        myAdapter=new MyAdapter(this,list);
        listView.setAdapter(myAdapter);
        image_to_byte=null;
        name_str=null;
        ///////////////////////////////////////////////////////////////////.
        listView2 = (ListView)findViewById(R.id.addStep_addMatListView);
        matAdapter=new MatAdapter(this,list2);
        listView2.setAdapter(matAdapter);
        ///////////////////////////////////////////////////////////////////
        //＠커스텀 다이얼로그
//        LayoutInflater dialogInflater;
//        EditText Matname;
//        EditText Matcount;
//        EditText Matunit;
//        String MatnameStr;
//        int MatcountInt;
//        String MatunitStr;
        dialogInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public void addStepImageItem(View view) {
        Intent intent = new Intent(AddStep.this, ImagePickerActivity.class);
        // limit image pick count to only 3 images.
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }
    public void addStepTextItem(View view) {
        bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
        list.add(new Item1(bitmap2,""));
        myAdapter.notifyDataSetChanged();
    }
    public void addStepAddMat(View view) {
        showDialog(DIALOG_1);
    }
    public void post(View view) {
        ParseObject editTextValues = new ParseObject("post4"); //parse에 만들어진 해당 클래스로 데이터를 업로드한다.
        ParseUser user = ParseUser.getCurrentUser();//■2015.04.02 (1)


        // 1.  Step 업로드
        bitmap = new Bitmap[list.size()];
        name_str = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {

            ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();//★4
            name_str[i]="upload"+i;
            bitmap[i] = myAdapter.getItem(i).img;

            bitmap[i].compress(Bitmap.CompressFormat.JPEG, 70, byteArray2); //JPEG포맷의 품질을 70(%)으로 byteArray2에 저장.//★5
            byte[] image_to_byte = byteArray2.toByteArray();//★6
            Imagefile = new ParseFile(name_str[i], image_to_byte);//★7 "image"+1부분에 name_str[i]를 넣으면 업로드가 안되요.

            Log.d(TAG, "" + list.get(i).edit);
            editTextValues.put("step" + String.valueOf(i) + "image", Imagefile);//★8
            editTextValues.put("content" + String.valueOf(i), list.get(i).edit);
            editTextValues.put("user",user);//■2015.04.02 (2)
        }

        //2.표지업로드
        ByteArrayOutputStream byteArray3 = new ByteArrayOutputStream();
        postTitle_image.compress(Bitmap.CompressFormat.JPEG,70,byteArray3);
        byte[] bitmapToByte = byteArray3.toByteArray();
        ParseFile mainImageFile = new ParseFile(name_str[0], bitmapToByte);
        editTextValues.put("MAIN_IMAGE",mainImageFile);

        editTextValues.put("MAIN_SUBJECT",postTitle_title);
        editTextValues.put("SUB_SUBJECTE",postTitle_subTitle);

        //3. 재료 업로드
        //matAdapter.getItem(i).Matname
        for(int i=0;i<list2.size();i++) {
            editTextValues.put("FOOD_NAME_"+i,matAdapter.getItem(i).Matname );
            editTextValues.put("FOOD_COUNT_"+i,Integer.parseInt(matAdapter.getItem(i).quantity));
            editTextValues.put("FOOD_KIND_"+i,matAdapter.getItem(i).unit );
        }
        //4. Parse Cloud에 데이터 업로드
        editTextValues.saveInBackground();
        Toast.makeText(this, "텍스트 업로드 완료", Toast.LENGTH_SHORT).show();
        finish();
    }



    private class Item1 {
        public Bitmap img;
        public String edit;
        public Item1(Bitmap img, String edit) {
            this.img = img;
            this.edit = edit;
        }
        public Item1(String edit){
            this.edit = edit;
        }
    }
    private class MatItem{
        public String Matname;
        public String quantity;
        public String unit;
        public MatItem(String Matname,String quantity,String unit){
            this.Matname = Matname;
            this.quantity = ""+quantity;
            this.unit = unit;
        }

    }
    private class MatAdapter extends BaseAdapter {

        private final static int resId = R.layout.add_mat_item;
        private Context context;
        List<MatItem> list2;

        public MatAdapter(Context context, List<MatItem> list) {
            super();
            this.context = context;
            this.list2 = list;
        }

        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public MatItem getItem(int position) {
            return list2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            MatItem MatItem = getItem(position);

            if (v == null) {

                v = getLayoutInflater().inflate(resId, null);
                TextView tv1 = (TextView)v.findViewById(R.id.addMatList_Matname);
                TextView tv2 = (TextView)v.findViewById(R.id.addMatList_quantity);
                TextView tv3 = (TextView)v.findViewById(R.id.addMatList_unit);

            }
            TextView tv1 = (TextView)v.findViewById(R.id.addMatList_Matname);
            TextView tv2 = (TextView)v.findViewById(R.id.addMatList_quantity);
            TextView tv3 = (TextView)v.findViewById(R.id.addMatList_unit);

            tv1.setText(MatItem.Matname);
            tv2.setText(MatItem.quantity);
            tv3.setText(MatItem.unit);
            return v;
        }
    }
    private class MyWatcher implements TextWatcher {
        private EditText edit;
        private Item1 item;
        public MyWatcher(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TAG", "onTextChanged: " + s);
            this.item = (Item1)edit.getTag();
            if (item != null) {
                item.edit = s.toString();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.post_add_step;
        private Context context;
        List<Item1> list;

        public MyAdapter(Context context, List<Item1> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Item1 getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            Item1 item = getItem(position);

            if (v == null) {

                v = getLayoutInflater().inflate(resId, null);
                ImageView iv=(ImageView)v.findViewById(R.id.image);
                EditText et = (EditText)v.findViewById(R.id.edit);
                //
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
    //이미지의 경로와 이름을 받아오는 함수
    public String getImageNameToUri(Uri data) {
        String imgPath = data.toString();
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
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
                            int height=bitmap[i].getHeight();
                            int width=bitmap[i].getWidth();
                            if(width>=600){
                                if(width>=height){
                                    bitmap[i] = Bitmap.createScaledBitmap(bitmap[i], 600, height/(width/600), true);
                                } else{
                                    bitmap[i] = Bitmap.createScaledBitmap(bitmap[i], width/(height/600),600, true);
                                }}
                            else{
                                //아무것도안해도됨. 비트맵 있는그대로 놔두기.
                            }
                            list.add(new Item1(bitmap[i],"" + i));

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

    protected Dialog onCreateDialog(int id){
        switch (id){
            case DIALOG_1:
                final LinearLayout linear = (LinearLayout)dialogInflater.inflate(R.layout.add_mat,null);

                return new AlertDialog.Builder(AddStep.this)
                        .setTitle("주소록")
                        .setIcon(R.drawable.ic_launcher)
                        .setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //MatnameStr ="dd";
                                MatnameEt = (EditText) linear.findViewById(R.id.addMat_matName);
                                MatcountEt = (EditText) linear.findViewById(R.id.addMat_matInt);
                                MatunitEt = (EditText) linear.findViewById(R.id.addMat_matUnit);

                                MatnameStr = MatnameEt.getText().toString();
                                MatunitStr = MatunitEt.getText().toString();
                                MatcountStr = MatcountEt.getText().toString();

                                list2.add(new MatItem(MatnameStr,MatcountStr,MatunitStr));
                                matAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소",null)
                        .create();
        }
        return null;
    }
}