package com.austinhlee.spoileralert;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private Button mSubmitButton;
    private EditText mTitleEditText;
    private EditText mFilterWordsEditText;
    private DatabaseReference mDatabase;
    private View mView;
    private Context mContext;

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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTitleEditText = (EditText)mView.findViewById(R.id.nameOfSpoilerAlert);
        mFilterWordsEditText = (EditText)mView.findViewById(R.id.triggerWords);
        mSubmitButton = (Button)mView.findViewById(R.id.submitSpoilerAlertButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spoilerTitle = mTitleEditText.getText().toString();
                String filterWords = mFilterWordsEditText.getText().toString();
                writeNewSpoiler(spoilerTitle, filterWords);
                Log.d("TAG", "onClick");
            }
        });
        return view;
    }

    public void writeNewSpoiler(String spoilerTitle, String filterWords){
        Spoiler spoiler = new Spoiler();
        spoiler.setTitle(spoilerTitle);
        spoiler.setFilterWords(filterWords);
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(spoiler);
    }
}
