package com.hoyoji.hoyoji.message;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MoneyBorrowApportion;
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnApportion;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyLendApportion;
import com.hoyoji.hoyoji.models.MoneyPaybackApportion;
import com.hoyoji.hoyoji.models.MoneyReturnApportion;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.money.MoneyBorrowFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositExpenseContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositIncomeContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositPaybackContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositReturnContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyExpenseContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyExpenseFormFragment;
import com.hoyoji.hoyoji.money.MoneyIncomeContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyIncomeFormFragment;
import com.hoyoji.hoyoji.money.MoneyLendFormFragment;
import com.hoyoji.hoyoji.money.MoneyPaybackFormFragment;
import com.hoyoji.hoyoji.money.MoneyReturnFormFragment;

public class MoneyShareMessageFormFragment extends HyjUserFormFragment {

	protected static final int IMPORT_MONEY = 0;
	private HyjModelEditor<Message> mMessageEditor = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjTextField mEditTextToUser = null;
	private HyjTextField mEditTextTitle = null;
	private HyjRemarkField mEditTextDetail = null;

	@Override
	public Integer useContentView() {
		return R.layout.message_formfragment_moneysharemessage;
	}

	@Override
	public Integer useOptionsMenuView() {
		return null;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Message shareAddMessage;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			shareAddMessage = new Select().from(Message.class)
					.where("_id=?", modelId).executeSingle();
			if(shareAddMessage.getMessageState().equalsIgnoreCase("unread") || 
					shareAddMessage.getMessageState().equalsIgnoreCase("new")){
				shareAddMessage.setMessageState("read");
				shareAddMessage.save();
			}
		} else {
			shareAddMessage = new Message();
		}
		mMessageEditor = shareAddMessage.newModelEditor();

		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(
				R.id.moneyShareMessageFormFragment_editText_date);
		mDateTimeFieldDate.setTime(shareAddMessage.getDate());
		mDateTimeFieldDate.setEnabled(false);

		mEditTextToUser = (HyjTextField) getView().findViewById(
				R.id.moneyShareMessageFormFragment_editText_toUser);
		mEditTextToUser.setEnabled(false);
		mEditTextTitle = (HyjTextField) getView().findViewById(
				R.id.moneyShareMessageFormFragment_editText_title);
		mEditTextTitle.setText(shareAddMessage.getMessageTitle());
		mEditTextTitle.setEnabled(false);

		mEditTextDetail = (HyjRemarkField) getView().findViewById(
				R.id.moneyShareMessageFormFragment_editText_detail);

		try {
			JSONObject messageData = null;
			messageData = new JSONObject(mMessageEditor.getModel().getMessageData());
			double amount = 0;
			try{
				amount = messageData.getDouble("amount") * messageData.getDouble("exchangeRate");
			} catch(Exception e) {
				amount = messageData.optDouble("amount");
			}
			java.util.Currency localeCurrency = java.util.Currency
					.getInstance(messageData.optString("currencyCode"));
			String currencySymbol = "";
			currencySymbol = localeCurrency.getSymbol();
			if(currencySymbol.length() == 0){
				currencySymbol = messageData.optString("currencyCode");
			}
					
			mEditTextDetail.setText(String.format(shareAddMessage.getMessageDetail(), shareAddMessage.getFromUserDisplayName(), currencySymbol, amount));
		} catch (Exception e){
		}
		
		Button actionButton = (Button) getView().findViewById(R.id.button_save);
		
		mDateTimeFieldDate
				.setLabel(R.string.moneyShareMessageFormFragment_textView_date_receive);
		mEditTextToUser
				.setLabel(R.string.moneyShareMessageFormFragment_textView_fromUser);
		mEditTextToUser.setText(shareAddMessage.getFromUserDisplayName());
		mEditTextDetail.setEnabled(false);
		JSONObject messageData;
		try {
			messageData = new JSONObject(mMessageEditor.getModel().getMessageData());
			if(messageData.optBoolean("isImported")){
				actionButton.setText(R.string.moneyShareMessageFormFragment_button_view_transaction);
			} else {
				if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyExpenseContainer")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_income);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyIncomeContainer")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_expense);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyDepositPaybackContainer")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_depositReturn);
				}  else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyDepositExpenseContainer")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_depositIncome);

				
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyBorrow")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_lend);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyLend")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_borrow);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyReturn")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_payback);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyPayback")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_return);

					
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyIncomeApportion")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_view_transaction);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyExpenseApportion")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_view_transaction);
					
					
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyDepositIncomeApportion")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_depositExpense);
				} else if (shareAddMessage.getType().equalsIgnoreCase("Money.Share.AddMoneyDepositReturnApportion")){
					actionButton.setText(R.string.moneyShareMessageFormFragment_button_import_depositPayback);
				}			
			}
		} catch (Exception ex){
		}
		
	}
//
//	private void fillData() {
//		Message modelCopy = (Message) mMessageEditor.getModelCopy();
//		modelCopy.setMessageDetail(mEditTextDetail.getText().toString().trim());
//	}
//
//	private void showValidatioErrors() {
//		HyjUtil.displayToast(R.string.app_validation_error);
//
//		mEditTextDetail.setError(mMessageEditor
//				.getValidationError("messageDetail"));
//	}

	@Override
	public void onSave(View v) {
		super.onSave(v);
		Bundle bundle = new Bundle();
		JSONObject messageData;
		try {
			messageData = new JSONObject(mMessageEditor.getModel().getMessageData());
			bundle.putLong("date", messageData.optLong("date"));
			bundle.putDouble("amount", messageData.optDouble("amount"));
			bundle.putDouble("exchangeRate", messageData.optDouble("exchangeRate"));
			bundle.putString("currencyId", messageData.optString("currencyCode"));
			bundle.putString("counterpartId", messageData.optString("counterpartId"));
			bundle.putString("friendUserId", mMessageEditor.getModel().getFromUserId());
			String projectId = messageData.optString("projectId");
			if(projectId != null && projectId.length() > 0){
				Project project = HyjModel.getModel(Project.class, projectId);
				if(project != null){
					bundle.putString("projectId", projectId);
				}
			}
			String eventId = messageData.optString("eventId");
			if(eventId != null && eventId.length() > 0){
				Event event = HyjModel.getModel(Event.class, eventId);
				if(event != null){
					bundle.putString("eventId", eventId);
				}
			}
			
			if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyExpenseContainer")){
//				if(messageData.optBoolean("isImported")){
//					MoneyExpenseContainer container = HyjModel.getModel(MoneyExpenseContainer.class, messageData.optString("counterpartId"));
//					if(container != null){
//						bundle.putLong("MODEL_ID", container.get_mId());
//						openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
//					} else {
//						HyjUtil.displayToast("该支出记录尚未下载，请先进行同步。");
//					}
//				} else {
					openActivityWithFragmentForResult(MoneyIncomeContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_income, bundle, IMPORT_MONEY);
//				}
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyIncomeContainer")){
//				if(messageData.optBoolean("isImported")){
//					MoneyIncomeContainer container = HyjModel.getModel(MoneyIncomeContainer.class, messageData.optString("counterpartId"));
//					if(container != null){
//						bundle.putLong("MODEL_ID", container.get_mId());
//						openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
//					} else {
//						HyjUtil.displayToast("该支收入记录尚未下载，请先进行同步。");
//					}
//				} else {
					openActivityWithFragmentForResult(MoneyExpenseContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_expense, bundle, IMPORT_MONEY);
//				}
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyDepositExpenseContainer")){
				if(messageData.optBoolean("isImported")){
					MoneyDepositExpenseContainer container = HyjModel.getModel(MoneyDepositExpenseContainer.class, messageData.optString("counterpartId"));
					if(container != null){
						bundle.clear();
						bundle.putLong("MODEL_ID", container.get_mId());
						openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_edit, bundle);
					} else {
						HyjUtil.displayToast("该会费预收记录尚未下载，请先进行同步。");
					}
				} else {
					openActivityWithFragmentForResult(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_depositIncome, bundle, IMPORT_MONEY);
				}
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyDepositPaybackContainer")){
				if(messageData.optBoolean("isImported")){
					MoneyDepositPaybackContainer container = HyjModel.getModel(MoneyDepositPaybackContainer.class, messageData.optString("counterpartId"));
					if(container != null){
						bundle.clear();
						bundle.putLong("MODEL_ID", container.get_mId());
						openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_edit, bundle);
					} else {
						HyjUtil.displayToast("该会费收回记录尚未下载，请先进行同步。");
					}
				} else {
					openActivityWithFragmentForResult(MoneyDepositReturnContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_depositReturn, bundle, IMPORT_MONEY);
				}
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyBorrow")){
					openActivityWithFragmentForResult(MoneyLendFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_lend, bundle, IMPORT_MONEY);
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyLend")){
					openActivityWithFragmentForResult(MoneyBorrowFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_borrow, bundle, IMPORT_MONEY);
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyReturn")){
					openActivityWithFragmentForResult(MoneyPaybackFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_payback, bundle, IMPORT_MONEY);
			} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyPayback")){
					openActivityWithFragmentForResult(MoneyReturnFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_return, bundle, IMPORT_MONEY);
			} else {
				String apportionId = messageData.getString("counterpartId");
				if(apportionId != null){
					if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyExpenseApportion")){
						MoneyExpenseApportion apportion = HyjModel.getModel(MoneyExpenseApportion.class, apportionId);
						if(apportion != null){
							MoneyExpenseContainer container = HyjModel.getModel(MoneyExpenseContainer.class, apportion.getMoneyExpenseContainerId());
							if(container != null){
								bundle.clear();
								bundle.putLong("MODEL_ID", container.get_mId());
								openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
							} else {
								HyjUtil.displayToast("该支出记录尚未下载，请先进行同步。");
							}
						} else {
							MoneyExpense moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId = ?", apportionId).executeSingle();
							if(moneyExpense != null){
								bundle.clear();
								bundle.putLong("MODEL_ID", moneyExpense.get_mId());
								openActivityWithFragment(MoneyExpenseFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
							} else {
								HyjUtil.displayToast("该支出记录尚未下载，请先进行同步。");
							}
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyIncomeApportion")){
						MoneyIncomeApportion apportion = HyjModel.getModel(MoneyIncomeApportion.class, apportionId);
						if(apportion != null){
							MoneyIncomeContainer container = HyjModel.getModel(MoneyIncomeContainer.class, apportion.getMoneyIncomeContainerId());
							if(container != null){
								bundle.clear();
								bundle.putLong("MODEL_ID", container.get_mId());
								openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
							} else {
								HyjUtil.displayToast("该收入记录尚未下载，请先进行同步。");
							}
						} else {
							MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId = ?", apportionId).executeSingle();
							if(moneyIncome != null){
								bundle.clear();
								bundle.putLong("MODEL_ID", moneyIncome.get_mId());
								openActivityWithFragment(MoneyIncomeFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
							} else {
								HyjUtil.displayToast("该收入记录尚未下载，请先进行同步。");
							}
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyDepositIncomeApportion")){
						if(messageData.optBoolean("isImported")){
							MoneyDepositIncomeApportion apportion = HyjModel.getModel(MoneyDepositIncomeApportion.class, apportionId);
							if(apportion != null){
								MoneyDepositIncomeContainer container = HyjModel.getModel(MoneyDepositIncomeContainer.class, apportion.getMoneyDepositIncomeContainerId());
								if(container != null){
									bundle.clear();
									bundle.putLong("MODEL_ID", container.get_mId());
									openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
								} else {
									HyjUtil.displayToast("该会费预收记录尚未下载，请先进行同步。");
								}
							} else {
								HyjUtil.displayToast("该会费预收记录尚未下载，请先进行同步。");
							}
						} else {
							openActivityWithFragmentForResult(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_depositExpense, bundle, IMPORT_MONEY);
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyDepositReturnApportion")){
						if(messageData.optBoolean("isImported")){
							MoneyDepositReturnApportion apportion = HyjModel.getModel(MoneyDepositReturnApportion.class, apportionId);
							if(apportion != null){
								MoneyDepositReturnContainer container = HyjModel.getModel(MoneyDepositReturnContainer.class, apportion.getMoneyDepositReturnContainerId());
								if(container != null){
									bundle.clear();
									bundle.putLong("MODEL_ID", container.get_mId());
									openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_edit, bundle);
								} else {
									HyjUtil.displayToast("该会费退款记录尚未下载，请先进行同步。");
								}
							} else {
								HyjUtil.displayToast("该会费退款录尚未下载，请先进行同步。");
							} 
						} else {
							openActivityWithFragmentForResult(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyShareMessageFormFragment_button_import_depositPayback, bundle, IMPORT_MONEY);
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyBorrowApportion")){
						MoneyBorrowApportion apportion = HyjModel.getModel(MoneyBorrowApportion.class, apportionId);
						if(apportion != null){
							bundle.clear();
							bundle.putString("MODEL_ID", apportion.getMoneyBorrowContainerId());
							openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_edit, bundle);
						} else {
							HyjUtil.displayToast("该借入记录尚未下载，请先进行同步。");
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyLendApportion")){
						MoneyLendApportion apportion = HyjModel.getModel(MoneyLendApportion.class, apportionId);
						if(apportion != null){
							bundle.putString("MODEL_ID", apportion.getMoneyLendContainerId());
							openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_edit, bundle);
						} else {
							HyjUtil.displayToast("该借出记录尚未下载，请先进行同步。");
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyReturnApportion")){
						MoneyReturnApportion apportion = HyjModel.getModel(MoneyReturnApportion.class, apportionId);
						if(apportion != null){
							bundle.clear();
							bundle.putString("MODEL_ID", apportion.getMoneyReturnContainerId());
							openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_edit, bundle);
						} else {
							HyjUtil.displayToast("该还款记录尚未下载，请先进行同步。");
						}
					} else if (mMessageEditor.getModel().getType().equalsIgnoreCase("Money.Share.AddMoneyPaybackApportion")){
						MoneyPaybackApportion apportion = HyjModel.getModel(MoneyPaybackApportion.class, apportionId);
						if(apportion != null){
							bundle.clear();
							bundle.putString("MODEL_ID", apportion.getMoneyPaybackContainerId());
							openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_edit, bundle);
						} else {
							HyjUtil.displayToast("该收款记录尚未下载，请先进行同步。");
						}
					} 
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case IMPORT_MONEY:
	        	 if(resultCode == Activity.RESULT_OK){
	 				mMessageEditor.getModelCopy().setMessageState("closed");
	 				mMessageEditor.save();
	 				HyjUtil.displayToast("导入成功");
	 				getActivity().finish();
	        	 }
	        	 break;
         }
   }
	
	private void displayError(Object object) {
		((HyjActivity) MoneyShareMessageFormFragment.this.getActivity())
				.dismissProgressDialog();
		JSONObject json = (JSONObject) object;
		HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
	}
}
