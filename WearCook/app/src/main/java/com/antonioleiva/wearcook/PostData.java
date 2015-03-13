package com.antonioleiva.wearcook;

import android.graphics.Bitmap;

/**
 * Created by hwanik on 2015. 3. 13..
 */
public class PostData {
    Bitmap myImg;
    String title;
    String content;
    PostData(Bitmap _myImg, String _title, String _content){
        myImg = _myImg;
        title = _title;
        content = _content;
    }
}
