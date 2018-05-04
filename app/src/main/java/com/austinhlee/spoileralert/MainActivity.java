package com.austinhlee.spoileralert;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.austinhlee.spoileralert.HomeFragment.CONTACT_RC;
import static com.austinhlee.spoileralert.HomeFragment.DATE_DAY_KEY;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference ref;
    private DatabaseReference mUsersRef;
    private FirebaseDatabase mDatabase;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private Context mContext;
    private String userUID;
    static public String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        ref = mDatabase.getReference();
        mUsersRef =  ref.child("users");
        mContext = this;
        Intent loginIntent = new Intent(this, LoginActivity.class);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                1);
        if (!Telephony.Sms.getDefaultSmsPackage(mContext).equals(getPackageName())){
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivity(intent);

        }
        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(loginIntent);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        userUID = FirebaseAuth.getInstance().getUid();
        if (userUID != null) {
            ref = database.getReference("users").child(userUID);
        }
        final List<String> filterWords = new ArrayList<>();
// Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filterWords.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Spoiler spoiler = data.getValue(Spoiler.class);
                    List<String> items = Arrays.asList(spoiler.getFilterWords().split(","));
                    filterWords.addAll(items);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HomeFragment.CONFIRM_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Toast.makeText(mContext, "Created spoiler!", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == SpoilerListAdapter.EDIT_RC) {
            if (resultCode == RESULT_OK) {
                String uid = data.getStringExtra(SpoilerListAdapter.UID_KEY);
                String title = data.getStringExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY);
                String words = data.getStringExtra(EditActivity.WORDS_KEY);
                if (data.hasExtra(EditActivity.DATE_DAY_KEY_EDIT)) {
                    int hour = data.getIntExtra(HomeFragment.TIME_HOUR_KEY, -1);
                    int minute = data.getIntExtra(HomeFragment.TIME_MINUTE_KEY, -1);
                    final int month = data.getIntExtra(HomeFragment.DATE_MONTH_KEY, -1);
                    int day = data.getIntExtra(HomeFragment.DATE_DAY_KEY, -1);
                    int year = data.getIntExtra(HomeFragment.DATE_YEAR_KEY, -1);
                    if (month != -1) {
                        Calendar myCal = Calendar.getInstance();
                        myCal.set(Calendar.YEAR, year);
                        myCal.set(Calendar.MONTH, month);
                        myCal.set(Calendar.DAY_OF_MONTH, day);
                        myCal.set(Calendar.HOUR_OF_DAY, hour);
                        myCal.set(Calendar.MINUTE, minute);
                        writeNewSpoiler(title,words,myCal.getTimeInMillis());
                    }
                    mUsersRef.child(userUID).child(uid).removeValue();
                } else {
                    writeNewSpoiler(title,words,0);
                    mUsersRef.child(userUID).child(uid).removeValue();
                }
            }
        } else if (requestCode == HomeFragment.CONTACT_RC) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the URI and query the content provider for the phone number
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection,
                        null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    // Do something with the phone number
                    mPhoneNumber = number;
                    Log.d("TAG", mPhoneNumber);
                }
            }
        }
    }
    private boolean wordInList(String word, List<String> list){
        for(String str: list) {
            if(str.trim().contains(word))
                return true;
        }
        return false;
    }
    public void writeNewSpoiler(String spoilerTitle, String filterWords, long time){
        Spoiler spoiler = new Spoiler();
        spoiler.setTitle(spoilerTitle);
        spoiler.setFilterWords(filterWords);
        spoiler.setReminderTime(time);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String uniqueId = user.getUid();
        DatabaseReference spoilerRef = mUsersRef.child(uniqueId).push();
        spoiler.setUid(spoilerRef.getKey());
        spoilerRef.setValue(spoiler);
    }
}