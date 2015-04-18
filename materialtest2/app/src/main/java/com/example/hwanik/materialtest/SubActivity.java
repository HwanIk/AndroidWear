package com.example.hwanik.materialtest;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SubActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Toolbar toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);       //Home위치에 버튼을 만들어주고
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼을 누르면 Up, 자신을 호출한 액티비티로 이동시키게 한다.

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//      서브 액티비티에서 홈 버튼(즉 뒤록가기 버)을 누르면 아래의 코드를 실행시킨다.
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this); //parent activity로 이동한다. 반드시 매니페스트에 parent를 명시해주어야 한다
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
