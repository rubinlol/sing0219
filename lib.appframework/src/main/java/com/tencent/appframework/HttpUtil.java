
package com.tencent.appframework;

import android.content.Context;
import android.util.Log;

import com.tencent.appframework.strategy.ProxyStrategy;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

    private static final String TAG = "HttpUtil";

    public static final int CONNECTION_TIMEOUT = 10 * 1000;
    public static final int READ_TIMEOUT = 10 * 1000;
    public static final int WRITE_TIMEOUT = 10 * 1000;

    public final static class RequestOptions {
        public ProxyStrategy proxyStrategy = ProxyStrategy.DEFAULT;

        public RequestOptions() {
        }
    }

    public static String prepareUrl(String url) {
        AssertUtil.assertTrue(url != null);
        url = url.trim();
        url = url.replace(" ", "");
        // handle hash.
        int hashIndex = url.indexOf('#');
        if (hashIndex > 0) {
            url = url.substring(0, hashIndex);
        }
        return url;
    }

    public static String prepareHost(String url) throws MalformedURLException {
        AssertUtil.assertTrue(url != null);
        return (new URL(url)).getAuthority();
    }

    public static NetworkUtil.NetworkProxy getProxy(Context context, RequestOptions options) {
        // for proxy.
        boolean allowProxy = false;
        if (options != null && options.proxyStrategy == ProxyStrategy.FORCE_PROXY) {
            allowProxy = true;
        }

        if (allowProxy) {
            NetworkUtil.NetworkProxy networkProxy;
            if (NetworkUtil.isViaMobile(context)) {
                networkProxy = NetworkUtil.getProxy(context, false);
            } else {
                networkProxy = new NetworkUtil.NetworkProxy(android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort());
            }
            if (networkProxy != null) {
                // do some log.
                Log.d(TAG, "use proxy[host:" + networkProxy.host + ",port:" + networkProxy.port + "]");
                return networkProxy;
            }
        }
        return null;
    }

}
