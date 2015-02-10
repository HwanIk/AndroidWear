/*
 * Copyright (C) 2014 Antonio Leiva Gordillo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antonioleiva.materialeverywhere;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class HomeActivity extends BaseActivity {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = (String) view.getTag();
                DetailActivity.launch(HomeActivity.this, view.findViewById(R.id.image), url);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class GridViewAdapter extends BaseAdapter {

        @Override public int getCount() {
            return 6;
        }

        @Override public Object getItem(int i) {
            String step []={
                    "김치는 작게 썬다.",
                    "양파는 굵게 다진다.",
                    "양념장을 고루 섞는다.",
                    "중간불로 달군 팬에 식용유(2)와 참기름(0.5)를 두른다.",
                    "김치를 넣고 부드러워질 때까지 볶는다",
                    "양파를 넣어 조금 더 볶는다"
            };
            return String.valueOf(i + 1)+" step : " + step[i];
        }

        @Override public long getItemId(int i) {
            return i;
        }

        @Override public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.grid_item, viewGroup, false);
            }
            String img_url[]= {
                    "http://postfiles8.naver.net/20150210_263/rlaghksdlr_1423539779151EfH9d_PNG/c_1.png?type=w3",
                    "http://postfiles7.naver.net/20150210_22/rlaghksdlr_1423539779508hFXr3_PNG/c_2.png?type=w3",
                    "http://postfiles2.naver.net/20150210_257/rlaghksdlr_1423539780485FNQxI_PNG/c_3.png?type=w3",
                    "http://postfiles14.naver.net/20150210_221/rlaghksdlr_1423539781002i3nK4_PNG/c_4.png?type=w3",
                    "http://postfiles5.naver.net/20150210_148/rlaghksdlr_1423539781523jo87u_PNG/c_5.png?type=w3",
                    "http://postfiles8.naver.net/20150210_151/rlaghksdlr_1423539781938bJnjJ_PNG/c_6.png?type=w3",
            };
            //String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
            view.setTag(img_url[i]);

            ImageView image = (ImageView) view.findViewById(R.id.image);
            Picasso.with(view.getContext())
                    .load(img_url[i])
                    .into(image);

            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(getItem(i).toString());

            return view;
        }
    }
}
