package com.example.hwanik.materialtest;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


public class SignIn extends FragmentActivity {

    //로그인에 관련된 변수들
    EditText userName;
    EditText password;
    String userNameTxt;
    String passwordTxt;
    ImageView loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Parse 앱 초기화, (Appication ID, Client Key)
        Parse.initialize(this, "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");

        userName=(EditText)findViewById(R.id.logUserName);
        password=(EditText)findViewById(R.id.logPassword);

        //userName.setBackgroundColor(.TRANSPARENT);




        loginBtn=(ImageView)findViewById(R.id.sign_in);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //사용자가 입력한 id와 pwd 값을 받아온다.
                userNameTxt=userName.getText().toString();
                passwordTxt=password.getText().toString();
                //유저를 관리하는 PasrUser객체 생성
                ParseUser user = new ParseUser();

                //로그인을 제어하는 함수
                if(userNameTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {

                    user.logInInBackground(userNameTxt, passwordTxt, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {//로그인 성공
                                loginSuccessful(); //메인 액티비티로 이동
                                finish();
                            } else { //parseUser가 Null일 경우, 자세히 분류하지는 않았지만 아이디가 일치하지 않는 경우인 것 같다
                                Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    //ParseUser에 저장되어 있는 id,pwd 값과 유저가 입력한 값이 같을 경우 호출되는 함수, 다음 MenuChoice 액티비티로 넘어간다.
    private void loginSuccessful() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    //회원가입으로 이동하는 함수
    public void sign_up(View view) {
        //Intent intent=new Intent(this,signUp.class);
        //startActivity(intent);
    }
    //facebook으로 가입하기, 로그인하기 둘다 제어한다.
    public void login_facebook(View view) {
        ParseFacebookUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                if (parseUser == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (parseUser.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT);
                    loginSuccessful();
                    finish();
                }
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
