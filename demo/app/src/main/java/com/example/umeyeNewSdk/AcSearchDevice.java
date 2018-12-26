package com.example.umeyeNewSdk;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.Player.Core.PlayerClient;
import com.Player.Source.TSearchDev;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.adpter.SearchDeviceAdapter;
import com.example.umeyesdk.entity.SearchDeviceInfo;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcSearchDevice extends Activity {
	public ShowProgress pd;
	AppMain appMain;
	private ListView listView;
	private SearchDeviceAdapter sAdapter;
	public static ArrayList<SearchDeviceInfo> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.ac_search_devce);
		appMain = (AppMain) this.getApplicationContext();
		listView = (ListView) findViewById(R.id.lvLive);
		listView.setVisibility(View.INVISIBLE);
		sAdapter = new SearchDeviceAdapter(AcSearchDevice.this);
		listView.setAdapter(sAdapter);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.menu_btn1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ThreadSearchDevice().execute();
			}
		});
		new ThreadSearchDevice().execute();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		if (sAdapter != null) {
			sAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public class ThreadSearchDevice extends
			AsyncTask<Void, Void, List<SearchDeviceInfo>> {

		@Override
		protected List<SearchDeviceInfo> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			list = new ArrayList<SearchDeviceInfo>();
			// String[] temp = StreamData.ServerAddress.split(":");
			// String address = temp[0];
			// int port = Integer.parseInt(temp[1]);
			// String userName = StreamData.UserName;
			// String password = StreamData.Password;
			// System.out.println(address + ":" + port + "  " + userName + "  "
			// + password);
			PlayerClient playerclient = appMain.getPlayerclient();
			int searchRet = playerclient.StartSearchDev(10);// 5代表等待多少秒
			for (int i = 0; i < searchRet; i++) {
				TSearchDev tsearch = playerclient.SearchDevByIndex(i);

				SearchDeviceInfo searchInfo = new SearchDeviceInfo(
						tsearch.dwVendorId, tsearch.sDevName, tsearch.sDevId,
						tsearch.sDevUserName, tsearch.bIfSetPwd,
						tsearch.bIfEnableDhcp, tsearch.sAdapterName_1,
						tsearch.sAdapterMac_1, tsearch.sIpaddr_1,
						tsearch.sNetmask_1, tsearch.sGateway_1, tsearch.usChNum);
				Log.w("searchRet", "UMId :" + searchInfo.toString());
				list.add(searchInfo);

			}
			playerclient.StopSearchDev();
			return list;
		}

		@Override
		protected void onPostExecute(List<SearchDeviceInfo> flist) {
			// TODO Auto-generated method stub
			pd.dismiss();
			if (list.size() > 0) {
				listView.setVisibility(View.VISIBLE);
				sAdapter.setNodeList(flist);
				// listView.startLayoutAnimation();
			} else {
				listView.setVisibility(View.INVISIBLE);
				Show.toast(AcSearchDevice.this, R.string.nodataerro);
			}

			super.onPostExecute(list);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (pd == null) {
				pd = new ShowProgress(AcSearchDevice.this);
				pd.setMessage(AcSearchDevice.this.getResources().getString(
						R.string.searching_device));
				pd.setCanceledOnTouchOutside(false);
			}
			pd.show();
			super.onPreExecute();
		}
	}
}
