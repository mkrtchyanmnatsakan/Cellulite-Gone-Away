package com.wildcard.cellulite.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;

import com.pixplicity.easyprefs.library.Prefs;
import com.wildcard.cellulite.R;
import com.wildcard.cellulite.constantValue.Constants;
import com.wildcard.cellulite.userInterface.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.support.v4.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;

/**
 * Created by mno on 4/14/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private ActivityManager.RunningAppProcessInfo appProcessInfo;






    private static final String CHANNEL_ID = "com.wildcard.cellulite.channelId";
    public static final String SHOW_NOTIFICATION = "com.wildcard.cellulite.receiver.SHOW_NOTIFICATION";


    private void validateDateTime(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;
        try {
            strDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long defTime = System.currentTimeMillis() - strDate.getTime();

        long difDay =   defTime / (24 * 60 * 60 * 1000);

        if(difDay >= 1){
            String newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            Prefs.putString(Constants.CREATE_APP_DATA,newDate);
        }

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
//        Intent intent1 = new Intent("custom-event-name");
//
        Prefs.putBoolean(Constants.IS_INACTIVE,false);
        appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if(appProcessInfo.importance == IMPORTANCE_FOREGROUND){
            context.startActivity(new Intent(context,MainActivity.class));
        }

        String dataCreate = Prefs.getString(Constants.CREATE_APP_DATA,"");

        //Toast.makeText(this, dataCreate, Toast.LENGTH_SHORT).show();

        if(dataCreate == null || dataCreate.isEmpty()){
            String dataCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            Prefs.putString(Constants.CREATE_APP_DATA,dataCreated);
            //  Toast.makeText(this, "new data " + dataCreated, Toast.LENGTH_SHORT).show();
        }else {
            validateDateTime(dataCreate);
        }



        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.setContentTitle("The time has come!\n" +
                "\nTime to burn your calories.")
                .setContentText("It's time to complete your daily activity")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= 26) {
                builder.setChannelId(CHANNEL_ID);
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = null;
            if (Build.VERSION.SDK_INT >= 26) {
                channel = new NotificationChannel(
                        CHANNEL_ID,
                        "NotificationDemo",
                        IMPORTANCE_DEFAULT
                );
            }
            if (Build.VERSION.SDK_INT >= 26) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        notificationManager.notify(0, notification);
    }
}
