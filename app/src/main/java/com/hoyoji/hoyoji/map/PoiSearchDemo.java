package com.hoyoji.hoyoji.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.hoyoji.btcontrol.R;
import com.hoyoji.btcontrol.R.color;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;

/**
 * 演示poi搜索功能
 */
public class PoiSearchDemo extends HyjUserFragment implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener, OnGetGeoCoderResultListener {

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private BaiduMap mBaiduMap = null;
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;
	
	private LatLng SHEN_ZHEN = new LatLng(22.560, 114.064);
	private LatLng THIS_POINT = null;
	private LatLng SELECT_POINT = null;
	private Marker marker = null;
	private Marker thisMarker = null;
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private String mAddress;
	
	public GeofenceClient mGeofenceClient;
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	
	private GeoCoder mSearch = null;
	
	private Button mSearchButton;
	
	private Button mSaveAddressButton;
	
	private BitmapDescriptor bitmap;
	
	private InfoWindow mInfoWindow;
	
	private boolean isOwnerProject;
	
	private EditText editCity;
	private EditText editSearchKey;
	
	private boolean isFirstOpen = true;
	
	@Override
	public Integer useContentView() {
		SDKInitializer.initialize(getActivity().getApplication());
		return R.layout.activity_poisearch;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
//		SDKInitializer.initialize(getApplication());
//		setContentView(R.layout.activity_poisearch);
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		
		Intent intent = getActivity().getIntent();
		double latitude = intent.getDoubleExtra("LATITUDE", -1);
		double longitude = intent.getDoubleExtra("LONGITUDE", -1);
		mAddress = intent.getStringExtra("ADDRESS");
		isOwnerProject = intent.getBooleanExtra("ISOWNERPROJECT", false);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		editCity = (EditText) getView().findViewById(R.id.city);
		editSearchKey = (EditText) getView().findViewById(R.id.searchkey);
		
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) getView().findViewById(R.id.searchkey);
		sugAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line);
		keyWorldsView.setAdapter(sugAdapter);
		mBaiduMap = ((SupportMapFragment) (getActivity().getSupportFragmentManager().findFragmentById(R.id.map))).getBaiduMap();
		
		setLocation();
		
		if(latitude != -1 && longitude != -1){
			mLatitude = latitude;
			mLongitude = longitude;
			SELECT_POINT = new LatLng(mLatitude, mLongitude);
			reverseGeoCodeOption(SELECT_POINT);
			
			MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(SELECT_POINT);
			mBaiduMap.setMapStatus(u4);
		} else if(mAddress != null &&!"".equals(mAddress)) {
			geoCodeOption(editCity.getText().toString(), mAddress);
			editSearchKey.setText(mAddress);
		}

		
		
		if(SELECT_POINT != null) {
			if(marker != null){
        		marker.remove();
        	}
    		//准备 marker 的图片  
//    		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
    		//准备 marker option 添加 marker 使用  
    		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(SELECT_POINT);  
    		//获取添加的 marker 这样便于后续的操作  
    		marker = (Marker) mBaiduMap.addOverlay(markerOptions);  
		}

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
            	mapPoi.getName(); //名称
            	mapPoi.getPosition(); //坐标 
                return false;
            }

            @Override
            public void onMapClick(LatLng point) {
            	if(isOwnerProject == true){
	            	mLatitude = point.latitude;
	            	mLongitude = point.longitude;
	            	
	            	MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(point);
	            	mBaiduMap.setMapStatus(u4); 
	            	if(marker != null){
	            		marker.remove();
	            	}
	            	LatLng latLng = mBaiduMap.getMapStatus().target;  
	            	
	            	reverseGeoCodeOption(latLng);
	        		//准备 marker 的图片  
	//        		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	        		//准备 marker option 添加 marker 使用  
	        		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(latLng);  
	        		//获取添加的 marker 这样便于后续的操作  
	        		marker = (Marker) mBaiduMap.addOverlay(markerOptions);  
	        		
            	}
            }
		});
		
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				String city = ((EditText) getView().findViewById(R.id.city)).getText()
						.toString();
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
		});
		
		
		mSearchButton = (Button) getView().findViewById(R.id.search);
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchButtonProcess(null);
			}
		});
		
		mSaveAddressButton = (Button) getView().findViewById(R.id.map_get_address);
		mSaveAddressButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAddress();
			}
		});

		if(isOwnerProject == true){
			LinearLayout cityLayout = (LinearLayout) getView().findViewById(R.id.map_linerLayout_city);
			LinearLayout buttonLayout = (LinearLayout) getView().findViewById(R.id.map_linerLayout_button);
			cityLayout.setVisibility(View.VISIBLE);
			buttonLayout.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    
	}
	
	@Override
	public Integer useOptionsMenuView(){
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//	}

	/**
	 * 影响搜索按钮点击事件
	 * 
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(editCity.getText().toString())
				.keyword(editSearchKey.getText().toString())
				.pageNum(load_Index));
	}

	public void goToNextPage(View v) {
		load_Index++;
		searchButtonProcess(null);
	}
	
	public void saveAddress() {
		 Intent intent = new Intent();
		 intent.putExtra("LATITUDE", mLatitude);
		 intent.putExtra("LONGITUDE", mLongitude);
		 intent.putExtra("ADDRESS", mAddress);
		 getActivity().setResult(Activity.RESULT_OK, intent);
		 
		 getActivity().finish();
	}

	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			HyjUtil.displayToast("未找到结果");
//			Toast.makeText(PoiSearchDemo.this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			HyjUtil.displayToast(strInfo);
//			Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			HyjUtil.displayToast("抱歉，未找到结果");
//			Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Button button = new Button(getActivity().getApplicationContext());
			button.setBackgroundResource(R.drawable.popup);
			button.setText("选择地址");
			button.setTextColor(color.black);
			final PoiDetailResult tResule = result;
			final Button tButton = button;
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					marker.setIcon(bitmap);
//					mBaiduMap.hideInfoWindow();
					mLatitude = tResule.getLocation().latitude;
	            	mLongitude = tResule.getLocation().longitude;
	            	mAddress = tResule.getAddress();
	            	
	            	if(marker != null){
	            		marker.remove();
	            	}
	            	
	        		//准备 marker 的图片  
//	        		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	        		//准备 marker option 添加 marker 使用  
	        		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(tResule.getLocation());  
	        		//获取添加的 marker 这样便于后续的操作  
	        		marker = (Marker) mBaiduMap.addOverlay(markerOptions);  
	        		tButton.destroyDrawingCache();
					
				}
			});
			LatLng ll = result.getLocation();
			mInfoWindow = new InfoWindow(button, ll, -47);
			mBaiduMap.showInfoWindow(mInfoWindow);
			
			HyjUtil.displayToast(result.getName() + ": " + result.getAddress());
//			Toast.makeText(PoiSearchDemo.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			// }
			return true;
		}
	}
	
	private void setLocation(){
		mLocationClient = new LocationClient(getActivity().getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getActivity().getApplicationContext());
		
		InitLocation();
		mLocationClient.start();
	}
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
		int span=1000;
		try {
			span = Integer.valueOf(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			THIS_POINT = new LatLng(location.getLatitude(), location.getLongitude());
			if(editCity.getText().toString() == null || "".equals(editCity.getText().toString())) {
				editCity.setText(location.getCity());
			}
			logMsg();
			
		}


	}
	
	public void logMsg() {
//		MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(THIS_POINT);
//    	mBaiduMap.setMapStatus(u4); 
    	

		if(thisMarker != null){
			thisMarker.remove();
    	}
		//准备 marker 的图片  
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		//准备 marker option 添加 marker 使用  
		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(THIS_POINT);  
		//获取添加的 marker 这样便于后续的操作  
		thisMarker = (Marker) mBaiduMap.addOverlay(markerOptions);  
//		Log.i("BaiduLocationApiDem", sb.toString());
		Intent intent = getActivity().getIntent();
		double latitude = intent.getDoubleExtra("LATITUDE", -1);
		if(latitude == -1 && marker == null && isFirstOpen){
			MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(THIS_POINT);
			mBaiduMap.setMapStatus(u4);
			isFirstOpen = false;
		}
	}
	
	private void reverseGeoCodeOption(LatLng point) {
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
	}
	
	private void geoCodeOption(String city, String editGeoCodeKey) {
		// Geo搜索
		mSearch.geocode(new GeoCodeOption().city(city).address(editGeoCodeKey));
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		if(arg0.getLocation() != null) {
			mLatitude = arg0.getLocation().latitude;
        	mLongitude = arg0.getLocation().longitude;
        	
			MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(arg0.getLocation());
        	mBaiduMap.setMapStatus(u4); 
        	if(marker != null){
        		marker.remove();
        	}
    		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(arg0.getLocation());  
    		//获取添加的 marker 这样便于后续的操作  
    		marker = (Marker) mBaiduMap.addOverlay(markerOptions);  
		} else {
			HyjUtil.displayToast("没有地图上找到地址");
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		if(arg0.getLocation() != null) {
			mAddress = arg0.getAddress();
			editCity.setText(arg0.getAddressDetail().city);
			editSearchKey.setText(arg0.getAddressDetail().district + arg0.getAddressDetail().street + arg0.getAddressDetail().streetNumber);
		}
	}
}
