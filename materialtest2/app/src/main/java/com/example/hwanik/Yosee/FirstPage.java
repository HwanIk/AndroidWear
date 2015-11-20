package com.example.hwanik.Yosee;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.*;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Bitmap.Config;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.hwanik.materialtest.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private MyAdapter myAdapter;
    private PullRefreshLayout refreshlayout;
    int pos;

    String tmpImg;
    String tmpTitle;
    String tmpSubTitle;

    ParseFile image;
    ProgressDialog dialog;
    ProgressDialog dialog1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.test_list, container, false);

        recyclerView = (RecyclerView)linearLayout.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter=new RecyclerAdapter(getActivity().getApplicationContext(), list, R.layout.test_list);
        recyclerView.setAdapter(mAdapter);

        matCheck=false;
//        listView = (ListView)linearLayout.findViewById(R.id.recent_lv);
//        listView.setDivider(null);
//        myAdapter=new MyAdapter(getActivity(),list);
//        listView.setAdapter(myAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //＆2015.04.03 listview item의 onclick이벤트 [4]
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //§4
//
//                dialog1 = ProgressDialog.show(getActivity(), "레시피 로딩 중", "잠시만 기다려주세요", true, true);
//                dialog1.show();
//
//                pos = position;
//                objectId = list.get(pos).objectId;
//
//                final GetDataFromParse data = new GetDataFromParse("test1", getContext() ,1);
//                data.whereEqualTo("objectId", objectId);
//                data.initData();
//
//                dialog1.dismiss();
//
//            }
//        });

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

        return linearLayout;
    }

    public void load_from_parse(){
        dialog = ProgressDialog.show(getActivity(), "데이터 로딩중", "잠시만 기다려주세요", true, true);
        mAdapter.removeAll();
        // Locate the class table named "Footer" in Parse.com

        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.orderByDescending("updatedAt");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseObjects.size(); i++) {
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        ParseFile image = (ParseFile) parseObjects.get(i).get("MAIN_IMAGE");

                        imgUrl = image.getUrl();
                        list.add(new Item(imgUrl, parseObjects.get(i).getString("MAIN_TITLE"),
                                parseObjects.get(i).getString("SUB_TITLE"), parseObjects.get(i).getObjectId()));
                    }
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Log.d("error", e.getMessage());
                }
            }
        });
        refreshlayout.setRefreshing(false);
    }

    public void goTop(){
        listView.setSelectionFromTop(0,0);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        Context context;
        List<Item> items;
        int item_layout;
        public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
            this.context=context;
            this.items=items;
            this.item_layout=item_layout;
        }
        public void removeAll(){   //■2015.04.02 (7)
//            while(myAdapter.getCount()!=0)
//                myAdapter.list.remove(0);
            mAdapter.items.clear();
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null);
            return new ViewHolder(v);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Item item=items.get(position);
            Picasso.with(context)
                    .load(item.img_url)
                    .transform(new RoundedTransformation(20,0))
                    .fit().centerCrop()
                    .into(holder.image);
            holder.title.setText(item.Title);
            holder.subTitle.setText(item.subTitle);
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1 = ProgressDialog.show(getActivity(), "레시피 로딩 중", "잠시만 기다려주세요", true, true);
                    dialog1.show();

                    pos = position;
                    objectId = list.get(pos).objectId;

                    final GetDataFromParse data = new GetDataFromParse("test1", getContext() ,1);
                    data.whereEqualTo("objectId", objectId);
                    data.initData();

                    dialog1.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView subTitle;
            CardView cardview;

            public ViewHolder(View itemView) {
                super(itemView);
                image=(ImageView)itemView.findViewById(R.id.recent_image);
                title=(TextView)itemView.findViewById(R.id.recent_Title);
                subTitle=(TextView)itemView.findViewById(R.id.recent_subTitle);
                cardview=(CardView)itemView.findViewById(R.id.cardView);
            }
        }
    }
    private class MyAdapter extends BaseAdapter {

        private final static int resId = R.layout.list_item;
        private Context context;
        List<Item> list;

        private static final String TYPEFACE_NAME
                = "fonts/NanumBarunGothic.otf";
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

    private class RoundedTransformation implements com.squareup.picasso.Transformation{
        int radius;
        int margin;
        public RoundedTransformation(int radius, int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(),
                    source.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()
                    - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}