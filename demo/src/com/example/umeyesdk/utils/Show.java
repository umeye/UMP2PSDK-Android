package com.example.umeyesdk.utils;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.umeyesdk.R;

public class Show {
	public static void toast(Context con, int id) {
		Toast.makeText(con, con.getResources().getString(id),
				Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context con, String text) {
		Toast.makeText(con, text, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context con, int drawableId, int id) {
		toast(con, drawableId, con.getString(id));
	}

	public static void toast(Context con, int drawableId, String text) {
		Toast.makeText(con, text, Toast.LENGTH_SHORT).show();
		Toast toast = new Toast(con);
		toast.setGravity(Gravity.CENTER, 0, 0);

		LayoutInflater layoutInflate = LayoutInflater.from(con);
		View view = layoutInflate.inflate(R.layout.layout_show_progress, null);
		TextView message = (TextView) view.findViewById(R.id.tv_message);
		message.setText(text);
		toast.setView(view);
	}

}
