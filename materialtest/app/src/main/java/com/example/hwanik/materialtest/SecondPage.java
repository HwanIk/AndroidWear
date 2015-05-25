package com.example.hwanik.materialtest;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwanik on 2015. 4. 18..
 */
public class SecondPage extends Fragment implements TextView.OnEditorActionListener {
    static String imgUrl;
    private ListView listView;
    private MaterialEditText et;
    String et_text;
    String []check_string;
    private ImageButton searchBtn;
    private List<Item> list = new ArrayList<Item>();

    ArrayList<Materials> matList=new ArrayList<Materials>(10);

    private MyAdapter myAdapter;

    String[] imgUrlArray = new String[20]; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    String[] contentArray = new String[20]; //§ 2
    String tmpImg;

    int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.second_page_search, container, false);
        LinearLayout secondPage=(LinearLayout)linearLayout.findViewById(R.id.secondPage);
        listView = (ListView)secondPage.findViewById(R.id.search_lv);

        check_string=new String[10];

        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);

        et=(MaterialEditText)secondPage.findViewById(R.id.search_title);
        et.setOnEditorActionListener(this); //edittext와 enter버튼 눌렀을때 리스너를 연결시켜준다.

        searchBtn=(ImageButton)secondPage.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                load_from_parse();
            }
        });

        return secondPage;
    }
    public void load_from_parse(){
        et_text=et.getText().toString();
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    for(int i=0;i<parseObjects.size();i++ ){
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        int j=0;
                        int k=0;
                        while(parseObjects.get(i).getString("M_NAME_"+String.valueOf(k))!=null){
                            check_string[k]=parseObjects.get(i).getString("M_NAME_"+String.valueOf(k));
                            matList.add(new Materials(parseObjects.get(i).getString("M_NAME_" + String.valueOf(k)),
                                    parseObjects.get(i).getString("M_NUM_" + String.valueOf(k)),
                                    parseObjects.get(i).getString("M_UNIT_" + String.valueOf(k))));
                            k++;
                        }
                        for(int l=0;l<k; l++){
                            if(check_string[l].contains(et_text)) {
                                while (parseObjects.get(i).get("step" + String.valueOf(j) + "Image") != null
                                        | parseObjects.get(i).get("step"+String.valueOf(j)+"Content") != null) { //§ (3)
                                    ParseFile image = (ParseFile) parseObjects.get(i).get("step" + String.valueOf(j) + "Image");
                                    ParseFile titleImg=(ParseFile)parseObjects.get(i).get("MAIN_IMAGE");
                                    tmpImg=titleImg.getUrl();
                                    imgUrlArray[count] = image.getUrl();
                                    contentArray[count] = parseObjects.get(i).getString("step"+String.valueOf(j)+"Content");
                                    count++;
                                    j++;
                                }
                            }
                        }
                        ParseFile image=(ParseFile) parseObjects.get(i).get("MAIN_IMAGE");
                        imgUrl=image.getUrl();
                        list.add(new Item(imgUrl,parseObjects.get(i).getString("MAIN_TITLE"),
                                parseObjects.get(i).getString("SUB_TITLE"),parseObjects.get(i).getObjectId()));
                        i++;
                    }
                    myAdapter.notifyDataSetChanged();
                }else{
                    Log.d("error",e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        //검색버튼 누르면 키보드가 자동으로 내려간다.
        InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);

        list.clear();
        load_from_parse();

        return false;
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
            TextView tv = (TextView)v.findViewById(R.id.recent_Title);
            TextView tv1 = (TextView)v.findViewById(R.id.recent_subTitle);

            tv.setTypeface(typeface);
            tv1.setTypeface(typeface);

            tv.setTag(item);
            tv1.setTag(item);
            Picasso.with(context)
                    .load(item.img_url)
                    .into(iv);
            return v;
        }
    }
}
