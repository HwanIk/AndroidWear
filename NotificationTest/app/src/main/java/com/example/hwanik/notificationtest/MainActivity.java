package com.example.hwanik.notificationtest;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NotificationManagerCompat; //알림을 관리하는 클래스
import android.text.SpannableStringBuilder;

public class MainActivity extends ActionBarActivity {

    static final int BASIC_NOTIFICATION_ID=0; //알림생성 중복을 구분하는 일련번호이다. 알림을 생성할 때 동일한 일련번호를 입력하면 추가생성x
    static final int ACTION_NOTIFICATION_ID=1;
    static final int BIG_PICTURE_NOTIFICATION_ID=3;
    static final int BIG_TEXT_NOTIFICATION_ID=4; //큰 글자 알림 일련번호
    static final int INBOX_NOTIFICATION_ID=5;
    static final int GROUP_NOTIFICATION1_ID=6;
    static final int GROUP_NOTIFICATION2_ID=7;
    static final int GROUP_NOTIFICATION3_ID=8;
    final static String GROUP_KEY="group_key";
    static final int SUMMARY_NOTIFICATION_ID=9;
    static final int PAGE_NOTIFICATION=10; //페이지 알림 일련번호
    static final int BACKGROUND_NOTIFICATION_ID=11; //배경 알림 일련번호
    static final int ICON_NOTIFICATION_ID=12; //아이콘 알림 일련번호
    static final int GRAVITY_NOTIFICATION_ID=13; //정렬알림 일련번
    static final int CONTENT_ACTION_NOTIFICATION_ID=14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void showBasicNotification(View view) {

        //큰 아이콘 리소스로부터 가져온다.
        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_accept);
        long afterOneMinute=System.currentTimeMillis()-60000; //현재시간보다 1분 전 시간을 보여줘!!

        Notification notification=new NotificationCompat.Builder(this) //builder는 알림의 속성을 설정한다.
                .setContentTitle("BasicNotification Title") //타이틀 설정
                .setContentText("BasicNotificationText") // 텍스트 설정
                .setSubText("SubText") //상태바 내렸을 때 하단에 작게 표시되는 메시지
                .setTicker("TickerText") //상태바에 뜨는 메시지
                .setUsesChronometer(true)//알림 발생 후 얼마나 시간이 경과되었는지 1초단위로 보여준다.
                .setWhen(afterOneMinute)//일분 후 시간 표시
                .setNumber(100) //숫자표시, 숫자나 시간 둘중하나 선택해서 표시하게 하면 된다. 두개 다 사용시 setNumber는 무시된다.
                .setContentInfo("ContentInfo") //우측하단 텍스트
                .setSmallIcon(R.drawable.ic_action_accept) //아이콘 설정
                .setLargeIcon(largeIcon)
                .build();
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this); //알림을 실행시키기 위해 객체 생성
        //this는 MyActivity 현재 클래스를 가리킨다. 이 앱의 MyActivity에서 실행했다는것을 지정

        notificationManager.notify(BASIC_NOTIFICATION_ID,notification);
    }
    public void showActionNotification(View view) {
        //액티비티 인텐트 생성, 알람의 액션 버튼을 눌렀을 때 실행할 액티비티를 생성
        Intent viewIntent=new Intent(this,MainActivity.class);
        //액션을 실행했을 때까지 대기할 팬딩인텐트 생성
        PendingIntent viewPendingIntent=PendingIntent.getActivity(this,0,viewIntent,0);
        //알림을 생성하며, 액션을 추가한다.
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("ActionNotificationTitle")
                .setContentText("ActionNotificationText")
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_action_call,"Call",viewPendingIntent)
                .addAction(R.drawable.ic_action_cut,"Cut",viewPendingIntent)
                .addAction(R.drawable.ic_action_accept,"Accept",viewPendingIntent)
                .setContentIntent(viewPendingIntent)
                .build();
        //알림 매니저 객체를 생성하고 실핸한다.
        NotificationManagerCompat.from(this).notify(ACTION_NOTIFICATION_ID,notification);
        //compat이 들어가는 것은 안드로이드 이전 버전 사용자와 호환이 가능하다는 뜻이다.
    }
    public void showBigPictureNotification(View view) {
        //리소스로부터 사진을 가져온다.
        Bitmap bigPicture=BitmapFactory.decodeResource(getResources(),R.drawable.example_big_picture);

        //알림을 위한 큰 사진 스타일 생성
        NotificationCompat.BigPictureStyle style=new NotificationCompat.BigPictureStyle();
        style.bigPicture(bigPicture); //리소스의 사진을 적용시킨다.
        style.setBigContentTitle("BigContentTitle"); //사진을 펼쳤을 때의 타이틀
        style.setSummaryText("SummaryText"); //사진을 펼쳤을 때의 텍스트

        //알림을 생성
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Title") //알림 호출시 보여주는 타이틀
                .setContentText("Text") //알림 호출시 보여주는 텍스트
                .setSmallIcon(R.drawable.ic_action_accept) //작은 아이콘 설정
                .setStyle(style) //스타일을 적용
                .build();

        //알림 매니저 객체를 생성하고 실행
        NotificationManagerCompat.from(this).notify(BIG_PICTURE_NOTIFICATION_ID,notification);
    }
    public void showBigTextNotification(View view) {
        //타이틀을 스타일에 적용하여 생성한다.
        SpannableStringBuilder title=new SpannableStringBuilder();
        //코딩으로 스타일을 만드는 클래스 : SpannableStringBuilder
        title.append("Stylized Title");
        title.setSpan(new RelativeSizeSpan(1.25f),0,8,0); //Stylized 크기 조정
        title.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0,8,0); //굵게, 기울기

        //텍스트를 스타일에 적용하여 생성한다.
        SpannableStringBuilder text=new SpannableStringBuilder();
        text.append("Stylized Text");
        text.setSpan(new RelativeSizeSpan(1.25f),0,8,0); //크기조정
        text.setSpan(new ForegroundColorSpan(Color.RED),0,3,0);//sty빨간 index 0부터 2(3-1)
        text.setSpan(new ForegroundColorSpan(Color.GREEN),3,6,0);//liz녹색
        text.setSpan(new ForegroundColorSpan(Color.BLUE),6,8,0);//ed파란색

        //알림을 위한 큰 글자 스타일을 생성한다.
        NotificationCompat.BigTextStyle style=new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title); //타이틀에 대한 스타일 적용
        style.bigText(text); //텍스트에 대한 스타일 적용

        //알림을 생성
        Notification notification=new NotificationCompat.Builder(this) //기본 알람
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setStyle(style)
                .build();
        NotificationManagerCompat.from(this).notify(BIG_TEXT_NOTIFICATION_ID,notification);
    }
    public void showInboxNotification(View view) {
        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        style.addLine("Inbox Style Text Example Line1");//첫번째 라인
        style.addLine("Inbox Style Text Example Line2");//두번째 라인
        style.addLine("Inbox Style Text Example Line3");//세번째 라인
        style.setBigContentTitle("Inbox Title");//인박스 타이틀
        style.setSummaryText("Inbox Text");//인박스 텍스트 세번째 라인 밑에 조그맣게 그레이 색깔로 설명 되어 있는 부분.

        //알림을 생성한다.
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setStyle(style)
                .build();
        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(INBOX_NOTIFICATION_ID,notification);
    }
    public void showGroupNotification1(View view) {
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Group Text 1")
                .setContentText("Group Text 1")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setGroup(GROUP_KEY) //문자 상수로 그룹을 지정하여 여러개의 알람을 동일한 그룹으로 보여준다.
                .setSortKey("1") //그룹을 열 때 안에 있는 index에 따라 보여준다.
                .build();
        NotificationManagerCompat.from(this).notify(GROUP_NOTIFICATION1_ID,notification);
    }
    public void showGroupNotification2(View view) {
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Group Text 2")
                .setContentText("Group Text 2")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setGroup(GROUP_KEY)
                .setSortKey("2")
                .build();
        NotificationManagerCompat.from(this).notify(GROUP_NOTIFICATION2_ID,notification);
    }
    public void showGroupNotification3(View view) {
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Group Text 3")
                .setContentText("Group Text 3")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setGroup(GROUP_KEY)
                .setSortKey("3")
                .build();
        NotificationManagerCompat.from(this).notify(GROUP_NOTIFICATION3_ID,notification);
    }
    public void showSummaryNotification(View view) {
        //알림을 위한 인박스 스타일을 생성한다.
        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        style.addLine("Group Text1");
        style.addLine("Group Text2");
        style.addLine("Group Text3");
        style.setBigContentTitle("Summary Title");
        style.setSummaryText("Summary Text");

        //알림을 생성한다.
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setStyle(style)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .build();
        NotificationManagerCompat.from(this).notify(SUMMARY_NOTIFICATION_ID,notification);

    }

    //알림빌더를 사용하여 액션을 추가하면 새로운 페이지에 반드시 버튼이 생성된다
    //하지만 웨어러블 확장 클래스를 활용하면 액션을 추가하지 않고, 새로운 페이지를 추가할 수 있다.
    public void showPageNotification(View view) {
        //두번째 페이지 웨어러블 옵션을 생성한다. 두번째 페이지를 위한 웨어러블 확장 클래스, 시계에서 첫번째 뜨는 알림 다음에 뜨는 두번째 페이지를 뜻한다.
        NotificationCompat.WearableExtender secondWearableExtender=new NotificationCompat.WearableExtender()
                .setContentIcon(R.drawable.ic_launcher)
                .setContentIconGravity(Gravity.END); //아이콘 배치를 우측 끝에다가 배치한다는 뜻 같다.
        //두번째 페이지를 생성한다.
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Second Page Title")
                .setContentText("Second Page Text")
                .extend(secondWearableExtender)
                .build();
        //세번째 페에지를 생성한다.
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Third Page Title")
                .setContentText("Third Page Text")
                .extend(new NotificationCompat.WearableExtender() //확장 알림객체를 생성하여 직접 매개변수로 입력
                        .setContentIcon(R.drawable.ic_launcher)   //두번째 페이지의 나눠진 코드를 축약한 코딩형태이다.
                        .setContentIconGravity(Gravity.END))
                .build();

        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째 세번째 페이지를 추가한다.
        NotificationCompat.WearableExtender wearableOption=new NotificationCompat.WearableExtender()
                .setContentIcon(R.drawable.ic_launcher)
                .setContentIconGravity(Gravity.END)
                .addPage(secondPage) //두번째 페이지 추가
                .addPage(thirdPage); //세번째 페이지 추가

        //웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Page title")
                .setContentText("Page Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOption) //확장메소드를 통해 wearableOption에 설정한 값들을 확장시켰다.
                .build();

        //알림 매니저 객체를 생성하고 실핸한다.
        NotificationManagerCompat.from(this).notify(PAGE_NOTIFICATION,notification);


    }
    public void showBackgroundNotification(View view) {
        //리소스로부터 사진을 가져온다.
        Bitmap background1=BitmapFactory.decodeResource(getResources(),R.drawable.bg_1);
        Bitmap background2=BitmapFactory.decodeResource(getResources(),R.drawable.bg_2);
        Bitmap background3=BitmapFactory.decodeResource(getResources(),R.drawable.bg_3);

        //두번째 페이지를 생성한다.
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Background2 Title")
                .setContentText("setHintShowBackgroundOnly(false)")
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(background2) //확장객체에 이미지를 입력한다.
                        .setHintShowBackgroundOnly(false)) //Content내용도 같이 보이게 설정
                .build();

        //세번째 페이지를 생성한다.
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Background3 Title")
                .setContentText("setHintShowBackgroundOnly(true)")
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(background3) //마찬가지로 확장객체에 이미지를 넣는다.
                        .setHintShowBackgroundOnly(true)) //오직 배경화면만 보이게 설정
                .build();

        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째, 세번째 페이지를 추가한다.
        NotificationCompat.WearableExtender wearableOptions=new NotificationCompat.WearableExtender()
                .setBackground(background1)
                .addPage(secondPage)
                .addPage(thirdPage);

        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Background1 Title")
                .setContentText("Background1 Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(BACKGROUND_NOTIFICATION_ID,notification);

    }

    //웨어러블 확장 알림에 아이콘을 넣는 것이 직관적이여서 사용자 편의성에 좋다.
    public void showIconNotification(View view) {
        //두번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("IconGravity Title")
                .setContentText("Gravity.START")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.START)) //아이콘의 위치를 Content 보다 앞단에 위치
                .build();
        //세번째 페이지를 생성한다
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("IconGravity Title")
                .setContentText("Gravity.END")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.END)) //아이콘의 위치를 Content 보다 뒷단에 위치, 설정하지않으면 기본값은 END이다.
                .build();

        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째 , 세번째 페이지를 추가한다.
        //우측 상단 힌트 아이콘을 숨긴다.
        NotificationCompat.WearableExtender wearableOptions = new NotificationCompat.WearableExtender()
                .setHintHideIcon(true) //아이콘 숨김 설정을 가져온다. true : 아이콘을 보여줌.
                .addPage(secondPage)
                .addPage(thirdPage);

        //웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Icon Title")
                .setContentText("Icon Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(ICON_NOTIFICATION_ID, notification);

    }

    public void showGravityNotification(View view) {
        //두번째 페이지를 생성한다.
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity.CENTER_VERTICAL")
                .extend(new NotificationCompat.WearableExtender()
                        .setGravity(Gravity.CENTER_VERTICAL)) //중앙에 배치, 생각보다 가운데 아래에 배치된다는 느낌이 든다.
                .build();
        //세번째 페이지를 생성한다.
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity.BOTTOM")
                .extend(new NotificationCompat.WearableExtender()
                .setGravity(Gravity.BOTTOM)) //알림의 기본값은 BOTTOM이므로 설정하지 않으면 하단에 배치된다.
                .build();
        //첫번째 페이지의 웨어러블 옵션 객체를 생성한다.
        //두번째, 세번째 페이지를 추가한다
        NotificationCompat.WearableExtender wearableOptions=new NotificationCompat.WearableExtender()
                .setGravity(Gravity.TOP) //상단에 배치, 근데 생각보다 위느낌보다는 가운데 느낌이 강하다. 라운드 와치여서 그런가
                .addPage(secondPage)
                .addPage(thirdPage);

        //웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        //알림 매니저 객체를 ㅐㅇ성하고 실행한다.
        NotificationManagerCompat.from(this).notify(GRAVITY_NOTIFICATION_ID,notification);
    }

    public void showContentActionNotification(View view) {
        //액티비티 인텐트를 생성.
        Intent viewIntent=new Intent(this,MainActivity.class);

        //액션을 실행했을 때까지 대기할 펜딩 인텐트 생성
        PendingIntent viewPendingIntent=
                PendingIntent.getActivity(this,ACTION_NOTIFICATION_ID,viewIntent,0);
        //첫번째 액션을 생성한다.
        NotificationCompat.Action action1=new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher,"Action1",viewPendingIntent)
                .build();
        //두번째 액션을 생성한다.
        NotificationCompat.Action action2=new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher,"Action2",viewPendingIntent)
                .build();
        //두번째 페이지를 생성한다.
        Notification secondPage=new NotificationCompat.Builder(this)
                .setContentTitle("Action1 Title")
                .setContentText("Action1 Text")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentAction(0)) // action[0] 배열에 넣는다.
                .build();
        //세번째 페이지를 생성한다.
        Notification thirdPage=new NotificationCompat.Builder(this)
                .setContentTitle("Action2 Title")
                .setContentText("Action2 Text")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentAction(1)) // action[1] 배열에 넣는다.
                .build();

        NotificationCompat.WearableExtender wearableOptions=new NotificationCompat.WearableExtender()
                .setHintHideIcon(true)
                .addAction(action1) //두번째 페이지를 클릭하면 action1이 실행된다.
                .addAction(action2)
                .addAction(action2)
                .addPage(secondPage)
                .addPage(thirdPage)
                .setContentAction(2); //첫번째 페이지는 마지막 action[2]에 들어간다.
        //웨어러블 옵션을 적용한 알림을 생성
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Content Action Title")
                .setContentText("Content Action Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();
        //알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(CONTENT_ACTION_NOTIFICATION_ID,notification);
    }
}
