package com.hoyoji.hoyoji.money;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.MoneyTemplate;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;

public class MoneyTemplateListFragment extends HyjUserListFragment {

	@Override
	public Integer useContentView() {
		return R.layout.money_listfragment_moneytemplate;
	}

	@Override
	public ListAdapter useListViewAdapter() {
		return new SimpleCursorAdapter(getActivity(),
				R.layout.home_listitem_row,
				null,
				new String[] {"data", "type", "id", "id", "id"},
				new int[] {R.id.homeListItem_picture,  R.id.homeListItem_amount ,R.id.homeListItem_remark, R.id.homeListItem_subTitle, R.id.homeListItem_title},
				0); 
	}	

	@Override
	public Integer useMultiSelectMenuView() {
		return R.menu.multi_select_menu;
	}
	
	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);
		Object loader = new CursorLoader(getActivity(),
				ContentProvider.createUri(MoneyTemplate.class, null),
				null, null, null, "date DESC"
			);
		return (Loader<Object>)loader;
	}


	@Override
	public void onInitViewData() {
		super.onInitViewData();
	}
	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) { 
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id == -1) {
			 return;
		}
		Bundle bundle = new Bundle();
//		HyjModel object = (HyjModel) getListAdapter().getItem(position);

		MoneyTemplate template = HyjModel.load(MoneyTemplate.class, id);
		bundle.putLong("MONEYTEMPLATE_ID", template.get_mId());
		
		if(template.getType().equals("MoneyExpenseTemplate")) {
			openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_addnew, bundle);
		} else if(template.getType().equals("MoneyIncomeTemplate")) {
			openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_addnew, bundle);
		}
		
    }
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		String id = cursor.getString(cursor.getColumnIndex("id"));
		String type = cursor.getString(cursor.getColumnIndex("type")); 
		String data = cursor.getString(cursor.getColumnIndex("data"));
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		HyjModel model;
//		if(type.equals("MoneyExpense")){
//			model = new MoneyExpense();
//			model.loadFromJSON(jsonObj, true);
//		}
		
//		if(view.getId() == R.id.homeListItem_date){
////			if(type.equals("MoneyExpense")){
//				((HyjDateTimeView)view).setText(jsonObj.optString("date"));
////			}
//			return true;
//			
//		} else 
		if(view.getId() == R.id.homeListItem_amount){
			((HyjNumericView)view).setPrefix(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol());
			((HyjNumericView)view).setNumber((jsonObj.optDouble("amount")*jsonObj.optDouble("exchangeRate")));
			return true;
		}else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
//			imageView.setImage(cursor.getString(columnIndex));
			
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			if(type.equals("MoneyExpenseTemplate")){
				imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			} else {
				imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			}
			imageView.setImage((Picture)null);
			return true;
		}else if(view.getId() == R.id.homeListItem_remark){
//			if(type.equals("MoneyExpense")){
			if(jsonObj.optString("remark").equals("") || jsonObj.optString("remark") == null){
				((TextView)view).setText("无备注");
			}else{
				((TextView)view).setText(jsonObj.optString("remark"));
			}
//			}
			return true;
		}else if(view.getId() == R.id.homeListItem_subTitle){
			Project project = HyjModel.getModel(Project.class, jsonObj.optString("projectId"));
			((TextView)view).setText(project.getDisplayName());
			return true;
		}else if(view.getId() == R.id.homeListItem_title){
			if(type.equals("MoneyExpenseTemplate")){
				((TextView)view).setText(jsonObj.optString("moneyExpenseCategory"));
				((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			}else{
				((TextView)view).setText(jsonObj.optString("moneyIncomeCategory"));
				((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			}
			return true;
		}else {
			return false;
		}
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.multi_select_menu_delete){
			deleteSelectedMessages();
			this.exitMultiChoiceMode(getListView());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteSelectedMessages() {
		long[] ids = this.getListView().getCheckedItemIds();
		if(ids.length == 0){
			HyjUtil.displayToast("请选择至少一条快记模版");
			return;
		}
		for(int i=0; i<ids.length; i++){
			MoneyTemplate template = Model.load(MoneyTemplate.class, ids[i]);
			if(template != null){
				template.delete();
			}
		}
		
	}
	
}
