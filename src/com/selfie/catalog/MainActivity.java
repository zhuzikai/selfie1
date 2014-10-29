package com.selfie.catalog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanselandpetal.catalog.R;
import com.selfie.catalog.model.selfie;
import com.selfie.catalog.parsers.SelfieJSONParser;
import com.selfie.catalog.view.XListView;
import com.selfie.catalog.view.XListView.IXListViewListener;

public class MainActivity extends Activity implements IXListViewListener{
	public String contenta;
	public static final String PHOTOS_BASE_URL = "https://api.instagram.com/v1/tags/selfie/media/recent?client_id=c8f1727f2646485c953f0e25f333534f";
	private XListView mListView;
	private SelfieAdapter mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	TextView output;
	ProgressBar pb;
	List<MyTask> tasks;

	List<selfie> selfieList;
//	List<selfie> List;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);

		tasks = new ArrayList<>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_get_data) {
			if (isOnline()) {
				requestData(PHOTOS_BASE_URL);
			} else {
				Toast.makeText(this, "Network isn't available",
						Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	private void geneItems() {
		if (isOnline()) {
			requestData(PHOTOS_BASE_URL);
		} else {
			Toast.makeText(this, "Network isn't available",
					Toast.LENGTH_LONG).show();
		}
//		List.addAll(selfieList);
//		List=selfieList;
//		for (int i = 0; i != 20; ++i) {
//			items.add("refresh cnt " + (++start));
//		}
	}
	
	private void requestData(String uri) {
		MyTask task = new MyTask();
		task.execute(uri);
	}

	protected void updateDisplay() {
		// Use FlowerAdapter to display data


		
		mListView = (XListView) findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new SelfieAdapter(this, R.layout.item_flower,selfieList);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(MainActivity.this);
		mHandler = new Handler();
		
	}

	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}

	private class MyTask extends AsyncTask<String, String, List<selfie>> {

		@Override
		protected void onPreExecute() {
			if (tasks.size() == 0) {
				pb.setVisibility(View.VISIBLE);
			}
			tasks.add(this);
		}

		@Override
		protected List<selfie> doInBackground(String... params) {

			String content = HttpManager.getData(params[0]);
			selfieList = SelfieJSONParser.parseFeed(content);
/*			contenta = content;*/
			return selfieList;
		}

		@Override
		protected void onPostExecute(List<selfie> result) {

			tasks.remove(this);
/*			TextView mTextview = (TextView) findViewById(R.id.textView1);
			mTextview.setText(contenta);*/
			if (tasks.size() == 0) {
				pb.setVisibility(View.INVISIBLE);
			}

			if (result == null) {
				Toast.makeText(MainActivity.this, "Web service not available",
						Toast.LENGTH_LONG).show();
				return;
			}

			selfieList = result;
			updateDisplay();

		}

	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(" ");
	}
	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = ++refreshCnt;
				items.clear();
				geneItems();
				// mAdapter.notifyDataSetChanged();
				mAdapter = new SelfieAdapter(MainActivity.this, R.layout.item_flower,selfieList);
				mListView.setAdapter(mAdapter);
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}

}