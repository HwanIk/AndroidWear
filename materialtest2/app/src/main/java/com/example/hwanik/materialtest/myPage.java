package com.example.hwanik.materialtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.*;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.widget.Toast;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class myPage extends Fragment {

    private LinearLayout linearLayout;
    private Typeface typeface;
    private ImageView profile_img;
    private int GET_PICTURE_URI=1;
    private byte []imgbytes;

    private GridViewWithHeaderAndFooter gridView;
    private LinearLayout headerView;
    private MyAdapter myAdapter;
    private List<Item> list = new ArrayList<Item>();

    private ParseFile image;
    private String tmpImg;
    private String imgUrl;
    private String tmpTitle;
    private String tmpSubTitle;
    private String[] Mats = new String[3];
    private ArrayList<Materials> matList=new ArrayList<Materials>(10);
    private int count;
    private String[] imgUrlArray=new String[20];
    private String[] contentArray=new String[20];

    private LinearLayout settingBtn;
    private LinearLayout likeBtn;
    private LinearLayout myPostBtn;

    private ImageView postIcon;
    private ImageView likeIcon;

    private TextView myLikeCnt;
    private TextView myPostCnt;
    private int mlc=0;
    private int mpc=0;
    private String objectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumBarunGothic.otf");
        Parse.initialize(getActivity().getApplicationContext(), "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        linearLayout=(LinearLayout)inflater.inflate(R.layout.fragment_mypage,container,false);
        headerView=(LinearLayout)inflater.inflate(R.layout.mypage_header,container,false);
        setGlobalFont(linearLayout);
        setGlobalFont(headerView);

        gridView=(GridViewWithHeaderAndFooter)linearLayout.findViewById(R.id.myPage_lv);
        gridView.addHeaderView(headerView);

        myAdapter=new MyAdapter(getActivity(),list);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                objectId=list.get(i).objectId;
                objectId = list.get(i).objectId;
                /////////////////////////////////////////////////////////////////////////////////
                ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
                query.whereEqualTo("objectId",objectId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < parseObjects.size(); i++) {
                                Log.d("object size", String.valueOf(parseObjects.size()));
                                int j = 1;
                                int k = 0;
                                count = 0;
                                Mats[0]=parseObjects.get(i).getString("COOK_TIME");
                                Mats[1]=parseObjects.get(i).getString("COOK_MAN");
                                Mats[2]=parseObjects.get(i).getString("TIP");
                                matList.clear();
                                while(parseObjects.get(i).getString("M_NAME_"+String.valueOf(k))!=null){
                                    //if(matCheck==false) {
                                    matList.add(new Materials(parseObjects.get(i).getString("M_NAME_" + String.valueOf(k)),
                                            parseObjects.get(i).getString("M_NUM_" + String.valueOf(k)),
                                            parseObjects.get(i).getString("M_UNIT_" + String.valueOf(k))));
                                    //}
                                    k++;
                                }
                                //matCheck=true;
                                while (parseObjects.get(i).get("step"+String.valueOf(j)+"Content") != null) { //§ (3)
                                    if(parseObjects.get(i).get("step" + String.valueOf(j) + "Image") != null) {
                                        image = (ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "Image");
                                    }else{
                                        image = null;
                                    }
                                    ParseFile titleImg=(ParseFile)parseObjects.get(i).get("MAIN_IMAGE");
                                    tmpImg=titleImg.getUrl();
                                    tmpTitle=parseObjects.get(i).getString("MAIN_TITLE");
                                    tmpSubTitle=parseObjects.get(i).getString("SUB_TITLE");
                                    imgUrlArray[count] = image.getUrl();
                                    contentArray[count] = parseObjects.get(i).getString("step"+String.valueOf(j)+"Content");
                                    count++;
                                    j++;
                                }
                            }
                            Intent intent=new Intent(getActivity(), Detail.class);
                            intent.putExtra("objectId",objectId);
                            intent.putExtra("TitleImg",tmpImg);
                            intent.putExtra("Title",tmpTitle);
                            intent.putExtra("SUB",tmpSubTitle);
                            intent.putExtra("imgUrlArray",imgUrlArray);
                            intent.putExtra("contentArray",contentArray);
                            intent.putParcelableArrayListExtra("materials", matList);
                            intent.putExtra("count",count);
                            intent.putExtra("mats",Mats);
                            startActivity(intent);
                        } else {
                            Log.d("Error",e.getMessage());
                        }
                    }
                });
            }
        });

        final ImageView profile_img=(ImageView)headerView.findViewById(R.id.profile_img);

        ParseUser user=ParseUser.getCurrentUser();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if(object.get("profile_img")!=null){
                    ParseFile profileImg=(ParseFile)object.get("profile_img");
                    Picasso.with(getActivity())
                            .load(profileImg.getUrl())
                            .into(profile_img);
                }
                else{
                    profile_img.setImageResource(R.drawable.myprofile);
                }
            }
        });
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_PICTURE_URI);
            }
        });

        likeIcon=(ImageView)headerView.findViewById(R.id.like_icon);
        postIcon=(ImageView)headerView.findViewById(R.id.post_icon);
        postIcon.setImageResource(R.drawable.postactive);

        myPostCnt=(TextView)headerView.findViewById(R.id.my_post_cnt);
        myPostCnt.setTextColor(0xFFFFBE00);
        myLikeCnt=(TextView)headerView.findViewById(R.id.my_like_cnt);

        settingBtn= (LinearLayout) headerView.findViewById(R.id.setting_Btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity().getApplicationContext(),settingPage.class);
                startActivity(intent);
            }
        });
        likeBtn=(LinearLayout)headerView.findViewById(R.id.like_post);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlc=0;
                likePostFromParse();
                likeIcon.setImageResource(R.drawable.likeactive);
                postIcon.setImageResource(R.drawable.myupload);
                myLikeCnt.setTextColor(0xFFFFBE00);
                myPostCnt.setTextColor(0xffc6c6c6);
            }
        });
        myPostBtn=(LinearLayout)headerView.findViewById(R.id.my_post);
        myPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpc=0;
                loadFromParse();
                postIcon.setImageResource(R.drawable.postactive);
                likeIcon.setImageResource(R.drawable.ilike);
                myPostCnt.setTextColor(0xFFFFBE00);
                myLikeCnt.setTextColor(0xffc6c6c6);
            }
        });

        likePostFromParse();
        loadFromParse();

        return linearLayout;
    }

    private void likePostFromParse() {
        final List<String> myLikePost=new ArrayList<>();
        ParseQuery<ParseObject> LikeQuery = ParseQuery.getQuery("Like");
        LikeQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getUsername());
        LikeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (int i = 0; i < parseObjects.size(); i++) {
                    Log.d("like list", String.valueOf(parseObjects.size()));
                    myLikePost.add(parseObjects.get(i).getString("foodId"));
                }
                searchData(myLikePost);
                myLikeCnt.setText(String.valueOf(mlc));
            }
        });
    }

    private void searchData(List<String> myLikePost) {
        myAdapter.removeAll();
        for(int i=0;i<myLikePost.size();i++) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
            query.getInBackground(myLikePost.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    ParseFile image = (ParseFile) object.get("MAIN_IMAGE");

                    imgUrl = image.getUrl();
                    tmpTitle = object.getString("MAIN_TITLE");
                    tmpSubTitle = object.getString("SUB_TITLE");
                    list.add(new Item(imgUrl, tmpTitle,
                            tmpSubTitle, object.getObjectId()));
                    myAdapter.notifyDataSetChanged();
                }
            });
            mlc++;
        }
    }
    private void loadFromParse() {
        myAdapter.removeAll();
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        mpc++;
                        ParseFile image=(ParseFile) parseObjects.get(i).get("MAIN_IMAGE");

                        imgUrl=image.getUrl();
                        tmpTitle=parseObjects.get(i).getString("MAIN_TITLE");
                        tmpSubTitle=parseObjects.get(i).getString("SUB_TITLE");
                        list.add(new Item(imgUrl,parseObjects.get(i).getString("MAIN_TITLE"),
                                parseObjects.get(i).getString("SUB_TITLE"),parseObjects.get(i).getObjectId()));
                    }
                    myAdapter.notifyDataSetChanged();
                }else{
                    Log.d("error", e.getMessage());
                }
                myPostCnt.setText(String.valueOf(mpc));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_URI) {
            if (resultCode == Activity.RESULT_OK) {
                ImageView profile_img=(ImageView)headerView.findViewById(R.id.profile_img);
                profile_img.setImageURI(data.getData());

                imgbytes=uriToByte(data.getData());


                ParseUser user=ParseUser.getCurrentUser();

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
                    public void done(ParseUser object, ParseException e) {
                        ParseFile proImg=new ParseFile("myImg",imgbytes);
                        object.put("profile_img",proImg);
                        // This will throw an exception, since the ParseUser is not authenticated
                        object.saveInBackground();
                    }
                });
            }
        }
        Toast.makeText(getActivity().getApplicationContext(), "사진이 변경되었습니다", Toast.LENGTH_SHORT);
    }

    private byte[] uriToByte(Uri data) {

        Bitmap bm = null;
        byte[] imgbytes=null;
        try {
            bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data);
            ByteArrayOutputStream  byteArray = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArray);
            imgbytes = byteArray.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgbytes;
    }

    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(typeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.mypage_item_list;
        private Context context;
        List<Item> list;

        private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
        private Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),TYPEFACE_NAME);

        public MyAdapter(Context context, List<Item> list) {
            super();
            this.context = context;
            this.list = list;
        }
        public void removeAll(){   //■2015.04.02 (7)
//            while(myAdapter.getCount()!=0)
//                myAdapter.list.remove(0);
            myAdapter.list.clear();
            myAdapter.notifyDataSetChanged();
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

                v = getActivity().getLayoutInflater().inflate(resId, null);
                //ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
                //TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            }
            ImageView iv=(ImageView)v.findViewById(R.id.mypost_img);
            TextView title = (TextView)v.findViewById(R.id.title);
            TextView subTitle = (TextView)v.findViewById(R.id.subTitle);

            title.setText(item.Title);
            subTitle.setText(item.subTitle);
            title.setTypeface(typeface);
            subTitle.setTypeface(typeface);
            //tv.setTag(item);
            //tv1.setTag(item);
            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
        }
    }
}
