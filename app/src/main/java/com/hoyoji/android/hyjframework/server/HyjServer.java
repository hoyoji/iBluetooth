package com.hoyoji.android.hyjframework.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.activeandroid.util.Log;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.User;

public class HyjServer {
	
	public static void sendMsg() {
		
	}
	
	public static void searchData() {
		
	}
	
	public static Object doHttpPost(Object asyncTask, String serverUrl, String postData, boolean returnJSONError){
    	User currentUser = HyjApplication.getInstance().getCurrentUser();
		Context appContext = HyjApplication.getInstance().getApplicationContext();

		InputStream is = null;
		String s = null;
		try {
			HttpParams my_httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(my_httpParams, 10000);
			HttpConnectionParams.setSoTimeout(my_httpParams, 40000);
			DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
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
				  
			HttpPost post = new HttpPost(serverUrl);
		
			post.setEntity(new StringEntity(postData, HTTP.UTF_8));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json; charset=UTF-8");
			post.setHeader("Accept-Encoding", "gzip");
			post.setHeader("HyjApp-Version", appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).versionName);
			if (currentUser != null) {
				String auth = URLEncoder.encode(currentUser.getUserName(), "UTF-8") + ":" + URLEncoder.encode(currentUser.getUserData().getPassword(), "UTF-8");
				
				//post.setHeader("Cookie", "authentication=" + Base64.encodeToString(auth.getBytes(), Base64.DEFAULT).replace("\r\n", "").replace("=", "%$09"));
				post.setHeader("Authorization", "BASIC " + Base64.encodeToString(auth.getBytes(), Base64.DEFAULT | Base64.NO_WRAP));
			}
			
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			s = EntityUtils.toString(entity, HTTP.UTF_8); 
			Log.i("Server", s);
		} catch (Exception e) {
			e.printStackTrace();
			if(returnJSONError){
				s = "{'__summary' : {'msg' : '"+HyjApplication.getInstance().getString(R.string.server_connection_error)+":\\n"+e.getLocalizedMessage()+"'}}";
			}			
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception squish) {
			}
		}

		try {
			if(s == null) {
				return null;
			} else if(s.startsWith("{")) {
				return new JSONObject(s);
			} else if(s.startsWith("[")) {
				return new JSONArray(s);
			} else if(s.equals("")) {
				return new JSONArray();
			} else {
				if(returnJSONError) {
					return new JSONObject("{'__summary' : {'msg' : '"+HyjApplication.getInstance().getString(R.string.server_dataparse_error)+"'}}");
				} 
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
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
