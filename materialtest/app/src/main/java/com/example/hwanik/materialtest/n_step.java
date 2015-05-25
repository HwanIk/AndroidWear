package com.example.hwanik.materialtest;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import nl.changer.polypicker.ImagePickerActivity;

public class n_step extends Fragment {

    private ImageView iv;
    private TextView tv;
    public EditText et;
    private Button nextStep_btn;
    private final int INTENT_REQUEST_GET_N_IMAGES=1;
    public Bitmap bitmap=null;
    private Uri uri=null;
    private String bitmapName = null;
    private int dataChange=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout v=(LinearLayout)inflater.inflate(R.layout.fragment_n_step,container,false);
        iv=(ImageView)v.findViewById(R.id.step_img);
        tv=(TextView)v.findViewById(R.id.step_title);
        et=(EditText)v.findViewById(R.id.step_content);
        nextStep_btn=(Button)v.findViewById(R.id.nextButton);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 1);
                startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
            }
        });
        nextStep_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                makeStep v=((makeStep)getActivity());
                v.mAdapter.addFragment(new n_step());
                v.mPager.setCurrentItem(v.curPage+1);
            }
        });

        //onCreate에서 다시 비트맵을 셋 해주는 이유는 FragmentPagerAdapter에 Fragment를 동적으로 생성함에 따라 notifyChangeData()함수가 호출된다.
        //이 때 각 Fragment의 데이터 변경이 생기면 Fragment는 다시 onCreateView를 호출하기 때문에 다시 값을 초기화 해주어야 제대로 출력이 된다.

        if(dataChange==1){
            iv.setImageBitmap(bitmap);
        }
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if(parcelableUris ==null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if(uris != null) {
                    uris[0]=Uri.parse("file://" + uris[0]);
                    uri=uris[0];
                    bitmapName = getImageNameToUri(uri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                        bitmap=resizeBitmapImageFn(bitmap,600);
                        iv.setImageBitmap(bitmap);
                        dataChange = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public Bitmap resizeBitmapImageFn(
            Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        //이미지의 가로 세로 비율에 맞게 조절
        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(
                bmpSource, newWidth, newHeight, true);
    }
    public String getImageNameToUri(Uri data) {
        String imgPath = data.toString();
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
}
