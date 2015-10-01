package com.example.hwanik.materialtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class CategoryDetail extends ActionBarActivity {

    private String imgUrl;


    private String str;
    private int count=0;
    private String[] Mats;
    private String tmpImg;
    private String tmpTitle;
    private String tmpSubTitle;
    private String objectId;
    private ArrayList<Materials> matList=new ArrayList<Materials>(10);

    private LinearLayout headerView;
    private GridViewWithHeaderAndFooter gridView;
    private List<Item> list = new ArrayList<Item>();

    private MyAdapter categoryAdater;
    private Toolbar toolbar;
    String[] imgUrlArray = new String[20]; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    String[] contentArray = new String[20]; //§ 2

    ImageView catTopImg;
    TextView catTopTitle;
    TextView catTopSub;

    private int likeCnt;

    private static final String TYPEFACE_NAME = "fonts/NanumBarunGothic.otf";
    private Typeface typeface;
    private TextView toolbarTitle;

    private ParseFile image;
    private String catTopOjbectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);

        Intent intent=getIntent();
        str=intent.getStringExtra("category");

        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);
        toolbarTitle.setText(str);
        setTitle("");

        Mats=new String[3];

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        headerView=(LinearLayout)layoutInflater.inflate(R.layout.category_header,null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /////////////////////////////////////////////////////////////////////////////////
                ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
                query.whereEqualTo("objectId",catTopOjbectId);
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
                            Intent intent=new Intent(getApplicationContext(), Detail.class);
                            intent.putExtra("objectId",catTopOjbectId);
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
        gridView=(GridViewWithHeaderAndFooter)findViewById(R.id.category_lv);
        gridView.addHeaderView(headerView);

        categoryAdater=new MyAdapter(this,list);
        gridView.setAdapter(categoryAdater);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                objectId = list.get(i).objectId;
                /////////////////////////////////////////////////////////////////////////////////
                ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
                query.whereEqualTo("objectId", objectId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < parseObjects.size(); i++) {
                                Log.d("object size", String.valueOf(parseObjects.size()));
                                int j = 1;
                                int k = 0;
                                count = 0;
                                Mats[0] = parseObjects.get(i).getString("COOK_TIME");
                                Mats[1] = parseObjects.get(i).getString("COOK_MAN");
                                Mats[2] = parseObjects.get(i).getString("TIP");
                                matList.clear();
                                while (parseObjects.get(i).getString("M_NAME_" + String.valueOf(k)) != null) {
                                    //if(matCheck==false) {
                                    matList.add(new Materials(parseObjects.get(i).getString("M_NAME_" + String.valueOf(k)),
                                            parseObjects.get(i).getString("M_NUM_" + String.valueOf(k)),
                                            parseObjects.get(i).getString("M_UNIT_" + String.valueOf(k))));
                                    //}
                                    k++;
                                }
                                //matCheck=true;
                                while (parseObjects.get(i).get("step" + String.valueOf(j) + "Content") != null) { //§ (3)
                                    if (parseObjects.get(i).get("step" + String.valueOf(j) + "Image") != null) {
                                        image = (ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "Image");
                                    } else {
                                        image = null;
                                    }
                                    ParseFile titleImg = (ParseFile) parseObjects.get(i).get("MAIN_IMAGE");
                                    tmpImg = titleImg.getUrl();
                                    tmpTitle = parseObjects.get(i).getString("MAIN_TITLE");
                                    tmpSubTitle = parseObjects.get(i).getString("SUB_TITLE");
                                    imgUrlArray[count] = image.getUrl();
                                    contentArray[count] = parseObjects.get(i).getString("step" + String.valueOf(j) + "Content");
                                    count++;
                                    j++;
                                }
                            }
                            Intent intent = new Intent(getApplicationContext(), Detail.class);
                            intent.putExtra("objectId", objectId);
                            intent.putExtra("TitleImg", tmpImg);
                            intent.putExtra("Title", tmpTitle);
                            intent.putExtra("SUB", tmpSubTitle);
                            intent.putExtra("imgUrlArray", imgUrlArray);
                            intent.putExtra("contentArray", contentArray);
                            intent.putParcelableArrayListExtra("materials", matList);
                            intent.putExtra("count", count);
                            intent.putExtra("mats", Mats);
                            startActivity(intent);
                        } else {
                            Log.d("Error", e.getMessage());
                        }
                    }
                });
            }
        });

        catTopImg=(ImageView)headerView.findViewById(R.id.category_topImg);
        catTopTitle=(TextView)headerView.findViewById(R.id.category_topTitle);
        catTopSub=(TextView)headerView.findViewById(R.id.category_topSub);
        loadFromparse();
    }

    private void loadFromparse() {
        categoryAdater.removeAll();

        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.whereEqualTo("category", str);
        query.orderByDescending("likeCnt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        ParseFile image=(ParseFile) parseObjects.get(i).get("MAIN_IMAGE");
                        if(i==0){
                            Picasso.with(getApplicationContext())
                                    .load(image.getUrl())
                                    .into(catTopImg);
                            catTopOjbectId=parseObjects.get(i).getObjectId();
                            catTopTitle.setText(parseObjects.get(i).getString("MAIN_TITLE"));
                            catTopSub.setText(parseObjects.get(i).getString("SUB_TITLE"));
                            catTopTitle.setTypeface(typeface);
                            catTopSub.setTypeface(typeface);
                        }
                        else {
                            imgUrl = image.getUrl();

                            list.add(new Item(imgUrl, parseObjects.get(i).getString("MAIN_TITLE"),
                                    parseObjects.get(i).getString("SUB_TITLE"), parseObjects.get(i).getObjectId()));
                        }
                    }
                    categoryAdater.notifyDataSetChanged();
                }else{
                    Log.d("error",e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.category_item_list;
        private Context context;
        List<Item> list;

        public MyAdapter(Context context, List<Item> list) {
            super();
            this.context = context;
            this.list = list;
        }
        public void removeAll(){   //■2015.04.02 (7)
//            while(myAdapter.getCount()!=0)
//                myAdapter.list.remove(0);
            categoryAdater.list.clear();
            categoryAdater.notifyDataSetChanged();
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
                //ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
                //TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            }
            ImageView iv=(ImageView)v.findViewById(R.id.category_img);

            TextView tv = (TextView)v.findViewById(R.id.title);
            TextView tv1 = (TextView)v.findViewById(R.id.subTitle);

            tv.setTypeface(typeface);
            tv1.setTypeface(typeface);

            tv.setTag(item);
            tv1.setTag(item);
            iv.setTag(item);

            tv.setText(item.Title);
            tv1.setText(item.subTitle);

            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
        }
    }
}
