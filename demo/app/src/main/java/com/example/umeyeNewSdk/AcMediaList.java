package com.example.umeyeNewSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umeyesdk.R;
import com.example.umeyesdk.adpter.MediaAdapter;
import com.example.umeyesdk.entity.MediaListItem;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.LocalFile;
import com.example.umeyesdk.utils.Utility;

import androidx.core.content.FileProvider;

public class AcMediaList extends Activity {
    private RelativeLayout rallSelectedAll;
    private CheckBox cbxSelectedAll;
    private ListView list;
    public static List<MediaListItem> data = null;
    private MediaAdapter adapter;
    private Handler handler;
    private final int INIT_LIST = 8888;// 初始化列表
    private final int CREATE_FAILE = 8889;// 创建失败
    private TextView tvTitle;
    private ProgressDialog dlgBusy;
    private Button btnBack;
    private Button btnDelete, btnEdit, btnFinish, btnCancel;// 编辑按钮
    private boolean isEdit = false;
    private boolean isImage = false;// 是否是图像列表
    List<String> files = null;
    private int isDelete = 0;
    private boolean isRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_media_list);

        isImage = getIntent().getBooleanExtra("isImage", true);
        System.out.println("是否是Ismage" + isImage);

        data = new ArrayList<MediaListItem>();
        data.clear();
        handler = new MyHandler();

        rallSelectedAll = (RelativeLayout) findViewById(R.id.bottom);
        cbxSelectedAll = (CheckBox) findViewById(R.id.cbxSelectedAll);

        cbxSelectedAll
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        try {
                            if (isChecked) {
                                // 选中
                                if (adapter != null) {
                                    adapter.SelectedAll();
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                // 未选中
                                adapter.notSelectedAll();
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (isImage)
            tvTitle.setText(R.string.mainSavedphotos);
        else
            tvTitle.setText(R.string.main_category_video);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnBack.setOnClickListener(new OnClick());
        btnDelete.setOnClickListener(new OnClick());

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new OnClick());
        // btnEdit.setOnClickListener(new OnClick());
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new OnClick());

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClick());

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            dlgBusy = new ProgressDialog(AcMediaList.this);
            dlgBusy.setCancelable(false);
            dlgBusy.setTitle(this.getString(R.string.loading_list));
            dlgBusy.setMessage(this.getString(R.string.wait_list));
            dlgBusy.show();
            new LoadDataThread().start();
        } else {
            Toast.makeText(AcMediaList.this, R.string.NoSdcard,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class OnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.btnEdit:
                        rallSelectedAll.setVisibility(View.VISIBLE);
                        adapter.ShowEditState();
                        btnEdit.setVisibility(View.GONE);
                        btnFinish.setVisibility(View.VISIBLE);
                        btnBack.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.VISIBLE);
                        isEdit = true;
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.btnFinish:
                        // 提醒
                        adapter.delete();
                        adapter.notifyDataSetChanged();
                        // LoadMediaData();
                        break;
                    case R.id.btnCancel:
                        reGetData();
                        System.out.println("data.siza------->" + data.size());
                        // 取消 checkbox 消失
                        rallSelectedAll.setVisibility(View.GONE);

                        btnBack.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.GONE);
                        btnFinish.setVisibility(View.GONE);
                        btnEdit.setVisibility(View.VISIBLE);
                        adapter.ShowFinishState();
                        adapter.notifyDataSetChanged();
                        isEdit = false;
                        break;
                    case R.id.btnBack:
                        GoBack();
                        break;
                    case R.id.btnDelete:
                        delete();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void delete() {
        isRemove = true;
        if (isRemove) {
            btnDelete.setBackgroundResource(R.drawable.btn_history_new_png);
        } else {
            btnDelete.setBackgroundResource(R.drawable.btn_history_focus);
        }

    }

    /**
     * 重新获取记录
     */
    public void reGetData() {
    }

    public void GoBack() {
        /*
         * Intent intent=new Intent(AcMediaList.this,AcMain.class);
         * startActivity(intent);
         */
        finish();
    }

    class OnItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            if (isEdit)
                return;

            if (isImage) {

                OpenImage(data.get(position).fileName);
            } else {
                OpenMp4(data.get(position).fileName);
            }
            // Intent intent;
            // intent = new Intent(AcMediaList.this, AcVideoDetail.class);
            // // intent.putExtra("fileName", data.get(position).fileName);
            // intent.putExtra("index", position);
            // System.out.println("index 的值" + position);
            // isDelete = position;
            // AcMediaList.this.startActivity(intent);
            // finish();

            // OpenMp4(data.get(position).fileName);
        }
    }

    public void OpenMp4(String fileName) {

        startActivity(getFileUriIntent(this,fileName,"video/mp4"));
        /*
         * Intent intent = new Intent(AcMediaList.this, VideoPlayback.class);//
         * 新添加的播放界面 intent.putExtra("fileName", fileName);
         * System.out.println("发过去的的：：~~~！~" + fileName); startActivity(intent);
         */
    }

    public void OpenImage(String fileName) {

        startActivity(getFileUriIntent(this,fileName,"image/*"));
    }

    public Intent getFileUriIntent(Context mContext, String fileName, String fileType) {
        File file = new File(fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contentUri = FileProvider.getUriForFile(mContext, getString(R.string.authorities), file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            contentUri = Uri.fromFile(file);
        }
        intent.setDataAndType(contentUri, fileType);
        return intent;
    }

    public void AddPicture(Bitmap bmp, String fileName, String des)// 添加图片项
    {
        MediaListItem item = new MediaListItem();
        item.bmp = bmp;
        item.fileName = fileName;
        item.description = des;
        data.add(item);
    }

    /**
     * 加载图片列表
     */
    public void LoadMediaData() {
        try {
            List<String> files = null;
            if (isImage) {
                boolean b = LocalFile.CreateDirectory(Constants.UserImageDir);
                if (b == false) {
                    handler.sendEmptyMessage(CREATE_FAILE);
                    return;
                }
                files = LocalFile.GetMatchExtFiles(Constants.UserImageDir,
                        "jpeg");
            } else {
                boolean b = LocalFile.CreateDirectory(Constants.UserVideoDir);
                if (b == false) {
                    handler.sendEmptyMessage(CREATE_FAILE);
                    return;
                }
                files = LocalFile.GetMatchExtFiles(Constants.UserVideoDir,
                        "MP4");
            }
            for (int i = 0; i < files.size(); i++)// 将文件夹内的内容添加到列表
            {
                try {
                    String fileName = files.get(i);
                    String name = fileName
                            .substring(fileName.lastIndexOf("/") + 1);
                    Bitmap bmp = isImage ? BitmapFactory.decodeResource(
                            getResources(), R.drawable.camera_online)
                            : BitmapFactory.decodeResource(getResources(),
                            R.drawable.camera_online);
                    String des = name;
                    AddPicture(bmp, fileName, des);
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
            Message msg = Message.obtain();
            msg.what = INIT_LIST;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class LoadDataThread extends Thread {
        @Override
        public void run() {
            LoadMediaData();
        }
    }

    public MediaListItem GetItemByName(String fileName) {
        for (int i = 0; i < data.size(); i++) {
            MediaListItem item = data.get(i);
            if (item.fileName.equalsIgnoreCase(fileName))
                return item;
        }
        return null;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INIT_LIST) {
                if (isImage)
                    adapter = new MediaAdapter(data, AcMediaList.this,
                            Constants.UserImageDir);
                else
                    adapter = new MediaAdapter(data, AcMediaList.this,
                            Constants.UserVideoDir);
                if (data.size() == 0) {
                    Toast.makeText(AcMediaList.this, R.string.none_files,
                            Toast.LENGTH_SHORT).show();
                }
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new OnItemClick());
                dlgBusy.dismiss();
                if (isImage)
                    new ImageLoadTask().execute();// 执行加载图片的任务
            } else if (msg.what == CREATE_FAILE) {
                Toast.makeText(AcMediaList.this, "无法创建访问SD卡内容",
                        Toast.LENGTH_SHORT).show();
                dlgBusy.dismiss();
            }

            /*
             * isSelected=new int[MediaAdapter.list.size()]; for (int i = 0; i <
             * isSelected.length; i++) { isSelected[i]=-1; }
             */
            super.handleMessage(msg);
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < data.size(); i++) {
                MediaListItem item = data.get(i);
                item.bmp = Utility.GetThumbImage(item.fileName, 50, 50);
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();// 更新UI
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GoBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void deleteDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(getResources().getString(R.string.delete_video));
        builder.setTitle(getResources().getString(R.string.message));
        builder.setPositiveButton(getResources().getString(R.string.Okao),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new File(files.get(isDelete)).delete();
                        files.remove(isDelete);
                        adapter.notifyDataSetChanged();
                        isRemove = false;
                        btnDelete
                                .setBackgroundResource(R.drawable.btn_history_focus);
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isRemove = false;
                        btnDelete
                                .setBackgroundResource(R.drawable.btn_history_focus);
                    }
                });
        builder.create().show();
    }
}
