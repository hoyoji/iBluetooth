package com.hoyoji.android.hyjframework;

public interface HyjAsyncTaskCallbacksInterface {
	public void errorCallback(Object object);
	public void progressCallback(int progress);
	public void finishCallback(Object object);
	public Object doInBackground(String... string);
}
