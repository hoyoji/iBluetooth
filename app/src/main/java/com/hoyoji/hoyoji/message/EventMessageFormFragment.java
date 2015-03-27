package com.hoyoji.hoyoji.message;

import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.HyjWebServiceExchangeRateAsyncTask;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.money.currency.ExchangeFormFragment;

public class EventMessageFormFragment extends HyjUserFormFragment {

	protected static final int FETCH_PROJECT_TO_LOCAL_EXCHANGE = 0;
	private HyjModelEditor<Message> mMessageEditor = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjTextField mEditTextToUser = null;
	private HyjTextField mEditTextTitle = null;
	private HyjRemarkField mEditTextDetail = null;

	@Override
	public Integer useContentView() {
		return R.layout.message_formfragment_eventmessage;
	}

	@Override
	public Integer useOptionsMenuView() {
		return null;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Message memberAddMessage;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			memberAddMessage = new Select().from(Message.class).where("_id=?", modelId).executeSingle();
			if(memberAddMessage.getMessageState().equalsIgnoreCase("unread") || memberAddMessage.getMessageState().equalsIgnoreCase("new")){
				memberAddMessage.setMessageState("read");
				memberAddMessage.save();
			}
		} else {
			memberAddMessage = new Message();
		}
		mMessageEditor = memberAddMessage.newModelEditor();

		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.eventMessageFormFragment_editText_date);
		mDateTimeFieldDate.setTime(memberAddMessage.getDate());
		mDateTimeFieldDate.setEnabled(false);

		mEditTextToUser = (HyjTextField) getView().findViewById(R.id.eventMessageFormFragment_editText_toUser);
		mEditTextToUser.setEnabled(false);
		mEditTextTitle = (HyjTextField) getView().findViewById(R.id.eventMessageFormFragment_editText_title);
		mEditTextTitle.setText(memberAddMessage.getMessageTitle());
		mEditTextTitle.setEnabled(false);

		mEditTextDetail = (HyjRemarkField) getView().findViewById(R.id.eventMessageFormFragment_editText_detail);
		mEditTextDetail.setText(memberAddMessage.getMessageDetail());
		Button actionButton = (Button) getView().findViewById(R.id.button_save);
		if (memberAddMessage.getFromUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
			mEditTextToUser.setText(memberAddMessage.getToUserDisplayName());
			actionButton.setVisibility(View.GONE);
		} else {
			mDateTimeFieldDate.setLabel(R.string.eventMessageFormFragment_textView_date_receive);
			mEditTextToUser.setLabel(R.string.eventMessageFormFragment_textView_fromUser);
			mEditTextToUser.setText(memberAddMessage.getFromUserDisplayName());
			mEditTextDetail.setEnabled(false);

			actionButton.setText(R.string.eventMessageFormFragment_button_accept);
		}
		if (memberAddMessage.getType().equalsIgnoreCase("Event.Member.Accept")
				|| memberAddMessage.getType().equalsIgnoreCase(
						"Event.Member.SignUp")
				|| memberAddMessage.getType().equalsIgnoreCase(
						"Event.Member.SignIn")
				|| memberAddMessage.getType().equalsIgnoreCase(
						"Event.Member.Cancel")
				|| memberAddMessage.getType().equalsIgnoreCase(
						"Event.Member.CancelSignUp")
				|| memberAddMessage.getType().equalsIgnoreCase(
						"Project.Share.AcceptInviteLink")) {
			actionButton.setVisibility(View.GONE);
			mEditTextDetail.setEnabled(false);
		}

	}

	private void fillData() {
		Message modelCopy = (Message) mMessageEditor.getModelCopy();
		modelCopy.setMessageDetail(mEditTextDetail.getText().toString().trim());
	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);

		mEditTextDetail.setError(mMessageEditor.getValidationError("messageDetail"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);
		if (mMessageEditor.getModel().getType().equalsIgnoreCase("Event.Member.Accept")|| mMessageEditor.getModel().getType().equalsIgnoreCase("Event.Member.Delete")) {
			return;
		}
		if (!mMessageEditor.getModel().getFromUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
			try {
				final JSONObject jsonMsgData = new JSONObject(mMessageEditor.getModelCopy().getMessageData());

//				ProjectShareAuthorization newPSA = new Select().from(ProjectShareAuthorization.class).where("projectId=? and friendUserId=?",
//						jsonMsgData.optString("projectId"), mMessageEditor.getModelCopy().getFromUserId()).executeSingle();
//				
//				if (newPSA != null && newPSA.getState().equals("Accept")) {
//					sendAcceptMessageToServer(jsonMsgData);
//				} else {
					//圈子存在才允许去接受活动邀请
					EventMember newEM = HyjModel.getModel(EventMember.class,jsonMsgData.optString("eventMemberId"));
//					if (newEM != null && (newEM.getState().equals("SignUp") || newEM.getState().equals("SignIn"))) {
					if (newEM != null && (newEM.getState().equals("SignUp") || newEM.getState().equals("SignIn"))) {
						// 该活动已经存在
						HyjUtil.displayToast(R.string.eventMessageFormFragment_addShare_already_exists);
					} else {
						final String projectCurrencyId = jsonMsgData.optJSONArray("projectCurrencyIds").optString(0);
						
						((HyjActivity)EventMessageFormFragment.this.getActivity()).displayProgressDialog(R.string.eventMessageFormFragment_addShare_fetch_exchange, R.string.eventMessageFormFragment_addShare_fetching_exchange);
						if(projectCurrencyId.equalsIgnoreCase(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId())){
							sendAcceptMessageToServer(jsonMsgData);
						} else {
							Exchange exchange = new Select().from(Exchange.class).where("foreignCurrencyId=? AND localCurrencyId=?", projectCurrencyId, HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId()).executeSingle();
							if(exchange != null){
								sendAcceptMessageToServer(jsonMsgData);
								return;
							}
							HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
								@Override
								public void finishCallback(Object object) {
									((HyjActivity)EventMessageFormFragment.this.getActivity()).dismissProgressDialog();
									Double exchangeRate = (Double) object;
									Exchange newExchange = new Exchange();
									newExchange.setForeignCurrencyId(projectCurrencyId);
									newExchange.setLocalCurrencyId(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId());
									newExchange.setRate(exchangeRate);
									newExchange.save();
									sendAcceptMessageToServer(jsonMsgData);
								}
	
								@Override
								public void errorCallback(Object object) {
									((HyjActivity)EventMessageFormFragment.this.getActivity()).dismissProgressDialog();
									if (object != null) {
										HyjUtil.displayToast(object.toString());
									} else {
										HyjUtil.displayToast(R.string.moneyExpenseFormFragment_toast_cannot_refresh_rate);
									}
									
									((HyjActivity)EventMessageFormFragment.this.getActivity()).displayDialog(-1, R.string.eventMessageFormFragment_addShare_cannot_fetch_exchange, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,  new DialogCallbackListener(){
										@Override
										public void doPositiveClick(Object object){
											Bundle bundle = new Bundle();
											bundle.putString("localCurrencyId", HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId());
											bundle.putString("foreignCurrencyId", projectCurrencyId);
											openActivityWithFragmentForResult(ExchangeFormFragment.class, R.string.exchangeFormFragment_title_addnew, bundle, FETCH_PROJECT_TO_LOCAL_EXCHANGE);
										}
										@Override
										public void doNegativeClick(){
											HyjUtil.displayToast("未能获取圈子币种到本币的汇率");
										}
									});
								}
							};
							HyjWebServiceExchangeRateAsyncTask.newInstance(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId(), 
									projectCurrencyId, 
									serverCallbacks);
						}
					}
//				}
			} catch (JSONException e) {
			}

		}
	}

	private void sendAcceptMessageToServer(final JSONObject jsonMsgData) {
		try {
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					loadSharedProjectData(jsonMsgData);
				}
	
				@Override
				public void errorCallback(Object object) {
					displayError(object);
				}
			};
	
			JSONObject msg = new JSONObject();
			msg.put("__dataType", "Message");
			msg.put("id", UUID.randomUUID().toString());
			msg.put("toUserId", mMessageEditor.getModelCopy().getFromUserId());
			msg.put("fromUserId", HyjApplication.getInstance().getCurrentUser().getId());
			msg.put("type", "Event.Member.Accept");
			msg.put("messageState", "new");
			msg.put("messageTitle", "接受活动邀请");
			msg.put("date", (new Date()).getTime());
			msg.put("detail", "用户"+ HyjApplication.getInstance().getCurrentUser().getDisplayName() + "接受了您的活动: "+ jsonMsgData.optString("eventName"));
			msg.put("messageBoxId",jsonMsgData.optString("fromMessageBoxId"));
			msg.put("ownerUserId", mMessageEditor.getModelCopy().getFromUserId());
	
			JSONObject msgData = new JSONObject();
			msgData.put("projectShareAuthorizationId", jsonMsgData.optString("projectShareAuthorizationId"));
			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
			msgData.put("projectIds", jsonMsgData.opt("projectIds"));
			msgData.put("eventId", jsonMsgData.opt("eventId"));
			msgData.put("eventMemberId", jsonMsgData.opt("eventMemberId"));
			
			msg.put("messageData", msgData.toString());
	
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[" + msg.toString() + "]", "postData");
			((HyjActivity) this.getActivity()).displayProgressDialog(
							R.string.eventListFragment_title_acceptShare,
							R.string.eventListFragment_acceptShare_progress_adding);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	protected void loadSharedProjectData(JSONObject jsonMsgData) {
		// load new ProjectData from server
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				try {

					JSONArray jsonArray = (JSONArray) object;
					ActiveAndroid.beginTransaction();
					
					for(int i = 0; i < jsonArray.length(); i++){
						JSONArray jsonObjects = jsonArray.getJSONArray(i);
						for(int j = 0; j < jsonObjects.length(); j++){
							JSONObject jsonObj = jsonObjects.optJSONObject(j);
							HyjModel model = HyjModel.createModel(jsonObj.optString("__dataType"), jsonObj.getString("id"));
							if(model != null){
								model.loadFromJSON(jsonObj, true);
								model.save();
							}
						}	
					}

					ActiveAndroid.setTransactionSuccessful();
					HyjUtil.displayToast(R.string.eventMessageFormFragment_toast_accept_success);
					getActivity().finish();
					
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					ActiveAndroid.endTransaction();
				}
				((HyjActivity) EventMessageFormFragment.this.getActivity()).dismissProgressDialog();
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		JSONArray data = new JSONArray();
		try {
			JSONArray projectIds = jsonMsgData.optJSONArray("projectIds");
			for (int i = 0; i < projectIds.length(); i++) {
				JSONObject newObj = new JSONObject();
				newObj.put("__dataType", "Project");
				newObj.put("main.id", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "ProjectShareAuthorization");
				newObj.put("main.projectId", projectIds.get(i));
//				newObj.put("main.state", "Accept");
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpenseContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpense");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpenseApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncomeContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositIncomeContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncome");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncomeApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositIncomeApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrow");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrowContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrowApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLend");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLendContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLendApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyReturn");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyReturnApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositReturnApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositReturnContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyPayback");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyPaybackApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyTransfer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "Picture");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "Event");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "EventMember");
				newObj.put("evt.projectId", projectIds.get(i));
				data.put(newObj);
			}
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case FETCH_PROJECT_TO_LOCAL_EXCHANGE:
	        	 if(resultCode == Activity.RESULT_OK){
	        		JSONObject jsonMsgData;
					try {
						jsonMsgData = new JSONObject(mMessageEditor.getModelCopy().getMessageData());
						
		        		Exchange exchange = new Select().from(Exchange.class).where("foreignCurrencyId=? AND localCurrencyId=?", jsonMsgData.optJSONArray("projectCurrencyIds").optString(0), HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId()).executeSingle();
						if(exchange != null){
							sendAcceptMessageToServer(jsonMsgData);
							return;
						} else {
							HyjUtil.displayToast("未能获取圈子币种到本币的汇率");
						}
					} catch (JSONException e) {}
	         	 }
	        	 break;
         }
   }
	
	private void displayError(Object object) {
		((HyjActivity) EventMessageFormFragment.this.getActivity()).dismissProgressDialog();
		JSONObject json = (JSONObject) object;
		HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
	}
}
