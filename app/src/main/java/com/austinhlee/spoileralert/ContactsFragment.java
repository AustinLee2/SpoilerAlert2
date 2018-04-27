package com.austinhlee.spoileralert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ContactsFragment extends Fragment {

    private Button scan_btn;
    private View mView;

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
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
               //Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
           }
           else{
               //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

           }
       }
       else{
           super.onActivityResult(requestCode, resultCode, data);
       }
    }
}
