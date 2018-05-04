package com.austinhlee.spoileralert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private Button mSubmitButton;
    private Button mTimeButton;
    private Button mDateButton;
    private Button mShareButton;
    private EditText mTitleEditText;
    private EditText mFilterWordsEditText;
    private DatabaseReference mDatabase;
    private View mView;
    private Context mContext;
    private TimePickerFragment mTimePickerFragment;
    private DatePickerFragment mDatePickerFragment;
    private Button mSignOutButton;

    public static final int CONTACT_RC = 42;
    public static final int CONFIRM_REQUEST_CODE = 521;
    public static final String SPOILER_TITLE_EXTRA_KEY = "com.austinhlee.spoileralert.SPOILER_TITLE";
    public static final String FILTER_WORDS_EXTRA_KEY = "com.austinhlee.spoileralert.FILTER_WORDS";
    public static final String DATE_DAY_KEY = "com.austinhlee.spoileralert.DATE_KEY";
    public static final String DATE_MONTH_KEY = "com.austinhlee.spoileralert.DATE_MONTH_KEY";
    public static final String DATE_YEAR_KEY = "com.austinhlee.spoileralert.DATE_YEAR_KEY";
    public static final String TIME_MINUTE_KEY = "com.austinhlee.spoileralert.TIME_MINUTE_KEY";
    public static final String TIME_HOUR_KEY = "com.austinhlee.spoileralert.TIME_HOUR_KEY";
    public static final String PHONE_NUMBER_KEY = "com.austinhlee.spoileralert.PHONE_HOUR_KEY";

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContext = getContext();
        this.mView = view;
        mShareButton = mView.findViewById(R.id.shareWithContacts);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            1);
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivityForResult(intent, CONTACT_RC);
                }

            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTitleEditText = (EditText)mView.findViewById(R.id.nameOfSpoilerAlert);
        mFilterWordsEditText = (EditText)mView.findViewById(R.id.triggerWords);
        mSignOutButton = (Button)mView.findViewById(R.id.sign_out);
        mSubmitButton = (Button)mView.findViewById(R.id.submitSpoilerAlertButton);
        mDatePickerFragment = new DatePickerFragment();
        mDateButton = (Button)mView.findViewById(R.id.setDate);
        mDatePickerFragment = new DatePickerFragment();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerFragment.show(getFragmentManager(), "datepicker");
            }
        });
        mTimePickerFragment = new TimePickerFragment();
        mTimeButton = (Button)mView.findViewById(R.id.setTime);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimePickerFragment.show(getFragmentManager(), "timepicker");
            }
        });
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                Log.d("TAG", "signout clicked");
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleEditText.getText()) || TextUtils.isEmpty(mFilterWordsEditText.getText())){
                    Toast.makeText(mContext, "Must fill out spoiler title and words!", Toast.LENGTH_LONG).show();
                } else {
                    String spoilerTitle = mTitleEditText.getText().toString();
                    String filterWords = mFilterWordsEditText.getText().toString();
                    Intent intent = new Intent(getActivity(), ConfirmActivity.class);
                    intent.putExtra(SPOILER_TITLE_EXTRA_KEY, spoilerTitle);
                    intent.putExtra(FILTER_WORDS_EXTRA_KEY, filterWords);
                    if (MainActivity.mPhoneNumber != null) {
                        intent.putExtra(PHONE_NUMBER_KEY, MainActivity.mPhoneNumber);
                    }
                    if (mDatePickerFragment.mDay != 0 && mDatePickerFragment.mMonth != 0 && mDatePickerFragment.mYear != 0 && mTimePickerFragment.mHour != 0 && mTimePickerFragment.mMinute != 0) {
                        intent.putExtra(DATE_DAY_KEY, mDatePickerFragment.mDay);
                        intent.putExtra(DATE_MONTH_KEY, mDatePickerFragment.mMonth);
                        intent.putExtra(DATE_YEAR_KEY, mDatePickerFragment.mYear);
                        intent.putExtra(TIME_HOUR_KEY, mTimePickerFragment.mHour);
                        intent.putExtra(TIME_MINUTE_KEY, mTimePickerFragment.mMinute);
                    }
                    getActivity().startActivityForResult(intent, CONFIRM_REQUEST_CODE);
                    //writeNewSpoiler(spoilerTitle, filterWords, getUnixTimeFromFragments());
                    Log.d("TAG", "onClick");
                }
            }
        });
        return view;
    }

}
