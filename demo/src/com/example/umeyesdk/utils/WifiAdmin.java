package com.example.umeyesdk.utils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class WifiAdmin implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5304895044151590209L;
	private final static String TAG = "WifiAdmin";
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	ConnectCallBack callBack;
	WifiCipherType cipherType;
	Context context;

	/**
	 * 构造方法
	 */
	public WifiAdmin(Context context) {
		this.context = context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	/**
	 * 打开Wifi网卡
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * 关闭Wifi网卡
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 检查当前Wifi网卡状态
	 */
	public int getWifiState() {
		return mWifiManager.getWifiState();
	}

	/**
	 * 扫描周边网络
	 */
	public List<ScanResult> scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		String currentSSid = getSSID();
		if (listResult != null) {
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");

			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
						.append(" :").append(mScanResult.SSID).append("->")
						.append(mScanResult.BSSID).append("->")
						.append(mScanResult.capabilities).append("->")
						.append(mScanResult.frequency).append("->")
						.append(mScanResult.level).append("->")
						.append(mScanResult.describeContents()).append("\n\n");
				if (TextUtils.isEmpty(currentSSid)) {
					continue;
				}
				if (currentSSid.equals(mScanResult.SSID)) {
					if (mScanResult.capabilities.contains("WPA")) {
						cipherType = WifiCipherType.WIFICIPHER_WPA;
					} else if (mScanResult.capabilities.contains("WEP")) {
						cipherType = WifiCipherType.WIFICIPHER_WEP;
					} else {
						cipherType = WifiCipherType.WIFICIPHER_NOPASS;
					}

				}

			}
			Log.i(TAG, mStringBuffer.toString());
		} else {
			Log.i(TAG, "当前区域没有无线网络");
		}
		return listResult;
	}

	/**
	 * 得到扫描结果 获取无线信号列表
	 */
	public List<ScanResult> getScanResultList() {

		return scan();
	}

	/**
	 * 当前网络加密类型
	 */
	public WifiCipherType getCipherType() {

		return cipherType;

	}

	/**
	 * 当前网络
	 */
	public WifiInfo getConnectInfo() {

		mWifiInfo = mWifiManager.getConnectionInfo();
		if (mWifiInfo != null) {
			Log.d("wifiInfo", mWifiInfo.toString() + "");
			Log.d("SSID", mWifiInfo.getSSID() + "");
		}
		return mWifiInfo;
	}

	/**
	 * 当前网络
	 */
	public String getSSID() {

		mWifiInfo = mWifiManager.getConnectionInfo();
		if (mWifiInfo != null) {
			String SSID = mWifiInfo.getSSID();
			String ssid = SSID.substring(1, SSID.length() - 1);
			return ssid;
		}
		return "";
	}

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * 检查当前网络状态
	 * 
	 * @return String
	 */
	public boolean checkNetWorkState() {
		if (mWifiInfo != null) {
			Log.i(TAG, "网络正常工作");
			return mWifiManager.isWifiEnabled();
		} else {
			Log.i(TAG, "网络已断开");
			return false;
		}
	}

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}

	// 打开wifi功能
	public boolean OpenWifi() {
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	// 提供一个外部接口，传入要连接的无线网
	public boolean Connect(String SSID, String Password, WifiCipherType Type) {
		if (!this.OpenWifi()) {
			return false;
		}
		// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi//状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒在检测……
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {

			}
		}
		WifiConfiguration wifiConfig = this
				.CreateWifiInfo(SSID, Password, Type); //

		if (wifiConfig == null) {
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = mWifiManager.addNetwork(wifiConfig);
		boolean bRet = mWifiManager.enableNetwork(netID, false);
		return bRet;

	}

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 提供一个外部接口，传入要连接的无线网
	public void connect(String ssid, String password, WifiCipherType type,
			ConnectCallBack callBack) {
		callBack.conectedStart();
		Thread thread = new Thread(new ConnectRunnable(ssid, password, type,
				callBack));
		thread.start();
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			String configSsid = existingConfig.SSID;
			if (configSsid.contains(SSID)) {
				return existingConfig;
			}
		}
		return null;
	}

	public static Object getField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		Object out = f.get(obj);
		return out;
	}

	public static Object getDeclaredField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
	}

	public static void setIpAddress(InetAddress addr, int prefixLength,
			WifiConfiguration wifiConf) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException,
			InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;
		Class laClass = Class.forName("android.net.LinkAddress");
		Constructor laConstructor = laClass.getConstructor(new Class[] {
				InetAddress.class, int.class });
		Object linkAddress = laConstructor.newInstance(addr, prefixLength);

		ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,
				"mLinkAddresses");
		mLinkAddresses.clear();
		mLinkAddresses.add(linkAddress);
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password,
			WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.status = WifiConfiguration.Status.ENABLED;
		} else {
			return null;
		}
		return config;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password,
			WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			// config.wepKeys[0] = "";
			config.wepKeys[0] = "\"" + "\"";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.wepTxKeyIndex = 0;
		}
		// wep
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		if (Type == WifiCipherType.WIFICIPHER_WPA) {

			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;

			// config.preSharedKey = "\"" + Password + "\"";
			// config.hiddenSSID = true;
			// config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			// config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			// config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			// config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// // 此处需要修改否则不能自动重联
			// // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			// config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			// config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			// config.status = WifiConfiguration.Status.ENABLED;

		}
		return config;
	}

	public interface ConnectCallBack {
		abstract void conectedStart();

		abstract void conectedFailed();

		abstract void conected(boolean arg);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == -1) {
				callBack.conectedStart();
			} else if (msg.what == 0) {
				callBack.conected(false);
			} else {
				callBack.conected(true);
			}
		}

	};

	class ConnectRunnable implements Runnable {
		private String ssid;

		private String password;

		private WifiCipherType type;

		public ConnectRunnable(String ssid, String password,
				WifiCipherType type, ConnectCallBack callBack) {
			this.ssid = ssid;
			this.password = password;
			this.type = type;
			WifiAdmin.this.callBack = callBack;
			// Looper.prepare();
		}

		@Override
		public void run() {

			// 打开wifi
			OpenWifi();
			// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
			// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
			while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
				try {
					// 为了避免程序一直while循环，让它睡个100毫秒检测……
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
			}

			WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
			Log.d(TAG, "连接 AP设备  ");
			if (wifiConfig == null) {
				Log.d(TAG, "wifiConfig is null!");
				handler.sendEmptyMessage(-1);
				return;

			}
			// WifiConfiguration tempConfig = isExsits(ssid);
			// int netID = 0;
			// if (tempConfig != null) {
			// netID = tempConfig.networkId;
			// } else {
			// netID = mWifiManager.addNetwork(wifiConfig);
			// }
			// mWifiManager.disconnect();
			// // boolean enabled = mWifiManager.enableNetwork(netID, true);
			// boolean enabled = ConnectWifi(netID);
			// Log.d(TAG, "enableNetwork status enable=" + enabled);
			//
			// boolean connected = mWifiManager.reconnect();

			// callBack.conected(enabled);
			// ConnectivityManager connManager = (ConnectivityManager) context
			// .getSystemService(Context.CONNECTIVITY_SERVICE);
			// NetworkInfo mWifi = connManager
			// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			// boolean wifiConnected = mWifi.isConnected();
			int connetTimes = 0;
			boolean isConnected = false;
			try {

				isConnected = ConnectWifiSSID(ssid, wifiConfig);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!isConnected) {
				Log.d("ConnectWifiSSID", "连接Ap设备失败");

				handler.sendEmptyMessage(0);
			} else {
				// if (ssid.equals(getSSID())) {
				handler.sendEmptyMessage(1);
				// } else {
				// handler.sendEmptyMessage(0);
				// }

			}
		}
	}

	public boolean ConnectWifiSSID(String ssid, WifiConfiguration newwifiConfig)
			throws InterruptedException {
		WifiConfiguration tempConfig = isExsits(ssid);
		int netID = 0;

		if (tempConfig != null) {
			netID = tempConfig.networkId;
		} else
			netID = mWifiManager.addNetwork(newwifiConfig);
		Thread.sleep(1000);
		boolean enabled = mWifiManager.enableNetwork(netID, true);
		Thread.sleep(10000);
		Log.d(TAG, "enableNetwork status enable=" + enabled + ",netID:" + netID);

		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		int connectTime = 0;
		boolean wifiConnected = false;
		while (!wifiConnected) {
			NetworkInfo mWifi = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			wifiConnected = mWifi.isConnected();
			if (wifiConnected) {
				break;
			}
			Thread.sleep(2000);
			connectTime++;
			if (connectTime == 20) {
				break;
			}
		}
		if (!wifiConnected) {
			mWifiManager.removeNetwork(netID);
		}
		Log.d(TAG, "ConnectivityManager wifiConnected=" + wifiConnected);
		return enabled && wifiConnected;
	}

	public boolean ConnectWifi(int wifiId) {
		List<WifiConfiguration> wifiConfigList = mWifiManager
				.getConfiguredNetworks();
		for (int i = 0; i < wifiConfigList.size(); i++) {
			WifiConfiguration wifi = wifiConfigList.get(i);
			if (wifi.networkId == wifiId) {
				while (!(mWifiManager.enableNetwork(wifiId, true))) {// 激活该Id，建立连接
					// status:0--已经连接，1--不可连接，2--可以连接
					Log.i("ConnectWifi",
							String.valueOf(wifiConfigList.get(wifiId).status));
					if (wifiConfigList.get(wifiId).status == 1) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f')) {
				return false;
			}
		}

		return true;
	}

}
