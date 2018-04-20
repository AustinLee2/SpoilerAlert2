package com.austinhlee.spoileralert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Austin Lee on 4/19/2018.
 */
public class IncomingSms extends BroadcastReceiver {

    private static SmsListener mListener;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            //do your stuff
            abortBroadcast();
        }
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i],"3gpp");

            String sender = smsMessage.getDisplayOriginatingAddress();
            //Check the sender to filter messages which we require to read
            String messageBody = smsMessage.getMessageBody();
            //Pass the message text to interface
            mListener.messageReceived(messageBody);
        }
    }

    public interface SmsListener {
        void messageReceived(String messageText);
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
