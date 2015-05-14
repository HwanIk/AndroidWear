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
    private EditText et;
    private Button nextStep_btn;
    private final int INTENT_REQUEST_GET_N_IMAGES=1;
    private Bitmap bitmap=null;
    private Uri uri=null;
    private String bitmapName = null;

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
        nextStep_btn=(Button)v.findViewById(R.id.next_btn);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 1);
                startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
            }
        });

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
                        int height=bitmap.getHeight();
                        int width=bitmap.getWidth();
                        if(width>=600){
                            if(width>=height){
                                bitmap = Bitmap.createScaledBitmap(bitmap, 600, height / (width / 600), true);
                            } else{
                                bitmap = Bitmap.createScaledBitmap(bitmap, width/(height/600),600, true);
                            }}
                        else{
                            //아무것도안해도됨. 비트맵 있는그대로 놔두기.
                        }
                        iv.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public String getImageNameToUri(Uri data) {
        String imgPath = data.toString();
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
}
