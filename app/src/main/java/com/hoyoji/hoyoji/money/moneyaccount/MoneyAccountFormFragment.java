package com.hoyoji.hoyoji.money.moneyaccount;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.HyjWebServiceExchangeRateAsyncTask;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjTextInputFormFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjSpinnerField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.friend.FriendListFragment;
import com.hoyoji.hoyoji.models.Currency;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.money.currency.CurrencyListFragment;
import com.hoyoji.hoyoji.money.currency.ExchangeFormFragment;

public class MoneyAccountFormFragment extends HyjUserFormFragment {
	private final static int GET_CURRENCY_ID = 1;
	private final static int GET_FRIEND_ID = 2;
	private final static int FETCH_PROJECT_TO_LOCAL_EXCHANGE = 3;
	private static final int GET_REMARK = 4;

	private HyjModelEditor<MoneyAccount> mMoneyAccountEditor = null;
	private HyjTextField mTextFieldName = null;
	private HyjSelectorField mSelectorFieldCurrency = null;
	private HyjSelectorField mSelectorFieldFriend = null;
	private HyjNumericField mNumericFieldCurrentBalance = null;
	private HyjSpinnerField mSpinnerFieldAccountType = null;
	private HyjRemarkField mRemarkFieldAccountNumber = null;
	private HyjRemarkField mRemarkFieldBankAddress = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	
	private LinearLayout autoHideLinearLayout = null;
	private TextView autoHideTextView = null;
	private View autoHideView = null;
	private RadioGroup autoHideRadioGroup = null;
	private RadioButton autoRadioButton = null;
	private RadioButton hideRadioButton = null;
	private RadioButton showRadioButton = null;
	

	@Override
	public Integer useContentView() {
		return R.layout.moneyaccount_formfragment_moneyaccount;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		MoneyAccount moneyAccount;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		String intentAccountType = intent.getStringExtra("accountType");
		if (modelId != -1) {
			moneyAccount = new Select().from(MoneyAccount.class).where("_id=?", modelId).executeSingle();
		} else {
			moneyAccount = new MoneyAccount();
			moneyAccount.setLocalFriendId(intent.getStringExtra("friendId"));
			if(intentAccountType != null && intentAccountType.equalsIgnoreCase("Topup")){
				moneyAccount.setAccountType("Topup");
//				if(moneyAccount.getFriendId() != null){
//					List<MoneyAccount> topupAccounts = new Select().from(MoneyAccount.class).where("accountType = ? AND friendId = ? AND ownerUserId = ?", "Topup", friend.getId(), HyjApplication.getInstance().getCurrentUser().getId()).execute();
//					int numberOfCards = topupAccounts.size() + 1;
//					
//					Friend friend = HyjModel.getModel(Friend.class, moneyAccount.getFriendId());
//					moneyAccount.setName(friend.getDisplayName() + "充值卡" + numberOfCards);
//				} 
			}
		}
		mMoneyAccountEditor = moneyAccount.newModelEditor();

		mTextFieldName = (HyjTextField) getView().findViewById(R.id.moneyAccountFormFragment_textField_name);
		mTextFieldName.setText(moneyAccount.getDisplayName());
		mTextFieldName.setEnabled(modelId == -1 || !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));

		Currency currency = moneyAccount.getCurrency();
		mSelectorFieldCurrency = (HyjSelectorField) getView().findViewById(R.id.moneyAccountFormFragment_selectorField_currency);
		mSelectorFieldCurrency.setEnabled(modelId == -1);
		if (currency != null) {
			mSelectorFieldCurrency.setModelId(currency.getId());
			mSelectorFieldCurrency.setText(currency.getName());
		}
		mSelectorFieldCurrency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyAccountFormFragment.this
						.openActivityWithFragmentForResult(
								CurrencyListFragment.class,
								R.string.currencyListFragment_title_select_currency,
								null, GET_CURRENCY_ID);
			}
		});

		mNumericFieldCurrentBalance = (HyjNumericField) getView().findViewById(R.id.moneyAccountFormFragment_textField_currentBalance);
		mNumericFieldCurrentBalance.setNumber(moneyAccount.getCurrentBalance());
		mNumericFieldCurrentBalance.setEnabled(modelId == -1 || !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));

		mSpinnerFieldAccountType = (HyjSpinnerField) getView().findViewById(R.id.moneyAccountFormFragment_textField_accountType);
		if(moneyAccount.getAccountType().equals("Debt")){
			mSpinnerFieldAccountType.setItems(R.array.moneyAccountFormFragment_spinnerField_accountType_array_debt,new String[] {"Debt"});
			
			autoHideLinearLayout = (LinearLayout) getView().findViewById(R.id.moneyAccountFormFragment_linearLayout_autoHide);
			autoHideTextView = (TextView) getView().findViewById(R.id.moneyAccountFormFragment_textView_autoHide);
			autoHideView = (View) getView().findViewById(R.id.field_separator_autoHide);
			autoHideRadioGroup = (RadioGroup) getView().findViewById(R.id.moneyAccountFormFragment_RadioGroup_autoHide);
			autoRadioButton = (RadioButton) getView().findViewById(R.id.moneyAccountFormFragment_RadioButton_auto);
			hideRadioButton = (RadioButton) getView().findViewById(R.id.moneyAccountFormFragment_RadioButton_hide);
			showRadioButton = (RadioButton) getView().findViewById(R.id.moneyAccountFormFragment_RadioButton_show);
			
			autoHideLinearLayout.setVisibility(View.VISIBLE);
			autoHideView.setVisibility(View.VISIBLE);
			
			if(moneyAccount.getAutoHide() == null || moneyAccount.getAutoHide().equals("") || moneyAccount.getAutoHide().equals("Show")) {
				showRadioButton.setChecked(true);
			} else if(moneyAccount.getAutoHide().equals("Hide")){
				hideRadioButton.setChecked(true);
			} else {
				autoRadioButton.setChecked(true);
			}
	         //绑定一个匿名监听器
//			autoHideRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//	             
//	             @Override
//	             public void onCheckedChanged(RadioGroup arg0, int arg1) {
//	                 // TODO Auto-generated method stub
//	                 //获取变更后的选中项的ID
//	                 int radioButtonId = arg0.getCheckedRadioButtonId();
//	                 //根据ID获取RadioButton的实例
//	                 RadioButton rb = (RadioButton)getView().findViewById(radioButtonId);
//	                 if (rb.getText().equals("显示")){
//	                	 autoRadioButtonString = "Show";
//	                 } else if(rb.getText().equals("隐藏")){
//	                	 autoRadioButtonString = "Hide";
//	                 } else{
//	                	 autoRadioButtonString = "Auto";
//	                 }
//	                 rb.setChecked(true);
//	             }
//	         });
		} else {
			mSpinnerFieldAccountType.setItems(R.array.moneyAccountFormFragment_spinnerField_accountType_array,new String[] { "Cash", "Deposit", "Topup", "Credit", "Online"});
		}
		mSpinnerFieldAccountType.setSelectedValue(moneyAccount.getAccountType());
		
		mSpinnerFieldAccountType.setEnabled(modelId == -1|| !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));
		mSpinnerFieldAccountType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				Friend friend = null;
				if(mSelectorFieldFriend.getModelId() != null){
					friend = Friend.getModel(Friend.class, mSelectorFieldFriend.getModelId());
				}
				setupFriendField(friend);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		

		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyAccountFormFragment_selectorField_friend);
		mSelectorFieldFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyAccountFormFragment.this
						.openActivityWithFragmentForResult(
								FriendListFragment.class,
								R.string.moneyAccountFormFragment_editText_hint_friend,
								null, GET_FRIEND_ID);
			}
		});		
		
		Friend friend = moneyAccount.getLocalFriend();
		setupFriendField(friend);
		
		// 当创建的类型就指定为Topup时（从充值卡账户列表打开），商家和类型不让修改
		if(intentAccountType != null && intentAccountType.equalsIgnoreCase("Topup")){
			mSelectorFieldFriend.setEnabled(false);
			mSpinnerFieldAccountType.setEnabled(false);
		}
		
		mRemarkFieldAccountNumber = (HyjRemarkField) getView().findViewById(R.id.moneyAccountFormFragment_textField_accountNumber);
		mRemarkFieldAccountNumber.setText(moneyAccount.getAccountNumber());
		mRemarkFieldAccountNumber.setEnabled(modelId == -1 || !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));

		mRemarkFieldBankAddress = (HyjRemarkField) getView().findViewById(R.id.moneyAccountFormFragment_textField_bankAddress);
		mRemarkFieldBankAddress.setText(moneyAccount.getBankAddress());
		mRemarkFieldBankAddress.setEnabled(modelId == -1 || !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));

		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyAccountFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyAccount.getRemark());
		mRemarkFieldRemark.setEnabled(modelId == -1 || !moneyAccount.getAccountType().equalsIgnoreCase("Debt"));
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyAccountFormFragment.this.openActivityWithFragmentForResult(
								HyjTextInputFormFragment.class,
								R.string.moneyExpenseFormFragment_textView_remark,
								bundle, GET_REMARK);
			}
		});
		
		if (modelId == -1){
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}else{
//			if(moneyAccount.getAccountType().equalsIgnoreCase("Debt") && this.mOptionsMenu != null){
//				setSaveActionEnable(false);
//			}
		}
	}
//	String mPreviousName = "";
	private void setupFriendField(Friend friend) {
		if(mSpinnerFieldAccountType.getSelectedValue() != null && mSpinnerFieldAccountType.getSelectedValue().equalsIgnoreCase("Topup")){
			mSelectorFieldFriend.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.field_separator_friend).setVisibility(View.VISIBLE);

			if (friend != null) {
				mSelectorFieldFriend.setModelId(friend.getId());
				mSelectorFieldFriend.setText(friend.getDisplayName());
				if(mTextFieldName.getText().trim().length() == 0){
					List<MoneyAccount> topupAccounts = new Select().from(MoneyAccount.class).where("accountType = ? AND localFriendId = ? AND ownerUserId = ?", "Topup", friend.getId(), HyjApplication.getInstance().getCurrentUser().getId()).execute();
					int numberOfCards = topupAccounts.size() + 1;
					mTextFieldName.setText(friend.getDisplayName() + "充值卡" + numberOfCards);
				}
			}
		} else {
			mSelectorFieldFriend.setVisibility(View.GONE);
			getView().findViewById(R.id.field_separator_friend).setVisibility(View.GONE);

			mSelectorFieldFriend.setModelId(null);
			mSelectorFieldFriend.setText(null);
//			mTextFieldName.setText(mPreviousName);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
//	    if(mMoneyAccountEditor!= null && mMoneyAccountEditor.getModel().get_mId() != null && mMoneyAccountEditor.getModel().getAccountType().equalsIgnoreCase("Debt")){
//	    	setSaveActionEnable(false);
//	    }
	}
	
	
	private void fillData() {
		MoneyAccount modelCopy = (MoneyAccount) mMoneyAccountEditor.getModelCopy();
		if(!"Debt".equals(modelCopy.getAccountType())){
			modelCopy.setName(mTextFieldName.getText().toString().trim());
			modelCopy.setLocalFriendId(mSelectorFieldFriend.getModelId());
			modelCopy.setCurrencyId(mSelectorFieldCurrency.getModelId());
			modelCopy.setCurrentBalance(mNumericFieldCurrentBalance.getNumber());
			modelCopy.setAccountType(mSpinnerFieldAccountType.getSelectedValue());
			modelCopy.setAccountNumber(mRemarkFieldAccountNumber.getText().toString().trim());
			modelCopy.setBankAddress(mRemarkFieldBankAddress.getText().toString().trim());
			modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
		} else {
			if(autoHideRadioGroup.getCheckedRadioButtonId() == R.id.moneyAccountFormFragment_RadioButton_auto){
				modelCopy.setAutoHide("Auto");
//				autoHideRadioGroup.check(R.id.moneyAccountFormFragment_RadioButton_auto);
			} else if(autoHideRadioGroup.getCheckedRadioButtonId() == R.id.moneyAccountFormFragment_RadioButton_show){
				modelCopy.setAutoHide("Show");
			} else if(autoHideRadioGroup.getCheckedRadioButtonId() == R.id.moneyAccountFormFragment_RadioButton_hide){
				modelCopy.setAutoHide("Hide");
			}
		}

	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);

		mTextFieldName.setError(mMoneyAccountEditor.getValidationError("name"));
		mSelectorFieldCurrency.setError(mMoneyAccountEditor.getValidationError("currency"));
		mNumericFieldCurrentBalance.setError(mMoneyAccountEditor.getValidationError("currentBalance"));
		mSpinnerFieldAccountType.setError(mMoneyAccountEditor.getValidationError("accountType"));
		mSelectorFieldFriend.setError(mMoneyAccountEditor.getValidationError("friend"));
		mRemarkFieldAccountNumber.setError(mMoneyAccountEditor.getValidationError("accountNumber"));
		mRemarkFieldBankAddress.setError(mMoneyAccountEditor.getValidationError("bankAddress"));
		mRemarkFieldRemark.setError(mMoneyAccountEditor.getValidationError("remark"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);

		fillData();

		mMoneyAccountEditor.validate();
				
		if(mMoneyAccountEditor.getModelCopy().getAccountType().equalsIgnoreCase("Topup")){
			if(mMoneyAccountEditor.getModelCopy().getLocalFriendId() == null){
				mMoneyAccountEditor.setValidationError("friend", R.string.moneyAccountFormFragment_editText_hint_friend);
			} else {
				mMoneyAccountEditor.removeValidationError("friend");
			}
		} else {
			mMoneyAccountEditor.removeValidationError("friend");
		}
		
		if (mMoneyAccountEditor.hasValidationErrors()) {
			showValidatioErrors();
		} else {
			// 检查汇率存不存在
			final String moneyAccountCurrencyId = mMoneyAccountEditor.getModelCopy()
					.getCurrencyId();
			if (moneyAccountCurrencyId.equalsIgnoreCase(HyjApplication.getInstance()
					.getCurrentUser().getUserData().getActiveCurrencyId())) {
				// 币种是一样的，不用新增汇率
				doSave();
			} else {
				Double rate = Exchange.getExchangeRate(moneyAccountCurrencyId, HyjApplication.getInstance()
					.getCurrentUser().getUserData().getActiveCurrencyId());
				if (rate != null) {
					// 汇率已经存在，直接保存新圈子
					doSave();
					return;
				}
				// 尝试到网上获取汇率
				((HyjActivity) MoneyAccountFormFragment.this.getActivity()).displayProgressDialog(
						R.string.moneyAccountFormFragment_addShare_fetch_exchange,
						R.string.moneyAccountFormFragment_addShare_fetching_exchange);
				HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
						// 到网上获取汇率成功，新建汇率然后保存
						((HyjActivity) MoneyAccountFormFragment.this
								.getActivity()).dismissProgressDialog();
						Double exchangeRate = (Double) object;
						Exchange newExchange = new Exchange();
						newExchange.setForeignCurrencyId(moneyAccountCurrencyId);
						newExchange.setLocalCurrencyId(HyjApplication
								.getInstance().getCurrentUser().getUserData()
								.getActiveCurrencyId());
						newExchange.setRate(exchangeRate);
						newExchange.save();
						doSave();
					}

					@Override
					public void errorCallback(Object object) {
						((HyjActivity) MoneyAccountFormFragment.this
								.getActivity()).dismissProgressDialog();
						if (object != null) {
							HyjUtil.displayToast(object.toString());
						} else {
							HyjUtil.displayToast(R.string.moneyAccountFormFragment_addShare_cannot_fetch_exchange);
						}

						// 到网上获取汇率失败，问用户是否要手工添加该汇率
						((HyjActivity) MoneyAccountFormFragment.this
								.getActivity())
								.displayDialog(
										-1,
										R.string.moneyAccountFormFragment_addShare_cannot_fetch_exchange,
										R.string.alert_dialog_yes,
										R.string.alert_dialog_no, -1,
										new DialogCallbackListener() {
											@Override
											public void doPositiveClick(
													Object object) {
												Bundle bundle = new Bundle();
												bundle.putString(
														"localCurrencyId",
														HyjApplication
																.getInstance()
																.getCurrentUser()
																.getUserData()
																.getActiveCurrencyId());
												bundle.putString(
														"foreignCurrencyId",
														moneyAccountCurrencyId);
												openActivityWithFragmentForResult(
														ExchangeFormFragment.class,
														R.string.exchangeFormFragment_title_addnew,
														bundle,
														FETCH_PROJECT_TO_LOCAL_EXCHANGE);
											}

											@Override
											public void doNegativeClick() {
												HyjUtil.displayToast("未能获取账户币种到本币的汇率");
											}

										});
					}
				};

				HyjWebServiceExchangeRateAsyncTask.newInstance(HyjApplication.getInstance().getCurrentUser()
						.getUserData().getActiveCurrencyId(),
						moneyAccountCurrencyId,
						serverCallbacks);
			}
		}
	}

	protected void doSave() {
		Double changeAmount = mMoneyAccountEditor.getModelCopy().getCurrentBalance0() - mMoneyAccountEditor.getModel().getCurrentBalance0();
		if(mMoneyAccountEditor.getModelCopy().get_mId() != null && changeAmount != 0){
			MoneyTransfer newMoneyTransfer = new MoneyTransfer();
			if(changeAmount > 0){
				newMoneyTransfer.setTransferOutAmount(changeAmount);
				newMoneyTransfer.setTransferOutId(null);
				newMoneyTransfer.setTransferInAmount(changeAmount);
				newMoneyTransfer.setTransferInId(mMoneyAccountEditor.getModelCopy().getId());
				newMoneyTransfer.setTransferInExchangeRate(1/transferExchangeRate(newMoneyTransfer.getProject().getCurrencyId(),mMoneyAccountEditor.getModelCopy().getCurrencyId()));
			}else{
				newMoneyTransfer.setTransferOutAmount(-changeAmount);
				newMoneyTransfer.setTransferOutId(mMoneyAccountEditor.getModelCopy().getId());
				newMoneyTransfer.setTransferOutExchangeRate(transferExchangeRate(mMoneyAccountEditor.getModelCopy().getCurrencyId(),newMoneyTransfer.getProject().getCurrencyId()));
				newMoneyTransfer.setTransferInAmount(-changeAmount);
				newMoneyTransfer.setTransferInId(null);
			}
			Project project = new Select("MIN(_id) AS _id, *").from(Project.class).where("ownerUserId = ?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			newMoneyTransfer.setProjectCurrencyId(project.getCurrencyId());
			newMoneyTransfer.setProjectId(project.getId());
			newMoneyTransfer.setDate((new Date()).getTime());
			newMoneyTransfer.setTransferOutFriendUserId(null);
			newMoneyTransfer.setTransferInFriendUserId(null);
			newMoneyTransfer.setRemark("修改账户金额");
			newMoneyTransfer.save();
		}
		
		mMoneyAccountEditor.save();
		HyjUtil.displayToast(R.string.app_save_success);
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", mMoneyAccountEditor.getModel().get_mId());
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			getActivity().finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_FRIEND_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				Friend friend = Friend.load(Friend.class, _id);
				setupFriendField(friend);
			}
			break;

		case GET_CURRENCY_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				Currency currency = Currency.load(Currency.class, _id);
				mSelectorFieldCurrency.setText(currency.getName());
				mSelectorFieldCurrency.setModelId(currency.getId());
			}
			break;

		case GET_REMARK:
			if (resultCode == Activity.RESULT_OK) {
				String text = data.getStringExtra("TEXT");
				mRemarkFieldRemark.setText(text);
			}
			break;
		case FETCH_PROJECT_TO_LOCAL_EXCHANGE:
			if (resultCode == Activity.RESULT_OK) {
				// 检查该汇率是否添加成功，如果是保存
				Exchange exchange = new Select()
						.from(Exchange.class)
						.where("foreignCurrencyId=? AND localCurrencyId=?",
								mMoneyAccountEditor.getModelCopy()
										.getCurrencyId(),
								HyjApplication.getInstance().getCurrentUser()
										.getUserData().getActiveCurrencyId())
						.executeSingle();
				if (exchange != null) {
					doSave();
				} else {
					HyjUtil.displayToast("未能获取账户币种到本币的汇率");
				}
			}
			break;

		}
	}
	
	public Double transferExchangeRate (String transferInCurrencyId , String transferOutCurrencyId) {
		String activityCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		Double transferInActivityRate; 
		if (transferInCurrencyId.equals(activityCurrencyId)) {
			transferInActivityRate = 1.00;
		} else {
			transferInActivityRate = Exchange.getExchangeRate(transferInCurrencyId, activityCurrencyId);
		}
		Double activityTransferOutRate;
		
		if (activityCurrencyId.equals(transferOutCurrencyId)) {
			activityTransferOutRate = 1.00;
		} else {
			activityTransferOutRate = Exchange.getExchangeRate(activityCurrencyId, transferOutCurrencyId);
		}
		return transferInActivityRate * activityTransferOutRate;
	}
}
