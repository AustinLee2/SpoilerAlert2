package com.austinhlee.spoileralert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

/**
 * Created by Austin Lee on 4/15/2018.
 */

public class SpoilerListAdapter extends FirebaseRecyclerAdapter<Spoiler, SpoilerListAdapter.SpoilerViewHolder> {

    Context mContext;
    DatabaseReference mRef;
    final public static int EDIT_RC = 14;
    final public static String UID_KEY = "com.austinhlee.UID_KEY";
    final public static String WORDS_KEY = "com.austinhlee.WORDS_KEY";
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
                ((Activity)mContext).startActivityForResult(intent,EDIT_RC);
            }
        });
        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child(model.getUid()).removeValue();
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
    }
}
