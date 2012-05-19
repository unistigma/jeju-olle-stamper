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
			//1. 사용자가 입력한 수의 제곱근을 구한다.
			int squareR = (int) (Math.sqrt(inputNumber));
			Log.i("squareR=", Integer.toString(squareR));
			
			//2. 제곱근보다 작은 소수들을 구한다.
			sievePrimeNumbers(squareR);
			
			//3. 제곱근보다 작은 소수들로 새로운 배열을 생성한 후, 사.입.수를 나누어 가며 소인수를 찾는다.
			createPrimeSet();
			searchPrimeFactor();
			
			//4. 부모프로세스의 핸들러로 소인수 리스트를 보낸다.
			mHandler.sendMessage(mHandler.obtainMessage(ACCUMULATE_COMPLETE, primeFactor));
			
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendMessage(mHandler.obtainMessage(THREAD_ERROR, e));
		}
	}
	
	private void sievePrimeNumbers(int inputNo) {
    	//1. 모두 false인 상태.(true: 소수, false: 소수가 아님)
    	primeNumbers = new boolean[inputNo+1];	
    	Log.i("Step1: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//2. 2를 true로 설정.(2는 소수)
    	primeNumbers[2] = true;
    	Log.i("Step2: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//3. 모든 홀수를 true로 설정.(모든 짝수는 소수가 아니므로 false로 계속 놔둔다)
    	for(int i = 3; i < primeNumbers.length; i=i+2) {
    		primeNumbers[i] = true;
    	}
    	Log.i("Step3: primeCount=", Integer.toString(getPrimeCount(primeNumbers)));
    	
    	//4. 모든 홀수를 대상으로 소수인지 판별.
    	moreSievePrimeNumbers();
    }
	
	/**
     * "소수의 배수는 소수가 아니다"에 착안하여,
     * 3부터 주어진 수까지 홀수에 대해서 소수인지 체크한 후 해당 소수의 배수를 모두 false로 설정.
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
