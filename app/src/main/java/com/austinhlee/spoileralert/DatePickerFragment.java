package com.austinhlee.spoileralert;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    int mYear;
    int mMonth;
    int mDay;
    private Button mDatePreview;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        mDatePreview = getActivity().findViewById(R.id.setDate);
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getDay() {
        return mDay;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        mDatePreview.setVisibility(View.VISIBLE);
        mDatePreview.setText((month+1)+"/"+day+"/"+year);
    }

    public void refreshPreview(){
        mDatePreview.setVisibility(View.VISIBLE);
        mDatePreview.setText((mMonth+1)+"/"+mDay+"/"+mYear);
    }
}
