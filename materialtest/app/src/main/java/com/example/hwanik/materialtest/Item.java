package com.example.hwanik.materialtest;

/**
 * Created by hwanik on 2015. 4. 17..
 */
public class Item {
    public String img_url;
    public String txt;
    public String objectId;
    public Item(String img_url, String txt, String objectId) {
        this.img_url = img_url;
        this.txt = txt;
        this.objectId=objectId;
    }
}