package com.antonioleiva.wearcook;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


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

    //페이스북 로그인과 관련된 함수. 자세한 설명은 나중에 공부해서 적겠다.
    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    //Sign_in버튼을 눌렀을 때 실행하는 함수로 유효한 id,pwd를 입력했을 때 다음 스텝으로 넘어간다.
    public void sign_in(View view) throws com.parse.ParseException {
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        //사용자가 입력한 id와 pwd 값을 받아온다.
        EditText write_userId=(EditText)findViewById(R.id.userId);
        String id=write_userId.getText().toString();

        EditText write_userPwd=(EditText)findViewById(R.id.userPwd);
        String pwd=write_userPwd.getText().toString();

        ParseUser user = new ParseUser();
        //로그인을 제어하는 함수
        user.logInInBackground(id,pwd,new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null && parseUser != null) {
                    loginSuccessful();
                } else if (parseUser == null) {
                    usernameOrPasswordIsInvalid();
                } else {
                    somethingWentWrong();
                }
            }

        });
    }
    //예기치 않는 에러가 발생시 호출되는 함
    private void somethingWentWrong() {
        Toast.makeText(this,"예기치 않는 에러가 발생했습니다",Toast.LENGTH_SHORT);
    }
    //유효하지 않은 id나 password가 입력됐을 경우 호출되는 함수이다.
    private void usernameOrPasswordIsInvalid() {
        Toast.makeText(this,"아이디나 비밀번호가 틀렸습니다",Toast.LENGTH_SHORT);
    }
    //ParseUser에 저장되어 있는 id,pwd값과 유저가 입력한 값이 같을 경우 호출되는 함수, 다음 MenuChoice 액티비티로 넘어간다.
    private void loginSuccessful() {
        Intent intent=new Intent(this,MenuChoice.class);
        startActivity(intent);
    }

    public void sign_up(View view) {
        Intent intent=new Intent(this,signUp.class);
        startActivity(intent);
    }
}
