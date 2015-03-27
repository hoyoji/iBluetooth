package com.hoyoji.android.hyjframework;

import java.util.HashMap;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.hoyoji.hoyoji.models.ClientSyncRecord;
import com.hoyoji.hoyoji.models.Currency;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.FriendCategory;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MessageBox;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyBorrowApportion;
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnApportion;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseCategory;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyIncomeCategory;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.MoneyLendApportion;
import com.hoyoji.hoyoji.models.MoneyLendContainer;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyPaybackApportion;
import com.hoyoji.hoyoji.models.MoneyPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.MoneyReturnApportion;
import com.hoyoji.hoyoji.models.MoneyReturnContainer;
import com.hoyoji.hoyoji.models.MoneyTemplate;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.ParentProject;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectRemark;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.QQLogin;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.models.WBLogin;
import com.hoyoji.hoyoji.models.WXLogin;

public abstract class HyjModel extends Model  implements Cloneable {

	public HyjModel(){
		super();
		if(HyjApplication.getInstance().getCurrentUser() != null){
			this.setCreatorId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		setLastClientUpdateTime(System.currentTimeMillis());
	}
	
	public static <T extends Model> T getModel(Class<T> modelClass, String id){
		T entity = (T)Cache.getEntity(modelClass, id);
		if (entity == null) {
			entity = new Select().from(modelClass).where("id=?", id).executeSingle();
		}
		return entity;
	}	
	
	
	@Override
	protected HyjModel clone() {
		try {
			return (HyjModel)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract void setId(String id);
	
	public abstract String getId();
	
	public abstract void setCreatorId(String id);
//	{
//		m_creatorId = id;
//	}
	public abstract String getCreatorId();
//	{
//		return m_creatorId;
//	}
	
	public abstract String getServerRecordHash();
//	{
//		return mServerRecordHash;
//	}

	public abstract void setServerRecordHash(String mServerRecordHash);
//	{
//		this.mServerRecordHash = mServerRecordHash;
//	}

	public abstract String getLastServerUpdateTime();
//	{
//		return mLastServerUpdateTime;
//	}

	public abstract void setLastServerUpdateTime(String mLastServerUpdateTime);
//	{
//		this.mLastServerUpdateTime = mLastServerUpdateTime;
//	}

	public abstract Long getLastClientUpdateTime();
//	{
//		return mLastClientUpdateTime;
//	}

	public abstract void setLastClientUpdateTime(Long mLastClientUpdateTime);
//	{
//		this.mLastClientUpdateTime = mLastClientUpdateTime;
//	}	
	
	public HyjModelEditor newModelEditor(){
		return new HyjModelEditor(this);
	}

	@Override
	public void save(){
		if(this.getCreatorId() == null){
			this.setCreatorId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		if(!this.getSyncFromServer()){
			this.setLastClientUpdateTime(System.currentTimeMillis());
		}
		super.save();
	}
	
	public boolean isClientNew(){
		ClientSyncRecord clientSyncRecord = this.getClientSyncRecord();
		if(clientSyncRecord == null){
			return false;
		} else {
			return clientSyncRecord.getOperation().equalsIgnoreCase("Create");
		}
	}
	
	public ClientSyncRecord getClientSyncRecord(){
		return new Select().from(ClientSyncRecord.class).where("id=?", getId()).executeSingle();
	}
	
	final public void deleteFromServer(){
		this.setSyncFromServer(true);
		super.delete();
	}
	
	public abstract void validate(HyjModelEditor<? extends HyjModel> hyjModelEditor);
	
	private static HashMap<String, Class<? extends Model>> modelTypeMap = new HashMap<String, Class<? extends Model>>();

	public static HyjModel createModel(String tableName) {
		Model model = null;
		Class<? extends Model> type = modelTypeMap.get(tableName);
		if(type == null){
			// 缓存到 HashMap, 提高下次访问的速度
			for(TableInfo tableInfo : Cache.getTableInfos()){
				if(tableInfo.getTableName().equals(tableName)){
					type = tableInfo.getType();
					modelTypeMap.put(tableName, type);
					break;
				}
			}
		}
		try {
			model = type.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return (HyjModel) model;
	}
	
	public static HyjModel createModel(String tableName, String id) {
		Model model = null;
		Class<? extends Model> type = modelTypeMap.get(tableName);
		if(type == null){
			// 缓存到 HashMap, 提高下次访问的速度
			for(TableInfo tableInfo : Cache.getTableInfos()){
				if(tableInfo.getTableName().equals(tableName)){
					type = tableInfo.getType();
					modelTypeMap.put(tableName, type);
					break;
				}
			}
		}
		model = HyjModel.getModel(type, id);
		if(model == null){
			try {
				model = type.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
		return (HyjModel) model;
		
//		if (tableName.equalsIgnoreCase("Currency")) {
//			model = HyjModel.getModel(Currency.class, id);
//			if(model == null){
//				model = new Currency();
//			}
//		} else if (tableName.equalsIgnoreCase("Exchange")) {
//			model = HyjModel.getModel(Exchange.class, id);
//			if(model == null){
//				model = new Exchange();
//			}
//		} else if (tableName.equalsIgnoreCase("Friend")) {
//			model = HyjModel.getModel(Friend.class, id);
//			if(model == null){
//				model = new Friend();
//			}
//		} else if (tableName.equalsIgnoreCase("FriendCategory")) {
//			model = HyjModel.getModel(FriendCategory.class, id);
//			if(model == null){
//				model = new FriendCategory();
//			}
//		} else if (tableName.equalsIgnoreCase("Message")) {
//			model = HyjModel.getModel(Message.class, id);
//			if(model == null){
//				model = new Message();
//			}
//		}  else if (tableName.equalsIgnoreCase("QQLogin")) {
//			model = HyjModel.getModel(QQLogin.class, id);
//			if(model == null){
//				model = new QQLogin();
//			}
//		}   else if (tableName.equalsIgnoreCase("WBLogin")) {
//			model = HyjModel.getModel(WBLogin.class, id);
//			if(model == null){
//				model = new WBLogin();
//			}
//		}    else if (tableName.equalsIgnoreCase("WXLogin")) {
//			model = HyjModel.getModel(WXLogin.class, id);
//			if(model == null){
//				model = new WXLogin();
//			}
//		} else if (tableName.equalsIgnoreCase("MessageBox")) {
//			model = HyjModel.getModel(MessageBox.class, id);
//			if(model == null){
//				model = new MessageBox();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyAccount")) {
//			model = HyjModel.getModel(MoneyAccount.class, id);
//			if(model == null){
//				model = new MoneyAccount();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyBorrow")) {
//			model = HyjModel.getModel(MoneyBorrow.class, id);
//			if(model == null){
//				model = new MoneyBorrow();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyBorrowApportion")) {
//			model = HyjModel.getModel(MoneyBorrowApportion.class, id);
//			if(model == null){
//				model = new MoneyBorrowApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyExpense")) {
//			model = HyjModel.getModel(MoneyExpense.class, id);
//			if(model == null){
//				model = new MoneyExpense();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyExpenseContainer")) {
//			model = HyjModel.getModel(MoneyExpenseContainer.class, id);
//			if(model == null){
//				model = new MoneyExpenseContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyExpenseApportion")) {
//			model = HyjModel.getModel(MoneyExpenseApportion.class, id);
//			if(model == null){
//				model = new MoneyExpenseApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyIncome")) {
//			model = HyjModel.getModel(MoneyIncome.class, id);
//			if(model == null){
//				model = new MoneyIncome();
//			}
//		}  else if (tableName.equalsIgnoreCase("MoneyIncomeContainer")) {
//			model = HyjModel.getModel(MoneyIncomeContainer.class, id);
//			if(model == null){
//				model = new MoneyIncomeContainer();
//			}
//		}  else if (tableName.equalsIgnoreCase("MoneyDepositIncomeContainer")) {
//			model = HyjModel.getModel(MoneyDepositIncomeContainer.class, id);
//			if(model == null){
//				model = new MoneyDepositIncomeContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyDepositReturnContainer")) {
//			model = HyjModel.getModel(MoneyDepositReturnContainer.class, id);
//			if(model == null){
//				model = new MoneyDepositReturnContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyDepositPaybackContainer")) {
//			model = HyjModel.getModel(MoneyDepositPaybackContainer.class, id);
//			if(model == null){
//				model = new MoneyDepositPaybackContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyIncomeApportion")) {
//			model = HyjModel.getModel(MoneyIncomeApportion.class, id);
//			if(model == null){
//				model = new MoneyIncomeApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyDepositIncomeApportion")) {
//			model = HyjModel.getModel(MoneyDepositIncomeApportion.class, id);
//			if(model == null){
//				model = new MoneyDepositIncomeApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyDepositExpenseContainer")) {
//			model = HyjModel.getModel(MoneyDepositExpenseContainer.class, id);
//			if(model == null){
//				model = new MoneyDepositExpenseContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyDepositReturnApportion")) {
//			model = HyjModel.getModel(MoneyDepositReturnApportion.class, id);
//			if(model == null){
//				model = new MoneyDepositReturnApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyLend")) {
//			model = HyjModel.getModel(MoneyLend.class, id);
//			if(model == null){
//				model = new MoneyLend();
//			}
//		}  else if (tableName.equalsIgnoreCase("MoneyLendContainer")) {
//			model = HyjModel.getModel(MoneyLendContainer.class, id);
//			if(model == null){
//				model = new MoneyLendContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyLendApportion")) {
//			model = HyjModel.getModel(MoneyLendApportion.class, id);
//			if(model == null){
//				model = new MoneyLendApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyPayback")) {
//			model = HyjModel.getModel(MoneyPayback.class, id);
//			if(model == null){
//				model = new MoneyPayback();
//			}
//		}  else if (tableName.equalsIgnoreCase("MoneyPaybackContainer")) {
//			model = HyjModel.getModel(MoneyPaybackContainer.class, id);
//			if(model == null){
//				model = new MoneyPaybackContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyPaybackApportion")) {
//			model = HyjModel.getModel(MoneyPaybackApportion.class, id);
//			if(model == null){
//				model = new MoneyPaybackApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyReturn")) {
//			model = HyjModel.getModel(MoneyReturn.class, id);
//			if(model == null){
//				model = new MoneyReturn();
//			}
//		}  else if (tableName.equalsIgnoreCase("MoneyReturnContainer")) {
//			model = HyjModel.getModel(MoneyReturnContainer.class, id);
//			if(model == null){
//				model = new MoneyReturnContainer();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyReturnApportion")) {
//			model = HyjModel.getModel(MoneyReturnApportion.class, id);
//			if(model == null){
//				model = new MoneyReturnApportion();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyTransfer")) {
//			model = HyjModel.getModel(MoneyTransfer.class, id);
//			if(model == null){
//				model = new MoneyTransfer();
//			}
//		} else if (tableName.equalsIgnoreCase("ParentProject")) {
//			model = HyjModel.getModel(ParentProject.class, id);
//			if(model == null){
//				model = new ParentProject();
//			}
//		} else if (tableName.equalsIgnoreCase("Picture")) {
//			model = HyjModel.getModel(Picture.class, id);
//			if(model == null){
//				model = new Picture();
//			}
//		} else if (tableName.equalsIgnoreCase("Project")) {
//			model = HyjModel.getModel(Project.class, id);
//			if(model == null){
//				model = new Project();
//			}
//		} else if (tableName.equalsIgnoreCase("ProjectShareAuthorization")) {
//			model = HyjModel.getModel(ProjectShareAuthorization.class, id);
//			if(model == null){
//				model = new ProjectShareAuthorization();
//			}
//		} else if (tableName.equalsIgnoreCase("User")) {
//			model = HyjModel.getModel(User.class, id);
//			if(model == null){
//				model = new User();
//			}
//		} else if (tableName.equalsIgnoreCase("UserData")) {
//			model = HyjModel.getModel(UserData.class, id);
//			if(model == null){
//				model = new UserData();
//			}
//		} else if (tableName.equalsIgnoreCase("ProjectRemark")) {
//			model = HyjModel.getModel(ProjectRemark.class, id);
//			if(model == null){
//				model = new ProjectRemark();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyIncomeCategory")) {
//			model = HyjModel.getModel(MoneyIncomeCategory.class, id);
//			if(model == null){
//				model = new MoneyIncomeCategory();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyExpenseCategory")) {
//			model = HyjModel.getModel(MoneyExpenseCategory.class, id);
//			if(model == null){
//				model = new MoneyExpenseCategory();
//			}
//		} else if (tableName.equalsIgnoreCase("MoneyTemplate")) {
//			model = HyjModel.getModel(MoneyTemplate.class, id);
//			if(model == null){
//				model = new MoneyTemplate();
//			}
//		} 
//		return model;
	}
	
}
