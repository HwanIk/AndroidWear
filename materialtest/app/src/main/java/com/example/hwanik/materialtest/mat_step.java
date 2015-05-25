package com.example.hwanik.materialtest;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class mat_step extends Fragment {

    private byte[] byteToBitmap;
    private Bitmap matStep_image;
    private String postTitle_title;

    private ListView listview;
    private matAdapter matAdapter;
    public List<matItem> list=new ArrayList<matItem>();

    public MaterialEditText cookT;
    public MaterialEditText cookM;
    public MaterialEditText tipT;
    public String cookTime;
    public String cookMan;
    public String tip;

    private String TYPEFACE_NAME;
    private Typeface typeface;

    public mat_step(byte[] byteToBitmap, String postTitle_title){
        this.byteToBitmap=byteToBitmap;
        this.postTitle_title=postTitle_title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TYPEFACE_NAME="fonts/NanumBarunGothic.otf";
        typeface=Typeface.createFromAsset(getActivity().getAssets(),TYPEFACE_NAME);

        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.fragment_mat_step, container, false);
        ImageView matStep_img=(ImageView)linearLayout.findViewById(R.id.matStep_image);
        TextView matStep_txt=(TextView)linearLayout.findViewById(R.id.matStep_title);
        TextView secondTitle=(TextView)linearLayout.findViewById(R.id.secondTitle);
        secondTitle.setTypeface(typeface);
        matStep_txt.setTypeface(typeface);


        matStep_image=BitmapFactory.decodeByteArray(byteToBitmap,0,byteToBitmap.length);

        matStep_img.setImageBitmap(matStep_image);
        matStep_txt.setText(postTitle_title);

        listview=(ListView)linearLayout.findViewById(R.id.mat_list);
        listview.setDivider(null);
        matAdapter=new matAdapter(getActivity(),list);
        listview.setAdapter(matAdapter);

        if(list.size() == 0) {
            list.add(new matItem("", "", ""));
        }

        ImageView addMat=(ImageView)linearLayout.findViewById(R.id.addMat);

        addMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new matItem("","",""));
                matAdapter.notifyDataSetChanged();
            }
        });

        cookT=(MaterialEditText)linearLayout.findViewById(R.id.time);
        cookM=(MaterialEditText)linearLayout.findViewById(R.id.man);
        tipT=(MaterialEditText)linearLayout.findViewById(R.id.tip);
        cookTime=cookT.getText().toString();
        cookMan=cookM.getText().toString();
        tip=tipT.getText().toString();
        // Inflate the layout for this fragment
        return linearLayout;
    }

    public class matItem{
        public String matName;
        public String matNum;
        public String matUnit;
        public matItem(String matName, String matNum, String matUnit) {
            this.matName = matName;
            this.matName = matNum;
            this.matUnit = matUnit;
        }
    }
    private class MyWatcher implements TextWatcher {
        private EditText edit;
        private matItem item;
        private int position;
        public MyWatcher(EditText edit, int position) {
            this.edit = edit;
            this.position=position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TAG", "onTextChanged: " + s);
            this.item=(matItem)edit.getTag();
            switch(position){
                case 0:
                    //if (item.matName != null) {
                    item.matName = s.toString();
                    //}
                    break;

                case 1:
                    //if (item.matNum != null) {
                    item.matNum = s.toString();
                    //}
                    break;

                case 2:
                    //if (item.matUnit != null) {
                    item.matUnit = s.toString();
                    //}
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public class matAdapter extends BaseAdapter{



        private final static int resId = R.layout.matlist_item;
        private List<matItem> list;
        private Context context;
        public matAdapter(Context context, List<matItem> list){
            super();
            this.list=list;
            this.context=context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public matItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v=view;
            matItem item=getItem(position);

            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(resId, null);

                EditText matName=(EditText)v.findViewById(R.id.mat_name);
                EditText matNum=(EditText)v.findViewById(R.id.mat_num);
                EditText matUnit=(EditText)v.findViewById(R.id.mat_unit);

                matName.addTextChangedListener(new MyWatcher(matName,0));
                matNum.addTextChangedListener(new MyWatcher(matNum,1));
                matUnit.addTextChangedListener(new MyWatcher(matUnit,2));

            }
            EditText matName=(EditText)v.findViewById(R.id.mat_name);
            EditText matNum=(EditText)v.findViewById(R.id.mat_num);
            EditText matUnit=(EditText)v.findViewById(R.id.mat_unit);

            matName.setTag(item);
            matNum.setTag(item);
            matUnit.setTag(item);

            matName.setText(item.matName);
            matNum.setText(item.matNum);
            matUnit.setText(item.matUnit);

            return v;
        }
    }
}
