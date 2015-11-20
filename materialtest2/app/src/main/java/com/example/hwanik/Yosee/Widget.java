package com.example.hwanik.Yosee;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.hwanik.materialtest.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.graphics.*;
import android.widget.Toast;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    final String[] before = new String[1];

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

    }

    void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                         final int appWidgetId) {

        final ParseFile[] imageFile = new ParseFile[1];
        final byte[][] parseBitmapByte = new byte[1][1];
        final Bitmap[] parseBitmap = new Bitmap[1];
        final String[] title = new String[1];
        final String[] subTitle = new String[1];
        final ComponentName providerComponentName = new ComponentName(context, Widget.class);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("test1");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                imageFile[0] = (ParseFile) list.get(0).get("MAIN_IMAGE");
                title[0] = list.get(0).getString("MAIN_TITLE");
                subTitle[0] = list.get(0).getString("SUB_TITLE");
                if(!(before.equals(subTitle[0]))){
                    Intent intent = new Intent ("com.lge.launcher2.smartbulletin.ADD_NOTIFICATION_ICON");
                    intent.putExtra("noti_type", "once");
                    intent.putExtra("component_name", providerComponentName);
                    context.sendBroadcast(intent);
                }
                try {
                    parseBitmapByte[0] = imageFile[0].getData();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                parseBitmap[0] = BitmapFactory.decodeByteArray(parseBitmapByte[0], 0, parseBitmapByte[0].length);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.title, title[0]);
                views.setTextViewText(R.id.subTitle, subTitle[0]);
                views.setImageViewBitmap(R.id.iv, parseBitmap[0]);

                Intent intent=new Intent(context, MainActivity.class);
                PendingIntent pe=PendingIntent.getActivity(context, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.iv, pe);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
                before[0] = subTitle[0];
            }
        });
        // Construct the RemoteViews object
    }

}

