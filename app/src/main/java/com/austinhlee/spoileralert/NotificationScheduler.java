package com.austinhlee.spoileralert;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Austin Lee on 2/21/2018.
 */

public class NotificationScheduler {

    public static void setReminder(Context context, Class<?> cls, Date date, String taskTitle, String uid, int requestCode, String taskAdditionalNotes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        intent1.putExtra("taskTitle", taskTitle);
        intent1.putExtra("additionalNotes", taskAdditionalNotes);
        intent1.putExtra("uid", uid );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
    }

    public static void showNotification(Context context, Class<?> cls, String title, String content){
        Intent notificationIntent = new Intent(context,cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
        builder.setContentTitle(title);
        builder.setSmallIcon(android.R.drawable.btn_star);
        builder.setContentText(content);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int _id = (int) System.currentTimeMillis();
        notificationManager.notify(_id, builder.build());
    }

    public static void deleteReminder(Context context, String uid, DatabaseReference usersRef){
        usersRef.child(uid).removeValue();
    }

    public static void cancelReminder(Context context, Class<?> myReceiver, int requestCode){
        ComponentName receiver = new ComponentName(context, myReceiver);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, myReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
