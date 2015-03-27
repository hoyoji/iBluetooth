package com.hoyoji.hoyoji.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.server.HyjServer;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyBorrowApportion;
import com.hoyoji.hoyoji.models.MoneyBorrowContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnApportion;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeApportion;
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
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.User;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Base64;

public class MessageDownloadService extends Service {
	public static final String TAG = "MessageDownloadService";
	private Thread mMessageDownloadThread = null;
	// private MessageSericeBinder mBinder = new MessageSericeBinder();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mMessageDownloadThread == null) {
			mMessageDownloadThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// 开始执行后台任务
					if (HyjApplication.getInstance().getCurrentUser() != null) {
						try {
							if (!HyjUtil.hasNetworkConnection()) {
								return;
							}

							User currentUser = HyjApplication.getInstance().getCurrentUser();
							Log.d(TAG, "checking messages ...");
							JSONObject postData = new JSONObject();
							postData.put("__dataType", "Message");
							postData.put("toUserId", currentUser.getId());
							JSONObject notFilter = new JSONObject();
							notFilter.put("messageState", "closed");
							postData.put("__NOT_FILTER__", notFilter);
							
							JSONObject jsonServerTime = null;
							String lastMessagesDownloadTime = currentUser
									.getUserData()
									.getLastMessagesDownloadTime();
							if (lastMessagesDownloadTime == null || lastMessagesDownloadTime.length() == 0) {

								Object serverTime = HyjServer
										.doHttpPost(null,
												HyjApplication.getServerUrl()
														+ "getServerTime.php",
												"", true);
								jsonServerTime = (JSONObject) serverTime;
							} else {
								JSONObject timeFilter = new JSONObject();
								timeFilter.put("lastServerUpdateTime",
										currentUser.getUserData()
												.getLastMessagesDownloadTime());
								postData.put("__GREATER_FILTER__", timeFilter);
							}

							Object returnedObject = HyjServer.doHttpPost(null,
									HyjApplication.getServerUrl()
											+ "getData.php",
									"[" + postData.toString() + "]", true);
							if (returnedObject instanceof JSONArray) {
								final JSONArray jsonArray = ((JSONArray) returnedObject)
										.optJSONArray(0);
								List<Message> friendMessages = new ArrayList<Message>();
								List<Message> projectShareMessages = new ArrayList<Message>();
								List<Message> projectEventMemberMessages = new ArrayList<Message>();
								try {
									ActiveAndroid.beginTransaction();
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject jsonMessage = jsonArray
												.optJSONObject(i);
										Message newMessage = new Message();
										newMessage.loadFromJSON(
												jsonMessage, true);
										newMessage.save();
										if (newMessage
												.getType().startsWith(
														"System.Friend.")) {
											friendMessages.add(newMessage);
										} else if (newMessage
												.getType()
												.startsWith(
														"Project.Share.")) {
											projectShareMessages
													.add(newMessage);
										} else if (newMessage
												.getType()
												.startsWith(
														"Event.Member.")) {
											projectEventMemberMessages
													.add(newMessage);
										}
										if (lastMessagesDownloadTime == null || lastMessagesDownloadTime.length() == 0
												|| lastMessagesDownloadTime
														.compareTo(jsonMessage
																.optString("lastServerUpdateTime")) < 0) {
											lastMessagesDownloadTime = jsonMessage
													.optString("lastServerUpdateTime");
										}
									}

									if ((lastMessagesDownloadTime == null || lastMessagesDownloadTime.length() == 0)
											&& jsonServerTime != null) {
										lastMessagesDownloadTime = jsonServerTime
												.optString("server_time");
									}

									if (!lastMessagesDownloadTime.equals(currentUser
											.getUserData()
											.getLastMessagesDownloadTime())) {
										// HyjModelEditor<UserData>
										// userDataEditor =
										// currentUser.getUserData().newModelEditor();
										// userDataEditor
										// .getModelCopy()
										// .setLastMessagesDownloadTime(
										// lastMessagesDownloadTime);
										// userDataEditor
										// .getModel().setSyncFromServer(true);
										// userDataEditor.save();
										
										currentUser.getUserData().setLastMessagesDownloadTime(lastMessagesDownloadTime);
										Cache.openDatabase()
												.execSQL(
														"Update UserData SET lastMessagesDownloadTime = '"
																+ lastMessagesDownloadTime
																+ "' WHERE id = '"
																+ currentUser.getUserDataId()
																+ "'");
									}
									ActiveAndroid.setTransactionSuccessful();
//									if (jsonArray.length() > 0) {
//										int newCount = 0;
//										for(int i=0; i < jsonArray.length(); i++){
//											if(jsonArray.optJSONObject(i).optString("messageState").equalsIgnoreCase("new")){
//												newCount++;
//											}
//										}
//										if(newCount > 0){
//											Handler handler = new Handler(Looper
//													.getMainLooper());
//											handler.post(new Runnable() {
//												public void run() {
//													HyjUtil.displayToast(String
//															.format(getApplicationContext()
//																	.getString(
//																			R.string.app_toast_new_messages),
//																	jsonArray
//																			.length()));
//												}
//											});
//										}
//									}
								} catch (Exception e) {
								} finally {
									ActiveAndroid.endTransaction();
								}
								processFriendMessages(friendMessages,
										currentUser);
								processProjectShareMessages(
										projectShareMessages, currentUser);
								processEventMemberMessages(
										projectEventMemberMessages, currentUser);

							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mMessageDownloadThread = null;
				}
			});
			mMessageDownloadThread.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	protected void processProjectShareMessages(List<Message> newMessages,
			User currentUser) {
		for (Message newMessage : newMessages) {
			try {
				String projectShareAuthorizationId;
				ProjectShareAuthorization psa;
				JSONObject msgData = new JSONObject(newMessage.getMessageData());
				projectShareAuthorizationId = msgData.optString("projectShareAuthorizationId");
				psa = HyjModel.getModel(ProjectShareAuthorization.class, projectShareAuthorizationId);
				
				if (newMessage.getType().equalsIgnoreCase("Project.Share.AcceptInviteLink")) {
//					String eventMemberId = msgData.optString("eventMemberId");
//					EventMember pem = HyjModel.getModel(EventMember.class, eventMemberId);
//					if(pem != null){
//						pem.getEvent().setSignUpCount(pem.getEvent().getSignUpCount()+1);
//						pem.getEvent().setSyncFromServer(true);
//						pem.getEvent().save();
//					}
					loadAllEventMembers(msgData.optString("eventId"));
					
					Friend newFriend = new Select().from(Friend.class).where("friendUserId=?", newMessage.getFromUserId()).executeSingle();
					if (newFriend == null) {
						loadNewlyAddedFriend(newMessage.getFromUserId());
					}
					loadSharedProjectData(msgData);
					
				} else if(psa == null){
					Friend newFriend = new Select().from(Friend.class).where("friendUserId=?", newMessage.getFromUserId()).executeSingle();
					if (newFriend == null) {
						loadNewlyAddedFriend(newMessage.getFromUserId());
					}
					loadAllProjectShareAuthorizations(msgData.optJSONArray("projectIds").get(0).toString());
					
					if(msgData.optString("eventId") != null) {
						loadAllEventMembers(msgData.optString("eventId"));
					}
				} else if (newMessage.getType().equalsIgnoreCase("Project.Share.Accept")) {
					Friend newFriend = new Select().from(Friend.class).where("friendUserId=?", newMessage.getFromUserId()).executeSingle();
					if (newFriend == null) {
						loadNewlyAddedFriend(newMessage.getFromUserId());
					}
					psa.setState("Accept");
					psa.setSyncFromServer(true);
					psa.save();
					if(msgData.optString("eventId") != null) {
						loadAllEventMembers(msgData.optString("eventId"));
					}
				} else if (newMessage.getType().equalsIgnoreCase(
						"Project.Share.Edit")) {
					doEditProjectShareAuthorization(msgData, psa);
					
				} else if (newMessage.getType().equalsIgnoreCase(
						"Project.Share.Delete")) {
	
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

	}
	
	private void loadAllProjectShareAuthorizations(String projectId) {
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
					JSONArray jsonArray = ((JSONArray) object).optJSONArray(0);

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.optJSONObject(i);

						HyjModel model = HyjModel.createModel(jsonObj.optString("__dataType"), jsonObj.optString("id"));
						model.loadFromJSON(jsonObj, true);
						model.save();
					}
			}

			@Override
			public void errorCallback(Object object) {
			}
		};
		try{
			JSONArray data = new JSONArray();
			JSONObject newObj = new JSONObject();
			newObj = new JSONObject();
			newObj.put("__dataType", "ProjectShareAuthorization");
			newObj.put("main.projectId", projectId);
			data.put(newObj);
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void processEventMemberMessages(List<Message> newMessages,
			User currentUser) {
		for (Message newMessage : newMessages) {
			try {
				String eventMemberId;
				EventMember pem;
				JSONObject msgData = new JSONObject(newMessage.getMessageData());
				eventMemberId = msgData.optString("eventMemberId");
				pem = HyjModel.getModel(EventMember.class, eventMemberId);
				if(pem == null){
					loadAllEventMembers(msgData.optString("eventId"));
				} else if (newMessage.getType().equalsIgnoreCase("Event.Member.Accept")) {
					pem.setState("SignUp");
					pem.setSyncFromServer(true);
					pem.save();
					
					loadSharedProjectData(msgData);
//					loadAllEventMembers(msgData.optString("eventId"));
				} else if (newMessage.getType().equalsIgnoreCase("Event.Member.SignUp")) {
					pem.setState("SignUp");
					pem.setSyncFromServer(true);
					pem.save();
					
					loadAllEventMembers(msgData.optString("eventId"));
				} else if (newMessage.getType().equalsIgnoreCase("Event.Member.SignIn")) {
					pem.setState("SignIn");
					pem.setSyncFromServer(true);
					pem.save();
					
					loadAllEventMembers(msgData.optString("eventId"));
				} else if (newMessage.getType().equalsIgnoreCase("Event.Member.Cancel")) {
					pem.getEvent().setState("Cancel");
					pem.getEvent().setSyncFromServer(true);
					pem.getEvent().save();
				} else if (newMessage.getType().equalsIgnoreCase("Event.Member.CancelSignUp")) {
					pem.setState("CancelSignUp");
					pem.setSyncFromServer(true);
					pem.save();
					
					loadAllEventMembers(msgData.optString("eventId"));
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

	}
	
	private void loadAllEventMembers(String eventId) {
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONArray jsonArray = ((JSONArray) object).optJSONArray(0);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.optJSONObject(i);

					HyjModel model = HyjModel.createModel(jsonObj.optString("__dataType"), jsonObj.optString("id"));
					model.loadFromJSON(jsonObj, true);
					model.save();
				}
				
				JSONArray jsonArrayEvent = ((JSONArray) object).optJSONArray(1);

				for (int i = 0; i < jsonArrayEvent.length(); i++) {
					JSONObject jsonObjEvent = jsonArrayEvent.optJSONObject(i);

					HyjModel modelEvent = HyjModel.createModel(jsonObjEvent.optString("__dataType"), jsonObjEvent.optString("id"));
					modelEvent.loadFromJSON(jsonObjEvent, true);
					modelEvent.save();
				}
			}

			@Override
			public void errorCallback(Object object) {
			}
		};
		try{
			JSONArray data = new JSONArray();
			JSONObject newObj = new JSONObject();
			newObj = new JSONObject();
			newObj.put("__dataType", "EventMember");
			newObj.put("main.eventId", eventId);
			data.put(newObj);
			JSONObject newEventObj = new JSONObject();
			newEventObj.put("__dataType", "Event");
			newEventObj.put("main.id", eventId);
			data.put(newEventObj);
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void doEditProjectShareAuthorization(final JSONObject jsonMsgData, final ProjectShareAuthorization psa) {
		// load new ProjectData from server
				HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
							JSONArray jsonArray = (JSONArray) object;
							JSONObject jsonObj = jsonArray.optJSONArray(0).optJSONObject(0);

							if(psa != null){
								if(psa.getState().equals("Accept") && jsonObj.optString("state").equals("Accept")){
									if(psa.getProjectShareMoneyExpenseOwnerDataOnly() && jsonObj.optInt("projectShareMoneyExpenseOwnerDataOnly") != 1){
										loadSharedProjectData(jsonMsgData);
									} else if(!psa.getProjectShareMoneyExpenseOwnerDataOnly() && jsonObj.optInt("projectShareMoneyExpenseOwnerDataOnly") == 1){
										removeProjectNonOwnerData(psa.getProjectId());
										psa.loadFromJSON(jsonObj, true);
										psa.save();
									}
								}
							}
					}

					@Override
					public void errorCallback(Object object) {
					}
				};
				try{
					JSONArray data = new JSONArray();
					JSONObject newObj = new JSONObject();
					newObj = new JSONObject();
					newObj.put("__dataType", "ProjectShareAuthorization");
					newObj.put("main.id", jsonMsgData.optString("projectShareAuthorizationId"));
					data.put(newObj);
					HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
				} catch (JSONException e) {
					e.printStackTrace();
				}
	}
	
	private void removeListModelFromDB(List<Model> list){
		for(Model m : list){
//			m.setSyncFromServer(true);
//			m.delete();
			Model.delete(m.getClass(), m.get_mId());

			Cache.removeEntity(m);
			HyjUtil.updateClicentSyncRecord(m.getClass().getName(), m.getId(), "Delete", ((HyjModel)m).getLastServerUpdateTime(), true);
			Cache.getContext()
					.getContentResolver()
					.notifyChange(ContentProvider.createUri(m.getClass(), m.get_mId()),
							null);
		}
	}

	protected void removeProjectNonOwnerData(String projectId) {
//		try{
			ActiveAndroid.beginTransaction();
			String curUserId = HyjApplication.getInstance().getCurrentUser().getId();
			removeListModelFromDB(new Select("t1.*").from(MoneyExpense.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyIncome.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyBorrow.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyLend.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyReturn.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyPayback.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyTransfer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			
			removeListModelFromDB(new Select("t.*").from(MoneyExpenseApportion.class).as("t").join(MoneyExpenseContainer.class).as("t1").on("t.moneyExpenseContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyIncomeApportion.class).as("t").join(MoneyIncomeContainer.class).as("t1").on("t.moneyIncomeContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyDepositIncomeApportion.class).as("t").join(MoneyDepositIncomeContainer.class).as("t1").on("t.moneyDepositIncomeContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyBorrowApportion.class).as("t").join(MoneyBorrowContainer.class).as("t1").on("t.moneyBorrowContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyLendApportion.class).as("t").join(MoneyLendContainer.class).as("t1").on("t.moneyLendContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyReturnApportion.class).as("t").join(MoneyReturnContainer.class).as("t1").on("t.moneyReturnContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyDepositReturnApportion.class).as("t").join(MoneyDepositReturnContainer.class).as("t1").on("t.moneyDepositReturnContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t.*").from(MoneyPaybackApportion.class).as("t").join(MoneyPaybackContainer.class).as("t1").on("t.moneyPaybackContainerId = t1.id").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select().from(Picture.class).where("projectId=? AND ownerUserId <> ?", projectId, curUserId).execute());
			
			removeListModelFromDB(new Select("t1.*").from(MoneyExpenseContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ?  AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyIncomeContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyDepositIncomeContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyLendContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyBorrowContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());
			removeListModelFromDB(new Select("t1.*").from(MoneyDepositReturnContainer.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.eventId = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND (t1.eventId IS NULL OR t2.id IS NULL OR t2.eventShareOwnerDataOnly = 1) ", projectId, curUserId).execute());

			removeListModelFromDB(new Select("t1.*").from(Event.class).as("t1").leftJoin(EventMember.class).as("t2").on("t1.id = t2.eventId AND t2.friendUserId='" + curUserId + "'").where("t1.projectId=? AND t1.ownerUserId <> ? AND t2.id IS NULL", projectId, curUserId).execute());

			Project project = HyjModel.getModel(Project.class, projectId);
			if(!project.getOwnerUserId().equals(curUserId)){
				removeListModelFromDB(new Select().from(ProjectShareAuthorization.class).where("projectId=? AND (friendUserId IS NULL OR (friendUserId<>? AND ownerUserId <> friendUserId))", projectId, curUserId).execute());
			}
			
			ActiveAndroid.setTransactionSuccessful();
//		} catch (Exception e){
//		}
		ActiveAndroid.endTransaction();
	}

	protected void loadSharedProjectData(JSONObject jsonMsgData) {
		// load new ProjectData from server
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				try {

					JSONArray jsonArray = (JSONArray) object;
					ActiveAndroid.beginTransaction();
					
					for(int i = 0; i < jsonArray.length(); i++){
						JSONArray jsonObjects = jsonArray.getJSONArray(i);
						for(int j = 0; j < jsonObjects.length(); j++){
//							if(jsonObjects.optJSONObject(j).optString("__dataType").equals("Project")){
//								Project newProject = new Project();
//								newProject.loadFromJSON(jsonObjects.optJSONObject(j), true);
//								newProject.save();
//							} else if(jsonObjects.optJSONObject(j).optString("__dataType").equals("ProjectShareAuthorization")){
//								ProjectShareAuthorization newProjectShareAuthorization = new ProjectShareAuthorization();
//								newProjectShareAuthorization.loadFromJSON(jsonObjects.optJSONObject(j), true);
//								newProjectShareAuthorization.save();
//							}
							JSONObject jsonObj = jsonObjects.optJSONObject(j);
							HyjModel model = HyjModel.createModel(jsonObj.optString("__dataType"), jsonObj.getString("id"));
							model.loadFromJSON(jsonObj, true);
							model.save();
						}	
					}

					ActiveAndroid.setTransactionSuccessful();
					
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					ActiveAndroid.endTransaction();
				}
			}

			@Override
			public void errorCallback(Object object) {
			}
		};

		JSONArray data = new JSONArray();
		try {
			JSONArray projectIds = jsonMsgData.optJSONArray("projectIds");
			for (int i = 0; i < projectIds.length(); i++) {
				JSONObject newObj = new JSONObject();
				newObj.put("__dataType", "Project");
				newObj.put("main.id", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "ProjectShareAuthorization");
				newObj.put("main.projectId", projectIds.get(i));
//				newObj.put("main.state", "Accept");
				data.put(newObj);
//				newObj = new JSONObject();
//				newObj.put("__dataType", "Event");
//				newObj.put("main.projectId", projectIds.get(i));
//				data.put(newObj);
//				newObj = new JSONObject();
//				newObj.put("__dataType", "EventMember");
//				newObj.put("evt.projectId", projectIds.get(i));
//				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpenseContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpense");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyExpenseApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncomeContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositIncomeContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncome");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyIncomeApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositIncomeApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrow");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrowContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyBorrowApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLend");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLendContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyLendApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyReturn");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyReturnApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositReturnApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyDepositReturnContainer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyPayback");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyPaybackApportion");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "MoneyTransfer");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "Picture");
				newObj.put("pst.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "Event");
				newObj.put("main.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "EventMember");
				newObj.put("evt.projectId", projectIds.get(i));
				data.put(newObj);
				newObj = new JSONObject();
				newObj.put("__dataType", "Exchange");
				newObj.put("foreignCurrencyId", jsonMsgData.opt("projectCurrencyId"));
				newObj.put("localCurrencyId", HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId());
				data.put(newObj);
			}
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void processFriendMessages(List<Message> newMessages,
			User currentUser) {
		for (Message newMessage : newMessages) {
			if (newMessage.getType().equalsIgnoreCase(
					"System.Friend.AddResponse")) {
				String newUserId = "";
				if (newMessage.getToUserId().equals(currentUser.getId())) {
					newUserId = newMessage.getFromUserId();
				} else if (newMessage.getFromUserId().equals(
						currentUser.getId())) {
					newUserId = newMessage.getToUserId();
				} else {
					continue;
				}
				Friend newFriend = new Select().from(Friend.class)
						.where("friendUserId=?", newUserId).executeSingle();
				if (newFriend == null) {
					loadNewlyAddedFriend(newUserId);
				}
			} else if (newMessage.getType().equalsIgnoreCase(
					"System.Friend.Delete")) {
				Friend delFriend = new Select().from(Friend.class)
						.where("friendUserId=?", newMessage.getFromUserId())
						.executeSingle();
				if (delFriend != null) {
					delFriend.delete();
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// return mBinder;
		return null;
	}

	private void loadNewlyAddedFriend(String friendUserId) {
		// load new friend from server
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				try {
					JSONArray jsonArray = (JSONArray) object;
					JSONObject jsonFriend;
					jsonFriend = jsonArray.getJSONArray(0).getJSONObject(0);
					JSONObject jsonUser = null;
					try {
						jsonUser = jsonArray.optJSONArray(1).getJSONObject(0);
					} catch (JSONException e) {
					}
					loadFriendPicturesAndSaveFriend(jsonUser, jsonFriend);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void errorCallback(Object object) {
			}
		};

		try {
			JSONObject data = new JSONObject();
			data.put("__dataType", "Friend");
			data.put("friendUserId", friendUserId);
			data.put("ownerUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			JSONObject dataUser = new JSONObject();
			dataUser.put("__dataType", "User");
			dataUser.put("id", friendUserId);
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,
					"[" + data.toString() + "," + dataUser.toString() + "]",
					"findDataFilter");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void loadFriendPicturesAndSaveFriend(final JSONObject jsonUser,
			final JSONObject jsonFriend) {
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				Friend newFriend = HyjModel.getModel(Friend.class,
						jsonFriend.optString("id"));
				if (newFriend == null) {
					newFriend = new Friend();
				}
				newFriend.loadFromJSON(jsonFriend, true);

				User newUser = HyjModel.getModel(User.class,
						jsonUser.optString("id"));
				if (newUser == null) {
					newUser = new User();
				}
				newUser.loadFromJSON(jsonUser, true);

				saveUserPictures(object);
				newUser.save();
				newFriend.save();
			}

			@Override
			public void errorCallback(Object object) {
			}
		};

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("recordType", "User");
			jsonObj.put("recordId", jsonUser.optString("id"));
		} catch (JSONException e) {
		}
		HyjHttpPostAsyncTask.newInstance(serverCallbacks,
				jsonObj.optString("id"), "fetchRecordPictures");
	}

	private void saveUserPictures(Object object) {
		JSONArray pictureArray = (JSONArray) object;
		for (int i = 0; i < pictureArray.length(); i++) {
			try {
				JSONObject jsonPic = pictureArray.getJSONObject(i);
				String base64PictureIcon = jsonPic
						.optString("base64PictureIcon");
				if (base64PictureIcon.length() > 0) {
					byte[] decodedByte = Base64.decode(base64PictureIcon, 0);
					Bitmap icon = BitmapFactory.decodeByteArray(decodedByte, 0,
							decodedByte.length);
					File imageFile = 
							HyjUtil.createImageFile(jsonPic.optString("id")
									+ "_icon");
					if(imageFile != null){
						FileOutputStream out = new FileOutputStream(imageFile);
						icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.close();
						out = null;
					}
					jsonPic.remove("base64PictureIcon");
				}
				Picture newPicture = new Picture();
				newPicture.loadFromJSON(jsonPic, true);

				newPicture.save();

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	// class MessageSericeBinder extends Binder {
	//
	// public void startDownloadMessages() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// // 执行具体的下载任务
	// }
	// }).start();
	// }
	//
	// }

}