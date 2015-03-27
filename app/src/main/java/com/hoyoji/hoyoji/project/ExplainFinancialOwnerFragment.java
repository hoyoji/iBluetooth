package com.hoyoji.hoyoji.project;

import android.content.Intent;
import android.view.View;

import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.btcontrol.R;


public class ExplainFinancialOwnerFragment extends HyjFragment {
	
	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_explain_finacialowner;
	}
	
	 public void onInitViewData() {
		 super.onInitViewData();
		 Intent intent = getActivity().getIntent();
		 String financialType = intent.getStringExtra("FINANCIAL_TYPE");
		 if ("Project".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_project).setVisibility(View.VISIBLE);
		 } else if ("MoneyExpense".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_expense).setVisibility(View.VISIBLE);
		 } else if ("MoneyIncome".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_income).setVisibility(View.VISIBLE);
		 } else if ("MoneyDepositExpense".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_depositeExpense).setVisibility(View.VISIBLE);
		 } else if ("MoneyDepositIncome".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_depositeIncome).setVisibility(View.VISIBLE);
		 } else if ("MoneyDepositPayback".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_depositePayback).setVisibility(View.VISIBLE);
		 } else if ("MoneyDepositReturn".equals(financialType)) {
			 getView().findViewById(R.id.explainFinancialOwnerFragment_textView_explain_depositeReturn).setVisibility(View.VISIBLE);
		 }
     }
}
