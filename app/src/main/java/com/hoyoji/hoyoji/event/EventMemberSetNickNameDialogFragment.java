package com.hoyoji.hoyoji.event;

import org.json.JSONException;
import org.json.JSONObject;

import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.btcontrol.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EventMemberSetNickNameDialogFragment extends DialogFragment {
	private EditText mEditTextNickName = null;
	
	public static EventMemberSetNickNameDialogFragment newInstance(Bundle args) {
    	EventMemberSetNickNameDialogFragment f = new EventMemberSetNickNameDialogFragment();
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        Intent intent = getActivity().getIntent();
        final Long modelId = intent.getLongExtra("MODEL_ID", -1);
//		final Event event = Event.load(Event.class, modelId);

		final Bundle bundle = getArguments();
    	View v = inflater.inflate(R.layout.eventmember_dialogfragment_setnickname, null);
    	
    	EventMember em = EventMember.load(EventMember.class, bundle.getLong("EVENTMEMBERID"));
    	
    	mEditTextNickName = (EditText) v.findViewById(R.id.eventMemberSetNickNameDialogFragment_editText_nickName);
		mEditTextNickName.setText(em.getNickName());
    	
//    	v.findViewById(R.id.EventMemberDialogFragment_invite_friend).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Bundle newBundle = new Bundle();
//				newBundle.putLong("EVENT_ID", modelId);
//				((HyjActivity)getActivity()).openActivityWithFragment(EventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, newBundle);
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_addnew, bundle);
//				dismiss();
//			}
//    	});
//    	v.findViewById(R.id.EventMemberDialogFragment_invite_qq).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_addnew, bundle);
////				dismiss();
//			}
//    	});    	
//    	v.findViewById(R.id.EventMemberDialogFragment_invite_wx).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
////				dismiss();
//			}
//    	});
//    	v.findViewById(R.id.EventMemberDialogFragment_invite_other).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_addnew, bundle);
////				dismiss();
//			}
//    	});
//    	v.findViewById(R.id.EventMemberDialogFragment_signIn_qq).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_addnew, bundle);
////				dismiss();
//			}
//    	});
//    	v.findViewById(R.id.EventMemberDialogFragment_signIn_wx).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_addnew, bundle);
////				dismiss();
//			}
//    	});
//    	v.findViewById(R.id.EventMemberDialogFragment_signIn_other).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTemplateListFragment.class, R.string.moneyTemplateListFragment_title, null);
////				dismiss();
//			}
//    	});

        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the layout for the dialog
        builder.setView(v);

        // Set title of dialog
    		builder.setTitle("修改昵称")
//          // Set Ok button
          .setPositiveButton(R.string.alert_dialog_ok, null);
          // Set Cancel button
//          .setNegativeButton(R.string.alert_dialog_ok, null); 
        

        // Create the AlertDialog object and return it
        return builder.create();
    }

	@Override
	public void onStart() {
	    super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null)   {
	        Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener(){
	                    @Override
	                    public void onClick(View v) {
	                  	  	changeNickName();
	                    }
	                });
	    }
	}

	private void changeNickName(){
		final HyjActivity activity = ((HyjActivity) this.getActivity());
		Bundle bundle = getArguments();
    	
    	final EventMember em = EventMember.load(EventMember.class, bundle.getLong("EVENTMEMBERID"));
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
		    	em.setNickName(mEditTextNickName.getText().toString().trim());
		    	em.setSyncFromServer(true);
		    	em.save();
		    	activity.dismissProgressDialog();
		    	dismiss();
				HyjUtil.displayToast(R.string.app_save_success);
			}

			@Override
			public void errorCallback(Object object) {
				activity.dismissProgressDialog();
				
				JSONObject json = (JSONObject) object;
				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
			}
		};

		try {
			JSONObject data = new JSONObject();
			data.put("id", em.getId());
			data.put("__dataType", "EventMember");
			data.put("nickName", mEditTextNickName.getText().toString().trim());
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + data.toString() + "]" , "putData");
			
			((HyjActivity) this.getActivity()).displayProgressDialog(
							R.string.changeNickNameFormFragment_title,
							R.string.changeNickNameFormFragment_toast_changing);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
