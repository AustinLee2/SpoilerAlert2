package com.austinhlee.spoileralert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpoilersFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private RecyclerView mRecyclerView;
    private View mView;
    private Context mContext;
    private DatabaseReference mDatabase;
    private SpoilerListAdapter mSpoilerListAdapter;

    public static SpoilersFragment newInstance() {
        SpoilersFragment fragment = new SpoilersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QRCodeHelper qrCodeHelper = new QRCodeHelper();
        qrCodeHelper.setContent("This is a test");
        Bitmap bitmap = qrCodeHelper.generate();
        View view = inflater.inflate(R.layout.fragment_spoilers, container, false);
        this.mView = view;
        mContext = getContext();
        String uid = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mSpoilerListAdapter = new SpoilerListAdapter(mContext, mDatabase);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.spoiler_recyclerview);
        mRecyclerView.setAdapter(mSpoilerListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mFirebaseAuth = FirebaseAuth.getInstance();
        return view;
    }
}
