package com.hoyoji.hoyoji.home;

import java.util.Map;

import com.hoyoji.android.hyjframework.view.HyjCalendarGridAdapter;
import com.hoyoji.btcontrol.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeCalendarGridEventAdapter extends HyjCalendarGridAdapter {
	public HomeCalendarGridEventAdapter(Context context, Resources rs) {
		super(context, rs);
	}

	public HomeCalendarGridEventAdapter(Context context, Resources rs, int year,
			int month, int day) {
		super(context, rs, year, month, day);
	}

	private static class ViewCache {
		TextView tvCount;
		TextView tvDay;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar_grid_event_item, null);
			viewCache = new ViewCache();
			viewCache.tvDay = (TextView) convertView.findViewById(R.id.tvtext);
			viewCache.tvCount = (TextView) convertView
					.findViewById(R.id.tvcount);
			convertView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) convertView.getTag();
		}
		int d = dayNumber[position];
		int m = monthNumber[position];
		int y = yearNumber[position];

		// 当前月信息显示
		viewCache.tvDay.setText(d + "");
		if (mListGroupData != null
				&& mListGroupData.size() > position) {
			Map<String, Object> data = mListGroupData.get(position);

			int eventCount = Integer.valueOf(data.get("count")
					.toString());
			if (eventCount > 0) {
				viewCache.tvCount.setVisibility(View.VISIBLE);
					viewCache.tvCount.setText(eventCount+"");
			} else {
				viewCache.tvCount.setVisibility(View.INVISIBLE);
			}
		} else {
			viewCache.tvCount.setVisibility(View.INVISIBLE);
		}

		

		convertView.setBackgroundColor(Color.TRANSPARENT);
		viewCache.tvDay.setBackgroundColor(Color.TRANSPARENT);
		// 显示选定的日期
		if (this.selectedDay == d && this.selectedMonth == m
				&& this.selectedYear == y) {
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//				convertView.setBackgroundDrawable(drawableSelectedBackground);
				viewCache.tvDay.setBackgroundDrawable(drawableSelectedBackground);
			} else {
//				convertView.setBackground(drawableSelectedBackground);
				viewCache.tvDay.setBackground(drawableSelectedBackground);
			}
			viewCache.tvDay.setTextColor(Color.WHITE);
		} else {
			// 显示当天
			if (this.sys_day == d && this.sys_month == m
					&& this.sys_year == y) {
				viewCache.tvDay.setTextColor(res.getColor(R.color.hoyoji_red));
			}  else {
				// 显示当月的
				if (currentYear == y && currentMonth == m) {
					viewCache.tvDay.setTextColor(Color.GRAY);
				} else {
					viewCache.tvDay.setTextColor(Color.LTGRAY);
				}
			}
		} 
//		// 显示当天背景
//		if (this.sys_day == d && this.sys_month == m
//				&& this.sys_year == y) {
//			convertView.setBackgroundColor(res.getColor(R.color.hoyoji_lightgray));
//		} 
//		
		return convertView;
	}

}
