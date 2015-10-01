package com.example.hwanik.materialtest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseUser;


public class Splash extends ActionBarActivity {

    private static final String TAG = "MainFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

//        Session.StatusCallback callback = new Session.StatusCallback() {
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//                onSessionStateChange(session, state, exception);
//            }
//        };

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Send logged in users to Welcome.class

            Handler hd=new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent intent=new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                }
            },1000);

        } else {
            // Send user to LoginSignupActivity.class
            Handler hd=new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent intent=new Intent(Splash.this,SignIn.class);
                    startActivity(intent);

                }
            },1000);
        }

    }
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (state.isOpened()) {
//            Log.i(TAG, "Logged in...");
//        } else if (state.isClosed()) {
//            Log.i(TAG, "Logged out...");
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
}
