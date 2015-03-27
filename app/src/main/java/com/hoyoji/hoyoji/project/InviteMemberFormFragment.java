package com.hoyoji.hoyoji.project;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.AppConstants;
import com.hoyoji.hoyoji.models.Project;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.Util;
import com.tencent.tauth.UiError;


public class InviteMemberFormFragment extends HyjUserFragment {
	private HyjTextField sendInviteTitle =null;
	private EditText sendInviteDetail =null;
	private HyjTextField mVerificationCode =null;
	
	private CheckBox mCheckBoxShareAuthExpenseSelf = null;
	private CheckBox mCheckBoxShareAuthExpenseAdd = null;
	private CheckBox mCheckBoxShareAuthExpenseEdit = null;
	private CheckBox mCheckBoxShareAuthExpenseDelete = null;

	private Button mButtonSendInvite = null;
	
	private IWXAPI api;
	private QQShare mQQShare = null;
	public static QQAuth mQQAuth;
	
	private Intent intent = null;
	private Project project = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.project_formfragment_invitemember;
	}
	
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		intent = getActivity().getIntent();
		Long project_id = intent.getLongExtra("PROJECT_ID", -1);
		project = Project.load(Project.class, project_id);
		
		final String way = intent.getStringExtra("INVITE_TYPE");
		
		
		sendInviteTitle = (HyjTextField) getView().findViewById(R.id.inviteMemberMessageFormFragment_editText_title);
		sendInviteTitle.setText("邀请加入圈子");
		sendInviteDetail = (EditText) getView().findViewById(R.id.inviteMemberMessageFormFragment_editText_detail);
		sendInviteDetail.setText(HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您加入圈子: "+project.getName()+"，一起参与记账。");
		
		mVerificationCode = (HyjTextField) getView().findViewById(R.id.inviteMemberFormFragment_hyjTextField_verificationCode);
		
		mCheckBoxShareAuthExpenseSelf = (CheckBox)getView().findViewById(R.id.inviteMemberFormFragment_checkBox_shareAuthorization_expense_self);
		mCheckBoxShareAuthExpenseSelf.setChecked(false);
		mCheckBoxShareAuthExpenseAdd = (CheckBox)getView().findViewById(R.id.inviteMemberFormFragment_checkBox_shareAuthorization_expense_add);
		mCheckBoxShareAuthExpenseAdd.setChecked(true);
		mCheckBoxShareAuthExpenseEdit = (CheckBox)getView().findViewById(R.id.inviteMemberFormFragment_checkBox_shareAuthorization_expense_edit);
		mCheckBoxShareAuthExpenseEdit.setChecked(true);
		mCheckBoxShareAuthExpenseDelete = (CheckBox)getView().findViewById(R.id.inviteMemberFormFragment_checkBox_shareAuthorization_expense_delete);
		mCheckBoxShareAuthExpenseDelete.setChecked(true);

		mButtonSendInvite = (Button)getView().findViewById(R.id.button_send_invite);
		mButtonSendInvite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendInviteMessage(way);
			}
		});
		if(way.equals("Other")) {
			mButtonSendInvite.setText("发送到其他好友");
		} else if(way.equals("QQ")) {
			mButtonSendInvite.setText("发送到QQ好友");
		} else if(way.equals("WX")) {
			mButtonSendInvite.setText("发送到微信好友");
		} else if(way.equals("WXCIRCLE")) {
			mButtonSendInvite.setText("发送到微信朋友圈");
		}
		mQQAuth = QQAuth.createInstance(AppConstants.TENTCENT_CONNECT_APP_ID, getActivity());
		mQQShare = new QQShare(getActivity(), mQQAuth.getQQToken());
	}

	public void sendInviteMessage(final String way) {
		((HyjActivity) getActivity()).displayProgressDialog(R.string.inviteMemberFormFragment_action_invite_title,R.string.inviteMemberFormFragment_action_invite_content);
		
		JSONObject inviteFriendObject = new JSONObject();
		final String id = UUID.randomUUID().toString();
   		try {
	   			JSONObject projectData = new JSONObject();
	   			projectData.put("projectId", project.getId());
	   			projectData.put("projectShareMoneyExpenseOwnerDataOnly", mCheckBoxShareAuthExpenseSelf.isChecked());
	   			projectData.put("projectShareMoneyExpenseAddNew", mCheckBoxShareAuthExpenseAdd.isChecked());
	   			projectData.put("projectShareMoneyExpenseEdit", mCheckBoxShareAuthExpenseEdit.isChecked());
	   			projectData.put("projectShareMoneyExpenseDelete", mCheckBoxShareAuthExpenseDelete.isChecked());
	   			
	   			inviteFriendObject.put("data", projectData.toString());
	   			inviteFriendObject.put("id", id);
				inviteFriendObject.put("__dataType", "InviteLink");
				inviteFriendObject.put("title", "邀请加入圈子");
				inviteFriendObject.put("type", "ProjectShare");
				inviteFriendObject.put("date", (new Date()).getTime());
				inviteFriendObject.put("verificationCode", mVerificationCode.getText().toString().trim());
				inviteFriendObject.put("description", sendInviteDetail.getText());
				inviteFriendObject.put("state", "Open");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
   	 
   	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
				if(way.equals("Other")){
					inviteOtherFriend(id);
				} else if(way.equals("WX")){
					inviteWXFriend(id);
				} else if(way.equals("WXCIRCLE")){
					inviteWXCircleFriend(id);
				} else if(way.equals("QQ")){
					inviteQQFriend(id);
				}
			}

			@Override
			public void errorCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
				try {
					JSONObject json = (JSONObject) object;
					((HyjActivity) getActivity()).displayDialog(null,
							json.getJSONObject("__summary")
									.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
   	 
   	 	HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + inviteFriendObject.toString() + "]", "postData");
	 }

	public void inviteOtherFriend(String id) {
		Intent intent=new Intent(Intent.ACTION_SEND);   
        intent.setType("image/*");   
        intent.putExtra(Intent.EXTRA_TITLE, sendInviteTitle.getText());  
        intent.putExtra(Intent.EXTRA_SUBJECT, sendInviteTitle.getText());   
        intent.putExtra(Intent.EXTRA_TEXT, sendInviteDetail.getText()+"\n\n" + HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id);   
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        startActivity(Intent.createChooser(intent, "邀请圈子成员")); 
        getActivity().finish();
	}
	
	public void inviteWXFriend(String id) {
		api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID);
		api.registerApp(AppConstants.WX_APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = sendInviteTitle.getText();
		msg.description = sendInviteDetail.getText() + "";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		getActivity().finish();
		
	}
	
	public void inviteWXCircleFriend(String id) {
		api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID, false);
		api.registerApp(AppConstants.WX_APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = sendInviteDetail.getText().toString();
		msg.description = sendInviteDetail.getText().toString();
		try{
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			bmp.recycle();
			msg.setThumbImage(thumbBmp);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
		getActivity().finish();
	}
	
	public void inviteQQFriend(String id) {
		final Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, sendInviteTitle.getText());
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  sendInviteDetail.getText() + "");
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id);
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, HyjApplication.getInstance().getServerUrl() + "imgs/invite_friend.png");
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "iBluetooth");
//	    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");		
	    mQQShare.shareToQQ(getActivity(), params, new BaseUIListener(getActivity()) {

            @Override
            public void onCancel() {
//            		Util.toastMessage(getActivity(), "onCancel: ");
            	//getActivity().finish();
            }

            @Override
            public void onComplete(Object response) {
                // TODO Auto-generated method stub
//                Util.toastMessage(getActivity(), "onComplete: " + response.toString());
            	getActivity().finish();
            }

            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
//                Util.toastMessage(getActivity(), "onError: " + e.errorMessage, "e");
            	getActivity().finish();
            }

        });
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	 
}
