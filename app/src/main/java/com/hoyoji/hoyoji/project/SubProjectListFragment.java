package com.hoyoji.hoyoji.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.money.MoneyTopupFormFragment;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;

public class SubProjectListFragment extends HyjUserListFragment {
	
	private static final int EDIT_PROJECT_DETAILS = 0;
	private static final int VIEW_PROJECT_MEMBERS = 1;
//	private ContentObserver mChangeObserver = null;
	
	private OnSelectSubProjectsListener mOnSelectSubProjectsListener;
	private ViewGroup mHeaderViewSharedProject;
	private List<Project> mProjectList = new ArrayList<Project>();
	
	public interface OnSelectSubProjectsListener {
		public void onSelectSubProjectsListener(String parentProject, String title);
	}
	
	public static SubProjectListFragment newInstance(String parentProjectId, String title){
		SubProjectListFragment fragment = new SubProjectListFragment();
		Bundle args = new Bundle();
		args.putString("parentProjectId", parentProjectId);
		args.putString("title", title);
		fragment.setArguments(args);
		return fragment;
	}
	
	public String getTitle(){
		String title = getArguments().getString("title");
		if(title == null){
			return "顶级圈子";
		}
		return title;
	}
	
	@Override
	public Integer useContentView() {
		return R.layout.project_listfragment_subproject;
	}

	@Override
	public ListAdapter useListViewAdapter() {
		return new ProjectListAdapter(getActivity(),
				mProjectList,
				R.layout.project_listitem_project,
				new String[] {"_id", "id","id", "id", "id"},
				new int[] {R.id.projectListItem_picture, R.id.projectListItem_name, R.id.projectListItem_owner, R.id.projectListItem_depositTotal, R.id.projectListItem_action_viewSubProjects }); 
	}
	
	@Override
	protected boolean disableOptionsMenuView() {
		return true;
	}
	
	@Override
	protected View useHeaderView(Bundle savedInstanceState){
		String parentProjectId = getArguments().getString("parentProjectId");
		if(parentProjectId == null && getActivity().getCallingActivity() == null){
			ViewGroup view = (ViewGroup)getLayoutInflater(savedInstanceState).inflate(R.layout.project_listfragment_header, null);
			
			// 添加 "共享来的收支" 到 headerView
			mHeaderViewSharedProject = (ViewGroup) view.findViewById(R.id.project_listitem_shared_project);
			mHeaderViewSharedProject.findViewById(R.id.projectListItem_action_viewSubProjects).setVisibility(View.GONE);
			mHeaderViewSharedProject.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(getActivity().getCallingActivity() == null){
						openActivityWithFragment(SharedProjectMoneySearchListFragment.class, R.string.projectListFragment_title_shared_project, null);
					}
				}
		    });
			setSharedProjectHeaderView(mHeaderViewSharedProject);
			
			return view;
		} else {
			return null;
		}
	}

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);
//		String[] projections = null;
//		String selection = null;
//		String[] selectionArgs = null;

		int offset = arg1.getInt("OFFSET");
		int limit = arg1.getInt("LIMIT");
		if(limit == 0){
			limit = getListPageSize();
		}
		arg1.putInt("LIMIT", limit + offset);
		
		String parentProjectId = getArguments().getString("parentProjectId");
		if(parentProjectId != null){
//			selection = "id IN (SELECT subProjectId FROM ParentProject WHERE parentProjectId = ?)";
//			selectionArgs = new String[]{ parentProjectId };
			arg1.putString("PARENT_PROJECTID", parentProjectId);
		}
//		else {
//			projections = new String[]{ "_id", "name", "id", "id AS _subProjectId" };
//			selection = "NOT EXISTS (SELECT id FROM ParentProject WHERE subProjectId = _subProjectId) OR EXISTS (SELECT id FROM ParentProject WHERE subProjectId = _subProjectId AND parentProjectId IS NULL)";
//		}

		Object loader = new SubProjectListLoader(getActivity(), arg1);
		
//		Object loader = new CursorLoader(getActivity(),
//				ContentProvider.createUri(Project.class, null),
//				projections, selection, selectionArgs, "name_pinYin ASC LIMIT " + (limit + offset)
//			);
		return (Loader<Object>)loader;
	}

	@Override
	public void onLoadFinished(Loader loader, Object list) {
			Collection<Project> childList = (ArrayList<Project>) list;
			mProjectList.clear();
			mProjectList.addAll(childList);

			((SimpleAdapter)getListAdapter()).notifyDataSetChanged();
	        setFooterLoadFinished(getListView(), childList.size());
		
		// The list should now be shown.
		if (isResumed()) {
			// setListShown(true);
		} else {
			// setListShownNoAnimation(true);
		}
	}
	
	@Override
	public void onInitViewData() {
		super.onInitViewData();
		getView().findViewById(R.id.project_listfragment_project_addnew).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_addnew, null);
			}
		});
		getView().findViewById(R.id.projectListFragment_action_money_account).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_manage_moneyAccount, null);
    		}
		});
		getView().findViewById(R.id.homeListFragment_event_action_money_topup).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTopupFormFragment.class, R.string.moneyTopupFormFragment_title_addnew, null);
			}
    	});	
//		getView().findViewById(R.id.projectListFragment_action_money_template).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				openActivityWithFragment(MoneyTemplateListFragment.class, R.string.moneyTemplateListFragment_title, null);
//    		}
//		});
//		
//		if (mChangeObserver == null) {
//			mChangeObserver = new ChangeObserver();
////			this.getActivity().getContentResolver()
////					.registerContentObserver(
////							ContentProvider.createUri(
////									ProjectShareAuthorization.class, null), true,
////							mChangeObserver);
////			this.getActivity().getContentResolver()
////			.registerContentObserver(
////					ContentProvider.createUri(
////							ParentProject.class, null), true,
////							mChangeObserver);
//			this.getActivity().getContentResolver()
//			.registerContentObserver(
//					ContentProvider.createUri(
//							UserData.class, null), true,
//							mChangeObserver);
//		}
	}

	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) { 
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id == -1) {
			 return;
		}
		
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("project_id", id);
			bundle.putLong("MODEL_ID", id);
			openActivityWithFragment(ProjectViewPagerFragment.class, R.string.projectListFragment_view_transactions, bundle);
		}
    }  

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(!getUserVisibleHint() || !getParentFragment().getUserVisibleHint()){
			return super.onContextItemSelected(item);
		}
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Bundle bundle = new Bundle();
		bundle.putLong("MODEL_ID", info.id);
		switch (item.getItemId()) {
			case EDIT_PROJECT_DETAILS:
				openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_edit, bundle);
				break;
			case VIEW_PROJECT_MEMBERS:
				openActivityWithFragment(ProjectMemberListFragment.class, R.string.memberListFragment_title, bundle);
				break;
		}
		return super.onContextItemSelected(item);
	}

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//		AdapterContextMenuInfo mi =(AdapterContextMenuInfo) menuInfo;
//		if(mi.id == -1){
//			return;
//		}
//		menu.add(0, EDIT_PROJECT_DETAILS, 0, "圈子资料");
//		menu.add(0, VIEW_PROJECT_MEMBERS, 1, "圈子成员");
//		menu.add(CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, R.string.app_action_cancel_list_item);
////		menu.add(0, ADD_SUB_PROJECT, 1, "创建子圈子");
//	}
	
	private void setSharedProjectHeaderView(ViewGroup view) {
		ImageButton imageButton = (ImageButton)view.findViewById(R.id.projectListItem_action_viewSubProjects);
		imageButton.setImageResource(R.drawable.ic_action_next_item);
		imageButton.setEnabled(false);

		ImageView picture = (ImageView)view.findViewById(R.id.projectListItem_picture);
		picture.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_event_white));
		picture.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
		picture.setEnabled(false);
		
		((TextView)view.findViewById(R.id.projectListItem_name)).setText(R.string.projectListFragment_title_shared_project);
//		((TextView)view.findViewById(R.id.projectListItem_owner)).setText("系统生成");
		
//		view.findViewById(R.id.projectListItem_depositTotalLabel).setVisibility(View.GONE);
		HyjNumericView numericView = (HyjNumericView)view.findViewById(R.id.projectListItem_depositTotal);
//			numericView.setSuffix(null);
//			numericView.setTextColor(Color.BLACK);
//			numericView.setPrefix("-");
//			numericView.setText(null);
		numericView.setVisibility(View.GONE);
	}
	
	public static boolean setProjectViewValue(Fragment f, View view, Object model, String field, OnClickListener onSubProjectClickListener, OnClickListener onPictureClickListener) {
		Project project = (Project)model;
		if(view.getId() == R.id.projectListItem_name){
			((TextView)view).setText(project.getDisplayName());
			return true;
		} else if(view.getId() == R.id.projectListItem_owner){
				if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					((TextView)view).setText("");
				} else {
					String ownerUserName = Friend.getFriendUserDisplayName(project.getOwnerUserId());
					((TextView)view).setText(ownerUserName);
				}
				return true;
//		} 
//else if(view.getId() == R.id.projectListItem_inOutTotal) {
//			HyjNumericView numericView = (HyjNumericView)view;
//			Project project = HyjModel.getModel(Project.class, cursor.getString(columnIndex));
//			
////			numericView.setPrefix(project.getCurrencySymbol());
//			numericView.setSuffix(null);
//			
//			Double projectBalance = project.getBalance();
//			if(projectBalance == 0){
//				numericView.setTextColor(Color.BLACK);
//				numericView.setPrefix(project.getCurrencySymbol());
//			} else if(projectBalance < 0){
//				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
//				numericView.setPrefix("支出"+project.getCurrencySymbol());
//			}else{
//				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
//				numericView.setPrefix("收入"+project.getCurrencySymbol());
//			}
//			
//			numericView.setNumber(Math.abs(projectBalance));
//			return true;
		} else if(view.getId() == R.id.projectListItem_depositTotal) {
			HyjNumericView numericView = (HyjNumericView)view;
			String projectId = project.getId();
			ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", projectId, HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(psa != null && psa.getProjectShareMoneyExpenseOwnerDataOnly() == true){
				numericView.setSuffix(null);
				numericView.setTextColor(Color.BLACK);
				numericView.setPrefix("-");
				numericView.setText(null);
				return true;
			}
			
//			numericView.setPrefix(project.getCurrencySymbol());
			numericView.setSuffix(null);
			
			Double depositBalance = project.getDepositBalance();
			if(depositBalance == 0){
				numericView.setTextColor(Color.BLACK);
				numericView.setPrefix(project.getCurrencySymbol());
			} else if(depositBalance < 0){
				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
				numericView.setPrefix("支出"+project.getCurrencySymbol());
			}else{
				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
				numericView.setPrefix("收入"+project.getCurrencySymbol());
			}
			
			numericView.setNumber(Math.abs(depositBalance));
			return true;
		} else if(view.getId() == R.id.projectListItem_action_viewSubProjects){
			if(!project.getSubProjects().isEmpty()){
				((ImageButton)view).setImageResource(R.drawable.ic_action_next_item_blue);
				((ImageButton)view).setEnabled(true);
			} else {
				((ImageButton)view).setImageResource(R.drawable.ic_action_next_item);
				((ImageButton)view).setEnabled(false);
			}
			if(view.getTag() == null){
				view.setOnClickListener(onSubProjectClickListener);
			}
			view.setTag(project.getId());
			return true;
		} else if(view.getId() == R.id.projectListItem_picture){
			ImageView imageView= (ImageView)view;
			if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				imageView.setBackgroundColor(f.getResources().getColor(R.color.hoyoji_yellow));
				imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_event_white));
			} else {
				imageView.setBackgroundColor(f.getResources().getColor(R.color.hoyoji_green));
				imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_event_white));
			}
			
			if(view.getTag() == null){
				view.setOnClickListener(onPictureClickListener);
			}
			view.setTag(project.get_mId());
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean setViewValue(View view, Object model, String field) {
		return setProjectViewValue(this, view, model, field, 
				new OnClickListener(){
				@Override
				public void onClick(View v) {
					String parentProjectId = v.getTag().toString();
					Project project = HyjModel.getModel(Project.class, parentProjectId);
					mOnSelectSubProjectsListener.onSelectSubProjectsListener(parentProjectId, project.getDisplayName());
				}
			},
			new OnClickListener(){
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putLong("MODEL_ID", (Long) v.getTag());
					openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_edit, bundle);
				}
			});
	}	
	
//	@Override
//	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//		if(view.getId() == R.id.projectListItem_name){
//			Project project = HyjModel.getModel(Project.class, cursor.getString(columnIndex));
//			((TextView)view).setText(project.getDisplayName());
//			return true;
//		} else if(view.getId() == R.id.projectListItem_owner){
//				Project project = HyjModel.getModel(Project.class, cursor.getString(columnIndex));
//				if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//					((TextView)view).setText("");
//				} else {
//					String ownerUserName = Friend.getFriendUserDisplayName(project.getOwnerUserId());
//					((TextView)view).setText(ownerUserName);
//				}
//				return true;
////		} 
////else if(view.getId() == R.id.projectListItem_inOutTotal) {
////			HyjNumericView numericView = (HyjNumericView)view;
////			Project project = HyjModel.getModel(Project.class, cursor.getString(columnIndex));
////			
//////			numericView.setPrefix(project.getCurrencySymbol());
////			numericView.setSuffix(null);
////			
////			Double projectBalance = project.getBalance();
////			if(projectBalance == 0){
////				numericView.setTextColor(Color.BLACK);
////				numericView.setPrefix(project.getCurrencySymbol());
////			} else if(projectBalance < 0){
////				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
////				numericView.setPrefix("支出"+project.getCurrencySymbol());
////			}else{
////				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
////				numericView.setPrefix("收入"+project.getCurrencySymbol());
////			}
////			
////			numericView.setNumber(Math.abs(projectBalance));
////			return true;
//		} else if(view.getId() == R.id.projectListItem_depositTotal) {
//			HyjNumericView numericView = (HyjNumericView)view;
//			String projectId = cursor.getString(columnIndex);
//			ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", projectId, HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
//			if(psa != null && psa.getProjectShareMoneyExpenseOwnerDataOnly() == true){
//				numericView.setSuffix(null);
//				numericView.setTextColor(Color.BLACK);
//				numericView.setPrefix("-");
//				numericView.setText(null);
//				return true;
//			}
//			
//			Project project = HyjModel.getModel(Project.class, projectId);
////			numericView.setPrefix(project.getCurrencySymbol());
//			numericView.setSuffix(null);
//			
//			Double depositBalance = project.getDepositBalance();
//			if(depositBalance == 0){
//				numericView.setTextColor(Color.BLACK);
//				numericView.setPrefix(project.getCurrencySymbol());
//			} else if(depositBalance < 0){
//				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
//				numericView.setPrefix("支出"+project.getCurrencySymbol());
//			}else{
//				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
//				numericView.setPrefix("收入"+project.getCurrencySymbol());
//			}
//			
//			numericView.setNumber(Math.abs(depositBalance));
//			return true;
//		} else if(view.getId() == R.id.projectListItem_action_viewSubProjects){
//			Project project = HyjModel.getModel(Project.class, cursor.getString(columnIndex));
//			if(!project.getSubProjects().isEmpty()){
//				((ImageButton)view).setImageResource(R.drawable.ic_action_next_item_blue);
//				((ImageButton)view).setEnabled(true);
//			} else {
//				((ImageButton)view).setImageResource(R.drawable.ic_action_next_item);
//				((ImageButton)view).setEnabled(false);
//			}
//			if(view.getTag() == null){
//				view.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						String parentProjectId = v.getTag().toString();
//						Project project = HyjModel.getModel(Project.class, parentProjectId);
//						mOnSelectSubProjectsListener.onSelectSubProjectsListener(parentProjectId, project.getDisplayName());
//					}
//				});
//			}
//			view.setTag(cursor.getString(columnIndex));
//			return true;
//		} else if(view.getId() == R.id.projectListItem_picture){
//			ImageView imageView= (ImageView)view;
//			Project project = HyjModel.getModel(Project.class, cursor.getString(cursor.getColumnIndex("id")));
//			if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
//				imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_event_white));
//			} else {
//				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_green));
//				imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_event_white));
//			}
//			
//			if(view.getTag() == null){
//				view.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						Bundle bundle = new Bundle();
//						bundle.putLong("MODEL_ID", (Long) v.getTag());
//						openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_edit, bundle);
//					}
//				});
//			}
//			view.setTag(cursor.getLong(columnIndex));
//			return true;
//		} else {
//			return false;
//		}
//	}	
//	
//	private class ChangeObserver extends ContentObserver {
//		AsyncTask<String, Void, String> mTask = null;
//		public ChangeObserver() {
//			super(new Handler());
//		}
//
//		@Override
//		public boolean deliverSelfNotifications() {
//			return true;
//		}
//
////		@Override
////		public void onChange(boolean selfChange, Uri uri) {
////			super.onChange(selfChange, uri);
////		}
//
//		@Override
//		public void onChange(boolean selfChange) {
//			super.onChange(selfChange);
//			if(mTask == null){
//				mTask = new AsyncTask<String, Void, String>() {
//			        @Override
//			        protected String doInBackground(String... params) {
//						try {
//							//等待其他的更新都到齐后再更新界面
//							Thread.sleep(200);
//						} catch (InterruptedException e) {}
//						return null;
//			        }
//			        @Override
//			        protected void onPostExecute(String result) {
//						((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();
//
////				    	getLoaderManager().restartLoader(0, new Bundle(), SubProjectListFragment.this);
//						mTask = null;
//			        }
//			    };
//			    mTask.execute();
//			}
//		}
//	}

//
//	@Override
//	public void onDestroy() {
//		if (mChangeObserver != null) {
//			this.getActivity().getContentResolver()
//					.unregisterContentObserver(mChangeObserver);
//		}
//		
//		super.onDestroy();
//	}


	public void setOnSelectSubProjectsListener(
			OnSelectSubProjectsListener onSelectSubProjectsListener) {
		mOnSelectSubProjectsListener = onSelectSubProjectsListener;
	}


	
//	private static class ProjectListSimpleCursorAdapter extends SimpleCursorAdapter {
//		int mLayout;
//		int[] mTo;
//		public ProjectListSimpleCursorAdapter(Context context, int layout,
//				Cursor c, String[] from, int[] to, int flags) {
//			super(context, layout, c, from, to, flags);
//			mLayout = layout;
//			mTo = to;
//		}
//		
//		@Override
//		public View getView(final int position, View convertView,
//                ViewGroup parent) {
//
//			View view = convertView;
//			View[] viewHolder;
//	        if (view == null) {
//	        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	            view = vi.inflate(mLayout, parent);
//	            viewHolder = new View[mTo.length];
//	            for(int i=0; i<mTo.length; i++){
//	            	View v = view.findViewById(mTo[i]);
//	            	viewHolder[i] = v;
//	            }
//	            view.setTag(viewHolder);
//	        } else {
//	        	viewHolder = (View[])view.getTag();
//	        }
//	        return view;
//		}
//	}
	
	private static class ProjectListAdapter extends SimpleAdapter{
		private Context mContext;
		private int[] mViewIds;
	    private String[] mFields;
	    private int mLayoutResource;
//	    private ViewBinder mViewBinder;
	    
		public ProjectListAdapter(Context context,
	                    List<Project> childData,
	                    int childLayout, String[] childFrom,
	                    int[] childTo) {
			super(context, (List<? extends Map<String, ?>>) ((Object)childData), childLayout, childFrom, childTo);

			mContext = context;
	        mLayoutResource = childLayout;
	        mViewIds = childTo;
	        mFields = childFrom;
		}

		@Override
	    public long getItemId(int position) {
	        return ((HyjModel)getItem(position)).get_mId();
	    }
		
		@Override
		public boolean hasStableIds(){
			return true;
		}
	    
		/**
	     * Populate new items in the list.
	     */
	    @Override public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        View[] viewHolder;
	        if (view == null) {
	        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = vi.inflate(mLayoutResource, null);
	            viewHolder = new View[mViewIds.length];
	            for(int i=0; i<mViewIds.length; i++){
	            	View v = view.findViewById(mViewIds[i]);
	            	viewHolder[i] = v;
	            }
	            view.setTag(viewHolder);
	        } else {
	        	viewHolder = (View[])view.getTag();
	        }

	        Object item = getItem(position);
	        for(int i=0; i<mViewIds.length; i++){
	        	View v = viewHolder[i];
	        	getViewBinder().setViewValue(v, item, mFields[i]);
	        }
	        
	        return view;
	    }
	}
}
