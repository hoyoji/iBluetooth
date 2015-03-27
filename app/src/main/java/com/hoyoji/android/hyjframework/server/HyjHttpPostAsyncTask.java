package com.hoyoji.android.hyjframework.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTask;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;


public class HyjHttpPostAsyncTask extends HyjAsyncTask {

	public HyjHttpPostAsyncTask(HyjAsyncTaskCallbacks callbacks) {
		super(callbacks);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static HyjHttpPostAsyncTask newInstance(HyjAsyncTaskCallbacks callbacks, String... params){
		HyjHttpPostAsyncTask newTask = new HyjHttpPostAsyncTask(callbacks);
		if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			newTask.execute(params);
		} else {
			newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
		return newTask;
	}	
	
	@Override
	protected Object doInBackground(String... params) {
		if (HyjUtil.hasNetworkConnection()) {
	    	String target = "post";
		    if(params.length == 2){
		    	target = params[1];
		    }
	        return HyjServer.doHttpPost(this, HyjApplication.getServerUrl()+target+".php", params[0], true);
	    } else {
	    	try {
				return new JSONObject("{'__summary' : {'msg' : '"+HyjApplication.getInstance().getString(R.string.server_connection_disconnected)+"'}}");
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
		return null;
	}
	

    public void doPublishProgress(Integer progress){
    	this.publishProgress(progress);
    }
    
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Object result) {
    	if(mServerCallback != null){
    		if(result != null){
    			if(result instanceof JSONObject){
    				JSONObject jsonResult = (JSONObject)result;
        			if(jsonResult.isNull("__summary")){
        				mServerCallback.finishCallback(result);
        			} else {
        				mServerCallback.errorCallback(result);
        			}		
    			} else {
    				mServerCallback.finishCallback(result);
    			}
            } else {
            	try {
					mServerCallback.errorCallback(new JSONObject("{'__summary' : {'msg' : '"+HyjApplication.getInstance().getString(R.string.server_dataparse_error)+"'}}"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}