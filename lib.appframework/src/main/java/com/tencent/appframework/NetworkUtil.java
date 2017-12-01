package com.tencent.appframework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.appframework.log.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Tencent. Author: raezlu Date: 12-10-30 Time: 下午7:22
 */
public class NetworkUtil {

    private final static String TAG = "NetworkUtil";

    // ------------------ common -------------------
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        // 这里必须用isConnected,不能用avaliable，因为有网络的情况isAvailable也可能是false
        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        if (context == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isViaMobile(Context context) {
        if (context == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return connMgr.getActiveNetworkInfo();
        } catch (Throwable e) {
            Log.e(TAG, "fail to get active network info", e);
            return null;
        }
    }

    // ------------------ apn & proxy -------------------
    public static class NetworkProxy implements Cloneable {

        public final String host;
        public final int port;

        public NetworkProxy(String host, int port) {
            this.host = host;
            this.port = port;
        }

        final NetworkProxy copy() {
            try {
                return (NetworkProxy) clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (obj != null && obj instanceof NetworkProxy) {
                NetworkProxy proxy = (NetworkProxy) obj;
                if (TextUtils.equals(this.host, proxy.host) && this.port == proxy.port)
                    return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + port;
            return result;
        }
    }

    public static String getNetworkType(Context context) {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String subTypeName = networkInfo.getSubtypeName();

                Log.i(TAG, "Network getSubtypeName : " + subTypeName);
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA") || subTypeName.equalsIgnoreCase("WCDMA") || subTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = subTypeName;
                        }

                        break;
                }

                Log.i(TAG, "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        Log.i(TAG, "Network Type : " + strNetworkType);

        return strNetworkType;
    }


    private final static Uri PREFERRED_APN_URI
            = Uri.parse("content://telephony/carriers/preferapn");

    private final static HashMap<String, NetworkProxy> sAPNProxies
            = new HashMap<String, NetworkProxy>();

    static {
        sAPNProxies.put(APNName.NAME_CMWAP, new NetworkProxy("10.0.0.172", 80));
        sAPNProxies.put(APNName.NAME_3GWAP, new NetworkProxy("10.0.0.172", 80));
        sAPNProxies.put(APNName.NAME_UNIWAP, new NetworkProxy("10.0.0.172", 80));
        sAPNProxies.put(APNName.NAME_CTWAP, new NetworkProxy("10.0.0.200", 80));
    }

    public static NetworkProxy getProxy(Context context, boolean apnProxy) {
        return !apnProxy ? getProxy(context) : getProxyByAPN(context);
    }

    public static NetworkProxy getProxy(Context context) {
        if (!isViaMobile(context)) {
            return null;
        }
        String proxyHost = getProxyHost(context);
        int proxyPort = getProxyPort(context);
        if (!isEmpty(proxyHost) && proxyPort >= 0) {
            return new NetworkProxy(proxyHost, proxyPort);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static String getProxyHost(Context context) {
        String host = null;
        if (PlatformUtil.version() < PlatformUtil.VERSION_CODES.HONEYCOMB) {
            host = Proxy.getDefaultHost();
        } else {
            host = System.getProperty("http.proxyHost");
        }
        return host;
    }

    @SuppressWarnings("deprecation")
    private static int getProxyPort(Context context) {
        int port = -1;
        if (PlatformUtil.version() < PlatformUtil.VERSION_CODES.HONEYCOMB) {
            port = Proxy.getDefaultPort();
        } else {
            String portStr = System.getProperty("http.proxyPort");
            if (!isEmpty(portStr)) {
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if (port < 0 || port > 65535) {
            // ensure valid port.
            port = -1;
        }
        return port;
    }

    public static NetworkProxy getProxyByAPN(Context context) {
        if (!isViaMobile(context)) {
            return null;
        }
        String apn = getAPN(context);
        NetworkProxy proxy = sAPNProxies.get(apn);
        return proxy == null ? null : proxy.copy();
    }

    public static String getAPN(Context context) {
        registerNetworkChangeReceiverIfNeeded(context.getApplicationContext());

        String apn = APNName.NAME_NONE;

        if (sNetworkChangeReceiver != null) {
            apn = sNetworkChangeReceiver.getApn();
            if (apn == APNName.NAME_NONE) {
                apn = sNetworkChangeReceiver.getApnValue();
            }
        }

        return apn;
    }

    private static volatile NetworkChangeReceiver sNetworkChangeReceiver;

    private static void registerNetworkChangeReceiverIfNeeded(Context context) {
        try {
            if (sNetworkChangeReceiver == null) {
                synchronized (NetworkUtil.class) {
                    if (sNetworkChangeReceiver == null) {
                        sNetworkChangeReceiver = new NetworkChangeReceiver(context);
                        IntentFilter upIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        context.registerReceiver(sNetworkChangeReceiver, upIntentFilter);
                    }
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "registe network receiver failed. " + e.getMessage(), e);
        }
    }

    public static boolean isWap(Context context) {
        String apn = getAPN(context);
        if (TextUtils.isEmpty(apn))
            return false;
        if (apn.contains(APNName.NAME_CMWAP) || apn.contains(APNName.NAME_UNIWAP) || apn.contains(APNName.NAME_3GWAP)
                || apn.contains(APNName.NAME_CTWAP)) {
            return true;
        }
        return false;
    }

    public static final class APNName {
        public final static String NAME_NONE = "none";
        public final static String NAME_UNKNOWN = "unknown";
        public final static String NAME_CMNET = "cmnet";
        public final static String NAME_CMWAP = "cmwap";
        public final static String NAME_3GNET = "3gnet";
        public final static String NAME_3GWAP = "3gwap";
        public final static String NAME_UNINET = "uninet";
        public final static String NAME_UNIWAP = "uniwap";
        public final static String NAME_WIFI = "wifi";
        public final static String NAME_CTWAP = "ctwap";
        public final static String NAME_CTNET = "ctnet";
        public final static String NAME_CMCC = "cmcc";
        public final static String NAME_UNICOM = "unicom";
        public final static String NAME_CMCT = "cmct";
        public final static String NAME_777 = "#777";
    }

    private static Object LOCK_NETLSTENER = new Object();
    private static List<WeakReference<NetStatusListener>> mNetworkListener = Collections
            .synchronizedList(new ArrayList<WeakReference<NetStatusListener>>());


    public static void registNetStatusListener(NetStatusListener listener) {
        WeakReference<NetStatusListener> wlistener = new WeakReference<NetStatusListener>(listener);
        if (wlistener != null) {
            synchronized (LOCK_NETLSTENER) {
                mNetworkListener.add(wlistener);
            }
        }
    }

    public static void unregistNetStatusListener(NetStatusListener listener) {
        synchronized (LOCK_NETLSTENER) {
            for (WeakReference<NetStatusListener> wlistener : mNetworkListener) {
                NetStatusListener rlistener = wlistener.get();
                if (rlistener == listener) {
                    mNetworkListener.remove(wlistener);
                    break;
                }
            }
        }
    }


    private static void notifyNetStatusChange(String oldApn, String newApn) {
        ArrayList<NetStatusListener> listeners = new ArrayList<>();
        synchronized (LOCK_NETLSTENER) {
            for (WeakReference<NetStatusListener> wlistener : mNetworkListener) {
                NetStatusListener rlistener = wlistener.get();
                if (rlistener != null) {
                    listeners.add(rlistener);
                }
            }
        }
        for (NetStatusListener listener : listeners) {
            if (listener != null) {
                listener.onNetworkChanged(oldApn, newApn);
            }
        }
    }

    public static interface NetStatusListener {
        void onNetworkChanged(String oldApn, String currApn);
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {
        private static final String TAG = "NetworkChangeReceiver";

        private Context mContext;

        public NetworkChangeReceiver(Context context) {
            mContext = context;
            mApn = getApnValue();
        }

        private String mApn = APNName.NAME_NONE; // APN类

        public String getApn() {
            return mApn;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "NetworkChangeReceiver onReceive()"
                    + (context != null ? " with Context" : " without Context"));

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                String apn = getApnValue();
                LogUtil.i(TAG, "old apn:" + mApn + "  new apn:" + apn);
                notifyNetStatusChange(mApn, apn);
                mApn = apn;
            }
        }

        public String getApnValue() {
            NetworkInfo ifo = NetworkUtil.getActiveNetworkInfo(mContext);

            String apn = null;

            if (null == ifo || !ifo.isConnected()) {
                apn = APNName.NAME_NONE;
            } else if (ConnectivityManager.TYPE_WIFI == ifo.getType()) {
                apn = APNName.NAME_WIFI;
            } else if (ifo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (PlatformUtil.version() < PlatformUtil.VERSION_CODES.JELLY_BEAN_MR1) {
                    Cursor cursor = null;
                    try {
                        cursor = mContext.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
                        while (cursor != null && cursor.moveToNext()) {
                            apn = cursor.getString(cursor.getColumnIndex("apn"));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                if (TextUtils.isEmpty(apn)) {
                    apn = ifo.getExtraInfo();
                }
            }
            if (apn != null) {
                // convert apn to lower case.
                apn = apn.toLowerCase();
            } else {
                apn = APNName.NAME_UNKNOWN;
            }
            return apn;

        }


    }

    // ---------------- dns ------------------
    public final static class DNS {
        public String primary;
        public String secondary;

        DNS() {
        }

        @Override
        public String toString() {
            return primary + "," + secondary;
        }
    }

    public static DNS getDNS(Context context) {
        DNS dns = new DNS();
        if (context != null) {
            if (isWifiConnected(context)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                if (dhcpInfo != null) {
                    dns.primary = int32ToIPStr(dhcpInfo.dns1);
                    dns.secondary = int32ToIPStr(dhcpInfo.dns2);
                }
            }
        }
        if (dns.primary == null && dns.secondary == null) {
            // retrieve dns with property.
            dns.primary = PropertyUtils.get(PropertyUtils.PROPERTY_DNS_PRIMARY, null);
            dns.secondary = PropertyUtils.get(PropertyUtils.PROPERTY_DNS_SECNDARY, null);
        }
        return dns;
    }

    private static String int32ToIPStr(int ip) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(ip & 0xFF).append(".");
        buffer.append((ip >> 8) & 0xFF).append(".");
        buffer.append((ip >> 16) & 0xFF).append(".");
        buffer.append((ip >> 24) & 0xFF);

        return buffer.toString();
    }

    // ---------------- utils ------------------
    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    // -----------------------------------------
    private NetworkUtil() {
        // static use.
    }

}
