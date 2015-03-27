package com.hoyoji.android.hyjframework;

import org.ksoap2.SoapEnvelope;  
import org.ksoap2.serialization.SoapObject;  
import org.ksoap2.serialization.SoapSerializationEnvelope;  
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.hoyoji.btcontrol.R;

public class HyjWebServiceExchangeRateAsyncTask extends HyjAsyncTask {

	public HyjWebServiceExchangeRateAsyncTask(HyjAsyncTaskCallbacks callbacks) {
		super(callbacks);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static HyjWebServiceExchangeRateAsyncTask newInstance(
			String fromCurrency, String toCurrency,
			HyjAsyncTaskCallbacks callbacks) {
		HyjWebServiceExchangeRateAsyncTask newTask = new HyjWebServiceExchangeRateAsyncTask(callbacks);
		if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			newTask.execute(fromCurrency, toCurrency);
		} else {
			newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fromCurrency, toCurrency);
		}
		return newTask;
	}

	@Override
	protected Object doInBackground(String... params) {
		if (HyjUtil.hasNetworkConnection()) {
			return doHttpGet(params[0], params[1]);
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
		if (result instanceof Double) {
			mServerCallback.finishCallback(result);
		} else {
			mServerCallback.errorCallback(result);
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