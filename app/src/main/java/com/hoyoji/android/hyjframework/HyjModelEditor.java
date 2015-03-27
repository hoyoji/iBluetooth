package com.hoyoji.android.hyjframework;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.hoyoji.hoyoji.models.Project;

public class HyjModelEditor<T extends HyjModel> {

	private T mModel;
	private T mModelCopy;
	private HashMap<String, String> mValidationErrors = new HashMap<String, String>();
	
	public HyjModelEditor(T model){
		this.mModel = model;
		this.mModelCopy = (T) model.clone();
	}
	
	public T getModelCopy(){
		return this.mModelCopy;
	}

	public T getModel() {
		return this.mModel;
	}
	
	public void save(){
		for (Field field : this.mModel.getClass().getDeclaredFields()) {
		    field.setAccessible(true); // You might want to set modifier to public first.
		    try {
				field.set(this.mModel, field.get(this.mModelCopy));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.mModel.save();
	}
	
	public void validate() {
		this.mModelCopy.validate(this);
	}
	
	public boolean hasValidationErrors(){
		return !mValidationErrors.isEmpty();
	}
	
	public int getValidationErrorsCount(){
		return mValidationErrors.size();
	}
	
	public void setValidationError(String field, String error){
		mValidationErrors.put(field, error);
	}	
	
	public void setValidationError(String field, int error){
		mValidationErrors.put(field, HyjApplication.getInstance().getString(error));
	}	
	
	public void removeValidationError(String field){
		mValidationErrors.remove(field);
	}
	
	public String getValidationError(String field){
		return mValidationErrors.get(field);
	}
}
