package com.austinhlee.spoileralert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Austin Lee on 4/15/2018.
 */

public class SpoilerListAdapter extends FirebaseRecyclerAdapter<Spoiler, SpoilerListAdapter.SpoilerViewHolder> {

    Context mContext;
    DatabaseReference mRef;
    final public static int EDIT_RC = 14;
    final public static String UID_KEY = "com.austinhlee.UID_KEY";
    final public static String WORDS_KEY = "com.austinhlee.WORDS_KEY";
    final public static String DATE_KEY = "com.austinhlee.DATE_KEY";
    public static class SpoilerViewHolder extends RecyclerView.ViewHolder{


        public TextView mSpoilerPlaceHolder;
        public TextView mTitleTextView;
        public TextView mFilterWordsTextView;
        public TextView mTimeAndDateTextView;
        public ImageButton mDeleteButton;
        public ImageButton mShareButton;
        public ImageButton mEditButton;



        public SpoilerViewHolder(View itemView){
            super(itemView);
            mSpoilerPlaceHolder = (TextView) itemView.findViewById(R.id.spoilerTextView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.spoilerAlertName_textview);
            mFilterWordsTextView = (TextView) itemView.findViewById(R.id.filterWords_textview);
            mTimeAndDateTextView = (TextView) itemView.findViewById(R.id.timeAndDate_textview);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            mShareButton =(ImageButton) itemView.findViewById(R.id.shareButton);
            mEditButton =(ImageButton) itemView.findViewById(R.id.editButton);
        }
    }

    SpoilerListAdapter(Context context, DatabaseReference ref){
        super(Spoiler.class, R.layout.spoiler_item_layout, SpoilerViewHolder.class, ref);
        mRef = ref;
        mContext = context;
    }


    @Override
    protected void populateViewHolder(SpoilerListAdapter.SpoilerViewHolder viewHolder, final Spoiler model, int position) {
        viewHolder.mTitleTextView.setText(model.getTitle());
        viewHolder.mFilterWordsTextView.setText(model.getFilterWords());
        viewHolder.mTimeAndDateTextView.setText(Long.toString(model.getReminderTime()));
        viewHolder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(HomeFragment.SPOILER_TITLE_EXTRA_KEY, model.getTitle());
                intent.putExtra(UID_KEY, model.getUid());
                intent.putExtra(WORDS_KEY, model.getFilterWords());
                if (model.getReminderTime() != 0){
                    intent.putExtra(DATE_KEY, model.getReminderTime());
                }
                ((Activity)mContext).startActivityForResult(intent,EDIT_RC);
            }
        });
        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog ad = new AlertDialog.Builder(mContext)
                        .setTitle("Title")
                        .setMessage("Do you really want to delete " + model.getTitle() + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mRef.child(model.getUid()).removeValue();
                                Toast.makeText(mContext, "Deleted " + model.getTitle() + "!", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        viewHolder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text2Qr = model.serialize();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    Intent intent = new Intent(mContext, QRActivity.class);
                    intent.putExtra("pic",bitmap);
                    mContext.startActivity(intent);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        if (model.getReminderTime() != 0) {
            long dateinLong = model.getReminderTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dateinLong);
            viewHolder.mTimeAndDateTextView.setText(formatDueDate(cal.getTime()));
        }
        else {
            viewHolder.mTimeAndDateTextView.setText("N/A");
        }
    }

    private String formatDueDate(Date date){
        DateFormat df = new SimpleDateFormat("HH:mm, dd MMM yy");
        return df.format(date);
    }
}
