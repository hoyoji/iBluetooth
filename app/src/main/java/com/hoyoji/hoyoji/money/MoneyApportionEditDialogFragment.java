package com.hoyoji.hoyoji.money;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.btcontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MoneyApportionEditDialogFragment extends DialogFragment {
	private HyjNumericField mNumericFieldApportionAmount;
//	private HyjSpinnerField mSpinnerFieldApportionType;
	private RadioGroup mRadioGroupApportionType;
	
	static MoneyApportionEditDialogFragment newInstance(Double apportionAmount, String apportionType, boolean isProjectMember, boolean isHideMoney) {
    	MoneyApportionEditDialogFragment f = new MoneyApportionEditDialogFragment();
        Bundle args = new Bundle();
        args.putDouble("apportionAmount", apportionAmount);
        args.putString("apportionType", apportionType);
	    args.putBoolean("isProjectMember", isProjectMember);
	    args.putBoolean("isHideMoney", isHideMoney);
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      	final String apportionType = getArguments().getString("apportionType");
    	Double apportionAmount = getArguments().getDouble("apportionAmount");
    	Boolean isHideMoney = getArguments().getBoolean("isHideMoney");
//    	boolean isProjectMember = getArguments().getBoolean("isProjectMember");
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
    	
    	View v = inflater.inflate(R.layout.money_dialogfragment_moneyapportion_edit, null);
    	final HyjNumericField numericFieldApportionAmount = (HyjNumericField)v.findViewById(R.id.moneyApportionDialogFragment_textField_amount);
        numericFieldApportionAmount.setNumber(apportionAmount);
        mNumericFieldApportionAmount = numericFieldApportionAmount;
//        final HyjSpinnerField spinnerFieldApportionType = (HyjSpinnerField)v.findViewById(R.id.moneyApportionDialogFragment_spinnerField_type);
//        mSpinnerFieldApportionType = spinnerFieldApportionType;
//        if(isProjectMember){
//        	spinnerFieldApportionType.setItems(R.array.moneyApportionDialogFragment_spinnerField_apportionType_array, new String[] {"Average", "Fix", "Share"});
//        } else {
//        	spinnerFieldApportionType.setItems(R.array.moneyApportionDialogFragment_spinnerField_apportionType_array_non_project_member, new String[] {"Average", "Fix"});
//        }
//        spinnerFieldApportionType.setSelectedValue(apportionType);
//        

        numericFieldApportionAmount.setEnabled("Fix".equals(apportionType));
		
      final RadioGroup radioGroupApportionType = (RadioGroup)v.findViewById(R.id.moneyApportionDialogFragment_radio_type);
      mRadioGroupApportionType = radioGroupApportionType;
      if("Average".equals(apportionType)){
    	  mRadioGroupApportionType.check(R.id.moneyApportionDialogFragment_radio_type_average);
      } else if("Fix".equals(apportionType)){
    	  mRadioGroupApportionType.check(R.id.moneyApportionDialogFragment_radio_type_fixed);
      }      if("Share".equals(apportionType)){
    	  mRadioGroupApportionType.check(R.id.moneyApportionDialogFragment_radio_type_share);
      }

      if(isHideMoney){
      	numericFieldApportionAmount.setVisibility(View.GONE);
      	mRadioGroupApportionType.findViewById(R.id.moneyApportionDialogFragment_radio_type_fixed).setVisibility(View.GONE);
      }
      
//        spinnerFieldApportionType.setOnItemSelectedListener(new OnItemSelectedListener(){
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int pos, long arg3) {
//				if(pos == 1){
//					numericFieldApportionAmount.setEnabled(true);
//					numericFieldApportionAmount.showSoftKeyboard();
//				} else {
//					numericFieldApportionAmount.setEnabled(false);
//				}
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//			}
//        });
        
		mRadioGroupApportionType.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.moneyApportionDialogFragment_radio_type_fixed){
					numericFieldApportionAmount.setEnabled(true);
					numericFieldApportionAmount.showSoftKeyboard();
				} else {
					numericFieldApportionAmount.setEnabled(false);
				}
				
			}
			
		});

        v.findViewById(R.id.moneyApportionDialogFragment_button_delete).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity) getActivity()).dialogDoNegativeClick();
		        dismiss();
			}
        });
         
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the layout for the dialog
        builder.setView(v);

        // Set title of dialog
        builder.setTitle("修改分摊")
                // Set Ok button
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
            			        Bundle args = new Bundle();
            			        args.putDouble("apportionAmount", numericFieldApportionAmount.getNumber());
            			        args.putString("apportionType", getApportionType());
								((HyjActivity) getActivity()).dialogDoPositiveClick(args);
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
    

	public void setApportionAmount(Double amount) {
		mNumericFieldApportionAmount.setNumber(amount);
	}
	
	public String getApportionType(){
//		return mSpinnerFieldApportionType.getSelectedValue();
		switch (mRadioGroupApportionType.getCheckedRadioButtonId()){
			case R.id.moneyApportionDialogFragment_radio_type_average :
				return "Average";
			case R.id.moneyApportionDialogFragment_radio_type_fixed :
				return "Fix";
			case R.id.moneyApportionDialogFragment_radio_type_share :
				return "Share";
		}
		return null;
	}
}