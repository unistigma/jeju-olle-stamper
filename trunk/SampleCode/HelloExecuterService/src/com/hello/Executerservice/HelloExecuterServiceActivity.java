package com.hello.Executerservice;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HelloExecuterServiceActivity extends Activity {

	private static final String TAG = HelloExecuterServiceActivity.class.toString();

	private static final int DIALOG_EXCEPTION_ERROR = 100;
	private static final int DIALOG_NODATA = 200;

	RunParent mThread;
	int mMainValue = 0;
	TextView mMainText;
	TextView mBackText;

	private ListView resultList;
	private ResultAdapter adapter;

	public static List<Item> items = new ArrayList<Item>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		adapter = new ResultAdapter(this);
		resultList = (ListView) findViewById(R.id.result_list);
		resultList.setVisibility(View.VISIBLE);
		resultList.setAdapter(adapter);
		
		startAloneThread();
	}
	
	private void startAloneThread() {
		RunAlone runAlone = new RunAlone(mHandler);
		new Thread(runAlone).start();
	}

	public void mOnClick(View v) {
		mMainValue++;
		mMainText = (TextView)findViewById(R.id.MainValue);
		mMainText.setText("MainValue : " + mMainValue);
	}

	public void mOnClick2(View v) {
		startThread();
	}
	
	private void startThread() {
		RunParent asyncRun = new RunParent(mHandler);
		new Thread(asyncRun).start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case RunAlone.RUN_ALONE_DATA:
				TextView tv = (TextView)findViewById(R.id.AloneValue);
				tv.setText(Integer.toString(msg.arg1));
				break;
			case RunParent.PAK_DATA:
				Item item = (Item)(msg.obj);
				items.add(item);
				adapter.notifyDataSetChanged();
				break;
			case RunParent.QUERYASYNC_ERROR:
				showDialog(DIALOG_EXCEPTION_ERROR);
				Exception e = (Exception) msg.obj;
				Log.w(TAG, e);
				break;
			case RunParent.QUERYASYNC_NODATA:
				showDialog(DIALOG_NODATA);
				Exception e1 = (Exception) msg.obj;
				Log.w(TAG, e1);
			}
		}
	};

	class ResultAdapter extends BaseAdapter {
		Context context;

		public ResultAdapter(Context context) {
			super();
			this.context = context;
		}

		public int getCount() {
			return items.size();
		}

		public Item getItem(int position) {
			return items.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.ret_info, null);
			}

			TextView MainValue = (TextView) v.findViewById(R.id.TextView_MainValue);
			TextView SubValue1 = (TextView) v.findViewById(R.id.TextView_SubValue1);
			TextView SubValue2 = (TextView) v.findViewById(R.id.TextView_SubValue2);
			TextView SubValue3 = (TextView) v.findViewById(R.id.TextView_SubValue3);
			TextView SubValue4 = (TextView) v.findViewById(R.id.TextView_SubValue4);
			TextView SubValue5 = (TextView) v.findViewById(R.id.TextView_SubValue5);
			TextView LastKey = (TextView) v.findViewById(R.id.TextView_LastKeyValue);

			Item pak = items.get(position);
			MainValue.setText(Integer.toString(pak.mainValue));
			SubValue1.setText(Integer.toString(pak.subValue[0]));
			SubValue2.setText(Integer.toString(pak.subValue[1]));
			SubValue3.setText(Integer.toString(pak.subValue[2]));
			SubValue4.setText(Integer.toString(pak.subValue[3]));
			SubValue5.setText(Integer.toString(pak.subValue[4]));
			LastKey.setText(pak.lastKeyValue);

			return v;
		}
	}
}