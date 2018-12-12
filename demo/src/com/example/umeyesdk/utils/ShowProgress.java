package com.example.umeyesdk.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.umeyesdk.R;


public class ShowProgress extends Dialog {

	public Context context;
	int layout;
	public LayoutInflater layoutInflate;
	public View view = null;
	public int width;
	public int height;
	TextView message;

	public ShowProgress(Context context) {
		super(context, R.style.progress_Dialog);
		this.context = context;
		this.layoutInflate = LayoutInflater.from(context);
		this.view = layoutInflate.inflate(R.layout.layout_show_progress, null);
		message = (TextView) view.findViewById(R.id.tv_message);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		
	}

	public void setMessage(String sMessage) {
		setMessageVisible(sMessage);
		message.setText(sMessage + "");
	}

	public void setMessage(int sMessageId) {
		String sMessage = context.getString(sMessageId);
		setMessageVisible(sMessage);
		try {
			message.setText(sMessage + "");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setMessageVisible(String sMessage) {
		if (TextUtils.isEmpty(sMessage)) {
			message.setVisibility(View.GONE);
		} else {
			message.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (view != null) {
			this.setContentView(view);
		}
	}

}