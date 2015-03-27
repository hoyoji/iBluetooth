package com.hoyoji.hoyoji.money;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyApportion;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.User;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MoneyApportionField extends GridView {
	private ImageGridAdapter mImageGridAdapter;
	private	Resources r = getResources();
	private Set<ApportionItem<MoneyApportion>> mHiddenApportionItems = new HashSet<ApportionItem<MoneyApportion>>();
	private double mTotalAmount = 0.0;
	private String mMoneyTransactionId = null;
	private boolean mIsHideMoney = false;
	
	public MoneyApportionField(Context context, AttributeSet attrs) {
		super(context, attrs);
//		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
//		this.setColumnWidth(px);
		this.setNumColumns(AUTO_FIT);
		this.setGravity(Gravity.CENTER);
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
		this.setVerticalSpacing(px);
		this.setHorizontalSpacing(px);
		//this.setStretchMode(STRETCH_COLUMN_WIDTH);
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		mImageGridAdapter = new ImageGridAdapter(context, 0);
		this.setAdapter(mImageGridAdapter);
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
	
	public void init(Double totalAmount, List<? extends MoneyApportion> apportions, String projectId, String eventId, String moneyTransactionId){
		mMoneyTransactionId = moneyTransactionId;
		mTotalAmount = totalAmount;
		//List<PictureItem> pis = new ArrayList<PictureItem>();
		for(int i=0; i < apportions.size(); i++){
			ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportions.get(i), projectId, eventId, apportions.get(i).get_mId() == null ? ApportionItem.NEW : ApportionItem.UNCHANGED);
			mImageGridAdapter.add(pi);
			//pis.add(pi);
		}
		//mImageGridAdapter.addAll(pis);
	}
	
	public ImageGridAdapter getAdapter(){
		return mImageGridAdapter;
	}
	
	public int getApportionCount(){
		int count = 0;
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED){
				count ++;
			}
		}
		return count;
	}
	
	public boolean addApportion(MoneyApportion apportion, String projectId, String eventId, int state){
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if((api.getApportion().getFriendUserId() != null && api.getApportion().getFriendUserId().equalsIgnoreCase(apportion.getFriendUserId())) ||
					(api.getApportion().getLocalFriendId() != null && api.getApportion().getLocalFriendId().equalsIgnoreCase(apportion.getLocalFriendId()))){
				return false;
			}
		}
		ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportion, projectId, eventId, state);
		mImageGridAdapter.add(pi);
		return true;
	}
	
	public void setAllApportionShare(){
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				api.setApportionType("Share");
			}
		}
	}
	
	public void setAllApportionAverage(){
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				api.setApportionType("Average");
			}
		}
	}
	
	public Double getTotalAmount(){
		double total = 0.0;
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				total += api.getAmount();
			}
		}
		return HyjUtil.toFixed2(total);
	}
	
	public int getCount(){
		int count = 0;
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				count++;
			}
		}
		return count;
	}
	
	
	public void setTotalAmount(Double totalAmount){
		double fixedTotal = 0.0;
		double sharePercentageTotal = 0.0;
		
		double averageAmount = 0.0;
		double shareTotal = 0.0;
		int numOfAverage = 0;
		if(totalAmount == null){
			totalAmount = mTotalAmount;
		} else {
			mTotalAmount = HyjUtil.toFixed2(totalAmount);
		}
		
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				if(api.getApportionType().equalsIgnoreCase("Average")){
					numOfAverage++;
					sharePercentageTotal += api.getSharePercentage();
				} else if(api.getApportionType().equalsIgnoreCase("Share")){
//					api.setAmount(api.getAmount());
					sharePercentageTotal += api.getSharePercentage();
				} else {
					fixedTotal += api.getAmount();
				}
			}
		}
		
		
		// 占股分摊=（总金额-定额分摊）*占股/（分摊人所占股数）
		shareTotal = totalAmount - fixedTotal;
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				if(api.getApportionType().equalsIgnoreCase("Share")){
					Double shareAmount = shareTotal * api.getSharePercentage() / sharePercentageTotal;
					api.setAmount(shareAmount);
					fixedTotal += api.getAmount();
				}
			}
		}
		
		// 平均分摊 = （总金额-定额分摊-占股分摊） / 平均分摊人数
		averageAmount = (totalAmount - fixedTotal) / numOfAverage;
		ApportionItem firstNonDeletedItem = null;
		for(int i = 0; i < mImageGridAdapter.getCount(); i++){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() != ApportionItem.DELETED) {
				if(api.getApportionType().equalsIgnoreCase("Average")){
					api.setAmount(averageAmount);
					fixedTotal += api.getAmount();
				}
				if(firstNonDeletedItem == null){
					firstNonDeletedItem = api;
				}
			}
		}
		if(mImageGridAdapter.getCount() > 0){
			if(fixedTotal != totalAmount && firstNonDeletedItem != null){
				double adjustedAmount = firstNonDeletedItem.getAmount() + (totalAmount - fixedTotal);
				firstNonDeletedItem.setAmount(adjustedAmount);
			}
		}
		mImageGridAdapter.notifyDataSetChanged();
	}
	
	public Set<ApportionItem<MoneyApportion>> getHiddenApportions(){
		return mHiddenApportionItems;
	}
	
	public void clearAll(){
		int i = 0; 
		while(mImageGridAdapter.getCount() > 0 && i < mImageGridAdapter.getCount()){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
				if(api.getState() == ApportionItem.NEW){
					mImageGridAdapter.remove(api);
				} else if(api.getState() != ApportionItem.DELETED){
					api.delete();
					i++;
				} else {
					i++;
				}
		}
	}
	
	public void removeAll(){
		int i = 0; 
		while(mImageGridAdapter.getCount() > 0 && i < mImageGridAdapter.getCount()){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			if(api.getState() == ApportionItem.NEW){
				mImageGridAdapter.remove(api);
			} else{
				mImageGridAdapter.remove(api);
				mHiddenApportionItems.add(api);
				i++;
			}
		}
	}
	
	public void changeProject(Project project, String eventId, Class<? extends MoneyApportion> type){
		List<ProjectShareAuthorization> projectShareAuthorizations = project.getShareAuthorizations();
//		Set<String> friendUserSet = new HashSet<String>();
		Set<String> gridUserSet = new HashSet<String>();
		
//		// 把新圈子的成员都记录下来
//		for(int i=0; i < projectShareAuthorizations.size(); i++){
//			if(!projectShareAuthorizations.get(i).getState().equals("Delete")) {
//				friendUserSet.add(HyjUtil.ifNull(projectShareAuthorizations.get(i).getFriendUserId(), projectShareAuthorizations.get(i).getLocalFriendId()));
//			}
//		}	

		// 把不属于当前圈子用户分摊隐藏掉
		int i = 0; 
		while(mImageGridAdapter.getCount() > 0 && i < mImageGridAdapter.getCount()){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			mHiddenApportionItems.add(api);
			mImageGridAdapter.remove(api);
			
//			if(api.getApportion().getFriendUserId() != null){
//				// 圈子成员, 但不是新圈子的成员, 隐藏起来
//	        	Friend friend = new Select().from(Friend.class).where("friendUserId=?", api.getApportion().getFriendUserId()).executeSingle();
//				if(!friendUserSet.contains(api.getApportion().getFriendUserId())) {
//					mHiddenApportionItems.add(api);
//					mImageGridAdapter.remove(api);
////					gridUserSet.add(friend.getId());
////					api.changeProjectWithNonProjectMember(project.getId(), friend);
//				} else {
//					gridUserSet.add(friend.getFriendUserId());
//					api.changeProject(project.getId());
//					i++;
//				}
//			} else {
//				// 非圈子成员
//				Friend friend = HyjModel.getModel(Friend.class, api.getApportion().getLocalFriendId());
//				if(friend.getFriendUserId() != null){ 
//					// 网络好友
//					if(!friendUserSet.contains(friend.getFriendUserId())) {
//						// 也不是是新圈子的成员。非圈子成员不能转为圈子成员，我们只好将其隐藏
////						mHiddenApportionItems.add(api);
////						mImageGridAdapter.remove(api);
//						gridUserSet.add(friend.getId());
//						api.changeProject(project.getId());
//						i++;
//					} else {
//						mHiddenApportionItems.add(api);
//						mImageGridAdapter.remove(api);
//						// 是新圈子的成员，我们直接转过去
////						gridUserSet.add(friend.getFriendUserId());
////						api.changeProjectWithProjectMember(project.getId(), friend);
////						i++;
//					}
//				} else {
//					//本地好友，直接转过去
//					gridUserSet.add(friend.getId());
//					api.changeProject(project.getId());
//					i++;
//				}
//			}
//			
////			if(!friendUserSet.contains(api.getApportion().getFriendUserId()) && api.getApportion().getLocalFriendId() == null){
////					mHiddenApportionItems.add(api);
////					mImageGridAdapter.remove(api);
////			} else {
////				gridUserSet.add(HyjUtil.ifNull(api.getApportion().getFriendUserId(), api.getApportion().getLocalFriendId()));
////				api.changeProject(project.getId());
////				i++;
////			}
		}

		// 把隐藏掉的分摊添加回去
	    Iterator<ApportionItem<MoneyApportion>> it = mHiddenApportionItems.iterator();
	    while (it.hasNext()) {
	        // Get element
	        ApportionItem<MoneyApportion> item = it.next();
	        if(eventId == null){
	        	eventId = "";
	        }
	        if(item.getProjectId().equals(project.getId()) && eventId.equals(HyjUtil.ifNull(item.getEventId(), ""))){
	        	mImageGridAdapter.add(item);
	        	if(item.getApportion().getFriendUserId() != null){
	        		gridUserSet.add(item.getApportion().getFriendUserId());
	        		it.remove();
	        	} else {
	        		gridUserSet.add(item.getApportion().getLocalFriendId());
	        		it.remove();
	        	}
//				item.changeProject(project.getId());
	        }
	        
//	        if(item.getApportion().getLocalFriendId() != null){
//	        	Friend friend = HyjModel.getModel(Friend.class, item.getApportion().getLocalFriendId());
//	        	if(friend.getFriendUserId() != null){ 
//					// 网络好友
//					if(!friendUserSet.contains(friend.getFriendUserId())) {
//						// 也不是是新圈子的成员
//			        	mImageGridAdapter.add(item);
//						gridUserSet.add(friend.getId());
//						item.changeProject(project.getId());
//			        	it.remove();
//					} else {
//						// 是新圈子的成员，我们直接转过去
////						mImageGridAdapter.add(item);
////						gridUserSet.add(friend.getFriendUserId());
////						item.changeProjectWithProjectMember(project.getId(), friend);
////			        	it.remove();
//					}
//				} else {
//					//本地好友，直接转过去
//					mImageGridAdapter.add(item);
//					gridUserSet.add(friend.getId());
//					item.changeProject(project.getId());
//		        	it.remove();
//				}
//	        } else {
////	        	Friend friend = new Select().from(Friend.class).where("friendUserId=?", item.getApportion().getFriendUserId()).executeSingle();
////	        	if(friendUserSet.contains(friend.getFriendUserId())){
//					mImageGridAdapter.add(item);
//	        		gridUserSet.add(item.getApportion().getFriendUserId());
//		        	item.changeProject(project.getId());
//		        	it.remove();
////		        } else {
////		        	gridUserSet.add(friend.getId());
////		        	item.changeProjectWithNonProjectMember(project.getId(), friend);
////			    }
//	        }
	    }
	    
	    if(project.getAutoApportion()){
		    int count = projectShareAuthorizations.size();
			for(i = 0; i < count; i++){
				if(projectShareAuthorizations.get(i).getState().equals("Delete")) {
					continue;
				}
				if(!gridUserSet.contains(HyjUtil.ifNull(projectShareAuthorizations.get(i).getFriendUserId(), projectShareAuthorizations.get(i).getLocalFriendId()))){
					try {
						MoneyApportion apportion;
						apportion = type.newInstance();
						apportion.setAmount(0.0);
						if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
							apportion.setApportionType("Average");
						} else {
							apportion.setApportionType("Share");
						}
						apportion.setMoneyId(mMoneyTransactionId);
						apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
						apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
						//this.addApportion(apportion, project.getId(), ApportionItem.NEW);
						ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportion, project.getId(), eventId, ApportionItem.NEW);
						mImageGridAdapter.add(pi);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
	    }
	    if(mImageGridAdapter.getCount() == 0){
	    	MoneyApportion apportion;
			try {
				apportion = type.newInstance();
				apportion.setAmount(0.0);
				apportion.setMoneyId(mMoneyTransactionId);
				apportion.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
				ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", project.getId(), apportion.getFriendUserId()).executeSingle();
				if(projectShareAuthorization.getSharePercentageType() != null && projectShareAuthorization.getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportion, project.getId(), eventId, ApportionItem.NEW);
				mImageGridAdapter.add(pi);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
	    }
	}
	
	public void changeEvent(Project project, Event event, Class<? extends MoneyApportion> type){
		List<ProjectShareAuthorization> projectShareAuthorizations = project.getShareAuthorizations();
		Set<String> gridUserSet = new HashSet<String>();
		// 把不属于当前圈子用户分摊隐藏掉
		int i = 0; 
		while(mImageGridAdapter.getCount() > 0 && i < mImageGridAdapter.getCount()){
			ApportionItem<MoneyApportion> api = (ApportionItem<MoneyApportion>) mImageGridAdapter.getItem(i);
			mHiddenApportionItems.add(api);
			mImageGridAdapter.remove(api);
		}
		// 把隐藏掉的分摊添加回去
	    Iterator<ApportionItem<MoneyApportion>> it = mHiddenApportionItems.iterator();
	    while (it.hasNext()) {
	        // Get element
	        ApportionItem<MoneyApportion> item = it.next();
	        String eventId = "";
	        if(event != null){
	        	eventId = event.getId();
	        }
	        if(item.getProjectId().equals(project.getId()) && eventId.equals(HyjUtil.ifNull(item.getEventId(), ""))){
	        	EventMember em = null;
	        	if(item.getApportion().getFriendUserId() != null){
//					em = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", event.getId(), item.getApportion().getFriendUserId()).executeSingle();
//					if(em != null) {
//						mImageGridAdapter.add(item);
//						gridUserSet.add(item.getApportion().getFriendUserId());
						it.remove();
//					}
				} else {
					em = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", event.getId(), item.getApportion().getLocalFriendId()).executeSingle();
					if(em != null) {
						if(em.getToBeDetermined()) {
							mImageGridAdapter.add(item);
							gridUserSet.add(item.getApportion().getLocalFriendId());
						}
						it.remove();
					}
				}
//		        	if(item.getApportion().getFriendUserId() != null){
//		        		gridUserSet.add(item.getApportion().getFriendUserId());
//		        	} else {
//		        		gridUserSet.add(item.getApportion().getLocalFriendId());
//		        	}
//					item.changeProject(project.getId());
	        }
	    }
	    
	    if(project.getAutoApportion()){
		    int count = projectShareAuthorizations.size();
			for(i = 0; i < count; i++){
				if(projectShareAuthorizations.get(i).getState().equals("Delete")) {
					continue;
				}
				if(!gridUserSet.contains(HyjUtil.ifNull(projectShareAuthorizations.get(i).getFriendUserId(), projectShareAuthorizations.get(i).getLocalFriendId()))){
					try {
						EventMember em = null;
						if(projectShareAuthorizations.get(i).getFriendUserId() != null){
							em = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", event.getId(), projectShareAuthorizations.get(i).getFriendUserId()).executeSingle();
						} else {
							em = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", event.getId(), projectShareAuthorizations.get(i).getLocalFriendId()).executeSingle();
						}
						if(em != null) {
							MoneyApportion apportion;
							apportion = type.newInstance();
							apportion.setAmount(0.0);
							if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
								apportion.setApportionType("Average");
							} else {
								apportion.setApportionType("Share");
							}
							apportion.setMoneyId(mMoneyTransactionId);
							apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
							apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
							//this.addApportion(apportion, project.getId(), ApportionItem.NEW);
							ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportion, project.getId(), event.getId(), ApportionItem.NEW);
							mImageGridAdapter.add(pi);
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
	    }
	    if(mImageGridAdapter.getCount() == 0){
	    	MoneyApportion apportion;
			try {
//				Friend toBeDeterminedFriend = new Select().from(Friend.class).where("toBeDetermined = 1 AND ownerUserId = ?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
//				if(toBeDeterminedFriend != null){
				ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND toBeDetermined=1", project.getId()).executeSingle();
				if(projectShareAuthorization != null){
					EventMember em = new Select().from(EventMember.class).where("eventId=? AND toBeDetermined=1", event.getId()).executeSingle();
					if(em != null) {
						apportion = type.newInstance();
						apportion.setAmount(0.0);
						apportion.setMoneyId(mMoneyTransactionId);
						apportion.setFriendUserId(em.getFriendUserId());
						apportion.setLocalFriendId(em.getLocalFriendId());
	//					ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", project.getId(), toBeDeterminedFriend.getId()).executeSingle();
						if(projectShareAuthorization.getSharePercentageType().equals("Average")){
							apportion.setApportionType("Average");
						} else {
							apportion.setApportionType("Share");
						}
						ApportionItem<MoneyApportion> pi = new ApportionItem<MoneyApportion>(apportion, project.getId(), event.getId(), ApportionItem.NEW);
						mImageGridAdapter.add(pi);
					}
				}
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
	    }
	}
	
	public void setError(String errMsg){
		if(errMsg != null){
			HyjUtil.displayToast(errMsg);
		}
	}
	
	public static class ImageGridAdapter extends ArrayAdapter<ApportionItem<MoneyApportion>> {
		LayoutInflater inflater;
		public ImageGridAdapter(Context context, int resource) {
			super(context, resource);
			inflater = (LayoutInflater)
		       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		static class ViewHolder{
			public HyjImageView imageViewPicture;
			public TextView textViewPercentage;
			public TextView textViewApportionType;
			public TextView textViewFriendName;
			public HyjNumericView textViewAmount;
			public ApportionItem<?> apportionItem;
			public ViewHolder(){}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View iv;
			ViewHolder vh;
			final MoneyApportionField self = (MoneyApportionField) parent;
			if (convertView != null) {
				iv = convertView;
				vh = (ViewHolder) convertView.getTag();
			} else {
				iv = inflater.inflate(R.layout.money_listitem_moneyapportion, null);
				vh = new ViewHolder();
				vh.imageViewPicture = (HyjImageView) iv.findViewById(R.id.moneyApportionListItem_picture);
				vh.imageViewPicture.setDefaultImage(R.drawable.ic_action_person_white);
				vh.textViewAmount = (HyjNumericView) iv.findViewById(R.id.moneyApportionListItem_amount);
				vh.textViewPercentage = (TextView) iv.findViewById(R.id.moneyApportionListItem_percentage);
				vh.textViewFriendName = (TextView) iv.findViewById(R.id.moneyApportionListItem_friendName);
				vh.textViewApportionType = (TextView) iv.findViewById(R.id.moneyApportionListItem_apportionType);
				iv.setTag(vh);
					
				if(self.mIsHideMoney){
					vh.textViewAmount.setVisibility(View.GONE);
				}
				
//				float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, self.r.getDisplayMetrics());
//				iv.setLayoutParams(new LayoutParams((int)px, (int)px));
				iv.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if(!self.isEnabled()){
							return;
						}
						final ApportionItem<MoneyApportion> apportionItem = (ApportionItem<MoneyApportion>) ((ViewHolder)v.getTag()).apportionItem;
						if(apportionItem.getState() == ApportionItem.DELETED){
							apportionItem.undelete();
							self.setTotalAmount(null);
						} else {
							final HyjActivity activity = (HyjActivity) getContext();
							
							activity.mDialogCallback = new HyjActivity.DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object bundle) {
//									if(bundle != null){
									Bundle b = (Bundle)bundle;
							    	final String apportionType = b.getString("apportionType");
							    	Double apportionAmount = b.getDouble("apportionAmount");
									apportionItem.setAmount(apportionAmount);
									apportionItem.setApportionType(apportionType);
									self.setTotalAmount(null);

//									} else {
//										MoneyApportionEditDialogFragment f = (MoneyApportionEditDialogFragment)activity.mDialogFragment;
//										apportionItem.setApportionType(f.getApportionType());
//										self.setTotalAmount(null);
//										f.setApportionAmount(apportionItem.getAmount());
//									}
								}
								
								@Override
								public void doNegativeClick() {
									if(apportionItem.getState() == ApportionItem.NEW){
										self.mImageGridAdapter.remove(apportionItem);
									} else if(apportionItem.getState() != ApportionItem.DELETED){
										apportionItem.delete();
									} 
									self.setTotalAmount(null);
								}
							};
							
							activity.mDialogFragment = MoneyApportionEditDialogFragment.newInstance(apportionItem.getAmount(), apportionItem.getApportionType(), apportionItem.getProjectShareAuthorization() != null, self.mIsHideMoney);
							activity.mDialogFragment.show(activity.getSupportFragmentManager(), "dialog");
						}
					}
				});
			}
			if(vh.apportionItem != getItem(position)){
				vh.apportionItem = getItem(position);

				vh.imageViewPicture.setBackgroundColor(self.r.getColor(R.color.hoyoji_yellow));
				if(vh.apportionItem.getApportion().getFriendUser() != null){
					if(vh.apportionItem.getApportion().getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						vh.imageViewPicture.setBackgroundColor(self.r.getColor(R.color.hoyoji_red));
					} else {
						vh.imageViewPicture.setBackgroundColor(self.r.getColor(R.color.hoyoji_green));
					}
					vh.imageViewPicture.setImage(vh.apportionItem.getApportion().getFriendUser().getPictureId());
				} else if(vh.apportionItem.getApportion().getLocalFriendId() != null){
					Friend friend = HyjModel.getModel(Friend.class, vh.apportionItem.getApportion().getLocalFriendId());
					if(friend == null){
//						User user = HyjModel.getModel(User.class, vh.apportionItem.getApportion().getOwnerUserId());
//						if(user != null){
//							vh.imageViewPicture.setImage(user.getPictureId());
//						} else {
							vh.imageViewPicture.setImage((Picture)null);
//						}
					} else if(friend.getFriendUserId() != null){
						vh.imageViewPicture.setImage(friend.getFriendUser().getPictureId());
					} else {
						vh.imageViewPicture.setImage((Picture)null);
					}
				} else {
					vh.imageViewPicture.setImage((Picture)null);
				}
//				if(vh.apportionItem.getFriend() != null){
//					vh.textViewFriendName.setText(vh.apportionItem.getFriend().getDisplayName());
//				} else {
//					vh.textViewFriendName.setText(vh.apportionItem.getApportion().getFriendUser().getDisplayName());
//				}
				vh.textViewFriendName.setText(vh.apportionItem.getFriendDisplayName());
			}
			
			if(vh.apportionItem.getState() == ApportionItem.DELETED){
				vh.textViewAmount.setPrefix(R.string.moneyListItem_apportion_to_be_removed);
				vh.textViewAmount.setNumber(null);
				vh.textViewAmount.setTextColor(self.r.getColor(R.color.hoyoji_red));
				vh.textViewPercentage.setPaintFlags(vh.textViewPercentage.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				vh.textViewFriendName.setPaintFlags(vh.textViewFriendName.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				vh.textViewApportionType.setPaintFlags(vh.textViewApportionType.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				vh.textViewAmount.setPrefix(null);
				vh.textViewAmount.setNumber(vh.apportionItem.getAmount());
				vh.textViewAmount.setTextColor(Color.parseColor("#000000"));
				vh.textViewPercentage.setPaintFlags(vh.textViewPercentage.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
				vh.textViewFriendName.setPaintFlags(vh.textViewFriendName.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
				vh.textViewApportionType.setPaintFlags(vh.textViewApportionType.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
			}
			if(vh.apportionItem.getProjectShareAuthorization() == null){
				if(vh.apportionItem.getEventId() == null){
					vh.textViewPercentage.setText(self.r.getString(R.string.moneyListItem_apportion_non_project_member));
				} else {
					vh.textViewPercentage.setText(self.r.getString(R.string.moneyListItem_apportion_event_member));
				}
			} else {
				vh.textViewPercentage.setText(self.r.getString(R.string.moneyListItem_apportion_share) + vh.apportionItem.getSharePercentage() + "%");
			}
			if(vh.apportionItem.getApportionType().equalsIgnoreCase("Average")){
				vh.textViewApportionType.setText(R.string.moneyListItem_apportion_average_apport);
			} else if(vh.apportionItem.getApportionType().equalsIgnoreCase("Fix")){
				vh.textViewApportionType.setText(R.string.moneyListItem_apportion_fixed_apport);
			} else {
				vh.textViewApportionType.setText(R.string.moneyListItem_apportion_share_apport);
			}
			return iv;
		}

	}
	
	public static class ApportionItem<T extends MoneyApportion> {
		public static final int UNCHANGED = 0;
		public static final int NEW = 1;
		public static final int DELETED = 2;
		public static final int CHANGED = 3;
		
		private int mState = UNCHANGED;
		private T mApportion;
		private String mProjectId;
		private String mEventId;
		private ProjectShareAuthorization mProjectShareAuthorization = null;
		private Friend mFriend = null;
		private Double mAmount;
		private String mApportionType;
		private EventMember mEventMember;
//		
//		ApportionItem(T apportion, String projectId){
//			mApportion = apportion;
//			mProjectId = projectId;
//		}
//		
		public ApportionItem(T apportion, String projectId, String eventId, int state){
			mApportion = apportion;
			mState = state;
			mProjectId = projectId;
			mEventId = eventId;
			mAmount = apportion.getAmount();
			mApportionType = apportion.getApportionType();
		}
		
		public String getEventId() {
			return mEventId;
		}

		public Double getSharePercentage() {
			if(this.getProjectShareAuthorization() != null){
				return this.getProjectShareAuthorization().getSharePercentage();
			} 
			return 0.0;
		}

		public void changeProject(String projectId, String eventId){
			mProjectId = projectId;
			mProjectShareAuthorization = null;
			mEventId = eventId;
			mEventMember = null;
		}
		
		public ProjectShareAuthorization getProjectShareAuthorization(){
			if(mProjectShareAuthorization == null){
				if(mApportion.getFriendUserId() != null) {
					mProjectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=? AND state <> ?", 
						mProjectId, mApportion.getFriendUserId(), "Delete").executeSingle();
				} else if(mApportion.getLocalFriendId() != null){
					mProjectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=? AND state <> ?", 
							mProjectId, mApportion.getLocalFriendId(), "Delete").executeSingle();
				}
			} 
			return mProjectShareAuthorization;
		}
		
		public EventMember getEventMember(){
			if(mEventId == null){
				return null;
			}
			if(mEventMember == null){
				if(mApportion.getFriendUserId() != null) {
					mEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
						mEventId, mApportion.getFriendUserId()).executeSingle();
				} else if(mApportion.getLocalFriendId() != null){
					mEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
							mEventId, mApportion.getLocalFriendId()).executeSingle();
				}
			} 
			return mEventMember;
		}
		
		public void setAmount(Double amount){
			mAmount = HyjUtil.toFixed2(amount);
		}
		
		public Double getAmount(){
			return mAmount;
		}
		
		public Friend getFriend(){
			if(mFriend == null){
				if(mApportion.getFriendUserId() != null){
					mFriend = new Select().from(Friend.class).where("friendUserId=?",mApportion.getFriendUserId()).executeSingle();
				} else if(mApportion.getLocalFriendId() != null){
					mFriend = HyjModel.getModel(Friend.class, mApportion.getLocalFriendId());
				}
			}
			return mFriend;
		}
		
		public String getFriendDisplayName(){
			EventMember em = getEventMember();
			if(em != null){
				return HyjUtil.ifEmpty(em.getNickName(), em.getFriendDisplayName());
			}
			if(this.getFriend() != null){
				return this.getFriend().getDisplayName();
			}
			if(mApportion.getFriendUserId() != null){
				User user = HyjModel.getModel(User.class, mApportion.getFriendUserId());
				if(user != null){
					return user.getDisplayName();
				}
			}
			ProjectShareAuthorization psa = getProjectShareAuthorization();
			if(psa != null){
				return psa.getFriendUserName();
			}
			return "NO NAME";
		}
		
//		public void setState(int state){
//			mState = state;
//		}
		
		public void delete(){
			mState = DELETED;
		}
		
		public void undelete(){
			mState = UNCHANGED;
		}
		
		public int getState(){
			if(mState == UNCHANGED){
				if(mApportion.getAmount().compareTo(mAmount) != 0 || 
						!mApportion.getApportionType().equalsIgnoreCase(mApportionType)){
					return CHANGED;
				}
			}
			return mState;
		}

		public void setApportionType(String apportionType){
			mApportionType = apportionType;
		}
		
		public String getApportionType(){
			return mApportionType;
		}
		
		public T getApportion() {
			return mApportion;
		}
		
		public void saveToCopy(MoneyApportion apportion){
			apportion.setAmount(mAmount);
			apportion.setApportionType(mApportionType);
		}
		
		public String getProjectId(){
			return mProjectId;
		}
		
	}

	public void setHideMoney(boolean b) {
		mIsHideMoney  = b;
	}

	
}
