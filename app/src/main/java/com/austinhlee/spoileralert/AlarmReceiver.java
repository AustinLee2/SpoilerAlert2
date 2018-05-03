package com.austinhlee.spoileralert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.austinhlee.spoileralert.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";
    private DatabaseReference ref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;

    @Override
    public void onReceive(Context context, Intent intent) {
        ref = FirebaseDatabase.getInstance().getReference();
        String userID = FirebaseAuth.getInstance().getUid();
        mUsersRef =  ref.child("users").child(userID);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationScheduler.showNotification(context, MainActivity.class, intent.getStringExtra("taskTitle"), intent.getStringExtra("additionalNotes"));
        NotificationScheduler.deleteReminder(context,intent.getStringExtra("uid"),mUsersRef);
    }
}
