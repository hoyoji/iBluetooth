package com.hoyoji.android.hyjframework.fragment;

import java.util.Calendar;
import java.util.Date;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.btcontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;


public class HyjDateTimePickerDialogFragment extends DialogFragment implements OnDateChangedListener, OnTimeChangedListener {
    // Define constants for date-time picker.
    public final static int DATE_PICKER = 1;
    public final static int TIME_PICKER = 2;
    public final static int DATE_TIME_PICKER = 3;

    // DatePicker reference
    private DatePicker datePicker;

    // TimePicker reference
    private TimePicker timePicker;

    // Calendar reference
    private Calendar mCalendar;


    // Define Dialog view
    private View mView;

    public static HyjDateTimePickerDialogFragment newInstance(String title, Date date) {
    	HyjDateTimePickerDialogFragment frag = new HyjDateTimePickerDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		if(date != null){
			args.putLong("time", date.getTime());
		}
		frag.setArguments(args);
		return frag; 
	}
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	String title = getArguments().getString("title");
		if(title == null){
			title = getString(getArguments().getInt("title"));
		}
		
		Long time = getArguments().getLong("time", -1);

		// Grab a Calendar instance
        mCalendar = Calendar.getInstance();
		if(time != -1){
			mCalendar.setTimeInMillis(time);
		}
		
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.datetime_dialogfragment, null);  

        
        // Init date picker
        datePicker = (DatePicker) mView.findViewById(R.id.DatePicker);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        
        // Init time picker
        timePicker = (TimePicker) mView.findViewById(R.id.TimePicker);
        // Set default Calendar and Time Style
        timePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));
        timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        
        timePicker.setOnTimeChangedListener(this);
       

        // Set the layout for the dialog
        builder.setView(mView);

        // Set title of dialog
        builder.setTitle(title)
                // Set Ok button
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User ok the dialog
								((HyjActivity) getActivity())
								.dialogDoPositiveClick(mCalendar.getTime());
								dismiss();
                            }
                        })
                // Set Cancel button
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
//                                HyjDateTimePickerDialogFragment.this.getDialog().cancel();
								dismiss();
                            }
                        }); 

        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Convenience wrapper for internal Calendar instance
    public int get(final int field) {
        return mCalendar.get(field);
    }

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }

    // Convenience wrapper for internal DatePicker instance
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    // Convenience wrapper for internal TimePicker instance
    public void updateTime(int currentHour, int currentMinute) {
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    // Called every time the user changes DatePicker values
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Update the internal Calendar instance
        mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    // Called every time the user changes TimePicker values
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // Update the internal Calendar instance
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    }
}