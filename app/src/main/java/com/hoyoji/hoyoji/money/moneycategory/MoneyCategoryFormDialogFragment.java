package com.hoyoji.hoyoji.money.moneycategory;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class MoneyCategoryFormDialogFragment extends DialogFragment {
	
	static MoneyCategoryFormDialogFragment newInstance(String title, String categoryName, int operation) {
    	MoneyCategoryFormDialogFragment f = new MoneyCategoryFormDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("categoryName", categoryName);
        args.putInt("operation", operation);
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      	final String categoryName = getArguments().getString("categoryName");
      	String title = getArguments().getString("title");
    	int operation = getArguments().getInt("operation");
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
    	
    	View v = inflater.inflate(R.layout.moneycategory_dialogfragment_moneycategory_form, null);
    	final HyjTextField categoryNameField = (HyjTextField)v.findViewById(R.id.moneyCategoryFormDialogFragment_textField_name);
    	categoryNameField.setText(categoryName);
    	categoryNameField.showSoftKeyboard();
        View deleteButton = v.findViewById(R.id.moneyCategoryFormDialogFragment_button_delete);
        if(operation == 0){
        	deleteButton.setVisibility(View.GONE);
        } else {
	        deleteButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					((HyjActivity) getActivity()).dialogDoNegativeClick();
			        dismiss();
				}
	        });
        }
         
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the layout for the dialog
        builder.setView(v);
//        int title = R.string.moneyCategoryFormDialogFragment_title_edit;
//        if(operation == 0){
//        	title = R.string.moneyCategoryFormDialogFragment_title_addnew;
//        }
        // Set title of dialog
        builder.setTitle(title)
                // Set Ok button
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            	if(categoryNameField.getText() == null || categoryNameField.getText().length() == 0){
                            		categoryNameField.setError(getString(R.string.moneyCategoryFormDialogFragment_editText_hint_name));
                            	} else {
	            			        Bundle args = new Bundle();
	            			        args.putString("categoryName", categoryNameField.getText());
									((HyjActivity) getActivity()).dialogDoPositiveClick(args);
                            	}
                            }
                        })
                // Set Cancel button
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                		        ((HyjActivity) getActivity()).dialogDoNeutralClick();
                            }
                        }); 
        

        // Create the AlertDialog object and return it
        return builder.create();
    }
}