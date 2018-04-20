package com.austinhlee.spoileralert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Austin Lee on 4/18/2018.
 */

public class ConfirmActivity extends AppCompatActivity{

    private TextView mNameTextView;
    private TextView mFilterWordsTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_spoiler_alerts);

        Intent intent = getIntent();
        mNameTextView = (TextView)findViewById(R.id.SpoilerAlertName);
        mNameTextView.setText(intent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY));
        mFilterWordsTextView = (TextView)findViewById(R.id.ListofSpoilerAlerts);
        mFilterWordsTextView.setText(intent.getStringExtra(HomeFragment.FILTER_WORDS_EXTRA_KEY));
    }
}
