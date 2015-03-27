package com.hoyoji.hoyoji.money.currency;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import android.widget.CheckBox;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Currency;
import com.hoyoji.hoyoji.models.Exchange;

public class ExchangeFormFragment extends HyjUserFormFragment {
	private final static int GET_LOCAL_CURRENCY_ID = 1;
	private final static int GET_FOREIGN_CURRENCY_ID = 2;

	private HyjModelEditor mExchangeEditor = null;
	private HyjSelectorField mSelectorFieldLocalCurrency = null;
	private HyjSelectorField mSelectorFieldForeignCurrency = null;
	private HyjNumericField mNumericFieldRate = null;
	private CheckBox mCheckBoxAutoUpdate = null;
	private ImageView mImageViewRefreshRate = null;

	@Override
	public Integer useContentView() {
		return R.layout.exchange_formfragment_exchange;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Exchange exchange;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			exchange = new Select().from(Exchange.class)
					.where("_id=?", modelId).executeSingle();
		} else {
			exchange = new Exchange();
			
			String foreignCurrencyId = intent.getStringExtra("foreignCurrencyId");
//			String localCurrencyId = intent.getStringExtra("localCurrencyId");
			exchange.setForeignCurrencyId(foreignCurrencyId);
//			exchange.setLocalCurrencyId(localCurrencyId);
		}
		mExchangeEditor = exchange.newModelEditor();

		mSelectorFieldLocalCurrency = (HyjSelectorField) getView().findViewById(
				R.id.exchangeFormFragment_editText_localCurrency);
		Currency localCurrency = exchange.getLocalCurrency();
		if (localCurrency != null) {
			mSelectorFieldLocalCurrency.setText(localCurrency.getName());
			mSelectorFieldLocalCurrency.setModelId(exchange.getLocalCurrencyId());
		}
		mSelectorFieldLocalCurrency.setEnabled(false);
		mSelectorFieldLocalCurrency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExchangeFormFragment.this
						.openActivityWithFragmentForResult(
								CurrencyListFragment.class,
								R.string.exchangeListFragment_title_select_local_currency,
								null, GET_LOCAL_CURRENCY_ID);
			}
		});
		
		mSelectorFieldForeignCurrency = (HyjSelectorField) getView().findViewById(
				R.id.exchangeFormFragment_editText_foreignCurrency);
		Currency foreignCurrency = exchange.getForeignCurrency();
		if (foreignCurrency != null) {
			mSelectorFieldForeignCurrency.setText(foreignCurrency.getName());
			mSelectorFieldForeignCurrency
					.setModelId(exchange.getForeignCurrencyId());
		}
		mSelectorFieldForeignCurrency.setEnabled(modelId == -1 && mSelectorFieldForeignCurrency.getText().length() == 0);
		mSelectorFieldForeignCurrency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExchangeFormFragment.this
						.openActivityWithFragmentForResult(
								CurrencyListFragment.class,
								R.string.exchangeListFragment_title_select_foreign_currency,
								null, GET_FOREIGN_CURRENCY_ID);
			}
		});

		mNumericFieldRate = (HyjNumericField) getView().findViewById(
				R.id.exchangeFormFragment_editText_rate);
		mNumericFieldRate.setNumber(exchange.getRate());

		mCheckBoxAutoUpdate = (CheckBox) getView().findViewById(
				R.id.exchangeFormFragment_checkBox_autoUpdate);
		mCheckBoxAutoUpdate.setChecked(exchange.getAutoUpdate());
		mCheckBoxAutoUpdate.setVisibility(View.GONE);
		getView().findViewById(R.id.exchangeFormFragment_checkBox_hint_autoUpdate).setVisibility(View.GONE);
		getView().findViewById(R.id.exchangeFormFragment_separatorField_autoUpdate).setVisibility(View.GONE);
		
		setupRefreshRateButton();
		
		// 只在无网络下新增圈子和账户时手动新增汇率时才自动打开软键盘， 修改时不自动打开
		if (modelId == -1 && mSelectorFieldForeignCurrency.getModelId() != null) {
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	private void setupRefreshRateButton(){
		mImageViewRefreshRate = (ImageView) getView().findViewById(
				R.id.exchangeFormFragment_imageView_refresh_rate);
		mImageViewRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String fromCurrency = mSelectorFieldLocalCurrency.getModelId();
				String toCurrency = mSelectorFieldForeignCurrency.getModelId();
				if(fromCurrency != null && toCurrency != null){
					HyjUtil.startRoateView(mImageViewRefreshRate);
					mImageViewRefreshRate.setEnabled(false);
					HyjUtil.updateExchangeRate(fromCurrency, toCurrency, mImageViewRefreshRate, mNumericFieldRate);
					
				} else {
					HyjUtil.displayToast(R.string.exchangeFormFragment_toast_select_currency);
				}
			}
		});
	}
	
	 private void fillData(){
		 Exchange modelCopy = (Exchange) mExchangeEditor.getModelCopy();
		 modelCopy.setAutoUpdate(this.mCheckBoxAutoUpdate.isChecked());
		 modelCopy.setLocalCurrencyId(mSelectorFieldLocalCurrency.getModelId());
		 modelCopy.setForeignCurrencyId(mSelectorFieldForeignCurrency.getModelId());
		 modelCopy.setRate(mNumericFieldRate.getNumber());
	 }

	 private void showValidatioErrors(){
		 HyjUtil.displayToast(R.string.app_validation_error);
		 mSelectorFieldLocalCurrency.setError(mExchangeEditor.getValidationError("localCurrency"));
		 mSelectorFieldForeignCurrency.setError(mExchangeEditor.getValidationError("foreignCurrency"));
		 mNumericFieldRate.setError(mExchangeEditor.getValidationError("rate"));
	 }

	 private boolean checkLocalExchange(){
		 Exchange exchange = new Select().from(Exchange.class).where("localCurrencyId=? AND foreignCurrencyId=?",((Exchange) mExchangeEditor.getModelCopy()).getLocalCurrencyId(),((Exchange) mExchangeEditor.getModelCopy()).getForeignCurrencyId()).executeSingle();
		 if(exchange != null){
			 return true;
		 }
		 return false;
	 }
	 
	@Override
	public void onSave(View v) {
		super.onSave(v);

		 fillData();

		 mExchangeEditor.validate();

		 if(mExchangeEditor.hasValidationErrors()){
			 showValidatioErrors();
		 } else if(mExchangeEditor.getModelCopy().get_mId() == null && checkLocalExchange()){
			 HyjUtil.displayToast(R.string.exchangeFormFragment_saveError_exchange_exist);
		 }else {
			mExchangeEditor.save();
			HyjUtil.displayToast(R.string.app_save_success);

			if(getActivity().getCallingActivity() != null){
				getActivity().setResult(Activity.RESULT_OK, null);
			}
			getActivity().finish();
		 }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_LOCAL_CURRENCY_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				Currency localCurrency = Currency.load(Currency.class, _id);
				mSelectorFieldLocalCurrency.setText(localCurrency.getName());
				mSelectorFieldLocalCurrency.setModelId(localCurrency.getId());
			}
			break;
		case GET_FOREIGN_CURRENCY_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				Currency foreignCurrency = Currency.load(Currency.class, _id);
				mSelectorFieldForeignCurrency.setText(foreignCurrency.getName());
				mSelectorFieldForeignCurrency.setModelId(foreignCurrency.getId());
			}
			break;
		}
	}
}
