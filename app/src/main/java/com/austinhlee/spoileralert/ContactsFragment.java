package com.austinhlee.spoileralert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ContactsFragment extends Fragment {

    private Button scan_btn;
    private View mView;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference ref;
    private DatabaseReference mUsersRef;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        ref = mDatabase.getReference();
        mUsersRef =  ref.child("users");
        this.mView = view;
        scan_btn = (Button) mView.findViewById(R.id.scanBtn);
        scan_btn.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view){
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

        }
    });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
       if(result != null){
           if(result.getContents()==null){
               Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_LONG).show();
           }
           else{
               Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
               Spoiler spoiler = Spoiler.deserialize(result.getContents());
               writeNewSpoiler(spoiler);
               Log.d("TAG", "SUCCESFULLY SCANNED");
           }
       }
       else{
           super.onActivityResult(requestCode, resultCode, data);
       }
    }
    public void writeNewSpoiler(Spoiler spoiler){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String uniqueId = user.getUid();
        DatabaseReference spoilerRef = mUsersRef.child(uniqueId).push();
        spoiler.setUid(spoilerRef.getKey());
        spoilerRef.setValue(spoiler);
    }
}
