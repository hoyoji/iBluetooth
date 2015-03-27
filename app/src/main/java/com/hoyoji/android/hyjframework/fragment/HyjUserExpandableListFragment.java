package com.hoyoji.android.hyjframework.fragment;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.HyjSimpleCursorTreeAdapter.OnGetChildrenCursorListener;
import com.hoyoji.android.hyjframework.HyjSimpleExpandableListAdapter;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjBlankUserActivity;
import com.hoyoji.android.hyjframework.view.HyjExpandableListView;
import com.hoyoji.android.hyjframework.view.HyjExpandableListView.OnOverScrollByListener;
import com.hoyoji.btcontrol.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleAdapter;

public abstract class HyjUserExpandableListFragment extends Fragment implements 
	LoaderManager.LoaderCallbacks<Object>, 
	SimpleCursorTreeAdapter.ViewBinder, 
	SimpleAdapter.ViewBinder, 
	OnChildClickListener,
	OnGroupClickListener,
	OnGroupCollapseListener,
	OnGroupExpandListener,
	OnGetChildrenCursorListener
{
	public final static int DELETE_LIST_ITEM = 1024;
	public final static int CANCEL_LIST_ITEM = 1025;
	protected boolean mIsViewInited = false;
	protected ExpandableListView mExpandableListView;
	protected View mFooterView;
//	protected TextView mEmptyView;
//	protected int mListPageSize = 10;
	protected DisplayMetrics displayMetrics;
	protected View mHeaderView;
	private View mMultiSelectActionBarView;
	private TextView mSelectedCount;
	private Object mOptionsMenu;
	private boolean mRestoreHomeAsUp = false;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			displayMetrics = getResources().getDisplayMetrics();
			//View v = super.onCreateView(inflater, container, savedInstanceState);
			ViewGroup rootView = (ViewGroup) inflater.inflate(useContentView(), container, false);
			//rootView.addView(v, 0);
//			mEmptyView = (TextView) rootView.findViewById(android.R.id.empty);
			
			mExpandableListView = (ExpandableListView) rootView.findViewById(android.R.id.list);
			mExpandableListView.setOnChildClickListener(this);
			mExpandableListView.setOnGroupClickListener(this);
			mExpandableListView.setOnGroupCollapseListener(this);
			mExpandableListView.setOnGroupExpandListener(this);
			mExpandableListView.setFooterDividersEnabled(false);
			
			if(useToolbarView() != null){
				// populate bottom toolbar
			}
			return rootView;
	}
	
	public ExpandableListView getListView(){
		return mExpandableListView;
	}
	
	protected int getListPageSize(){
		return (int) (displayMetrics.heightPixels / displayMetrics.density / 40);
	}
	
	protected View useHeaderView(Bundle savedInstanceState){
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mHeaderView = useHeaderView(savedInstanceState); 
		if(mHeaderView != null){
			getListView().addHeaderView(mHeaderView);
		}
		View footerLayout = getLayoutInflater(savedInstanceState).inflate(R.layout.list_view_footer_fetch_more, null);
		mFooterView = footerLayout.findViewById(android.R.id.text1);
		getListView().addFooterView(footerLayout);
		mFooterView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				doFetchMore(getListView().getExpandableListAdapter().getGroupCount(), getListPageSize());
			}
		});
//	    mExpandableListView.setOverscrollFooter(getResources().getDrawable(R.drawable.ic_action_refresh));
	    if(getListView() instanceof HyjExpandableListView){
		    ((HyjExpandableListView)getListView()).setOnOverScrollByListener(new OnOverScrollByListener(){
				private boolean mIsFetchingMore = false;

				@Override
				public void onOverScrollBy(int deltaX, int deltaY, int scrollX,
						int scrollY, int scrollRangeX, int scrollRangeY,
						int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	
					final float density = displayMetrics.density;
					if(!mIsFetchingMore && scrollY / density > 50.0){
						mIsFetchingMore   = true;
						doFetchMore(getListView().getExpandableListAdapter().getGroupCount(), getListPageSize());
					} else if(scrollY == 0){
						mIsFetchingMore  = false;
					}
					
				}
		    });
	    }
//		getListView().setEmptyView(mEmptyView);
		this.registerForContextMenu(getListView());
		ExpandableListAdapter adapter = (ExpandableListAdapter) getListView().getExpandableListAdapter();
		if(adapter == null){
			adapter = useListViewAdapter();
			if(adapter instanceof SimpleCursorTreeAdapter){
				((SimpleCursorTreeAdapter) adapter).setViewBinder(this);
			} else if(adapter instanceof HyjSimpleExpandableListAdapter){
				((HyjSimpleExpandableListAdapter) adapter).setViewBinder(this);
			}
			getListView().setAdapter(adapter); 
		}
	}
	public void setFooterLoadStart(){
//        if(getListView().getExpandableListAdapter().getGroupCount() == 0){
//        	mEmptyView.setText(R.string.app_listview_footer_fetching_more);
//        } else {
            ((TextView)mFooterView).setText(R.string.app_listview_footer_fetching_more);
//        }
        ((TextView)mFooterView).setEnabled(false);
	}
	
	public int getHeaderHeight(){
		if(mHeaderView != null){
			return mHeaderView.getHeight();
		} 
		return 0;
	}
	
	public void setFooterLoadFinished(int count){
        mFooterView.setEnabled(true);
//        mEmptyView.setText(R.string.app_listview_no_content);
		if(count >= getListView().getExpandableListAdapter().getGroupCount() + getListPageSize()){
	        ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}  else if(count == 0 && getListView().getExpandableListAdapter().getGroupCount() == 0){
	        ((TextView)mFooterView).setText(R.string.app_listview_no_content);
	        ((TextView)mFooterView).setBackgroundColor(Color.TRANSPARENT);
	        ((TextView)mFooterView).setHeight(getListView().getHeight()-getHeaderHeight());
//	        if(mEmptyView != null){
//				mEmptyView.setText(R.string.app_listview_no_content);
//	        }
		} else {
		    ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_no_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}
	}
	public void setFooterLoadFinished(boolean hasMoreData){
        mFooterView.setEnabled(true);
//        mEmptyView.setText(R.string.app_listview_no_content);
		if(hasMoreData){
	        ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}  else if(getListView().getExpandableListAdapter().getGroupCount() == 0){
	        ((TextView)mFooterView).setText(R.string.app_listview_no_content);
	        ((TextView)mFooterView).setBackgroundColor(Color.TRANSPARENT);
	        ((TextView)mFooterView).setHeight(getListView().getHeight()-getHeaderHeight());
//	        if(mEmptyView != null){
//				mEmptyView.setText(R.string.app_listview_no_content);
//	        }
		} else {
		    ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_no_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}
	}
	@Override
	public void onStart(){
		super.onStart();
		if(HyjApplication.getInstance().isLoggedIn()) {
	        //setListShown(false);  
			if(!mIsViewInited){
				onInitViewData();
				initLoader(-1);
				mIsViewInited = true;
			}
		}

//		if(mOptionsMenu == null){
//			getActivity().supportInvalidateOptionsMenu();
//		}
	}
	
	public void initLoader(int loaderId) {
		Loader<Object> loader = getLoaderManager().getLoader(loaderId);
		if(loader != null && !loader.isReset()) {
			getLoaderManager().restartLoader(loaderId, null,this);
		} else {
			Bundle bundle = new Bundle();
			if(loaderId == -1) {
				bundle.putInt("OFFSET", 0);
				bundle.putInt("LIMIT", getListPageSize());
			}
			getLoaderManager().initLoader(loaderId, bundle,this);
		}
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(!disableOptionsMenuView()){
			setHasOptionsMenu (true);
		}
	}

	protected boolean disableOptionsMenuView() {
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// will throw ensureList: content view not yet created. so we check if getView() != null
//		if(getView() != null){
//			mOptionsMenu = menu;

		if(getView() != null && getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			menu.clear();
			if(getActivity().getCallingActivity() == null){
				if(useMultiSelectMenuView() != null){
					inflater.inflate(useMultiSelectMenuView(), menu);
				}
			} else {
				if(useMultiSelectMenuPickerView() != null){
					inflater.inflate(useMultiSelectMenuPickerView(), menu);
				}
			}
		} else {
			if(useOptionsMenuView() != null){
				inflater.inflate(useOptionsMenuView(), menu);
			} 
		}

//		}
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.multi_select_menu_ok){
			returnSelectedItems();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void returnSelectedItems() {
		long[] ids = getListView().getCheckedItemIds();
		if(ids.length == 0){
			HyjUtil.displayToast("请选择至少一条记录");
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra("MODEL_IDS", ids);
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
		
	}

	public Integer useToolbarView(){
		return null;
	}
	

	public Integer useOptionsMenuView(){
		return null;
	}
	
	public Integer useMultiSelectMenuView(){
//		return R.menu.multi_select_menu;
		return null;
	}

	public Integer useMultiSelectMenuPickerView() {
//		return R.menu.multi_select_menu_ok;
		return null;
	}
	

	@SuppressLint("NewApi")
	public void enterMultiChoiceMode(final HyjExpandableListView listView, int position){
		if(getActivity().getIntent().getBooleanExtra("disableMultiChoiceMode", false)){
			return;
		}
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//			listView.setItemChecked(position, true);
		((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
		mRestoreHomeAsUp  = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
		if (mMultiSelectActionBarView == null) {
		      mMultiSelectActionBarView = LayoutInflater.from(getActivity()).inflate(R.layout.multi_select_actionbar, null);
		      mSelectedCount = (TextView)mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_count);
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_close).setOnClickListener(new OnClickListener(){
			    	@Override
					public void onClick(View v) {
			    		exitMultiChoiceMode(listView, -1);
					}
		      });
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_select_all).setOnClickListener(new OnClickListener(){
			    	@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
						for(int g = 0; g < listView.getExpandableListAdapter().getGroupCount(); g++){
							for(int c = 0; c < listView.getExpandableListAdapter().getChildrenCount(g); c++){
					    		int position = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(g, c));
								listView.setItemChecked(position, true);
							}
						}
						mSelectedCount.setText(listView.getCheckedItemCount() + "");
					}
		      });
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_select_clear).setOnClickListener(new OnClickListener(){
			    	@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
			    		listView.clearChoices();
			    		int position = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(0, 0));
			    		if(position > -1) {
			    			getListView().setItemChecked(position, false);
			    		}
						mSelectedCount.setText(listView.getCheckedItemCount() + "");
					}
		      });
			  actionBar.setCustomView(mMultiSelectActionBarView);
			  
			  listView.setOnChildClickListener(new OnChildClickListener(){
					@SuppressLint("NewApi")
					@Override
					public boolean onChildClick(ExpandableListView parent, View v,
							int groupPosition, int childPosition, long id) {
						final int position = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
						if(parent.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){  
							parent.setItemChecked(position, !parent.isItemChecked(position));  

							mSelectedCount.setText(((HyjExpandableListView)parent).getCheckedItemCount() + "");
						} else {
							if(((HyjActivity)getActivity()).getChoiceMode() != ListView.CHOICE_MODE_NONE){
								// 长按触发 onCreateContextMenu 的同时会触发 onChildClick 事件，
								// 所以我们在 onChildClick 里去设置 Activity 的 ChoiceMode
								((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_NONE);
							} else {
								HyjUserExpandableListFragment.this.onChildClick(parent, v, groupPosition, childPosition, id);
							}
						}
						return true;
					} 
				  });
		}
		mSelectedCount.setText(listView.getCheckedItemCount() + "");
		getActivity().supportInvalidateOptionsMenu();
	}
	
	public void exitMultiChoiceMode(final ExpandableListView listView, int position){
		listView.clearChoices();
		if(position >= 0){
			listView.setItemChecked(position, false);
			// 长按触发 onCreateContextMenu 的同时会触发 onChildClick 事件，
			// 所以我们在 onChildClick 里去设置 Activity 的 ChoiceMode
//			((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_NONE);
		} else {
			position = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(0, 0));
			getListView().setItemChecked(position, false);
			((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_NONE);
		}
		listView.post(new Runnable(){
			@Override
			public void run() {
				listView.setChoiceMode(ListView.CHOICE_MODE_NONE);	
				getActivity().supportInvalidateOptionsMenu();
			}
		});

		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
		if(mRestoreHomeAsUp){
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(false);
	}
	
	public abstract Integer useContentView();

	public abstract ExpandableListAdapter useListViewAdapter();
	
	public void onInitViewData(){
		
	}
	

	@Override
	public void onLoadFinished(Loader<Object> loader, Object cursor) {
		SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListView().getExpandableListAdapter();
		if(loader.getId() < 0){
	        setFooterLoadFinished(((Cursor)cursor).getCount());
			boolean expandFirstGroup = adapter.getGroupCount() == 0;
			adapter.setGroupCursor((Cursor)cursor);
			if(expandFirstGroup && adapter.getGroupCount() > 0){
				getListView().expandGroup(0);
			}
		} else {
			// 
			if(adapter.getGroupCount() > loader.getId()){
				adapter.setChildrenCursor(loader.getId(), (Cursor)cursor);
			} else {
				getLoaderManager().destroyLoader(loader.getId());
			}
		}
		// The list should now be shown. 
        if (isResumed()) {
          //  setListShown(true);  
        } else {  
          //  setListShownNoAnimation(true);  
        } 
	}
	
	
	@Override
	public void onLoaderReset(Loader<Object> loader) {
		SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListView().getExpandableListAdapter();
		if(loader.getId() < 0){
			adapter.setGroupCursor(null);
		} else {
			if(adapter.getGroupCount() > loader.getId()){
				adapter.setChildrenCursor(loader.getId(), null);
			} else {
				getLoaderManager().destroyLoader(loader.getId());
			}
		}
	}	

	
	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		if(arg0 < 0){
			setFooterLoadStart();
		}
		return null;
	}


	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, false, null);
	}
	
	public void openActivityWithFragmentForResult(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle, int requestCode){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, true, requestCode);
	}
	
	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this.getActivity(), HyjBlankUserActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}
	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		if(!getUserVisibleHint()){
//			return super.onContextItemSelected(item);
//		}
//		switch (item.getItemId()) {
//			case DELETE_LIST_ITEM:
//				ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
//				int type = ExpandableListView
//			            .getPackedPositionType(info.packedPosition);
//				   int groupPos = ExpandableListView
//			                .getPackedPositionGroup(info.packedPosition);
//			     
//			    if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//			        int childPos = ExpandableListView
//			                .getPackedPositionChild(info.packedPosition);
//				    Long itemId = getListView().getExpandableListAdapter().getChildId(groupPos, childPos);
//					onDeleteListItem(itemId);
//					return true;
//			    } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//			    	 Long itemId = getListView().getExpandableListAdapter().getGroupId(groupPos);
//					onDeleteListGroup(groupPos, itemId);
//					return true;
//			    }
//				break;
//			case CANCEL_LIST_ITEM:
//				break;
//		}
//		return super.onContextItemSelected(item);
//	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ExpandableListContextMenuInfo adapterContextMenuInfo = (ExpandableListContextMenuInfo) menuInfo;

		if(ExpandableListView.getPackedPositionType(adapterContextMenuInfo.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
			int position = this.getListView().getFlatListPosition(adapterContextMenuInfo.packedPosition);
			if(this.getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
				exitMultiChoiceMode(this.getListView(), position);
			} else {	
				if(getActivity().getCallingActivity() != null){
					if(this.useMultiSelectMenuPickerView() != null){
						enterMultiChoiceMode((HyjExpandableListView)this.getListView(), position);
					}
				} else {
					if(this.useMultiSelectMenuView() != null){
						enterMultiChoiceMode((HyjExpandableListView)this.getListView(), position);
					}
				}
			}
		}
	}	
	
	@Override
	public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
		return false;
	}

	@Override
	public boolean setViewValue(View arg0, Object arg1, String arg2) {
		return false;
	}

	@Override
	public void onGroupExpand(int arg0) {
		
	}

	@Override
	public void onGroupCollapse(int arg0) {
		
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View v, int arg2,
			long arg3) {
			return false;
	}

	public void doFetchMore(int offset, int pageSize){
//		if(getLoaderManager().getLoader(-1) != null && getLoaderManager().getLoader(-1).isStarted()){
//			return;
//		}
		Bundle bundle = new Bundle();
		bundle.putInt("OFFSET", offset);
		bundle.putInt("LIMIT", offset + pageSize);
		getLoaderManager().restartLoader(-1, bundle,this);
	}
	
//	@Override
//	public boolean onChildClick(ExpandableListView parent, View v,
//			int groupPosition, int childPosition, long id) {
//		return false;
//	}  	

	@Override
	public void onGetChildrenCursor(Cursor groupCursor) {

	}
	
	public boolean handleBackPressed() {
		boolean backPressedHandled = false;
		if(getChildFragmentManager().getFragments() != null){
			for(Fragment f : getChildFragmentManager().getFragments()){
				if(f instanceof HyjFragment){
					backPressedHandled = backPressedHandled || ((HyjFragment)f).handleBackPressed();
				} else if(f instanceof HyjUserListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserListFragment)f).handleBackPressed();
				}  else if(f instanceof HyjUserExpandableListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserExpandableListFragment)f).handleBackPressed();
				} 
			}
		} else if(getListView().getChoiceMode() != ListView.CHOICE_MODE_NONE){
			backPressedHandled = true;
			this.exitMultiChoiceMode(getListView(), -1);
		}
		return backPressedHandled;
	}
}


