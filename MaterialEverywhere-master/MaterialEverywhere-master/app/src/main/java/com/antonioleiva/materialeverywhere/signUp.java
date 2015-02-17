package com.antonioleiva.materialeverywhere;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.text.ParseException;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class signUp extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    public void signUpBtn(View view) {
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        EditText editId = (EditText)findViewById(R.id.userId);
        String id = editId.getText().toString();

        EditText editPass = (EditText)findViewById(R.id.userPwd);
        String password = editPass.getText().toString();

        EditText editEmail = (EditText)findViewById(R.id.userEmail);
        String email = editEmail.getText().toString();
        Toast.makeText(this,id,Toast.LENGTH_SHORT).show();


        ParseUser user = new ParseUser();
        user.setUsername(id);
        user.setPassword(password);
        user.setEmail(email);

// other fields can be set just like with ParseObject
        //user.put("phone", "1234-555-0000");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }

            @Override
            public void done(com.parse.ParseException e) {

            }
        });
    }
}
