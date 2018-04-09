package com.austinhlee.spoileralert;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, RC_SIGN_IN);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

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

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, HomeFragment.newInstance());
        transaction.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            //succesfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Already Signed In!", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No network connection!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    }

