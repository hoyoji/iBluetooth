package com.hoyoji.android.hyjframework;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;


public class HyjAsyncTask extends AsyncTask<String, Integer, Object> {

	protected HyjAsyncTaskCallbacks mServerCallback = null; 
	
	public HyjAsyncTask(HyjAsyncTaskCallbacks callbacks) {
		mServerCallback = callbacks;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static HyjAsyncTask newInstance(HyjAsyncTaskCallbacks callbacks, String... params){
		HyjAsyncTask newTask = new HyjAsyncTask(callbacks);
		if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			newTask.execute(params);
		} else {
			newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
		return newTask;
	}
	
	public void doPublishProgress(Integer... progress){
		publishProgress(progress);
	}
	
	@Override
	protected Object doInBackground(String... params) {
		return mServerCallback.doInBackground(params);
	}
	    
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Object object) {
		mServerCallback.errorCallback(object);
		mServerCallback.finishCallback(object);
  }
    
    protected void onProgressUpdate(Integer... progress) {
        if(mServerCallback != null){
        	mServerCallback.progressCallback(progress[0]);
        }
    }
}