package com.example.hwanik.Yosee;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwanik.materialtest.R;
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
public class SecondPage extends Fragment implements TextView.OnEditorActionListener{

    private GridView listView;

    private static String imgUrl;
    private EditText et;
    private String et_text;
    private List<String>wordStack=new ArrayList<>();
    String[] Mats = new String[3];

    private ImageView searchBtn;
    private List<Item> list = new ArrayList<Item>();
    ArrayList<Materials> matList=new ArrayList<Materials>(10);


    private LinearLayout linearLayout;
    private LinearLayout wordContainer;

    private MyAdapter myAdapter;

    String[] imgUrlArray = new String[20]; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    String[] contentArray = new String[20]; //§ 2
    String tmpImg;
    String objectId;
    private String tmpTitle;
    private String tmpSubTitle;
    ParseFile image;
    int count=0;
    private android.graphics.Typeface typeface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumBarunGothic.otf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        linearLayout=(LinearLayout)inflater.inflate(R.layout.second_page_search, container, false);
        LinearLayout searchItemList=(LinearLayout)inflater.inflate(R.layout.search_item_list, container, false);

        setGlobalFont(searchItemList);
        setGlobalFont(linearLayout);

        LinearLayout secondPage=(LinearLayout)linearLayout.findViewById(R.id.secondPage);
        listView = (GridView) secondPage.findViewById(R.id.search_lv);
        wordContainer=(LinearLayout)secondPage.findViewById(R.id.searchWord);

        myAdapter=new MyAdapter(getActivity(),list);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
        et=(EditText)secondPage.findViewById(R.id.search_title);
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
                return true;
            }
        });
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if(!et.getText().toString().equals("")) {
                            list.clear();
                            makeWord();
                            load_from_parse();
                            et.setText("");
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        }else{
                            Toast.makeText(getActivity(),"음식이름 또는 재료를 입력해주세요",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        }); //edittext와 enter버튼 눌렀을때 리스너를 연결시켜준다.

        searchBtn=(ImageView)secondPage.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et.getText().toString().equals("")) {
                    list.clear();
//                  makeWord();
                    load_from_title();
                    et.setText("");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                }else{
                    Toast.makeText(getActivity(),"음식이름 또는 재료를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return secondPage;
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

    private void makeWord() {
        final LinearLayout parentLL = new LinearLayout(getActivity().getApplicationContext());
        parentLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parentLL.setOrientation(LinearLayout.HORIZONTAL);


        final TextView word=new TextView(getActivity().getApplicationContext());
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(20,20,5,20); //left top right bottm
        word.setLayoutParams(llp);
        word.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        word.setTextColor(Color.parseColor("#000000"));
        word.setTextSize(18);
        word.setTypeface(typeface);
        word.setGravity(Gravity.LEFT);
        word.setText(et.getText().toString());
        parentLL.addView(word);

        ImageView delete=new ImageView(getActivity().getApplicationContext());
        delete.setLayoutParams(new LinearLayout.LayoutParams(
                (int)getResources().getDimension(R.dimen.deleteWith),
                (int)getResources().getDimension(R.dimen.deleteWith)));
        delete.setImageResource(R.drawable.delete);
        parentLL.addView(delete);

        wordContainer.addView(parentLL);

        parentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<wordStack.size();i++){
                    if(wordStack.get(i).equals(word.getText().toString())) {
                        wordStack.remove(i);
                    }
                }
                parentLL.removeAllViews();
                list.clear();
                load_from_parse();

            }
        });
        wordStack.add(et.getText().toString());
    }

    public void load_from_parse(){
        et_text=et.getText().toString();
        final String[] objectId = new String[1];
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");

        query.whereContainsAll("M_ARRAY",wordStack);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseObjects.size(); i++) {
                        Log.d("object size", String.valueOf(parseObjects.size()));
                        int j = 1;
                        int k = 0;
                        count = 0;
                        objectId[0] =parseObjects.get(i).getObjectId();
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
                        list.add(new Item(tmpImg, tmpTitle,
                                tmpSubTitle, parseObjects.get(i).getObjectId()));
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Error",e.getMessage());
                }
                for(int i=0;i<list.size();i++){
                    Log.d("objectId",list.get(i).objectId);
                }
            }
        });
    }

    public void load_from_title(){
        et_text=et.getText().toString();
        final String[] objectId = new String[1];
        // Locate the class table named "Footer" in Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseObjects.size(); i++) {
                        if(parseObjects.get(i).getString("MAIN_TITLE").contains(et_text)) {
                            int j = 1;
                            int k = 0;
                            count = 0;
                            objectId[0] = parseObjects.get(i).getObjectId();
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
                            list.add(new Item(tmpImg, tmpTitle,
                                    tmpSubTitle, parseObjects.get(i).getObjectId()));
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Error",e.getMessage());
                }
                for(int i=0;i<list.size();i++){
                    Log.d("objectId",list.get(i).objectId);
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

        private final static int resId = R.layout.search_item_list;
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

            final Item item = getItem(position);

            if (v == null) {

                v = getActivity().getLayoutInflater().inflate(resId, null);
                ImageView iv=(ImageView)v.findViewById(R.id.search_image);
                TextView title = (TextView)v.findViewById(R.id.title);
                TextView subTitle = (TextView)v.findViewById(R.id.subTitle);
            }
            ImageView iv=(ImageView)v.findViewById(R.id.search_image);
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