package com.hoyoji.android.hyjframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.hoyoji.btcontrol.R;

public class HyjHttpGetExchangeRateAsyncTask extends HyjAsyncTask {

	public HyjHttpGetExchangeRateAsyncTask(HyjAsyncTaskCallbacks callbacks) {
		super(callbacks);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static HyjHttpGetExchangeRateAsyncTask newInstance(
			String fromCurrency, String toCurrency,
			HyjAsyncTaskCallbacks callbacks) {
		HyjHttpGetExchangeRateAsyncTask newTask = new HyjHttpGetExchangeRateAsyncTask(
				callbacks);
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
			String target = "https://www.google.com/finance/converter?a=1&from=" + params[0] + "&to=" + params[1];
			return doHttpGet(target);
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

	private Object doHttpGet(String serverUrl) {
		InputStream is = null;
		String s = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();

			client.addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(HttpResponse response, HttpContext context)
						throws HttpException, IOException {
					// Inflate any responses compressed with gzip
				    final HttpEntity entity = response.getEntity();
				    final Header encoding = entity.getContentEncoding();
				    if (encoding != null) {
				      for (HeaderElement element : encoding.getElements()) {
				        if (element.getName().equalsIgnoreCase("gzip")) {
				          response.setEntity(new InflatingEntity(response.getEntity()));
				          break;
				        }
				      }
				    }
					
				}
			});
			HttpGet get = new HttpGet(serverUrl);

			get.setHeader("Accept", "application/json");
			get.setHeader("Content-type", "application/json; charset=UTF-8");
			get.setHeader("Accept-Encoding", "gzip");

			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			s = EntityUtils.toString(entity, HTTP.UTF_8); 
		} catch (Exception e) {
			e.printStackTrace();
			return HyjApplication.getInstance().getString(
					R.string.server_connection_error)
					+ ":\\n" + e.getLocalizedMessage();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception squish) {
			}
		}

		if (s != null) {
			try {
				String[] tokens = s.split("<span class=bld>");
				tokens = tokens[1].split("</span>");
				s = tokens[0];
				Pattern p = Pattern.compile("([^\\s]+).+");
				Matcher m = p.matcher(s);
				if (m.find()) {
					//DecimalFormat df = new DecimalFormat("0.####");
					// double d = Double.parseDouble(m.group(1));
					return Double.valueOf(m.group(1));
				}
			} catch(Exception e) {
				return null;
			}
			
//			boolean errorMatch = s.matches(".+,error: \"(\\d)\".+");
//			if (errorMatch) {
//				return null;
//			} else {
//				Pattern p = Pattern.compile(".+,rhs: \"([^\\s]+).+");
//				Matcher m = p.matcher(s);
//
//				if (m.find()) {
//					DecimalFormat df = new DecimalFormat("0.####");
//					// double d = Double.parseDouble(m.group(1));
//					return Double.valueOf(df.format(m.group(1)));
//				}
//				return null;
//			}
		}
		return null;
	}
	private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}