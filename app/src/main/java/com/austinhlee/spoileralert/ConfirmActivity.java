package com.austinhlee.spoileralert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Austin Lee on 4/18/2018.
 */

public class ConfirmActivity extends AppCompatActivity{

    private TextView mNameTextView;
    private TextView mFilterWordsTextView;
    private TextView mDateAndTimeTextView;
    private TextView mPeopleSharedwithTextView;
    private Button confirmButton;
    private Button cancelButton;
    private Intent mIntent;
    private Context mContext;
    private DatabaseReference mUsersRef;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;
    private Date theDate;
    private String mPhoneNumber;

    public static final int SENDSMSKEY = 12512;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_spoiler_alerts);
        mIntent = getIntent();
        mContext = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mUsersRef =  ref.child("users");
        mDateAndTimeTextView = (TextView)findViewById(R.id.TimeAndDate);
        //if (mIntent.hasExtra())
        //mDateAndTimeTextView.setText(mIntent);
        confirmButton = (Button)findViewById(R.id.ConfirmSpoilerAlertButton);
        cancelButton = (Button)findViewById(R.id.cancelSpoilerAlertButton);
        mPeopleSharedwithTextView = (TextView) findViewById(R.id.PeopleSharedWith);
        if (mIntent.hasExtra(HomeFragment.PHONE_NUMBER_KEY)){
            mPhoneNumber = mIntent.getStringExtra(HomeFragment.PHONE_NUMBER_KEY);
            mPeopleSharedwithTextView.setText(mPhoneNumber);
        } else {
            mPeopleSharedwithTextView.setText("N/A");
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                MainActivity.mPhoneNumber = null;
                finish();
            }
        });
        mFilterWordsTextView = (TextView)findViewById(R.id.ListofSpoilerAlerts);
        int hour = mIntent.getIntExtra(HomeFragment.TIME_HOUR_KEY,-1);
        int minute = mIntent.getIntExtra(HomeFragment.TIME_MINUTE_KEY,-1);
        final int month = mIntent.getIntExtra(HomeFragment.DATE_MONTH_KEY,-1);
        int day = mIntent.getIntExtra(HomeFragment.DATE_DAY_KEY,-1);
        int year = mIntent.getIntExtra(HomeFragment.DATE_YEAR_KEY,-1);
        if (month != -1) {
            Calendar myCal = Calendar.getInstance();
            myCal.set(Calendar.YEAR, year);
            myCal.set(Calendar.MONTH, month);
            myCal.set(Calendar.DAY_OF_MONTH, day);
            myCal.set(Calendar.HOUR_OF_DAY, hour);
            myCal.set(Calendar.MINUTE, minute);
            myCal.set(Calendar.SECOND, 1);
            theDate = myCal.getTime();
            mDateAndTimeTextView.setText(formatDueDate(theDate));
        }
        else {
            mDateAndTimeTextView.setText("N/A");
        }
        mNameTextView = (TextView)findViewById(R.id.SpoilerAlertName);
        mNameTextView.setText(mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY));
        confirmButton = (Button)findViewById(R.id.ConfirmSpoilerAlertButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (theDate != null) {
                    Spoiler spoiler = writeNewSpoiler(mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY),mIntent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY),theDate.getTime());
                    NotificationScheduler.setReminder(mContext, AlarmReceiver.class, theDate, mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY),spoiler.getUid(), 12,
                            "The spoiler " + mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY) + " has ended!");
                }
                if (mPhoneNumber != null) {
                    sendSMS(mPhoneNumber, "I setup a spoiler alert to prevent spoiler alerts!");
                }
                else {
                    writeNewSpoiler(mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY),mIntent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY),0);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
        mFilterWordsTextView.setText(mIntent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY));
    }

    public Spoiler writeNewSpoiler(String spoilerTitle, String filterWords, long time){
        Spoiler spoiler = new Spoiler();
        spoiler.setTitle(spoilerTitle);
        spoiler.setFilterWords(filterWords);
        spoiler.setReminderTime(time);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String uniqueId = user.getUid();
        DatabaseReference spoilerRef = mUsersRef.child(uniqueId).push();
        spoiler.setUid(spoilerRef.getKey());
        spoilerRef.setValue(spoiler);
        return spoiler;
    }

    private String formatDueDate(Date date){
        DateFormat df = new SimpleDateFormat("HH:mm, dd MMM yy");
        return df.format(date);
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent to " + phoneNo + "!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
