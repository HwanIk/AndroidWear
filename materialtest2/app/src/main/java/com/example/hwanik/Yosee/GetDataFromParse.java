package com.example.hwanik.Yosee;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwanik on 2015. 11. 19..
 */
public class GetDataFromParse {
    String tableName;
    Context context;
    int page;

    private ParseQuery<ParseObject> query;
    private String Mats[]=new String[3];
    private int count;
    ArrayList<Materials> matList=new ArrayList<Materials>(10);
    ParseFile image;
    String tmpImg;
    String tmpTitle;
    String tmpSubTitle;
    String objectId;
//    String[] objectId = new String[20];
    String[] imgUrlArray = new String[20]; //§ (1) 생각해볼점.. 배열크기를 20정도로 하면 .ArrayIndexOutOfBoundsException 발
    String[] contentArray = new String[20]; //§ 2

    public GetDataFromParse(String tableName, Context context, int page){
        this.context=context;
        this.page=page;
        query = ParseQuery.getQuery(tableName);
    }
    public void whereEqualTo(String column, String data){
        objectId=data;
        query.whereEqualTo(column,data);
    }
    public void orderByDescending(String order){
        query.orderByDescending(order);
    }
    public void setLimit(int limit){
        query.setLimit(limit);
    }
    public void initData(){
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseObjects.size(); i++) {
                        int j = 1;
                        int k = 0;
                        count = 0;
                        Mats[0] = parseObjects.get(i).getString("COOK_TIME");
                        Mats[1] = parseObjects.get(i).getString("COOK_MAN");
                        Mats[2] = parseObjects.get(i).getString("TIP");

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
//                            objectId[count] = parseObjects.get(0).getObjectId();
                            imgUrlArray[count] = image.getUrl();
                            contentArray[count] = parseObjects.get(i).getString("step" + String.valueOf(j) + "Content");
                            count++;
                            j++;
                        }
                    }
                } else {
                    Log.d("Error", e.getMessage());
                }
                goToActivities(1);
            }
        });
    }

    private void goToActivities(int n) {

        Intent intent = new Intent(context, Detail.class);
        intent.putExtra("objectId", objectId);
        intent.putExtra("TitleImg", tmpImg);
        intent.putExtra("Title", tmpTitle);
        intent.putExtra("SUB", tmpSubTitle);
        intent.putExtra("imgUrlArray", imgUrlArray);
        intent.putExtra("contentArray", contentArray);
        intent.putParcelableArrayListExtra("materials", matList);
        intent.putExtra("count", count);
        intent.putExtra("mats", Mats);
        context.startActivity(intent);
    }

    public String[] getMaterials(){
        return Mats;
    }
    public ArrayList<Materials> getMaterialsList(){
        return matList;
    }
    public String[] getImgUrlArray(){
        return imgUrlArray;
    }
    public String[] getContentArray(){
        return contentArray;
    }
    public String getTmpImg(){
        return tmpImg;
    }
    public String getTmpTitle(){
        return tmpTitle;
    }
    public String getTmpSubTitle(){
        return tmpSubTitle;
    }
    public int getCount(){
        return count;
    }
}
