package com.austinhlee.spoileralert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Austin Lee on 4/27/2018.
 */
public class EditActivity extends Activity {

    private Button mResubmitSpoilerAlert;
    private Button mCancelButton;
    private EditText mEditTextTitle;
    private EditText mEditWords;
    final public static String UID_KEY = "com.austinhlee.UID_KEY";
    final public static String WORDS_KEY = "com.austinhlee.WORDS_KEY";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_spoiler_alerts);
        mEditTextTitle = (EditText)findViewById(R.id.nameOfSpoilerAlert);
        mEditWords = (EditText)findViewById(R.id.triggerWords);
        final Intent intent = getIntent();
        String title = intent.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY);
        String filterWords = intent.getStringExtra(SpoilerListAdapter.WORDS_KEY);
        mEditTextTitle.setText(title);
        mEditWords.setText(filterWords);
        mResubmitSpoilerAlert = (Button)findViewById(R.id.resubmitSpoilerAlertButton);
        mResubmitSpoilerAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY, mEditTextTitle.getText().toString());
                replyIntent.putExtra(UID_KEY, intent.getStringExtra(UID_KEY));
                replyIntent.putExtra(WORDS_KEY, mEditWords.getText().toString());
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
