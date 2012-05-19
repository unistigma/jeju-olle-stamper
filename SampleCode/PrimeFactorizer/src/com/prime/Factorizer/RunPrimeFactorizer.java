package com.prime.Factorizer;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.util.Log;

public class RunPrimeFactorizer extends Thread {
	
	public static final int ACCUMULATE_COMPLETE = 0;
	public static final int THREAD_ERROR = 1;

	Handler mHandler;
	long inputNumber;
	boolean [] primeNumbers;
	List<Integer> primeSet = new ArrayList<Integer> ();
	List<Long> primeFactor = new ArrayList<Long> ();
	
	public RunPrimeFactorizer(Handler handler, long inputNumber) {
		super();
		this.mHandler = handler;
		this.inputNumber = inputNumber;
		
		primeNumbers = null;
    	primeSet.clear();
    	primeFactor.clear();
	}
	
	@Override
	public void run() {
		super.run();
		
		try {
			//1. ����ڰ� �Է��� ���� �������� ���Ѵ�.
			int squareR = (int) (Math.sqrt(inputNumber));
			Log.i("squareR=", Integer.toString(squareR));
			
			//2. �����ٺ��� ���� �Ҽ����� ���Ѵ�.
			sievePrimeNumbers(squareR);
			
			//3. �����ٺ��� ���� �Ҽ���� ���ο� �迭�� ������ ��, ��.��.���� ������ ���� ���μ��� ã�´�.
			createPrimeSet();
			searchPrimeFactor();
			
			//4. �θ����μ����� �ڵ鷯�� ���μ� ����Ʈ�� ������.
			mHandler.sendMessage(mHandler.obtainMessage(ACCUMULATE_COMPLETE, primeFactor));
			
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(mHandler.obtainMessage(THREAD_ERROR, e));
		}
	}
	
	private void sievePrimeNumbers(int inputNo) {
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
	
	/**
     * "�Ҽ��� ����� �Ҽ��� �ƴϴ�"�� �����Ͽ�,
     * 3���� �־��� ������ Ȧ���� ���ؼ� �Ҽ����� üũ�� �� �ش� �Ҽ��� ����� ��� false�� ����.
     */
    private void moreSievePrimeNumbers() {
    	
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
    
    private boolean isItPrimeNumber(int expected) {    	
    	for(int i = 2; i < expected; i++) {
    		if(0 == (expected % i)) {
    			return false;
    		}
    	}
    	return true;
    }
	
	private void createPrimeSet() {
    	for(int i = 0; i < primeNumbers.length; i++) {
    		if(primeNumbers[i] == true) {
    			primeSet.add(i);
    		}
    	}
    	
    	Log.i("StepY: primeSet Size : ", Integer.toString(primeSet.size()));
    }
    
    public void searchPrimeFactor() {
    	primeFactor.add(divideTry(inputNumber));
    	
    	Log.i("StepZ: primeFactor Size : ", Integer.toString(primeFactor.size()));
    }
    
    private long divideTry(long toBeDivided) {
    	for(int primeNumber : primeSet) {
    		if((toBeDivided > primeNumber) && (0 == toBeDivided % primeNumber)) {
    			long r = toBeDivided / primeNumber;
    			primeFactor.add((long)primeNumber);
    			return divideTry(r);
    		} 
    	}
		return toBeDivided;
    }
    
    private int getPrimeCount(boolean[] pNumbers) {
    	int primeCount = 0;

    	for(boolean i : pNumbers) {
    		if(i == true) {
    			primeCount++;
    		}
    	}
    	return primeCount;
    }
    
}
