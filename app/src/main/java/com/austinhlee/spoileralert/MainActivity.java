package com.austinhlee.spoileralert;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mFirebaseAuth;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mContext = this;
        Intent loginIntent = new Intent(this, LoginActivity.class);
        if (!Telephony.Sms.getDefaultSmsPackage(mContext).equals(getPackageName())){
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivity(intent);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        }
        if (mFirebaseAuth == null) {
            startActivity(loginIntent);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("spoilers");
        final List<String> filterWords = new ArrayList<>();
// Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Spoiler spoiler = data.getValue(Spoiler.class);
                    filterWords.add(spoiler.getFilterWords());
                }
                for (String s: filterWords){
                    System.out.println(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.home_item:
                                selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.contacts_item:
                                selectedFragment = ContactsFragment.newInstance();
                                break;
                            case R.id.spoilers_item:
                                selectedFragment = SpoilersFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });


        IncomingSms.bindListener(new IncomingSms.SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String myPackageName = getPackageName();
                List<String> textWordList = new ArrayList<>(Arrays.asList(messageText.split(" ")));
                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
                if (!Telephony.Sms.getDefaultSmsPackage(mContext).equals(myPackageName)) {
                    Toast.makeText(mContext, "Must set this app as default!", Toast.LENGTH_LONG).show();
                } else {
                    //can bypass filter by changing case
                    //can bypass filter by not formatting correctly
                    Log.d("Message", messageText);
                    if (!Collections.disjoint(textWordList,filterWords)) {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "Channel_ID")
                                .setSmallIcon(R.drawable.ic_star_white_24dp)
                                .setContentTitle("Spoiler!")
                                .setContentText("Someone just sent a spoiler!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                        notificationManager.notify(2, mBuilder.build());
                    } else {
                        Toast.makeText(mContext, "Message: " + messageText, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, HomeFragment.newInstance());
        transaction.commit();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    private boolean wordInList(String word, List<String> list){
        for(String str: list) {
            if(str.trim().contains(word))
                return true;
        }
        return false;
    }
}