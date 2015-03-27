package com.hoyoji.hoyoji.money;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.btcontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class MoneyAddNewDialogFragment extends DialogFragment {
	
	public static MoneyAddNewDialogFragment newInstance(Bundle args) {
    	MoneyAddNewDialogFragment f = new MoneyAddNewDialogFragment();
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();


		final Bundle bundle = getArguments();
    	View v = inflater.inflate(R.layout.money_dialogfragment_addnew, null);
    	v.findViewById(R.id.moneyDialogFragment_addnew_expense).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyExpenseViewPagerFragment.class, R.string.moneyExpenseFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_income).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyIncomeViewPagerFragment.class, R.string.moneyIncomeFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});    	
    	v.findViewById(R.id.moneyDialogFragment_addnew_deposit_expense).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_deposit_income).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_deposit_return).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_deposit_payback).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_template).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTemplateListFragment.class, R.string.moneyTemplateListFragment_title, null);
				dismiss();
			}
    	});

    	v.findViewById(R.id.moneyDialogFragment_addnew_borrow).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_lend).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_return).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_payback).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_transfer).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});
    	v.findViewById(R.id.moneyDialogFragment_addnew_topup).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTopupFormFragment.class, R.string.moneyTopupFormFragment_title_addnew, bundle);
				dismiss();
			}
    	});

        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the layout for the dialog
        builder.setView(v);

        // Set title of dialog
        builder.setTitle("记一笔")
//                // Set Ok button
//                .setPositiveButton(R.string.alert_dialog_ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//            			        
//                            }
//                        })
                // Set Cancel button
                .setNegativeButton(R.string.alert_dialog_cancel, null); 

        // Create the AlertDialog object and return it
        return builder.create();
    }
	
}