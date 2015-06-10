package com.example.hwanik.materialtest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment extends ActionBarActivity {
    ListView list;
    ArrayList<ItemData> itemDataArr;
    ItemAdapter mAdapter;
    EditText et;

    String objectId;

    //리스트에 추가하는 아이템 3개. 유저아이디, 코맨트,스트링데이터
    //이 작업은 UserId가 이미 있다는 전제 하에 진행된 테스트입니다.
    String userId; //유저아이디
    List<String> loadImgUrl;
    List<String> loadUserId;
    String currentUserId;
    String strDate; //날짜를 스트링으로 변환.
    String etStr; //내용

    // 업로드속도를 보완하기 위한 비교용 변수
    int currentLength =0;
    int currentState =0; //0이면 추가 1이면 삭제

    // 삭제시 사용하는 변수
    int removePosition =0;

    Toolbar toolbar;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.otf");
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle=(TextView)toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(typeface);
        toolbarTitle.setText("댓글");
        setTitle("");

        Intent intent=getIntent();

        objectId=intent.getStringExtra("objectId");
        et = (EditText)findViewById(R.id.comment);
        et.setTypeface(typeface);
        currentUserId =userId = ParseUser.getCurrentUser().getUsername();


        itemDataArr = new ArrayList<ItemData>();
        mAdapter = new ItemAdapter(this,R.layout.comment_list_item,itemDataArr);
        list = (ListView)findViewById(R.id.activity2ListView);
        list.setAdapter(mAdapter);

        commentRefresh();

//        for(int i=0;i<imgUrl.size();i++){
//            Log.d("asdf",imgUrl.get(i));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
    //코멘트 추가하는 함수.
    public void btn2(View view){

        ParseQuery<ParseObject> query= ParseQuery.getQuery("test1");
        query.getInBackground(objectId, new GetCallback<ParseObject>() { //여기 오브젝트 아이디 넣으면 돼요.
            public void done(ParseObject comment1, com.parse.ParseException e) {
                if (e == null) {
                    JSONArray commentContentArray = new JSONArray();
                    JSONArray commentUserIdArray = new JSONArray();
                    JSONArray commentDateArray = new JSONArray();

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                    strDate = dateFormat.format(date);

                    try{currentLength = comment1.getJSONArray("commentContentArray").length();}
                    catch (NullPointerException e1){
                        currentLength =0;
                    }

                    etStr = et.getText().toString();
                    commentContentArray.put(etStr);
                    commentUserIdArray.put(currentUserId);
                    commentDateArray.put(strDate);

                    comment1.add("commentContentArray", commentContentArray);
                    comment1.add("commentUserIdArray", commentUserIdArray);
                    comment1.add("commentDateArray", commentDateArray);
                    comment1.saveInBackground();

                    et.setText("");
                    currentState=0;
                    commentRefresh();
                }
            }
        });

    }


    public void commentRemove(final int removePosition){

//        itemDataArr.remove(removePosition);
//        mAdapter.notifyDataSetChanged();

        ParseQuery<ParseObject> query= ParseQuery.getQuery("test1");

        query.getInBackground(objectId, new GetCallback<ParseObject>() { //여기 오브젝트 아이디 넣으면 돼요.
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void done(ParseObject comment1, com.parse.ParseException e) {
                if (e == null) {


                    try{currentLength = comment1.getJSONArray("commentContentArray").length();}
                    catch (NullPointerException e1){
                        currentLength =0;
                    }

                    comment1.getJSONArray("commentContentArray").remove(removePosition);
                    comment1.getJSONArray("commentUserIdArray").remove(removePosition);
                    comment1.getJSONArray("commentDateArray").remove(removePosition);
                    comment1.saveInBackground();

                    currentState =1;
                    commentRefresh();
                }
            }
        });
    }


    public void commentRefresh(){
        final JSONArray[] commentContentArray = {new JSONArray()};
        final JSONArray[] commentUserIdArray = {new JSONArray()};
        final JSONArray[] commentDateArray = {new JSONArray()};
        final int[] finishI = new int[1];
        final String[] etStrtmp = new String[1];
        final String[] userIdtmp = new String[1];
        final String[] strDatetmp = new String[1];
        final String[] imgUrl = new String[1];

        itemDataArr.removeAll(itemDataArr);

        ParseQuery<ParseObject> query= ParseQuery.getQuery("test1");
        query.getInBackground(objectId, new GetCallback<ParseObject>() { //여기 오브젝트 아이디 넣으면 돼요.
            public void done(ParseObject comment1, com.parse.ParseException e) {
                if (e == null) {

                    try{
                        finishI[0] = comment1.getJSONArray("commentContentArray").length();
                    }
                    catch (NullPointerException e1){
                        finishI[0] =0;
                    }

                    commentContentArray[0] = comment1.getJSONArray("commentContentArray");
                    commentUserIdArray[0] = comment1.getJSONArray("commentUserIdArray");
                    commentDateArray[0] = comment1.getJSONArray("commentDateArray");

                    if(currentLength==finishI[0]){
                        etStrtmp[0] = etStr;
                        userIdtmp[0] = userId;
                        strDatetmp[0] = strDate;
                    }

                    for (int i = 0; i < finishI[0]; i++) {
                        try {
                            etStr = commentContentArray[0].getString(i);
                            etStr = etStr.substring(2, etStr.length() - 2);

                            userId = commentUserIdArray[0].getString(i);
                            userId = userId.substring(2, userId.length() - 2);

                            strDate = commentDateArray[0].getString(i);
                            strDate = strDate.substring(2, strDate.length() - 2);

                            itemDataArr.add(new ItemData(userId, etStr, strDate));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if(currentLength==finishI[0]){
                        if(currentState==0){
                            Log.d("0:", "추가");
                            itemDataArr.add(new ItemData(currentUserId, etStrtmp[0], strDatetmp[0]));
                        }else{
                            Log.d("1:", "삭제");
                            itemDataArr.remove(removePosition);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    list.smoothScrollToPosition(itemDataArr.size());
                }
            }
        });
    }

    public class ItemData {
        String kindTxt;
        String title;
        String Date;
        //String imgUrl;
        ItemData(String kindTxt,String title,String Date){
            this.kindTxt=kindTxt;
            this.title=title;
            this.Date=Date;
            //this.imgUrl=imgUrl;
        }
    }

    public class ItemAdapter extends BaseAdapter {
        Context context;
        int layoutId;
        ArrayList<ItemData> ItemDataArr;
        LayoutInflater Inflater;
        ItemAdapter(Context context,int layoutId,ArrayList<ItemData> ItemDataArr){
            this.context=context;
            this.layoutId=layoutId;
            this.ItemDataArr=ItemDataArr;
            Inflater= (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ItemDataArr.size();
        }

        @Override
        public Object getItem(int position) {
            return ItemDataArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = Inflater.inflate(layoutId,parent,false);
            }


            TextView listItemKindTv = (TextView)convertView.findViewById(R.id.activity2list1);
            listItemKindTv.setText(ItemDataArr.get(position).kindTxt);//유저 아이디
            listItemKindTv.setTypeface(typeface);

            ImageView listDeleteBtn = (ImageView)convertView.findViewById(R.id.activityDelete);
            String userIdtmp = ItemDataArr.get(position).kindTxt;
            if(currentUserId.equals(userIdtmp)){
                listDeleteBtn.setVisibility(convertView.VISIBLE);
                listDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removePosition = position;
                        commentRemove(position);
                    }
                });}else{
                listDeleteBtn.setVisibility(convertView.INVISIBLE);
            }

            TextView listItemTitle = (TextView)convertView.findViewById(R.id.activity2list2);
            listItemTitle.setText(ItemDataArr.get(position).title); //내용
            listItemTitle.setTypeface(typeface);

            TextView DateTv = (TextView)convertView.findViewById(R.id.activity2list3);
            DateTv.setText(ItemDataArr.get(position).Date); //날짜
            DateTv.setTypeface(typeface);

            ImageView iv=(ImageView)convertView.findViewById(R.id.userImg);

            return convertView;
        }
    }


}
