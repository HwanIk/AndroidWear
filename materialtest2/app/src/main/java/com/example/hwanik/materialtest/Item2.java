package com.example.hwanik.materialtest;

import java.util.ArrayList;

/**
 * Created by hwanik on 2015. 6. 1..
 */
public class Item2 {
    public String img_url;
    public String Title;
    public String subTitle;
    public String objectId;
    public String[] imgUrlArray;
    public String[] contentArray;
    public ArrayList<Materials> matList;
    public int count;
    public String[] Mats;
    public Item2(String img_url, String Title,String subTitle, String objectId,
                 String[] imgUrlArray, String[] contentArray, ArrayList<Materials> matList,
                 int count, String[] Mats) {
        this.img_url = img_url;
        this.Title = Title;
        this.subTitle=subTitle;
        this.objectId=objectId;
        this.imgUrlArray=imgUrlArray;
        this.contentArray=contentArray;
        this.matList=matList;
        this.count=count;
        this.Mats=Mats;
    }
}