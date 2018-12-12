package com.example.umeyesdk;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.Player.Source.TVideoFile;
import com.example.umeyesdk.adpter.SearchAdapter;

public class AcSearchRecordResult extends Activity
{
	private ListView lvResult;
	private SearchAdapter adapter;
	private Button btnBack;
	private ImageButton btnSearch;
	private TextView tvTitle;
	private List<TVideoFile> data;
	private String deviceId;
	public static TVideoFile VideoFile;
	public static String title;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_search_record_result);
		InitView();
	}
	public void InitView()
	{
		tvTitle=(TextView)findViewById(R.id.tvTitle);
		tvTitle.setText("");		
		
		btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClick());
		
		btnSearch=(ImageButton)findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClick());
		
		lvResult=(ListView)findViewById(R.id.lvResult);
		adapter=new SearchAdapter(this, AcSearchRecord.data);
		lvResult.setAdapter(adapter);
		lvResult.setOnItemClickListener(new OnItemClick());
		deviceId = getIntent().getStringExtra("deviceId");
		title=getIntent().getStringExtra("title");
		data=AcSearchRecord.data;

		

	}

	class OnItemClick implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
		{
			VideoFile=data.get(position);
			Intent intent=new Intent(AcSearchRecordResult.this,AcVod.class);

			intent.putExtra("deviceId",
					getIntent().getStringExtra("deviceId"));
			intent.putExtra("title",title);
			startActivity(intent);
		}
	}

	public class OnClick implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnSearch:
				onSearchRequested();
				break;
			}
		}
	}
}
