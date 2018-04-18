package com.austinhlee.spoileralert;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

/**
 * Created by Austin Lee on 4/15/2018.
 */

public class SpoilerListAdapter extends FirebaseRecyclerAdapter<Spoiler, SpoilerListAdapter.SpoilerViewHolder> {

    Context mContext;

    public static class SpoilerViewHolder extends RecyclerView.ViewHolder{


        public TextView mSpoilerPlaceHolder;
        public TextView mNameofSpoilerAlert;
        public TextView mAddTextView;
        public TextView mDeleteTextView;
        public TextView mShareTextView;
        public TextView mTitleTextView;
        public TextView mFilterWordsTextView;
        public ImageButton mAddButton;
        public ImageButton mDeleteButton;
        public ImageButton mShareButton;



        public SpoilerViewHolder(View itemView){
            super(itemView);
            mSpoilerPlaceHolder = (TextView) itemView.findViewById(R.id.spoilerTextView);
            mNameofSpoilerAlert = (TextView) itemView.findViewById(R.id.nameOfSpoilerAlert_textview);
            mAddTextView = (TextView) itemView.findViewById(R.id.add_textview);
            mDeleteTextView = (TextView) itemView.findViewById(R.id.delete_textview);
            mShareTextView = (TextView) itemView.findViewById(R.id.share_textview);
            mTitleTextView = (TextView) itemView.findViewById(R.id.spoilerAlertName_textview);
            mFilterWordsTextView = (TextView) itemView.findViewById(R.id.filterWords_textview);
            mAddButton = (ImageButton) itemView.findViewById(R.id.addButton);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            mShareButton =(ImageButton) itemView.findViewById(R.id.shareButton);
        }
    }

    SpoilerListAdapter(Context context, DatabaseReference ref){
        super(Spoiler.class, R.layout.spoiler_item_layout, SpoilerViewHolder    .class, ref);
        mContext = context;
    }


    @Override
    protected void populateViewHolder(SpoilerListAdapter.SpoilerViewHolder viewHolder, Spoiler model, int position) {
        viewHolder.mTitleTextView.setText(model.getTitle());
        viewHolder.mFilterWordsTextView.setText(model.getFilterWords());
    }
}
