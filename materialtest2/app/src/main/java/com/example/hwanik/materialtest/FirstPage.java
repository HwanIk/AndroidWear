package com.example.hwanik.materialtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwanik on 2015. 4. 17..
 */
public class FirstPage extends Fragment {
    String objectId;//§3
    String[] imgUrlArray = new String[20]; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    String[] contentArray = new String[20]; //§ 2
    String[] Mats = new String[3];
    boolean matCheck;
    ArrayList<Materials> matList=new ArrayList<Materials>(10);

    int count = 0;

    static String imgUrl;
    private ListView listView;
    private List<Item> list = new ArrayList<Item>();

    private MyAdapter myAdapter;
    private PullRefreshLayout refreshlayout;
    int pos;

    String tmpImg;
    String tmpTitle;
    String tmpSubTitle;

    ParseFile image;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.test_list, container, false);

        matCheck=false;
        listView = (ListView)linearLayout.findViewById(R.id.recent_lv);
        listView.setDivider(null);
        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //＆2015.04.03 listview item의 onclick이벤트 [4]
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //§4
                dialog = ProgressDialog.show(getActivity(), "레시피 로딩 중", "잠시만 기다려주세요", true, true);

                pos=position;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //intent.putExtra("objectId",list.get(pos).objectId);
                        objectId = list.get(pos).objectId;
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
                                    dialog.dismiss();
                                } else {
                                    Log.d("Error",e.getMessage());
                                }
                            }
                        });
                        /////////////////////////////////////////////////////////////////////////////////
                    }
                }, 250);

            }
        });

        refreshlayout= (PullRefreshLayout)linearLayout.findViewById(R.id.swipeRefreshLayout);
        refreshlayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshlayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                load_from_parse();
                // refresh complete
            }
        });

        load_from_parse();

//        TextView page_num=(TextView)linearLayout.findViewById(R.id.page_num);
//        page_num.setText(String.valueOf(1));
        return linearLayout;
    }
    public void load_from_parse(){
        dialog = ProgressDialog.show(getActivity(), "데이터 로딩중", "잠시만 기다려주세요", true, true);
        myAdapter.removeAll();
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.orderByDescending("updatedAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        ParseFile image=(ParseFile) parseObjects.get(i).get("MAIN_IMAGE");

                        imgUrl=image.getUrl();
                        list.add(new Item(imgUrl,parseObjects.get(i).getString("MAIN_TITLE"),
                                parseObjects.get(i).getString("SUB_TITLE"),parseObjects.get(i).getObjectId()));
                    }
                    myAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    Log.d("error",e.getMessage());
                }
            }
        });
        refreshlayout.setRefreshing(false);
    }

    public void goTop(){
        listView.setSelectionFromTop(0,0);
    }

    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.list_item;
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
            ImageView iv=(ImageView)v.findViewById(R.id.recent_image);
            ImageView best=(ImageView)v.findViewById(R.id.best);
            iv.setBackgroundResource(R.drawable.img_frame);

            TextView tv = (TextView)v.findViewById(R.id.recent_Title);
            TextView tv1 = (TextView)v.findViewById(R.id.recent_subTitle);

            tv.setTypeface(typeface);
            tv1.setTypeface(typeface);

            tv.setTag(item);
            tv1.setTag(item);
            iv.setTag(item);

            tv.setText(item.Title);
            tv1.setText(item.subTitle);

            if(position<=9) {
                best.setImageResource(getResources().getIdentifier("best" + (position + 1), "drawable", getActivity().getPackageName()));
            }

            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
        }
    }
}