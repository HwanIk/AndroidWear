package com.example.hwanik.materialtest;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Created by hwanik on 2015. 4. 18..
 */
public class SecondPage extends Fragment {


    static String imgUrl;
    private ListView listView;
    private EditText et;
    String et_text;
    String check_string;
    private ImageButton searchBtn;
    private List<Item> list = new ArrayList<Item>();
    private MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.second_page_search, container, false);
        listView = (ListView)linearLayout.findViewById(R.id.search_lv);
        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);

        et=(EditText)linearLayout.findViewById(R.id.search_title);
        searchBtn=(ImageButton)linearLayout.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_from_parse();
            }
        });

        return linearLayout;
    }
    public void load_from_parse(){
        et_text=et.getText().toString();
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("hi");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        int j=0;
                        while(parseObjects.get(i).get("step" + String.valueOf(j) + "image")!=null){
                            check_string=parseObjects.get(i).getString("content" + String.valueOf(j));

                            if(et_text.equals(check_string)) {
                            Log.d("object size", parseObjects.get(i).getString("content" + String.valueOf(j)));

                                ParseFile image = (ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "image");

                                imgUrl = image.getUrl();
                                list.add(new Item(imgUrl, parseObjects.get(i).getString("content" + String.valueOf(j))));
                            }
                            j++;
                            if (j > 3) break;
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }else{
                    Log.d("error",e.getMessage());
                }
            }
        });
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
            TextView tv = (TextView)v.findViewById(R.id.recent_txt);
            tv.setTag(item);
            iv.setTag(item);

            tv.setText(item.txt);
            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
        }
    }
}
