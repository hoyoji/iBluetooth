package com.hoyoji.android.hyjframework.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.btcontrol.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HyjCalendarGridAdapter extends BaseAdapter {
	public static final int CALENDAR_MODE_WEEK = 0;
	public static final int CALENDAR_MODE_MONTH = 1;
	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	protected Context context;
	protected int[] dayNumber; // 一个gridview中的日期存入此数组中
	protected int[] monthNumber; // 一个gridview中的日期存入此数组中
	protected int[] yearNumber; // 一个gridview中的日期存入此数组中
	// private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};

	protected Resources res = null;
	// private Drawable drawable = null;

	protected int currentYear = -1;
	protected int currentMonth = -1;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	// private int currentFlag = -1; //用于标记当天

	protected int selectedYear = -1; // 用于在头部显示的年份
	protected int selectedMonth = -1; // 用于在头部显示的月份
	protected int selectedDay = -1;
	// private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	// private String cyclical = ""; //天干地支
	// 系统当前时间
	private String sysDate = "";
	protected int sys_year = -1;
	protected int sys_month = -1;
	protected int sys_day = -1;
	private SpecialCalendar sc;
	protected List<Map<String, Object>> mListGroupData;
	private int mCalendarMode;
//	private long mDateFrom;
//	private long mDateTo;
	private int mNumberOfDaysInGrid = 7;
	protected static Drawable drawableSelectedBackground;

	public HyjCalendarGridAdapter(Context context, Resources rs) {
		this.context = context;
		this.sc = new SpecialCalendar();
		this.res = rs;
		this.mCalendarMode = CALENDAR_MODE_WEEK;

		drawableSelectedBackground = res
				.getDrawable(R.drawable.button_circle_solid);
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = Integer.parseInt(sysDate.split("-")[0]);
		sys_month = Integer.parseInt(sysDate.split("-")[1]);
		sys_day = Integer.parseInt(sysDate.split("-")[2]);

		selectedYear = sys_year;
		selectedMonth = sys_month+1;
		selectedDay = sys_day;

		setCalendar(sys_year, sys_month);
	}

	public HyjCalendarGridAdapter(Context context, Resources rs, int year,
			int month, int day) {
		this(context, rs);

		setCalendar(year, month);
	}

	@Override
	public int getCount() {
		return mNumberOfDaysInGrid;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewCache {
		TextView tvExpense;
		TextView tvIncome;
		TextView tvDay;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar_grid_item, null);
			viewCache = new ViewCache();
			viewCache.tvDay = (TextView) convertView.findViewById(R.id.tvtext);
			viewCache.tvExpense = (TextView) convertView
					.findViewById(R.id.tvexpense);
			viewCache.tvIncome = (TextView) convertView
					.findViewById(R.id.tvincome);
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

			Double expenseTotal = Double.valueOf(data.get("expenseTotal")
					.toString());
			if (expenseTotal > 0.0) {
				viewCache.tvExpense.setVisibility(View.VISIBLE);
				if (Double.compare(expenseTotal, expenseTotal.longValue()) == 0) {
					viewCache.tvExpense.setText(HyjApplication.getInstance()
							.getCurrentUser().getUserData()
							.getActiveCurrencySymbol()
							+ expenseTotal.longValue());
				} else {
					viewCache.tvExpense.setText(HyjApplication.getInstance()
							.getCurrentUser().getUserData()
							.getActiveCurrencySymbol()
							+ expenseTotal);
				}
				viewCache.tvExpense.setTextColor(Color
						.parseColor(HyjApplication.getInstance()
								.getCurrentUser().getUserData()
								.getExpenseColor()));
			} else {
				viewCache.tvExpense.setVisibility(View.INVISIBLE);
			}

			Double incomeTotal = Double.valueOf(data.get("incomeTotal")
					.toString());
			if (incomeTotal > 0.0) {
				viewCache.tvIncome.setVisibility(View.VISIBLE);
				if (Double.compare(incomeTotal, incomeTotal.longValue()) == 0) {
					viewCache.tvIncome.setText(HyjApplication.getInstance()
							.getCurrentUser().getUserData()
							.getActiveCurrencySymbol()
							+ incomeTotal.longValue());
				} else {
					viewCache.tvIncome.setText(HyjApplication.getInstance()
							.getCurrentUser().getUserData()
							.getActiveCurrencySymbol()
							+ incomeTotal);
				}
				viewCache.tvIncome.setTextColor(Color.parseColor(HyjApplication
						.getInstance().getCurrentUser().getUserData()
						.getIncomeColor()));
			} else {
				viewCache.tvIncome.setVisibility(View.INVISIBLE);
			}
		} else {
			viewCache.tvExpense.setVisibility(View.INVISIBLE);
			viewCache.tvIncome.setVisibility(View.INVISIBLE);
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
		}  else {
			// 显示当月的
			// 显示当天
			if (this.sys_day == d && this.sys_month == m
					&& this.sys_year == y) {
				viewCache.tvDay.setTextColor(res.getColor(R.color.red));
			} else {
				if (currentYear == y && currentMonth == m) {
					viewCache.tvDay.setTextColor(Color.GRAY);
				} else {
					viewCache.tvDay.setTextColor(Color.LTGRAY);
				}
			}
		}
//			// 显示当天背景
//			if (this.sys_day == d && this.sys_month == m
//					&& this.sys_year == y) {
//				convertView.setBackgroundColor(res.getColor(R.color.hoyoji_lightgray));
//			}
		
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void setCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数

		selectedMonth = this.currentMonth = month;
		selectedYear = this.currentYear = year;
		
		getDayNumber(year, month);
	}

	public void setJumpCalendar(int jumpMonth, int jumpYear) {

		int stepYear = currentYear + jumpYear;
		int stepMonth = currentMonth + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = currentYear + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = currentYear + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = currentYear - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		setCalendar(stepYear, stepMonth);

	}

	public void setJumpCalendar(int jumpMonth, int jumpYear, int year_c,
			int month_c, int day_c) {

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		setCalendar(stepYear, stepMonth);

	}

	// 将一个月中的每一天的值添加入数组dayNumber中
	private void getDayNumber(int year, int month) {
		if (this.mCalendarMode == CALENDAR_MODE_WEEK) {
			getWeekDayNumber(year, month);
		} else {
			getMonthDayNumber(year, month);
		}
	}

	// 将一个月中的每一天的值添加入数组dayNumber中
		public void getDayNumber() {
			int year = this.currentYear;
			int month = this.currentMonth;
			getDayNumber(year, month);
		}
	
	// 将一个月中的每一天的值添加入数组dayNumber中
	private void getMonthDayNumber(int year, int month) {
		int j = 1;
		mNumberOfDaysInGrid = 0;
		dayNumber = new int[42];
		monthNumber = new int[42];
		yearNumber = new int[42];

		int lastMonth = month - 1;
		int nextMonth = month + 1;
		int lastYear = year;
		int nextYear = year;
		if(lastMonth < 1){
			lastMonth = 12;
			lastYear = year - 1;
		}
		if(nextMonth > 12){
			nextMonth = 1;
			nextYear = year + 1;
		}
		for (int i = 0; i < dayNumber.length; i++) {
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				dayNumber[i] = temp + i;
				monthNumber[i] = lastMonth;
				yearNumber[i] = lastYear;
				mNumberOfDaysInGrid ++;
			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				int day = i - dayOfWeek + 1; // 得到的日期
				dayNumber[i] = day;
				monthNumber[i] = month;
				yearNumber[i] = year;
				mNumberOfDaysInGrid ++;
			} else { // 下一个月
				if(mNumberOfDaysInGrid % 7 == 0){
					break;
				}
				dayNumber[i] = j;
				monthNumber[i] = nextMonth;
				yearNumber[i] = nextYear;
				j++;
				mNumberOfDaysInGrid ++;
			}
		}
//		if(dayOfWeek == 0){
//			setDateFrom(year, month, dayNumber[0]);
//		} else if(dayOfWeek > 0){
//			setDateFrom(year, month - 1, dayNumber[0]);
//		}
	}

	// 将一个月中的每一天的值添加入数组dayNumber中
	private void getWeekDayNumber(int year, int month) {
		int firstDay = 0;
		mNumberOfDaysInGrid = 7;
		dayNumber = new int[mNumberOfDaysInGrid];
		monthNumber = new int[mNumberOfDaysInGrid];
		yearNumber = new int[mNumberOfDaysInGrid];
		
		int lastMonth = month - 1;
		int nextMonth = month + 1;
		int lastYear = year;
		int nextYear = year;
		if(lastMonth < 1){
			lastMonth = 12;
			lastYear = year - 1;
		} 
		if(nextMonth > 12){
			nextMonth = 1;
			nextYear = year + 1;
		}
		
		int monthIndex = month;
		int yearIndex = year;
		int selectedDayOffset =  (selectedDay + dayOfWeek - 1) % 7;
		if(month+year*12 == selectedMonth+selectedYear*12){
			firstDay = selectedDay - selectedDayOffset;
			if(firstDay <= 0){
				firstDay = lastDaysOfMonth + firstDay;
				monthIndex = lastMonth;
				yearIndex = lastYear;
			}
		} else if(month+year*12 > selectedMonth+selectedYear*12){
			firstDay = lastDaysOfMonth - dayOfWeek + 1;
			monthIndex = lastMonth;
			yearIndex = lastYear; 
		} else if(month+year*12 < selectedMonth+selectedYear*12){
			firstDay = selectedDay - (selectedDay + dayOfWeek + daysOfMonth - 1) % 7;
			if(firstDay <= 0){
				firstDay = daysOfMonth + firstDay;
			} else {
				monthIndex = nextMonth;
				yearIndex = nextYear;
			}
		}

		for (int i = 0; i < dayNumber.length; i++) {
			dayNumber[i] = firstDay;
			monthNumber[i] = monthIndex;
			yearNumber[i] = yearIndex;
			
			if(month+year*12 < selectedMonth+selectedYear*12){
				if(firstDay == daysOfMonth){
					firstDay = 0;

					monthIndex = nextMonth;
					yearIndex = nextYear ;
				}
			} else if(month+year*12 > selectedMonth+selectedYear*12){
				if(firstDay == lastDaysOfMonth){
					firstDay = 0;
					
					monthIndex = month;
					yearIndex = year ;
				}
			} else {
				if(selectedDay <= selectedDayOffset){
					if(firstDay == lastDaysOfMonth){
						firstDay = 0;
						
						monthIndex = month;
						yearIndex = year ;
					}
				} else {
					if(firstDay == daysOfMonth){
						firstDay = 0;
						
						monthIndex = nextMonth;
						yearIndex = nextYear ;
					}
				}
			}

			firstDay ++;
		}
	}

//	private void setDateFrom(int year, int month, int day) {
//		if (month < 1) {
//			month = 12;
//			year = year - 1;
//		} else if (month > 12) {
//			month = 1;
//			year = year + 1;
//		}
//
//		Calendar calToday = Calendar.getInstance();
//		calToday.set(Calendar.YEAR, year);
//		calToday.set(Calendar.MONTH, month-1);
//		calToday.set(Calendar.DATE, day);
//		calToday.set(Calendar.HOUR_OF_DAY, 0);
//		calToday.clear(Calendar.MINUTE);
//		calToday.clear(Calendar.SECOND);
//		calToday.clear(Calendar.MILLISECOND);
//
//		mDateFrom = calToday.getTimeInMillis();
//		if (this.mCalendarMode == CALENDAR_MODE_WEEK) {
//			mDateTo = mDateFrom + mNumberOfDaysInGrid * 24 * 3600000;
//		} else {
//			mDateTo = mDateFrom + mNumberOfDaysInGrid * 24 * 3600000;
//		}
//	}

	public int getSelectedDay() {
		return selectedDay;
	}

	/**
	 * 点击每一个item时返回item中的日期
	 * 
	 * @param position
	 * @return
	 */
	public int getDayAtPosition(int position) {
		return dayNumber[position];
	}

	public int getMonthAtPosition(int position) {
		return monthNumber[position];
	}
	
	public int getYearAtPosition(int position){
		return yearNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * 
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * 
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}
	
	public int getSysYear() {
		return sys_year;
	}
	
	public int getSysDay() {
		return sys_day;
	}
	
	public int getSysMonth() {
		return sys_month;
	}

	public void setSysYear(int year) {
		sys_year = year;
	}
	
	public void setSysDay(int day) {
		sys_day = day;
	}
	
	public void setSysMonth(int month) {
		sys_month = month;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}

	private static class SpecialCalendar {

		private int daysOfMonth = 0; // 某月的天数
		private int dayOfWeek = 0; // 具体某一天是星期几

		// 判断是否为闰年
		public boolean isLeapYear(int year) {
			if (year % 100 == 0 && year % 400 == 0) {
				return true;
			} else if (year % 100 != 0 && year % 4 == 0) {
				return true;
			}
			return false;
		}

		// 得到某月有多少天数
		public int getDaysOfMonth(boolean isLeapyear, int month) {
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				daysOfMonth = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				daysOfMonth = 30;
				break;
			case 2:
				if (isLeapyear) {
					daysOfMonth = 29;
				} else {
					daysOfMonth = 28;
				}

			}
			return daysOfMonth;
		}

		// 指定某年中的某月的第一天是星期几
		public int getWeekdayOfMonth(int year, int month) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, 1);
			dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
			return dayOfWeek;
		}

	}

	public void setData(List<Map<String, Object>> listGroupData) {
		mListGroupData = listGroupData;
	}

	public void setSelectedDay(int d) {
		selectedDay = d;
	}

	public int getSelectedYear() {
		return selectedYear;
	}

	public int getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedYear(int year) {
		selectedYear = year;
	}

	public void setSelectedMonth(int month) {
		selectedMonth = month;
	}

	public void setCalendarMode(int mode) {
		this.mCalendarMode = mode;
	}

	public int getCalendarMode() {
		return this.mCalendarMode;
	}

	// public Map<String, Object> getSelectedDayData() {
	// if(selectedDay == -1 || selectedMonth != currentMonth || selectedYear !=
	// currentYear){
	// return null;
	// }
	// if(selectedDay <= mListGroupData.size()){
	// return mListGroupData.get(selectedDay-1);
	// }
	// return null;
	// }

	public long getDateFrom() {
		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.YEAR, yearNumber[0]);
		calToday.set(Calendar.MONTH, monthNumber[0]-1);
		calToday.set(Calendar.DATE, dayNumber[0]);
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.clear(Calendar.MINUTE);
		calToday.clear(Calendar.SECOND);
		calToday.clear(Calendar.MILLISECOND);

		return calToday.getTimeInMillis();
	}

	public long getDateTo() {
		Calendar calToday = Calendar.getInstance();
		calToday.setTimeInMillis(getDateFrom());
		calToday.add(Calendar.DAY_OF_YEAR, mNumberOfDaysInGrid);
		return calToday.getTimeInMillis();
//		return getDateFrom() + mNumberOfDaysInGrid * 24 * 3600000;
	}

}
