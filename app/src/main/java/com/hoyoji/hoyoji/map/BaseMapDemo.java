package com.hoyoji.hoyoji.map;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.hoyoji.btcontrol.R;

/**
 * 演示MapView的基本用法
 */
public class BaseMapDemo extends Activity {
	@SuppressWarnings("unused")
	private static final String LTAG = BaseMapDemo.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private static final LatLng GEO_SHENGZHENG = new LatLng(22.560, 114.064);
	private Marker marker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(this.getApplication());  
		setContentView(R.layout.activity_map);
		
		
		mMapView = (MapView) findViewById(R.id.bmapView);  
		MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(GEO_SHENGZHENG);
		mBaiduMap = mMapView.getMap();  
		//普通地图  
		mBaiduMap.setMapStatus(u4);
		
		
//		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); 
//		SupportMapFragment map4 = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map4));
//		map4.getBaiduMap().setMapStatus(u4);
		
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
            	mapPoi.getName(); //名称
            	mapPoi.getPosition(); //坐标 
                return false;
            }

            @Override
            public void onMapClick(LatLng point) {
            	MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(point);
            	mBaiduMap.setMapStatus(u4); 
            	if(marker != null){
            		marker.remove();
            	}
            	LatLng latLng = mBaiduMap.getMapStatus().target;  
        		//准备 marker 的图片  
        		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);  
        		//准备 marker option 添加 marker 使用  
        		MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(latLng);  
        		//获取添加的 marker 这样便于后续的操作  
        		marker = (Marker) mBaiduMap.addOverlay(markerOptions);  
            }
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}

}
