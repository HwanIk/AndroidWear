package com.antonioleiva.materialeverywhere;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;

import java.text.ParseException;


public class welcome extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void sign_in(View view) throws com.parse.ParseException {
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");
        EditText write_userId=(EditText)findViewById(R.id.userId);
        String id=write_userId.getText().toString();

        EditText write_userPwd=(EditText)findViewById(R.id.userPwd);
        String pwd=write_userPwd.getText().toString();

        ParseUser user = new ParseUser();

        user.logIn(id,pwd);

        Intent intent=new Intent(this,MenuChoice.class);
    }

    public void sign_up(View view) {
        Intent intent=new Intent(this,signUp.class);

        startActivity(intent);
    }
}
