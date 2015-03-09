package com.antonioleiva.wearcook;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Arrays;

public class welcome extends FragmentActivity {
    //페이스북 로그인 관련
    private LoginButton loginBtn;
    private TextView check_login;
    private UiLifecycleHelper uiHelper;

    EditText userName;
    EditText password;
    String userNameTxt;
    String passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        check_login = (TextView) findViewById(R.id.check_login);
        loginBtn = (LoginButton) findViewById(R.id.authButton);
        loginBtn.setReadPermissions(Arrays.asList("email"));
        loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    check_login.setText("You are currently logged in as " + user.getName());
                } else {
                    check_login.setText("You are not logged in.");
                }
            }

        });
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("MainActivity", "Facebook session opened.");
                loginSuccessful();
                finish();
            } else if (state.isClosed()) {
                Log.d("MainActivity", "Facebook session closed.");
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
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

    //Sign_in버튼을 눌렀을 때 실행하는 함수로 유효한 id,pwd를 입력했을 때 다음 스텝으로 넘어간다.
    public void sign_in(View view) throws com.parse.ParseException {
        userName=(EditText)findViewById(R.id.logUserName);
        password=(EditText)findViewById(R.id.logPassword);
        //사용자가 입력한 id와 pwd 값을 받아온다.
        userNameTxt=userName.getText().toString();
        passwordTxt=password.getText().toString();

        ParseUser user = new ParseUser();
        //로그인을 제어하는 함수
        user.logInInBackground(userNameTxt,passwordTxt,new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    loginSuccessful();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"아이디가 존재하지 않습니다. 회원가입 버튼을 눌러주세요",Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    //ParseUser에 저장되어 있는 id,pwd 값과 유저가 입력한 값이 같을 경우 호출되는 함수, 다음 MenuChoice 액티비티로 넘어간다.
    private void loginSuccessful() {
        Intent intent=new Intent(this,MenuChoice.class);
        startActivity(intent);
    }
    public void sign_up(View view) {
        Intent intent=new Intent(this,signUp.class);
        startActivity(intent);
        finish();
    }
}
