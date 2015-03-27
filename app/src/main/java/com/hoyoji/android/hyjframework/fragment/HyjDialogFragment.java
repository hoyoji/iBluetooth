package com.hoyoji.android.hyjframework.fragment;

import com.hoyoji.android.hyjframework.activity.HyjActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.os.Bundle;

public class HyjDialogFragment extends DialogFragment {
    
	public static HyjDialogFragment newInstance(int title, int msg, int positiveButton, int negativeButton, int neutralButton) {
		HyjDialogFragment frag = new HyjDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("message", msg);
		args.putInt("positiveButton", positiveButton);
		args.putInt("negativeButton", negativeButton);
		args.putInt("neutralButton", neutralButton);
		frag.setArguments(args);
		return frag; 
	}

	public static HyjDialogFragment newInstance(String title, String msg, int positiveButton, int negativeButton, int neutralButton) {
		HyjDialogFragment frag = new HyjDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", msg);
		args.putInt("positiveButton", positiveButton);
		args.putInt("negativeButton", negativeButton);
		args.putInt("neutralButton", neutralButton);
		frag.setArguments(args);
		return frag;
	}
 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		if(title == null){
			int titleInt = getArguments().getInt("title", -1);
			if(titleInt != -1){
				title = getString(titleInt);
			}
		}
		String msg = getArguments().getString("message");
		if(msg == null){
			msg = getString(getArguments().getInt("message"));
		}
		int positiveButton = getArguments().getInt("positiveButton");
		int negativeButton = getArguments().getInt("negativeButton");
		int neutralButton = getArguments().getInt("neutralButton");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				//.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(positiveButton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((HyjActivity) getActivity())
										.dialogDoPositiveClick(null);
								dismiss();
							}
						});
		if(negativeButton != -1){
			builder.setNegativeButton(negativeButton,
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
		}
		if(neutralButton != -1){
			builder.setNeutralButton(neutralButton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((HyjActivity) getActivity())
										.dialogDoNeutralClick();
								dismiss();
							}
						});
		}
		return builder.create();
	}
}
