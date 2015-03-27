package com.hoyoji.hoyoji.money.currency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Currency;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.UserData;

public class CurrencyFormFragment extends HyjUserFormFragment {

	private HyjModelEditor mCurrencyEditor = null;
	private HyjTextField mTextFieldName = null;
	private HyjTextField mTextFieldSymbol = null;
	private HyjTextField mTextFieldCode = null;

	@Override
	public Integer useContentView() {
		return R.layout.currency_formfragment_currency;
	}

	@Override
	public Integer useOptionsMenuView() {
		return null;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Currency currency;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			currency = new Select().from(Currency.class)
					.where("_id=?", modelId).executeSingle();
		} else {
			currency = new Currency();
		}
		mCurrencyEditor = currency.newModelEditor();

		mTextFieldName = (HyjTextField) getView().findViewById(
				R.id.currencyFormFragment_textField_name);
		mTextFieldName.setText(currency.getName());
		mTextFieldName.setEnabled(modelId == -1);

		mTextFieldSymbol = (HyjTextField) getView().findViewById(
				R.id.currencyFormFragment_textField_symbol);
		mTextFieldSymbol.setText(currency.getSymbol());
		mTextFieldSymbol.setEnabled(modelId == -1);

		mTextFieldCode = (HyjTextField) getView().findViewById(
				R.id.currencyFormFragment_textField_code);
		mTextFieldCode.setText(currency.getCode());
		mTextFieldCode.setEnabled(modelId == -1);

		Button setAsLocalCurrency = (Button) getView().findViewById(
				R.id.currencyFormFragment_button_setAsLocalCurrency);
		if (currency.getId().equalsIgnoreCase(
				HyjApplication.getInstance().getCurrentUser().getUserData()
						.getActiveCurrencyId())) {
			setAsLocalCurrency.setClickable(false);
			setAsLocalCurrency.setTextColor(Color.GRAY);
		}
		setAsLocalCurrency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setLocalCurrency();
			}
		});

		// if(modelId == -1){
		// getView().findViewById(R.id.button_save).setVisibility(View.GONE);
		// }

	}

	protected void setLocalCurrency() {
		final String currentCurrencyId = mCurrencyEditor.getModelCopy().getId();

			((HyjActivity)CurrencyFormFragment.this.getActivity()).displayProgressDialog(R.string.currencyFormFragment_addShare_fetch_exchange, R.string.currencyFormFragment_addShare_fetching_exchange);
			if (!currentCurrencyId.equalsIgnoreCase(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId())) {
				
					List<Currency> currencies = HyjApplication.getInstance().getCurrentUser().getCurrencies();
					final List<String> toCurrencyList = new ArrayList<String>();
					for (Iterator<Currency> it = currencies.iterator(); it.hasNext();) {
						final Currency currency = it.next();
						if (!currency.getId().equalsIgnoreCase(currentCurrencyId) && Exchange.getExchangeRate(currency.getId(),currentCurrencyId) == null) {
							// 尝试到网上获取汇率
							toCurrencyList.add(currency.getId());
						}
					}
					List<String> fromCurrencyList = new ArrayList<String>();
					fromCurrencyList.add(currentCurrencyId);

					HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
						// 到网上获取汇率成功，新建汇率然后保存
						((HyjActivity)CurrencyFormFragment.this.getActivity()).dismissProgressDialog();
						List<Double> exchangeRates = (List<Double>) object;

						try {
							ActiveAndroid.beginTransaction();
							for(int i=0; i<exchangeRates.size(); i++){
								Double exchangeRate = (Double) exchangeRates.get(i);
								Exchange newExchange = new Exchange();
								newExchange.setForeignCurrencyId(toCurrencyList.get(i));
								newExchange.setLocalCurrencyId(currentCurrencyId);
								newExchange.setRate(exchangeRate);
								newExchange.save();
							}

							HyjModelEditor<UserData> userDataEditor = HyjApplication.getInstance().getCurrentUser().getUserData().newModelEditor();
							userDataEditor.getModelCopy().setActiveCurrencyId(currentCurrencyId);
							userDataEditor.save();
							ActiveAndroid.setTransactionSuccessful();
							getActivity().finish();
							
						} catch (Exception e) {
							HyjUtil.displayToast(e.getMessage());
						} finally {
							ActiveAndroid.endTransaction();
						}
					}

					@Override
					public void errorCallback(Object object) {
						
						((HyjActivity)CurrencyFormFragment.this.getActivity()).dismissProgressDialog();
						if (object != null) {
							HyjUtil.displayToast(object.toString());
						} else {
							HyjUtil.displayToast(R.string.currencyFormFragment_addShare_cannot_fetch_exchange);
						}
					}
				};

					
					HttpGetExchangeRateAsyncTask.newInstance(fromCurrencyList,toCurrencyList, serverCallbacks);


	}

	// private void fillData(){
	// Currency modelCopy = (Currency) mCurrencyEditor.getModelCopy();
	// modelCopy.setName(mTextFieldName.getText().toString().trim());
	// modelCopy.setSymbol(mTextFieldSymbol.getText().toString().trim());
	// modelCopy.setCode(mTextFieldCode.getText().toString().trim());
	// }
	//
	// private void showValidatioErrors(){
	// HyjUtil.displayToast(R.string.app_validation_error);
	//
	// mTextFieldName.setError(mCurrencyEditor.getValidationError("name"));
	// mTextFieldSymbol.setError(mCurrencyEditor.getValidationError("symbol"));
	// mTextFieldCode.setError(mCurrencyEditor.getValidationError("code"));
	// }

	// @Override
	// public void onSave(View v){
	// super.onSave(v);
	//
	// if(mCurrencyEditor.getModel().get_mId() != null){
	// return;
	// }
	//
	// fillData();
	//
	// mCurrencyEditor.validate();
	//
	// if(mCurrencyEditor.hasValidationErrors()){
	// showValidatioErrors();
	// } else {
	// mCurrencyEditor.save();
	// HyjUtil.displayToast(R.string.app_save_success);
	// getActivity().finish();
	// }
	// }
	
	
	}
	
	private static class HttpGetExchangeRateAsyncTask extends AsyncTask<List<String>, Integer, Object> {

		HyjAsyncTaskCallbacks mCallbacks = null;
		
		public HttpGetExchangeRateAsyncTask(HyjAsyncTaskCallbacks callbacks) {
			mCallbacks = callbacks;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public static HttpGetExchangeRateAsyncTask newInstance(
				List<String> fromCurrencies, List<String> toCurrencies,
				HyjAsyncTaskCallbacks callbacks) {
			HttpGetExchangeRateAsyncTask newTask = new HttpGetExchangeRateAsyncTask(callbacks);
			if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
				newTask.execute(fromCurrencies, toCurrencies);
			} else {
				newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fromCurrencies, toCurrencies);
			}
			return newTask;
		}

		@Override
		protected Object doInBackground(List<String>... params) {
			if (HyjUtil.hasNetworkConnection()) {
				List<Double> results = new ArrayList<Double>();
				String fromCurrency = params[0].get(0);
				
				for(int i=0; i < params[1].size(); i++){
					String toCurrency = params[1].get(i);
					Object resultObject = doHttpGet(fromCurrency, toCurrency);
					if(resultObject instanceof Double){
						results.add((Double) resultObject);
					} else {
						return resultObject;
					}
				}
				return results;
			
			} else {
				return HyjApplication.getInstance().getString(R.string.server_connection_disconnected);
			}
		}

		public void doPublishProgress(Integer progress) {
			this.publishProgress(progress);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Object result) {
			if(result instanceof String){
				mCallbacks.errorCallback(result);
			} else {
				mCallbacks.finishCallback(result);
			}
		}

		private Object doHttpGet(String fromCurrency, String toCurrency) {
			// 命名空间  
	        String nameSpace = "http://www.webserviceX.NET/";  
	        // 调用的方法名称  
	        String methodName = "ConversionRate";  
	        // EndPoint  
	        String endPoint = "http://www.webserviceX.NET/CurrencyConvertor.asmx";  
	        // SOAP Action  
	        String soapAction = "http://www.webserviceX.NET/ConversionRate";  
	  
	        // 指定WebService的命名空间和调用的方法名  
	        SoapObject rpc = new SoapObject(nameSpace, methodName);  
	  
	        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId  
	        rpc.addProperty("FromCurrency", fromCurrency);  
	        rpc.addProperty("ToCurrency", toCurrency);  
	  
	        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本  
	        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);  
	  
	        envelope.bodyOut = rpc;  
	        // 设置是否调用的是dotNet开发的WebService  
	        envelope.dotNet = true;  
	        // 等价于envelope.bodyOut = rpc;  
	        envelope.setOutputSoapObject(rpc);  
	  
	        HttpTransportSE transport = new HttpTransportSE(endPoint);  
	        try {  
	            // 调用WebService  
	            transport.call(soapAction, envelope);  
	            
	            // 获取返回的数据  
	            SoapObject object = (SoapObject) envelope.bodyIn;  
	            // 获取返回的结果  
	            String result = object.getProperty(0).toString();  
	      
	            return Double.valueOf(result);
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            return null;
	        } 
		}
	}
}
