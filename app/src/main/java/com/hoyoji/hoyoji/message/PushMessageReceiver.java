package com.hoyoji.hoyoji.message;

import android.content.Context;
import android.content.Intent;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class PushMessageReceiver extends XGPushBaseReceiver {
//	private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
	public static final String LogTag = "PushMessageReceiver";

//	private void show(Context context, String text) {
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//	}

	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) {
			return;
		}

		Intent startIntent = new Intent(context, MessageDownloadService.class);
		HyjApplication.getInstance().getApplicationContext().startService(startIntent);
		
//		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationManager.cancel((int) notifiShowedRlt.getMsgId());
		
//		String text = "֪ͨ��չʾ ��title:" + notifiShowedRlt.getTitle()
//				+ ",content:" + notifiShowedRlt.getContent()
//				+ ",custom_content:" + notifiShowedRlt.getCustomContent();
//		Log.d(LogTag, text);
//		show(context, text);
//		XGNotification notific = new XGNotification();
//		notific.setMsg_id(notifiShowedRlt.getMsgId());
//		notific.setTitle(notifiShowedRlt.getTitle());
//		notific.setContent(notifiShowedRlt.getContent());
//		notific.setUrl(notifiShowedRlt.getUrl());
//		notific.setActivity(notifiShowedRlt.getActivity());
//		notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				.format(Calendar.getInstance().getTime()));
//		NotificationService.getInstance(context).save(notific);
//		context.sendBroadcast(intent);
//		show(context, "����1������Ϣ");
	}

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
		if (context == null) {
			return;
		}
//		String text = null;
//		if (errorCode == XGPushBaseReceiver.SUCCESS) {
//			text = "��ע��ɹ�";
//		} else {
//			text = "��ע��ʧ��" + errorCode;
//		}
//		Log.d(LogTag, text);
//		show(context, text);
	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
//		String text = null;
//		if (errorCode == XGPushBaseReceiver.SUCCESS) {
//			text = "\"" + tagName + "\"���óɹ�";
//		} else {
//			text = "\"" + tagName + "\"����ʧ��,�����룺" + errorCode;
//		}
//		Log.d(LogTag, text);
//		show(context, text);

	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
//		String text = null;
//		if (errorCode == XGPushBaseReceiver.SUCCESS) {
//			text = "\"" + tagName + "\"ɾ��ɹ�";
//		} else {
//			text = "\"" + tagName + "\"ɾ��ʧ��,�����룺" + errorCode;
//		}
//		Log.d(LogTag, text);
//		show(context, text);

	}

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
//        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
//        	Intent intent = new Intent(context, HyjBlankUserActivity.class);
//        	Class<? extends Fragment> fragmentClass = MessageListFragment.class;
//    		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
//    		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
//    		intent.putExtra("TITLE", context.getString(R.string.friendListFragment_title_manage_message));
//    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    		context.startActivity(intent);
//        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
//        }
	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult message) {
//		// TODO Auto-generated method stub
//		if (context == null || message == null) {
//			return;
//		}
//		String text = null;
//		if (errorCode == XGPushBaseReceiver.SUCCESS) {
//			text = message + "ע��ɹ�";
//			// ��������token
//			String token = message.getToken();
//		} else {
//			text = message + "ע��ʧ�ܣ������룺" + errorCode;
//		}
//		Log.d(LogTag, text);
//		show(context, text);
	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
//		String text = message.toString();
//		// ��ȡ�Զ���key-value
//		String customContent = message.getCustomContent();
//		if (customContent != null && customContent.length() != 0) {
//			try {
//				JSONObject obj = new JSONObject(customContent);
//				// key1Ϊǰ̨���õ�key
//				if (!obj.isNull("key")) {
//					String value = obj.getString("key");
//					Log.d(LogTag, "get custom value:" + value);
//				}
//				// ...
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		// APP����������Ϣ�Ĺ��...
//		Log.d(LogTag, text);
//		show(context, text);
	}

}
