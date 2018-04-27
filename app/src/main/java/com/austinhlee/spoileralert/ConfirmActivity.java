package com.austinhlee.spoileralert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Austin Lee on 4/18/2018.
 */

public class ConfirmActivity extends AppCompatActivity{

    private TextView mNameTextView;
    private TextView mFilterWordsTextView;
    private TextView mDateAndTimeTextView;
    private Button confirmButton;
    private Button cancelButton;
    private Intent mIntent;
    private Context mContext;
    private DatabaseReference mSpoilersRef;
    private DatabaseReference mNewSpoilerRef;
    private DatabaseReference ref;
    private FirebaseDatabase database;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_spoiler_alerts);
        mIntent = getIntent();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mSpoilersRef =  ref.child("spoilers");
        mDateAndTimeTextView = (TextView)findViewById(R.id.timeAndDate_textview);
        //if (mIntent.hasExtra())
        //mDateAndTimeTextView.setText(mIntent);
        confirmButton = (Button)findViewById(R.id.ConfirmSpoilerAlertButton);
        cancelButton = (Button)findViewById(R.id.cancelSpoilerAlertButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mNameTextView = (TextView)findViewById(R.id.SpoilerAlertName);
        mNameTextView.setText(mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY));
        mFilterWordsTextView = (TextView)findViewById(R.id.ListofSpoilerAlerts);
        confirmButton = (Button)findViewById(R.id.ConfirmSpoilerAlertButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNewSpoiler(mIntent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY),mIntent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY),0);
                setResult(RESULT_OK);
                finish();
            }
        });
        mFilterWordsTextView.setText(mIntent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY));
    }

    public void writeNewSpoiler(String spoilerTitle, String filterWords, long time){
        Spoiler spoiler = new Spoiler();
        spoiler.setTitle(spoilerTitle);
        spoiler.setFilterWords(filterWords);
        spoiler.setReminderTime(time);
        mNewSpoilerRef = mSpoilersRef.push();
        mNewSpoilerRef.setValue(spoiler);
    }
}
