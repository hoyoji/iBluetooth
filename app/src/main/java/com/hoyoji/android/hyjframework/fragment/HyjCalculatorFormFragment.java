package com.hoyoji.android.hyjframework.fragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hoyoji.btcontrol.R;

public class HyjCalculatorFormFragment extends HyjUserFormFragment implements OnClickListener{

	private TextView mHyjTextViewAmount = null;
	private TextView mHyjRemarkFieldAmount = null;
	boolean mTextCannotbeEmpty = false;
	boolean isClear = false; //用于是否显示器需要被清理
	
	
	private Button button_1;
	private Button button_2;
	private Button button_3;
	private Button button_4;
	private Button button_5;
	private Button button_6;
	private Button button_7;
	private Button button_8;
	private Button button_9;
	private Button button_0;
	private Button button_point;
	private Button button_clear;
	private Button button_plus;
	private Button button_subtract;
	private Button button_multiply;
	private Button button_divide;
	private Button button_delete;
	private Button button_equal;
	
	@Override
	public Integer useContentView() {
		return R.layout.calculator_formfragment;
	}

//	@Override
//	public Integer useOptionsMenuView() {
//		return null;
//	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Intent intent = getActivity().getIntent();
		Double amount = intent.getDoubleExtra("AMOUNT",0.00);
		
		mHyjTextViewAmount = (TextView) getView().findViewById(R.id.hyjCalculatorFormFragment_textField_amount);
		mHyjTextViewAmount.setText(subZeroAndDot(amount+ ""));
		mHyjTextViewAmount.post(new Runnable(){
			@Override
			public void run() {
//				mHyjTextViewAmount.setGravity(Gravity.RIGHT);
				mHyjTextViewAmount.setMovementMethod(ScrollingMovementMethod.getInstance()); 
//				mHyjTextViewAmount.scrollTo(mHyjTextViewAmount.getRight(), 0);

			}
		});
		mHyjRemarkFieldAmount = (TextView) getView().findViewById(R.id.hyjCalculatorFormFragment_hyjRemarkField_remark);
		mHyjRemarkFieldAmount.setMovementMethod(ScrollingMovementMethod.getInstance()); 
//		mHyjRemarkFieldAmount.setHorizontalGravity(Gravity.RIGHT);//右对齐
		
		
		button_0 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_0);
		button_1 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_1);
		button_2 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_2);
		button_3 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_3);
		button_4 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_4);
		button_5 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_5);
		button_6 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_6);
		button_7 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_7);
		button_8 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_8);
		button_9 = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_9);
		button_clear = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_clear);
		button_delete = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_delete);
		button_plus = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_plus);
		button_subtract = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_subtract);
		button_multiply = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_multiply);
		button_divide = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_divide);
		button_equal = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_equal);
		button_point = (Button)getView().findViewById(R.id.hyjCalculatorFromFragment_button_point);
		button_0.setOnClickListener(this);
		button_1.setOnClickListener(this);
		button_2.setOnClickListener(this);
		button_3.setOnClickListener(this);
		button_4.setOnClickListener(this);
		button_5.setOnClickListener(this);
		button_6.setOnClickListener(this);
		button_7.setOnClickListener(this);
		button_8.setOnClickListener(this);
		button_9.setOnClickListener(this);
		button_clear.setOnClickListener(this);
		button_delete.setOnClickListener(this);
		button_point.setOnClickListener(this);
		button_plus.setOnClickListener(this);
		button_subtract.setOnClickListener(this);
		button_multiply.setOnClickListener(this);
		button_divide.setOnClickListener(this);
		button_equal.setOnClickListener(this);
	}
	
	@Override
    public void onClick(View e) {
        Button btn = (Button)e;
        String exp = mHyjTextViewAmount.getText().toString();
        if(btn.getText().equals(".")){
        	if(!isEmpty(exp)){
	        	String[] expStrArray = exp.split("\\.");
				if(expStrArray.length > 1){
					if(expStrArray[expStrArray.length-1].contains("+") 
						|| expStrArray[expStrArray.length-1].contains("-")
						|| expStrArray[expStrArray.length-1].contains("×")
						|| expStrArray[expStrArray.length-1].contains("÷")){
						canSet(exp,".");
					}
				}else{
					canSet(exp,".");
				}
			}
        	
        }else if(btn.getText().equals("C")){
        	mHyjTextViewAmount.setText("0");
        	mHyjRemarkFieldAmount.setText("");
        }else if(btn.getText().equals("DEL")){ 
            if(isEmpty(exp)) return;
            else
            	mHyjTextViewAmount.setText(exp.substring(0, exp.length()-1));
        }else if(btn.getText().equals("+")){
        	canSet(exp,"+");
        }else if(btn.getText().equals("-")){
        	if(exp.equals("")) {
        		mHyjTextViewAmount.setText("-");
        	} else{
            	canSet(exp,"-");
        	}
        }else if(btn.getText().equals("×")){
        	canSet(exp,"×");
        }else if(btn.getText().equals("÷")){
        	canSet(exp,"÷");
        }else if(btn.getText().equals("=")){
        	if (exp.substring(exp.length()-1, exp.length()).equals(".")
	    		||exp.substring(exp.length()-1, exp.length()).equals("+")
	    		||exp.substring(exp.length()-1, exp.length()).equals("-")
	    		||exp.substring(exp.length()-1, exp.length()).equals("×")
	    		||exp.substring(exp.length()-1, exp.length()).equals("÷")){
        		exp = exp.substring(0, exp.length()-1);
        	}
        	if(exp.contains("+") || exp.contains("-") || exp.contains("×")|| exp.contains("÷")){
	        	String expStr = exp.replaceAll("×", "*");
	        	String expString = expStr.replaceAll("÷", "/");
	        	BigDecimal result = parse(expString);
	        	DecimalFormat df = new DecimalFormat( "#0.0000");//   16位整数位，两小数位 
	        	String setResult = df.format(result); 
	        	
	        	mHyjRemarkFieldAmount.setText(mHyjRemarkFieldAmount.getText().toString().equals("")?exp + "=" + subZeroAndDot(setResult):mHyjRemarkFieldAmount.getText()+"\n"+exp+ "=" + subZeroAndDot(setResult));
	        	mHyjTextViewAmount.setText(subZeroAndDot(setResult));
        	}
        }else{
        	if(exp.length() == 1 && exp.equals("0")){
        		mHyjTextViewAmount.setText(btn.getText());
        	} else {
        		mHyjTextViewAmount.setText(exp+""+btn.getText());
        	}
        }
    }
	
	private void canSet (String exp ,String operate){
		if(!isEmpty(exp)){
			if (exp.substring(exp.length()-1, exp.length()).equals(".")
	    		||exp.substring(exp.length()-1, exp.length()).equals("+")
	    		||exp.substring(exp.length()-1, exp.length()).equals("-")
	    		||exp.substring(exp.length()-1, exp.length()).equals("×")
	    		||exp.substring(exp.length()-1, exp.length()).equals("÷")){
				if(!operate.equals(".")){
					mHyjTextViewAmount.setText(exp.substring(0, exp.length()-1) + operate);
				}
	    	}else{
	    		mHyjTextViewAmount.setText(exp + operate);
	    	}
		}
		
	}
     
    /***
     * @param str
     * @return 字符串非空验证
     */
    private boolean isEmpty(String str){
        return (str==null || str.trim().length()==0);
    }
    
    
    private boolean compare(char str) {
        if (chs.empty()) {
            // 当为空时，显然 当前优先级最低，返回高
            return true;
        }
        char last = (char) chs.lastElement();
        switch (str) {
        case '*': {
            // '*/'优先级只比'+-'高
            if (last == '+' || last == '-')
                return true;
            else
                return false;
        }
        case '/': {
            if (last == '+' || last == '-')
                return true;
            else
                return false;
        }
            // '+-'为最低，一直返回false
        case '+':
            return false;
        case '-':
            return false;
        }
        return true;
    }
    private Stack<BigDecimal> numbers = new Stack<BigDecimal>();
    
    private Stack<Character> chs = new Stack<Character>();
    public BigDecimal caculate(String st) {
        StringBuffer sb = new StringBuffer(st);
        StringBuffer num = new StringBuffer();
        String tem = null;
        char next;
        while (sb.length() > 0) {
            tem = sb.substring(0, 1);// 获取字符串的第一个字符
            sb.delete(0, 1);
            if (isNum(tem.trim())||tem.trim().equals(".")) {
                num.append(tem);// 如果是数字，将其放入num当中
            } else {
  
                if (num.length() > 0 && !"".equals(num.toString().trim())) {// 当截取的字符不是数字时，则认为num中放置的时一个完整的数字，
                    // 如123+1,当获取到+时，前面的123可以认为是一个完整的数
                    BigDecimal bd = new BigDecimal(num.toString().trim());
                    numbers.push(bd);
                    num.delete(0, num.length());
                }
                // 如果chs为空，这认为这时第一个字符直接放入
                if (!chs.isEmpty()) {
                    // 当当前的运算符优先级等于或者小于栈顶得预算符时，做运算.
                    // 例如,1+2+3,当截取到2,3之间的“+”与1,2之间的"+"优先级相等时，可以先计算1+2，使其变成3+3
                    // 同样，1*2+3,当截取到2,3之间的“+”与1,2之间的"*"优先级小，可以先计算1*2，使其变成2+3
  
                    while (!compare(tem.charAt(0))) {
                        caculate();
                    }
                }
                // 当数字栈也为空时,既运算式的第一个数字为负数时
                if (numbers.isEmpty()) {
                    num.append(tem);
                } else {
                    chs.push(new Character(tem.charAt(0)));
                }
                // 判断后一个字符是否为“-”号，为"-"号时，认为数字为负数
                // 例如 1*2*(-5)，因为此运算不计算()，因此将被改写为1*2*-5,如此情况，须将"-"认为是负数表达式而非减号
                next = sb.charAt(0);
                if (next == '-') {
                    num.append(next);
                    sb.delete(0, 1);
                }
  
            }
        }
        // 由于前面将数字放入栈时，是通过获取符号为时处理，导致最后一个数字没有放入栈中，因此将最后的数字放入栈中
        BigDecimal bd = new BigDecimal(num.toString().trim());
        numbers.push(bd);
        // 此时符号栈上最多只有2个符号，并且栈顶得符号优先级高，做运算
        while (!chs.isEmpty()) {
            caculate();
        }
        return numbers.pop();
    }
  
    private void caculate() {
        BigDecimal b = numbers.pop();// 第二个运算数
        BigDecimal a = null;// 第一个运算数
        a = numbers.pop();
        char ope = chs.pop();
        BigDecimal result = null;// 运算结果
        switch (ope) {
        // 如果是加号或者减号，则
        case '+':
//            result = a.add(b);
            result = new BigDecimal(Double.parseDouble(a.toString())+Double.parseDouble(b.toString()));
            // 将操作结果放入操作数栈
            numbers.push(result);
            break;
        case '-':
            // 将操作结果放入操作数栈
//            result = a.subtract(b);
            result = new BigDecimal(Double.parseDouble(a.toString())-Double.parseDouble(b.toString()));
            numbers.push(result);
            break;
        case '*':
//        	MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
//            result = a.multiply(b,mc);
        	result = new BigDecimal(Double.parseDouble(a.toString())*Double.parseDouble(b.toString()));
            // 将操作结果放入操作数栈
            numbers.push(result);
            break;
        case '/':
//        	MathContext dc = new MathContext(2, RoundingMode.HALF_DOWN);
        	if ( Double.parseDouble(b.toString()) == 0) {
        		result = new BigDecimal(0);
        	} else {
        		result = new BigDecimal(Double.parseDouble(a.toString())/Double.parseDouble(b.toString()));
//        		result = a.divide(b,dc);// 将操作结果放入操作数栈
        	}
            numbers.push(result);
            break;
        }
    }
  
    private boolean isNum(String num) {
        return num.matches("[0-9]");
    }
      
    /**
     *
     * 功能描述。
     * 解析，将带有括号的运算符变成没有带括号的字运算
     */
        public BigDecimal parse(String st) {
            int start = 0;
            StringBuffer    sts = new StringBuffer(st);
            int end = -1;
            while ((end = sts.indexOf(")")) > 0) {
                String s = sts.substring(start, end + 1);
                int first = s.lastIndexOf("(");
                BigDecimal value = caculate(sts.substring(first + 1, end));
                sts.replace(first, end + 1, value.toString());
            }
            return caculate(sts.toString());
        }  
        
    public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    } 

	 @Override
	 public void onSave(View v){
		 super.onSave(v);
		 Intent intent = new Intent();
		 if(mHyjTextViewAmount.getText().toString().equals("")){
			 intent.putExtra("calculatorAmount", "0");
		 } else {
			 button_equal.performClick();
			 intent.putExtra("calculatorAmount", mHyjTextViewAmount.getText().toString());
		 }
		 getActivity().setResult(Activity.RESULT_OK, intent);
		 
		 getActivity().finish();
	 }
}
