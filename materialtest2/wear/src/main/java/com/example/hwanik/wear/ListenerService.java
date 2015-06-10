package com.example.hwanik.wear;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by jang on 2015-05-11.
 */

//웨어러블리스너 서비스는 앱이 실행되 있지 않더라도 데이터가 변경되거나 메시지를 수신할 때 이를 감지할 수 있는것.
public class ListenerService extends WearableListenerService {

    String[] receiveString;
    int receiveCount;
    int currentPage;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {


        // 데이터 이벤트 횟수별로 동작한다.
        for (DataEvent event : dataEvents) {

            // 데이터 변경 이벤트일 때 실행된다.
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // 동작을 구분할 패스를 가져온다.
                String path = event.getDataItem().getUri().getPath();

                // 패스가 문자 데이터 일 때
                if (path.equals("/STRING_DATA_PATH")) {
                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());


                    //앱을 실행시킨다.
                    Intent startIntent = new Intent(this, MainActivity.class);

                    // 데이터맵으로부터 수신한 문자열을 가져온다.
                    receiveCount = dataMapItem.getDataMap().getInt("stepCount");
                    currentPage = dataMapItem.getDataMap().getInt("currentPage");
                    receiveString = new String[receiveCount];
                    final String[] receiveString = dataMapItem.getDataMap().getStringArray("content");

                    byte[] receiveByte;
                    for(int i=0;i<receiveCount;i++){
                        receiveByte = dataMapItem.getDataMap().getByteArray("stepImage"+i);
                        startIntent.putExtra("receiveImage"+i,receiveByte);
                    }

                    startIntent.putExtra("currentPage",currentPage);
                    startIntent.putExtra("receiveCount",receiveCount);
                    startIntent.putExtra("receiveString",receiveString);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startIntent);

                }
                // 데이터 삭제 이벤트일 때 실행된다.
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 데이터가 삭제됐을 때 수행할 동작
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // Check to see if the message is to start an activity

        if (messageEvent.getPath().equals("/message_path")) {
            final String message = new String(messageEvent.getData());

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}