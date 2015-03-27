package com.hoyoji.hoyoji.event;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.btcontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;

public class EventFormCreateProjectDialogFragment extends DialogFragment {

	public static EventFormCreateProjectDialogFragment newInstance(Bundle args) {
		EventFormCreateProjectDialogFragment f = new EventFormCreateProjectDialogFragment();
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();


		final Bundle bundle = getArguments();
    	View v = inflater.inflate(R.layout.event_dialogfragment_createproject, null);
    	v.findViewById(R.id.eventFormCreateProjectDialogFragment_button_createNewProject).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity) getActivity())
						.dialogDoPositiveClick(null);
				dismiss();
			}
    	});
    	v.findViewById(R.id.eventFormCreateProjectDialogFragment_button_selectProject).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity) getActivity())
				.dialogDoNeutralClick();
				dismiss();
			}
    	});
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("选择活动圈子");
			builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((HyjActivity) getActivity())
										.dialogDoNegativeClick();
								dismiss();
							}
						});
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int arg1,
						KeyEvent arg2) {
					if(arg1 == KeyEvent.KEYCODE_BACK){
						((HyjActivity) getActivity()).dialogDoNegativeClick();
						dismiss();
						return true;
					}
					return false;
				}
			});
		return builder.create();
	}
}
