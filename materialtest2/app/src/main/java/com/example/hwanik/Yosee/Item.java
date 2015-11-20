package com.example.hwanik.Yosee;

/**
 * Created by hwanik on 2015. 4. 17..
 */

public class Item {
    public String img_url;
    public String Title;
    public String subTitle;
    public String objectId;
    public Item(String img_url, String Title,String subTitle, String objectId) {
        this.img_url = img_url;
        this.Title = Title;
        this.subTitle=subTitle;
        this.objectId=objectId;
    }
}