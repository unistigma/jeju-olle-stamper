package com.prime.Factorizer;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PrimeFactorizerActivity extends Activity {
	
	long inputNumber;
	boolean [] primeNumbers;
	List<Integer> primeSet = new ArrayList<Integer> ();
	List<Long> primeFactor = new ArrayList<Long> ();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void mOnClick_spec1(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	Button tvSpec1 = (Button)findViewById(R.id.specificValue1);
    	tvInput.setText(tvSpec1.getText());
    }
    
    public void mOnClick_spec2(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	Button tvSpec2 = (Button)findViewById(R.id.specificValue2);
    	tvInput.setText(tvSpec2.getText());
    }
    
    public void mOnClick_clear(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	tvInput.setText("");
    }
    
    public void mOnClick(View v) {
    	TextView tv = (TextView) findViewById(R.id.inputValue);
    	
    	if(0 == tv.getText().length()) {
    		Toast.makeText(this, "���ڸ� �Է����ּ���", 1).show();
    	} else {
        	//0. �Է°� ���� & �ʱ�ȭ
    		inputNumber = Long.parseLong(tv.getText().toString());
    		initialize();
    		
    		//1. ����ڰ� �Է��� ���� �������� ���Ѵ�.
    		int squareR = (int)(Math.sqrt(inputNumber));
    		Log.i("squareR=", Integer.toString(squareR));
    		
    		//2. �����ٺ��� ���� �Ҽ����� ���Ѵ�.
        	sievePrimeNumbers(squareR);
        	
        	//3. �����ٺ��� ���� �Ҽ���� ���ο� �迭�� ������ ��, ��.��.���� ������ ���� ���μ��� ã�´�.
        	createPrimeSet();
        	searchPrimeFactor();
        	
        	//4. ����� ���
        	String primeFactors = "";
        	for(long r : primeFactor) {
	        	Log.i("primeFactor : ", Long.toString(r));
	        	primeFactors += Long.toString(r) + ",";
        	}
        	TextView tvResult = (TextView) findViewById(R.id.resultValue);
        	tvResult.setText(primeFactors);
    	}
	}
    public void initialize() {
    	primeNumbers = null;
    	primeSet.clear();
    	primeFactor.clear();
    }
    
    public void searchPrimeFactor() {
    	primeFactor.add(divideTry(inputNumber));
    	
    	Log.i("StepZ: primeFactor Size : ", Integer.toString(primeFactor.size()));
    }
    
    public long divideTry(long toBeDivided) {
    	for(int primeNumber : primeSet) {
    		if((toBeDivided > primeNumber) && (0 == toBeDivided % primeNumber)) {
    			long r = toBeDivided / primeNumber;
    			primeFactor.add((long)primeNumber);
    			return divideTry(r);
    		} 
    	}
		return toBeDivided;
    }
    
    public void sievePrimeNumbers(int inputNo) {
    	//1. ��� false�� ����.(true: �Ҽ�, false: �Ҽ��� �ƴ�)
    	primeNumbers = new boolean[inputNo+1];	
    	Log.i("Step1: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//2. 2�� true�� ����.(2�� �Ҽ�)
    	primeNumbers[2] = true;
    	Log.i("Step2: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//3. ��� Ȧ���� true�� ����.(��� ¦���� �Ҽ��� �ƴϹǷ� false�� ��� ���д�)
    	for(int i = 3; i < primeNumbers.length; i=i+2) {
    		primeNumbers[i] = true;
    	}
    	Log.i("Step3: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//4. ��� Ȧ���� ������� �Ҽ����� �Ǻ�.
    	moreSievePrimeNumbers();
    }
    
    public boolean isItPrimeNumber(int expected) {    	
    	for(int i = 2; i < expected; i++) {
    		if(0 == (expected % i)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * "�Ҽ��� ����� �Ҽ��� �ƴϴ�"�� �����Ͽ�,
     * 3���� �־��� ������ Ȧ���� ���ؼ� �Ҽ����� üũ�� �� �ش� �Ҽ��� ����� ��� false�� ����.
     */
    public void moreSievePrimeNumbers() {
    	
    	for(int i = 3; i < primeNumbers.length; i=i+2) {
    		if(false == primeNumbers[i])
        		continue;
    		
    		if(isItPrimeNumber(i)){
    			for(int j = i*2; j < primeNumbers.length; j=j+i) {
    				primeNumbers[j] = false;
    			}
    		}
    	}
    	
    	Log.i("StepX: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    }
    
    public int getPrimeCount(boolean[] pNumbers) {
    	int primeCount = 0;

    	for(boolean i : pNumbers) {
    		if(i == true) {
    			primeCount++;
    		}
    	}

    	return primeCount;
    }
    
    public void createPrimeSet() {
    	for(int i = 0; i < primeNumbers.length; i++) {
    		if(primeNumbers[i] == true) {
    			primeSet.add(i);
    		}
    	}
    	
    	Log.i("StepY: primeSet Size : ", Integer.toString(primeSet.size()));
    }
}

	
