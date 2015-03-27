package com.hoyoji.hoyoji.home;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.UserData;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class HomeCalendarGridGroupListLoader extends
		AsyncTaskLoader<List<Map<String, Object>>> {

	private DateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private List<Map<String, Object>> mGroupList;
	private long mStartDateInMillis = -1;
	private long mEndDateInMillis = -1;
	private ChangeObserver mChangeObserver;
	private boolean mIsLoading;

	public HomeCalendarGridGroupListLoader(Context context, Bundle queryParams) {
		super(context);
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (queryParams != null) {
			mStartDateInMillis = queryParams.getLong("startDateInMillis", -1);
			mEndDateInMillis = queryParams.getLong("endDateInMillis", -1);
		}

		mChangeObserver = new ChangeObserver();
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyExpense.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyIncome.class, null), true,
				mChangeObserver);
//		context.getContentResolver().registerContentObserver(
//				ContentProvider.createUri(MoneyExpenseContainer.class, null), true,
//				mChangeObserver);
//		context.getContentResolver().registerContentObserver(
//				ContentProvider.createUri(MoneyIncomeContainer.class, null), true,
//				mChangeObserver);
//		context.getContentResolver().registerContentObserver(
//				ContentProvider.createUri(MoneyDepositExpenseContainer.class, null), true,
//				mChangeObserver);
//		context.getContentResolver().registerContentObserver(
//				ContentProvider.createUri(MoneyDepositIncomeContainer.class, null), true,
//				mChangeObserver);
//		context.getContentResolver().registerContentObserver(
//				ContentProvider.createUri(MoneyDepositReturnContainer.class, null), true,
//				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyTransfer.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyBorrow.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyLend.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyReturn.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyPayback.class, null), true,
				mChangeObserver);
//		context.getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
//				mChangeObserver);
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<Map<String, Object>> loadInBackground() {
		mIsLoading = true;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		String currentUserId = HyjApplication.getInstance().getCurrentUser().getId();
		String localCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		
		DateFormat df = SimpleDateFormat.getDateInstance();
		Calendar calToday = Calendar.getInstance();
		calToday.setTimeInMillis(mStartDateInMillis);
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.clear(Calendar.MINUTE);
		calToday.clear(Calendar.SECOND);
		calToday.clear(Calendar.MILLISECOND);
		
// get start of this week in milliseconds
//		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		// cal.add(Calendar.WEEK_OF_YEAR, -1);

		int loadCount = 0;
		while (calToday.getTimeInMillis() <= mEndDateInMillis) {
			int count = 0;
			String[] args = new String[] {String.valueOf(calToday.getTimeInMillis()), String.valueOf(calToday.getTimeInMillis() + 24 * 3600000) };
			double expenseTotal = 0.00;
			double incomeTotal = 0.00;
			
			Cursor cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyExpense main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				expenseTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyLend main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				expenseTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyReturn main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				expenseTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.transferOutAmount * main.transferOutExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyTransfer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE main.transferOutId IS NOT NULL AND date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				expenseTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyIncome main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				incomeTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyBorrow main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				incomeTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyPayback main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				incomeTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.transferInAmount * main.transferInExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
									+ "FROM MoneyTransfer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
									+ localCurrencyId
									+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
									+ localCurrencyId + "') "
									+ "WHERE main.transferInId IS NOT NULL AND date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				incomeTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			
//			Cursor cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyExpense main LEFT JOIN MoneyExpenseApportion mea ON main.moneyExpenseApportionId = mea.id LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE mea.id IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
//				expenseTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyIncome main LEFT JOIN MoneyIncomeApportion mea ON main.moneyIncomeApportionId = mea.id LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE mea.id IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
//				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyExpenseContainer main JOIN Project prj1 ON prj1.id = main.projectId LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = prj1.currencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = prj1.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
//				expenseTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyIncomeContainer main JOIN Project prj1 ON prj1.id = main.projectId LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = prj1.currencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = prj1.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
//				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyDepositIncomeContainer main JOIN Project prj1 ON prj1.id = main.projectId LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = prj1.currencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = prj1.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyDepositExpenseContainer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyDepositReturnContainer main JOIN Project prj1 ON prj1.id = main.projectId LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = prj1.currencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = prj1.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(transferOutAmount) as total FROM MoneyTransfer WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				transferTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyBorrow main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE moneyDepositIncomeApportionId IS NULL AND moneyIncomeApportionId IS NULL AND moneyExpenseApportionId IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyLend main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE moneyIncomeApportionId IS NULL AND moneyExpenseApportionId IS NULL AND moneyDepositExpenseContainerId IS NULL AND moneyDepositIncomeApportionId IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyReturn main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE moneyDepositReturnApportionId IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyDepositPaybackContainer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
//			cursor = Cache
//					.openDatabase()
//					.rawQuery(
//							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyPayback main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.currencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
//							"WHERE moneyDepositPaybackContainerId IS null AND moneyDepositReturnApportionId IS NULL AND date >= ? AND date < ?",
//							args);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count += cursor.getInt(0);
////				incomeTotal += cursor.getDouble(1);
//				cursor.close();
//				cursor = null;
//			}
				String ds = df.format(calToday.getTime());
//				ds = ds.replaceAll("Z$", "+0000");
				HashMap<String, Object> groupObject = new HashMap<String, Object>();
				groupObject.put("date", ds);
				groupObject.put("dateInMilliSeconds", calToday.getTimeInMillis());
					groupObject.put("expenseTotal",
							HyjUtil.toFixed2(expenseTotal));
					groupObject.put("incomeTotal",
						HyjUtil.toFixed2(incomeTotal));
				list.add(groupObject);
				loadCount += count + 1;

//			// 我们要检查还有没有数据可以加载的，如果没有了，我们就break出。否则会进入无限循环。
//			if(count == 0){
//				long moreDataInMillis = getHasMoreDataDateInMillis(calToday.getTimeInMillis());
//				if(moreDataInMillis == -1){
//					break;
//				} else {
//					calToday.setTimeInMillis(moreDataInMillis);
//				}
//			} else {
				calToday.add(Calendar.DAY_OF_YEAR, 1);
//			}
		}
		return list;
	}
	
	public boolean isLoading(){
		return mIsLoading;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<Map<String, Object>> objects) {
		mGroupList = objects;

		if (isStarted() && mGroupList != null) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(objects);
		}
		mIsLoading = false;
	}

	@Override
	protected void onAbandon() {
		super.onAbandon();
		this.getContext().getContentResolver()
				.unregisterContentObserver(mChangeObserver);
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
//		super.onStartLoading();
		if (mGroupList != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mGroupList);
		}

		if (takeContentChanged() || mGroupList == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
//		super.onStopLoading();
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

//	/**
//	 * Handles a request to cancel a load.
//	 */
//	@Override
//	public void onCanceled(List<Map<String, Object>> objects) {
//		super.onCanceled(objects);
//	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		mGroupList = null;
	}

	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			onContentChanged();
		}
	}
	
}
