package com.globalbit.tellyou.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by alex on 09/10/2016.
 */

public class BirthdayPickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final int MIN_AGE_RESTRICTION=13;
    private static final int MAX_AGE_RESTRICTION=120;
    private BirthdayPickerDialogFragment.OnDateSet mListener;
    private Calendar mMaxCalendar;
    private Calendar mMinCalendar;

    public static BirthdayPickerDialogFragment newInstance(BirthdayPickerDialogFragment.OnDateSet listener)
    {
        BirthdayPickerDialogFragment fragment=new BirthdayPickerDialogFragment();
        fragment.mListener=listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mMaxCalendar=Calendar.getInstance();
        mMaxCalendar.set(Calendar.YEAR, year-MIN_AGE_RESTRICTION);
        mMaxCalendar.set(Calendar.HOUR_OF_DAY,23);
        mMaxCalendar.set(Calendar.MINUTE,59);
        mMaxCalendar.set(Calendar.SECOND,59);
        mMaxCalendar.set(Calendar.MILLISECOND,0);
        mMinCalendar=Calendar.getInstance();
        mMinCalendar.set(Calendar.YEAR, year-MAX_AGE_RESTRICTION);
        mMinCalendar.set(Calendar.HOUR_OF_DAY,0);
        mMinCalendar.set(Calendar.MINUTE,0);
        mMinCalendar.set(Calendar.SECOND,0);
        mMinCalendar.set(Calendar.MILLISECOND,0);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), this, year-MIN_AGE_RESTRICTION, month, day);
        datePickerDialog.getDatePicker().setMaxDate(mMaxCalendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(mMinCalendar.getTimeInMillis());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (view.isShown()) {
            boolean isProperDate=true;
            int yearMax = mMaxCalendar.get(Calendar.YEAR);
            int monthMax = mMaxCalendar.get(Calendar.MONTH);
            int dayMax = mMaxCalendar.get(Calendar.DAY_OF_MONTH);
            int yearMin = mMinCalendar.get(Calendar.YEAR);
            int monthMin = mMinCalendar.get(Calendar.MONTH);
            int dayMin = mMinCalendar.get(Calendar.DAY_OF_MONTH);
            if(yearMax<year) {
                isProperDate=false;
            }
            else if(yearMax==year) {
                if(monthMax<monthOfYear) {
                    isProperDate=false;
                }
                else if(monthMax==monthOfYear) {
                    if(dayMax<dayOfMonth) {
                        isProperDate=false;
                    }
                }
            }
            if(yearMin>year) {
                isProperDate=false;
            }
            else if(yearMin==year) {
                if(monthMin>monthOfYear) {
                    isProperDate=false;
                }
                else if(monthMin==monthOfYear) {
                    if(dayMin>dayOfMonth) {
                        isProperDate=false;
                    }
                }
            }
            mListener.onDateSet(year,monthOfYear,dayOfMonth, isProperDate);
        }
    }

    public interface OnDateSet {
        void onDateSet(int year, int monthOfYear, int dayOfMonth, boolean isProperDate);
    }
}
