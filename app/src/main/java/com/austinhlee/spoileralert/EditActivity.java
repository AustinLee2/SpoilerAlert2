package com.austinhlee.spoileralert;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.austinhlee.spoileralert.HomeFragment.DATE_DAY_KEY;
import static com.austinhlee.spoileralert.HomeFragment.DATE_MONTH_KEY;
import static com.austinhlee.spoileralert.HomeFragment.DATE_YEAR_KEY;
import static com.austinhlee.spoileralert.HomeFragment.TIME_HOUR_KEY;
import static com.austinhlee.spoileralert.HomeFragment.TIME_MINUTE_KEY;

/**
 * Created by Austin Lee on 4/27/2018.
 */
public class EditActivity extends AppCompatActivity {

    private static final String SPOILER_TITLE_EXTRA_KEY = "com.austinhlee.SPOILER_TITLE_EXTRA_KEY";
    public static final String DATE_DAY_KEY_EDIT = "com.austinhlee.DATE_DAY_KEY_EDIT";
    private Button mResubmitSpoilerAlert;
    private Button mCancelButton;
    private DatePickerFragment mDatePickerFragment;
    private TimePickerFragment mTimePickerFragment;
    private Button mTimeButton;
    private Button mDateButton;
    private EditText mEditTextTitle;
    private EditText mEditWords;
    final public static String UID_KEY = "com.austinhlee.UID_KEY";
    final public static String WORDS_KEY = "com.austinhlee.WORDS_KEY";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_spoiler_alerts);
        mTimePickerFragment = new TimePickerFragment();
        mDatePickerFragment = new DatePickerFragment();
        mEditTextTitle = (EditText)findViewById(R.id.nameOfSpoilerAlert);
        mEditWords = (EditText)findViewById(R.id.triggerWords);
        final Intent intent = getIntent();
        String title = intent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY);
        String filterWords = intent.getStringExtra(SpoilerListAdapter.WORDS_KEY);
        mEditTextTitle.setText(title);
        mEditWords.setText(filterWords);
        mTimeButton = (Button)findViewById(R.id.setTime);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                mTimePickerFragment.show(fm, "tag");
            }
        });
        mDateButton = (Button)findViewById(R.id.setDate);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                mDatePickerFragment.show(fm, "tag");
            }
        });
        mResubmitSpoilerAlert = (Button)findViewById(R.id.resubmitSpoilerAlertButton);
        mResubmitSpoilerAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY, mEditTextTitle.getText().toString());
                replyIntent.putExtra(UID_KEY, intent.getStringExtra(UID_KEY));
                replyIntent.putExtra(WORDS_KEY, mEditWords.getText().toString());
                if (mDatePickerFragment.mDay != 0 && mDatePickerFragment.mMonth != 0 && mDatePickerFragment.mYear != 0 && mTimePickerFragment.mHour != 0 && mTimePickerFragment.mMinute != 0){
                    replyIntent.putExtra(DATE_DAY_KEY_EDIT, mDatePickerFragment.mDay);
                    replyIntent.putExtra(DATE_MONTH_KEY, mDatePickerFragment.mMonth);
                    replyIntent.putExtra(DATE_YEAR_KEY, mDatePickerFragment.mYear);
                    replyIntent.putExtra(TIME_HOUR_KEY, mTimePickerFragment.mHour);
                    replyIntent.putExtra(TIME_MINUTE_KEY, mTimePickerFragment.mMinute);
                }
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });
        mCancelButton = (Button)findViewById(R.id.cancelSpoilerAlertButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
