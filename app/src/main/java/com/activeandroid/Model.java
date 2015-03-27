package com.activeandroid;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.serializer.TypeSerializer;
import com.activeandroid.util.Log;
import com.activeandroid.util.ReflectionUtils;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.ClientSyncRecord;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unchecked")
public abstract class Model {
	// ////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE MEMBERS
	// ////////////////////////////////////////////////////////////////////////////////////

	@Column(name = BaseColumns._ID)
	private Long mId = null;

	private final TableInfo mTableInfo;
	private final String idName;
	private boolean mSyncFromServer = false;
	
	// ////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// ////////////////////////////////////////////////////////////////////////////////////

	public Model() {
		mTableInfo = Cache.getTableInfo(getClass());
		idName = mTableInfo.getIdName();
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	public final Long get_mId() {
		return mId;
	}

	public abstract String getId();

	public void delete() {
		Cache.openDatabase().delete(mTableInfo.getTableName(), "id=?",
				new String[] { getId() });
		Cache.removeEntity(this);
		HyjUtil.updateClicentSyncRecord(mTableInfo.getTableName(), getId(), "Delete", ((HyjModel)this).getLastServerUpdateTime(), mSyncFromServer);
		mSyncFromServer = false;
		Cache.getContext()
				.getContentResolver()
				.notifyChange(
						ContentProvider.createUri(mTableInfo.getType(), mId),
						null);
	}

	public void setSyncFromServer(boolean b) {
		mSyncFromServer = b;
	}
	
	public Boolean getSyncFromServer() {
		return mSyncFromServer;
	}
	
	public void save() {
		final SQLiteDatabase db = Cache.openDatabase();
		final ContentValues values = new ContentValues();

		for (Field field : mTableInfo.getFields()) {
			final String fieldName = mTableInfo.getColumnName(field);

			if (fieldName.equals(idName)) {
				continue;
			}

			Class<?> fieldType = field.getType();

			field.setAccessible(true);

			try {
				Object value = field.get(this);

				if (value != null) {
					final TypeSerializer typeSerializer = Cache
							.getParserForType(fieldType);
					if (typeSerializer != null) {
						// serialize data
						value = typeSerializer.serialize(value);
						// set new object type
						if (value != null) {
							fieldType = value.getClass();
							// check that the serializer returned what it
							// promised
							if (!fieldType.equals(typeSerializer
									.getSerializedType())) {
								Log.w(String
										.format("TypeSerializer returned wrong type: expected a %s but got a %s",
												typeSerializer
														.getSerializedType(),
												fieldType));
							}
						}
					}
				}

				// TODO: Find a smarter way to do this? This if block is
				// necessary because we
				// can't know the type until runtime.
				if (value == null) {
					values.putNull(fieldName);
				} else if (fieldType.equals(Byte.class)
						|| fieldType.equals(byte.class)) {
					values.put(fieldName, (Byte) value);
				} else if (fieldType.equals(Short.class)
						|| fieldType.equals(short.class)) {
					values.put(fieldName, (Short) value);
				} else if (fieldType.equals(Integer.class)
						|| fieldType.equals(int.class)) {
					values.put(fieldName, (Integer) value);
				} else if (fieldType.equals(Long.class)
						|| fieldType.equals(long.class)) {
					values.put(fieldName, (Long) value);
				} else if (fieldType.equals(Float.class)
						|| fieldType.equals(float.class)) {
					values.put(fieldName, (Float) value);
				} else if (fieldType.equals(Double.class)
						|| fieldType.equals(double.class)) {
					values.put(fieldName, (Double) value);
				} else if (fieldType.equals(Boolean.class)
						|| fieldType.equals(boolean.class)) {
					values.put(fieldName, (Boolean) value);
				} else if (fieldType.equals(Character.class)
						|| fieldType.equals(char.class)) {
					values.put(fieldName, value.toString());
				} else if (fieldType.equals(String.class)) {
					values.put(fieldName, value.toString());
				} else if (fieldType.equals(Byte[].class)
						|| fieldType.equals(byte[].class)) {
					values.put(fieldName, (byte[]) value);
				} else if (ReflectionUtils.isModel(fieldType)) {
					values.put(fieldName, ((Model) value).getId());
				} else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
					values.put(fieldName, ((Enum<?>) value).name());
				}
			} catch (IllegalArgumentException e) {
				Log.e(e.getClass().getName(), e);
			} catch (IllegalAccessException e) {
				Log.e(e.getClass().getName(), e);
			}
		}

		if (mId == null) {
			boolean alreadyInTrans = ActiveAndroid.inTransaction();
			try {
				if (!alreadyInTrans) {
					ActiveAndroid.beginTransaction();
				}
				Cursor cursor = Cache.openDatabase().query(
						mTableInfo.getTableName(), new String[] { "_id" },
						"id=?", new String[] { values.getAsString("id") },
						null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					mId = cursor.getLong(0);
					cursor.close();
					cursor = null;
					db.update(mTableInfo.getTableName(), values, "id=?",
							new String[] { values.getAsString("id") });
					HyjUtil.updateClicentSyncRecord(mTableInfo.getTableName(), values.getAsString("id"), "Update", ((HyjModel)this).getLastServerUpdateTime(), mSyncFromServer);
				} else {
					if(cursor != null){
						cursor.close();
						cursor = null;
					}
					mId = db.insert(mTableInfo.getTableName(), null, values);
					HyjUtil.updateClicentSyncRecord(mTableInfo.getTableName(), values.getAsString("id"), "Create", ((HyjModel)this).getLastServerUpdateTime(), mSyncFromServer);
				}
				if (!alreadyInTrans) {
					ActiveAndroid.setTransactionSuccessful();
				}
			} finally {
				if (!alreadyInTrans) {
					ActiveAndroid.endTransaction();
				}
			}
		} else {
			db.update(mTableInfo.getTableName(), values, idName + "=" + mId, null);
			HyjUtil.updateClicentSyncRecord(mTableInfo.getTableName(), values.getAsString("id"), "Update", ((HyjModel)this).getLastServerUpdateTime(), mSyncFromServer);
		}
		mSyncFromServer = false;
		Cache.getContext()
				.getContentResolver()
				.notifyChange(
						ContentProvider.createUri(mTableInfo.getType(), mId),
						null);
	}

	// Convenience methods

	public static void delete(Class<? extends Model> type, long id) {
		TableInfo tableInfo = Cache.getTableInfo(type);
		
		new Delete().from(type).where(tableInfo.getIdName() + "=?", id)
				.execute();
	}

	public static <T extends Model> T load(Class<T> type, long id) {
		TableInfo tableInfo = Cache.getTableInfo(type);
		return new Select().from(type).where(tableInfo.getIdName() + "=?", id)
				.executeSingle();
	}

	// Model population

	public final void loadFromCursor(Cursor cursor) {
		for (Field field : mTableInfo.getFields()) {
			final String fieldName = mTableInfo.getColumnName(field);
			Class<?> fieldType = field.getType();
			final int columnIndex = cursor.getColumnIndex(fieldName);

			if (columnIndex < 0) {
				continue;
			}

			field.setAccessible(true);

			try {
				boolean columnIsNull = cursor.isNull(columnIndex);

				if (columnIsNull) {
					try {
						field.set(this, null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				TypeSerializer typeSerializer = Cache
						.getParserForType(fieldType);
				Object value = null;

				if (typeSerializer != null) {
					fieldType = typeSerializer.getSerializedType();
				}

				// TODO: Find a smarter way to do this? This if block is
				// necessary because we
				// can't know the type until runtime.
				if (columnIsNull) {
					field = null;
				} else if (fieldType.equals(Byte.class)
						|| fieldType.equals(byte.class)) {
					value = cursor.getInt(columnIndex);
				} else if (fieldType.equals(Short.class)
						|| fieldType.equals(short.class)) {
					value = cursor.getInt(columnIndex);
				} else if (fieldType.equals(Integer.class)
						|| fieldType.equals(int.class)) {
					value = cursor.getInt(columnIndex);
				} else if (fieldType.equals(Long.class)
						|| fieldType.equals(long.class)) {
					value = cursor.getLong(columnIndex);
				} else if (fieldType.equals(Float.class)
						|| fieldType.equals(float.class)) {
					value = cursor.getFloat(columnIndex);
				} else if (fieldType.equals(Double.class)
						|| fieldType.equals(double.class)) {
					value = cursor.getDouble(columnIndex);
				} else if (fieldType.equals(Boolean.class)
						|| fieldType.equals(boolean.class)) {
					value = cursor.getInt(columnIndex) != 0;
				} else if (fieldType.equals(Character.class)
						|| fieldType.equals(char.class)) {
					value = cursor.getString(columnIndex).charAt(0);
				} else if (fieldType.equals(String.class)) {
					value = cursor.getString(columnIndex);
				} else if (fieldType.equals(Byte[].class)
						|| fieldType.equals(byte[].class)) {
					value = cursor.getBlob(columnIndex);
				} else if (ReflectionUtils.isModel(fieldType)) {
					final String entityId = cursor.getString(columnIndex);
					final Class<? extends Model> entityType = (Class<? extends Model>) fieldType;

					Model entity = Cache.getEntity(entityType, entityId);
					if (entity == null) {
						entity = new Select().from(entityType)
								.where("id=?", entityId).executeSingle();
					}

					value = entity;
				} else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
					@SuppressWarnings("rawtypes")
					final Class<? extends Enum> enumType = (Class<? extends Enum>) fieldType;
					value = Enum.valueOf(enumType,
							cursor.getString(columnIndex));
				}

				// Use a deserializer if one is available
				if (typeSerializer != null && !columnIsNull) {
					value = typeSerializer.deserialize(value);
				}

				// Set the field value
				if (value != null) {
					field.set(this, value);
				}
			} catch (IllegalArgumentException e) {
				Log.e(e.getClass().getName(), e);
			} catch (IllegalAccessException e) {
				Log.e(e.getClass().getName(), e);
			} catch (SecurityException e) {
				Log.e(e.getClass().getName(), e);
			}
		}

		if (mId != null) {
			Cache.addEntity(this);
		}
	}

	public void loadFromJSON(JSONObject json, boolean syncFromServer) {
		mSyncFromServer = syncFromServer;
		for (Field field : mTableInfo.getFields()) {
			final String fieldName = mTableInfo.getColumnName(field);
			
			if (!json.has(fieldName) || fieldName.equals(idName)) {
				continue;
			}

			Class<?> fieldType = field.getType();
			field.setAccessible(true);
			
			if (json.isNull(fieldName)) {
				try {
					field.set(this, null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				continue;
			}

			try {
				TypeSerializer typeSerializer = Cache
						.getParserForType(fieldType);
				Object value = null;

				if (typeSerializer != null) {
					fieldType = typeSerializer.getSerializedType();
				}

				// TODO: Find a smarter way to do this? This if block is
				// necessary because we
				// can't know the type until runtime.

				if (fieldType.equals(Byte.class)
						|| fieldType.equals(byte.class)) {
					value = json.getInt(fieldName);
				} else if (fieldType.equals(Short.class)
						|| fieldType.equals(short.class)) {
					value = json.getInt(fieldName);
				} else if (fieldType.equals(Integer.class)
						|| fieldType.equals(int.class)) {
					value = json.getInt(fieldName);
				} else if (fieldType.equals(Long.class)
						|| fieldType.equals(long.class)) {
					value = json.getLong(fieldName);
				} else if (fieldType.equals(Float.class)
						|| fieldType.equals(float.class)) {
					value = json.getDouble(fieldName);
				} else if (fieldType.equals(Double.class)
						|| fieldType.equals(double.class)) {
					value = json.getDouble(fieldName);
				} else if (fieldType.equals(Boolean.class)
						|| fieldType.equals(boolean.class)) {
					value = json.getInt(fieldName) != 0;
				} else if (fieldType.equals(Character.class)
						|| fieldType.equals(char.class)) {
					value = json.getString(fieldName).charAt(0);
				} else if (fieldType.equals(String.class)) {
					value = json.getString(fieldName);
				} else if (fieldType.equals(Byte[].class)
						|| fieldType.equals(byte[].class)) {
					value = json.getString(fieldName);
				} else if (ReflectionUtils.isModel(fieldType)) {
					final String entityId = json.getString(fieldName);
					final Class<? extends Model> entityType = (Class<? extends Model>) fieldType;

					Model entity = Cache.getEntity(entityType, entityId);
					if (entity == null) {
						entity = new Select().from(entityType)
								.where("id=?", entityId).executeSingle();
					}

					value = entity;
				} else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
					@SuppressWarnings("rawtypes")
					final Class<? extends Enum> enumType = (Class<? extends Enum>) fieldType;
					value = Enum.valueOf(enumType, json.getString(fieldName));
				}

				// Use a deserializer if one is available
				if (typeSerializer != null && value != null) {
					value = typeSerializer.deserialize(value);
				}

				// Set the field value
				if (value != null && field != null) {
					field.set(this, value);
				}
			} catch (IllegalArgumentException e) {
				Log.e(e.getClass().getName(), e);
			} catch (IllegalAccessException e) {
				Log.e(e.getClass().getName(), e);
			} catch (SecurityException e) {
				Log.e(e.getClass().getName(), e);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized JSONObject toJSON() {
		final JSONObject jsonObj = new JSONObject();

		for (Field field : mTableInfo.getFields()) {
			final String fieldName = mTableInfo.getColumnName(field);
			Class<?> fieldType = field.getType();
			if(fieldName == idName){
				continue;
			}
			field.setAccessible(true);

			try {
				Object value = field.get(this);

				if (value != null) {
					final TypeSerializer typeSerializer = Cache
							.getParserForType(fieldType);
					if (typeSerializer != null) {
						// serialize data
						value = typeSerializer.serialize(value);
						// set new object type
						if (value != null) {
							fieldType = value.getClass();
							// check that the serializer returned what it
							// promised
							if (!fieldType.equals(typeSerializer
									.getSerializedType())) {
								Log.w(String
										.format("TypeSerializer returned wrong type: expected a %s but got a %s",
												typeSerializer
														.getSerializedType(),
												fieldType));
							}
						}
					}
				}

				// TODO: Find a smarter way to do this? This if block is
				// necessary because we
				// can't know the type until runtime.
				if (value == null) {
					jsonObj.put(fieldName, JSONObject.NULL);
				} else if (fieldType.equals(Byte.class)
						|| fieldType.equals(byte.class)) {
					jsonObj.put(fieldName, (Byte) value);
				} else if (fieldType.equals(Short.class)
						|| fieldType.equals(short.class)) {
					jsonObj.put(fieldName, (Short) value);
				} else if (fieldType.equals(Integer.class)
						|| fieldType.equals(int.class)) {
					jsonObj.put(fieldName, (Integer) value);
				} else if (fieldType.equals(Long.class)
						|| fieldType.equals(long.class)) {
					jsonObj.put(fieldName, (Long) value);
				} else if (fieldType.equals(Float.class)
						|| fieldType.equals(float.class)) {
					jsonObj.put(fieldName, (Float) value);
				} else if (fieldType.equals(Double.class)
						|| fieldType.equals(double.class)) {
					jsonObj.put(fieldName, (Double) value);
				} else if (fieldType.equals(Boolean.class)
						|| fieldType.equals(boolean.class)) {
					jsonObj.put(fieldName, (Boolean) value);
				} else if (fieldType.equals(Character.class)
						|| fieldType.equals(char.class)) {
					jsonObj.put(fieldName, value.toString());
				} else if (fieldType.equals(String.class)) {
					jsonObj.put(fieldName, value.toString());
				} else if (fieldType.equals(Byte[].class)
						|| fieldType.equals(byte[].class)) {
					jsonObj.put(fieldName, (byte[]) value);
				} else if (ReflectionUtils.isModel(fieldType)) {
					jsonObj.put(fieldName, ((Model) value).getId());
				} else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
					jsonObj.put(fieldName, ((Enum<?>) value).name());
				}
			} catch (IllegalArgumentException e) {
				Log.e(e.getClass().getName(), e);
			} catch (IllegalAccessException e) {
				Log.e(e.getClass().getName(), e);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			jsonObj.put("__dataType", mTableInfo.getTableName());
			String lastServerUpdateTime = jsonObj
					.optString("lastServerUpdateTime");
			if (lastServerUpdateTime != null 
					&& lastServerUpdateTime.length() > 0
					&& !lastServerUpdateTime.equals("null")
					&& lastServerUpdateTime != JSONObject.NULL) {
				// jsonObj.remove("lastServerUpdateTime");
				jsonObj.put("lastServerUpdateTime",
						Long.valueOf(lastServerUpdateTime));
			}
			jsonObj.remove("serverRecordHash");
			jsonObj.remove("lastSyncTime");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	protected final <T extends Model> List<T> getMany(Class<T> type,
			String foreignKey) {
		return new Select()
				.from(type)
				.where(Cache.getTableName(type) + "." + foreignKey + "=?",
						getId()).execute();
	}
	
	protected final <T extends Model> List<T> getMany(Class<T> type,
			String foreignKey, String orderBy) {
		return new Select()
				.from(type)
				.where(Cache.getTableName(type) + "." + foreignKey + "=?",
						getId()).orderBy(orderBy).execute();
	}
	// ////////////////////////////////////////////////////////////////////////////////////
	// OVERRIDEN METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return mTableInfo.getTableName() + "@" + getId();
	}

	@Override
	public boolean equals(Object obj) {
		final Model other = (Model) obj;

		return this.mId != null
				&& (this.mTableInfo.getTableName().equals(other.mTableInfo
						.getTableName())) && (this.mId.equals(other.mId))
				&& (this.getId().equals(other.getId()));
	}
}
