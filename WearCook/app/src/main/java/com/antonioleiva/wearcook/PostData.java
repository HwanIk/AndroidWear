package com.antonioleiva.wearcook;

import android.graphics.Bitmap;
import android.widget.EditText;

/**
 * Created by hwanik on 2015. 3. 13..
 */
public class PostData {
    public Bitmap myImg;
    public EditText title;
    public EditText content;
    public PostData(){

    }
    public PostData(Bitmap _myImg, EditText _title, EditText _content){
        myImg = _myImg;
        title=_title;
        content=_content;
    }
}
