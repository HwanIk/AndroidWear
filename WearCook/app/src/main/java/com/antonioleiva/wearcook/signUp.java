package com.antonioleiva.wearcook;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.text.ParseException;

import com.facebook.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class signUp extends ActionBarActivity {

    String userNameTxt;
    String passwordTxt;
    EditText userName;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName=(EditText)findViewById(R.id.userName);
        password=(EditText)findViewById(R.id.password);

        //Pasre앱에 대한 부분 초기화
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");//parse와 페이스북 연동작업 초기화
        ParseFacebookUtils.initialize("461714007312357");
    }

    //사용자 기기에 Facebook 앱이 설치되어 있지 않은 경우 기본 대화상자 기반 인증을 하는 함수. 이 기능을 SSO(Single-Sign On)이라고 한다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
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
    //Parse 사용자에서 Facebook을 사용하는 방법은 기본적으로 (1) Facebook 사용자로 로그인하고 ParseUser 만들기 또는
    //                                            (2) Facebook을 기존 ParseUser에 연결하는 두 가지
    //Parse에 사용자를 가입시키는 버튼
    public void signUpBtn(View view) {

        userNameTxt= userName.getText().toString();
        passwordTxt= password.getText().toString();

        if (userNameTxt.equals("") || passwordTxt.equals("")) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 확인해주세요",
                    Toast.LENGTH_LONG).show();
        }else{
            //PasreUser클래스에 새로운 사용자에 대한 id,pwd,email을 추가한다.
            ParseUser user = new ParseUser();
            user.setUsername(userNameTxt);
            user.setPassword(passwordTxt);

            user.signUpInBackground(new SignUpCallback() {
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(),
                                "회원가입이 완료되었습니다",Toast.LENGTH_SHORT)
                                .show();
                        loginSuccessful();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "이미 등록된 이름입니다",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }

    public void sign_in_Facebook(View view) {
        ParseFacebookUtils.logIn(this,new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                if (parseUser == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (parseUser.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다",Toast.LENGTH_SHORT);
                    loginSuccessful();
                    finish();
                }
            }
        });
    }

    private void loginSuccessful() {
        Intent intent=new Intent(this,MenuChoice.class);
        startActivity(intent);
    }

}
