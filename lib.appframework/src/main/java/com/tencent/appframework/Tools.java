package com.tencent.appframework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Tools {

    private static final int TIME_INTERVAL = 500;
    private static final int TIME_LONG_INTERVAL = 1500;
    private static final String TAG = Tools.class.getSimpleName();

    private Tools() {
    }

    public static String getInitUserAgent() {
        String userAgent = "";
        {
            // 初始化UA
            Locale locale = Locale.getDefault();
            StringBuffer buffer = new StringBuffer();
            // Add version
            final String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                // default to "1.0"
                buffer.append("1.0");
            }
            buffer.append("; ");
            final String language = locale.getLanguage();
            if (language != null) {
                buffer.append(language.toLowerCase());
                final String country = locale.getCountry();
                if (country != null) {
                    buffer.append("-");
                    buffer.append(country.toLowerCase());
                }
            } else {
                // default to "en"
                buffer.append("en");
            }
            // add the model for the release build
            if (Integer.parseInt(Build.VERSION.SDK) > 3 /*&& "REL".equals(CupcakeAdapter.getCodeName())*/) {
                final String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append("; ");
                    buffer.append(model);
                }
            }
            final String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }

            final String base = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Mobile Safari/533.1";

            userAgent = String.format(base, buffer);
            return userAgent;
        }
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        String devNetInfo = netInfo == null ? "none" : netInfo.getTypeName();
        return devNetInfo;
    }

    private static long lastClickTime;

    /**
     * 防止快速点击出现多次,1500秒
     */
    public static boolean isQuiteDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < TIME_LONG_INTERVAL) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void resetLastClickTime() {
        lastClickTime = 0;
    }

    /**
     * 防止快速点击出现多次
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < TIME_INTERVAL) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断是否是双击
     *
     * @param timeInterval 是否是双击的间隔
     */
    public static boolean isFastDoubleClick(int timeInterval) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < timeInterval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {

        int bytesRead;
        byte[] BUFFER = new byte[8196];
        while ((bytesRead = input.read(BUFFER)) != -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }

    /**
     * DIP 转 PIX,
     */
    public static final int getPixFromDip(float aDipValue, final Context context) {
        return UITools.getPixFromDip(aDipValue, context);
    }

    public static class ImgTool {

        /**
         * 画背景图
         */
        public static Bitmap addBackgroundColor(Bitmap drawable, int color) {
            Rect rect = new Rect(0, 0, drawable.getWidth(), drawable.getHeight());
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(drawable.getWidth(), drawable.getHeight(), Bitmap.Config.ARGB_8888);
            } catch (Throwable e) {
//                ExceptionManager.getInstance().handle(e); // delete by rubinqiu
                e.printStackTrace();
                return null;
            }
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(color);
            canvas.drawBitmap(drawable, rect, rect, new Paint());
            return bitmap;
        }


    }

    public static class BaseTool {

        private static final DecimalFormat mDf = new DecimalFormat("##.##");
        private static final DecimalFormat mDf3 = new DecimalFormat("##");
        private static final DecimalFormat mDf2 = new DecimalFormat("##.00");
        private static final DecimalFormat mDf1 = new DecimalFormat("##.0");
        private static final String[] UNITS = new String[]{"G", "M", "K", "B"};
        private static final long[] DIVIDERS = new long[]{1024 * 1024 * 1024, 1024 * 1024, 1024, 1};

        /**
         * 小数点最后面出现0，不显示 ，普通数量显示 1-1000 直接显示 1000-999999显示K， 1000000以上显示W
         */
        public static String numberToString(final long value) {
            if (value < 1)
                return "0";
            String result = null;
            if (value < 10000) {
                result = String.valueOf(value);
            } else {
                long num = value / 10000;
                result = num + "万";
            }
            return result;
        }

        /**
         * 小于1万，显示精确数字； 超过1万，保留到小数点后一位，取整，如：18982，1.8万
         */
        public static String numberToString2(long value) {
            if (value < 10000) {
                return String.valueOf(value);
            } else if (10000 <= value && value < 1000000) {
                return String.format("%.1f", value / 10000f) + "万";
            } else {
                return value / 10000 + "万";
            }
        }

        /**
         * 小数点最后面出现0，不显示
         */
        public static String byteToString(final long value) {
            if (value < 1)
                return "0B";
            String result = null;
            for (int i = 0; i < DIVIDERS.length; i++) {
                final long divider = DIVIDERS[i];
                if (value >= divider) {

                    if (i == 2) {
                        result = format(value, DIVIDERS[1], UNITS[1]);
                    } else {
                        result = format(value, divider, UNITS[i]);
                    }
                    break;
                }
            }
            return result;
        }

        /**
         * 小数点最后面出现0，显示0.0
         */
        public static String byteToString1(final long value) {
            if (value < 1)
                return "0B";
            String result = null;
            for (int i = 0; i < DIVIDERS.length; i++) {
                final long divider = DIVIDERS[i];
                if (value >= divider) {
                    result = format1(value, divider, UNITS[i]);
                    break;
                }
            }
            return result;
        }

        private static String format(final long value, final long divider, final String unit) {
            double result = divider > 1 ? (double) value / (double) divider : (double) value;
            // 特殊处理下 保留2位 太小的则规避为2位
            if (result <= 0.01d) {
                result = 0.01d;
            }
            return mDf.format(result) + unit;
        }

        private static String format1(final long value, final long divider, final String unit) {
            final double result = divider > 1 ? (double) value / (double) divider : (double) value;
            return mDf1.format(result) + unit;
        }

        public static int parseInt(String str) {
            return parseInt(str, 0);
        }

        public static int parseInt(String str, int defaultValue) {
            int value = defaultValue;
            try {
                value = Integer.parseInt(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }

        public static long parseLong(String str) {
            return parseLong(str, 0);
        }

        public static long parseLong(String str, long defaultValue) {
            long value = defaultValue;
            try {
                value = Long.parseLong(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }
    }

    /**
     * 判断飞行模式是否打开
     */
    public static boolean isAirModeOn(Context context) {
        return (Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false);
    }

    /**
     * 一键清理所有内存
     */
    static public void clearAllProcess(Context context) {
        ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();

        Method mKillBackgroundProcesses = null;
        if (Build.VERSION.SDK_INT >= 8) {
            try {
                mKillBackgroundProcesses = ActivityManager.class.getDeclaredMethod("killBackgroundProcesses", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                String[] pkgList = apinfo.pkgList;
                for (int j = 0; j < pkgList.length; j++) {
                    // 2.2以上是过时的,请用killBackgroundProcesses代替
                    if (Build.VERSION.SDK_INT >= 8 && mKillBackgroundProcesses != null) {
                        try {
                            mKillBackgroundProcesses.invoke(activityManger, pkgList[j]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        activityManger.restartPackage(pkgList[j]);
                    }
                }
            }
        }
    }


    /**
     * 加载最近任务
     *
     * @return HashMap<String, Integer>: <包名, 权重>:第4位为类型启动类型, 后台服务和非后台服务
     */
    static public HashMap<String, Integer> loadRencentTask(Context context) {
        // SystemClock.elapsedRealtime();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // 包含后台启动的服务
        List<RecentTaskInfo> list = am.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED | 0x00000002);

        if (list != null) {
            HashMap<String, Integer> packageList = new HashMap<String, Integer>();
            for (int i = list.size() - 1; i >= 0; i--) {
                // Log.v("AppInfoActivity", "" + info.baseIntent.getComponent().getPackageName());
                RecentTaskInfo info = list.get(i);
                if (info.baseIntent != null && info.baseIntent.getComponent() != null) {
                    packageList.put(info.baseIntent.getComponent().getPackageName(), i + 1);
                }
            }

            // 不包含后台启动的服务
            list = am.getRecentTasks(Integer.MAX_VALUE, 0);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    RecentTaskInfo info = list.get(i);
                    if (info.baseIntent != null && info.baseIntent.getComponent() != null) {
                        String packageName = info.baseIntent.getComponent().getPackageName();
                        Integer weight = packageList.get(packageName);
                        if (weight != null) {
                            // 非后台线程, 高4位置1
                            packageList.put(packageName, weight | 0x1000);
                        }
                    }
                }
            }
            return packageList;
        }
        return null;
    }

    /**
     * 显示软键盘
     */
    public static void showSoftKeyBroad(Context context, View editText) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // only will trigger it if no physical keyboard is open
        mgr.showSoftInput(editText, 0);
        showSoftKeyBroad(context, editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 显示软键盘
     */
    public static void showSoftKeyBroad(Context context, View editText, int flag) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // only will trigger it if no physical keyboard is open
        mgr.showSoftInput(editText, flag);
    }


    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyBroad(Context context, View editText) {
        if (context == null || editText == null) {
            return;
        }
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void hideSoftKeyBroad(Context context) {
        if (context instanceof Activity) {
            View currentFocus = ((Activity) (context)).getCurrentFocus();
            hideSoftKeyBroad(context, currentFocus);
        }
    }

    /**
     * 字符串转换为整数
     */
    public static int tryParseInt(String seed, int defValue) {
        try {
            return Integer.parseInt(seed);
        } catch (Exception e) {

        }

        return defValue;
    }

    public static int[] getScreenWidthAndHeight(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        return new int[]{w_screen, h_screen};
    }

    public static int[] getDpi(Activity ctx) {
        /*Display display = ctx.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, dm);
            return new int[]{dm.widthPixels, dm.heightPixels};
        }catch(Exception e){
            e.printStackTrace();
            return getScreenWidthAndHeight(ctx);
        }*/
        return getScreenWidthAndHeight(ctx);
    }

    public static int getScreenWidth(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        return w_screen;
    }

    public static int getScreenHeight(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int w_screen = dm.heightPixels;
        return w_screen;
    }

    /**
     * 简单的json协议解析器
     */
    @SuppressLint("DefaultLocale")
    public static <T> T fromJson(String data, Class<T> jsonClass) {
        T json = null;

        try {
            JSONObject jsonObj = new JSONObject(data);
            Field[] fields = jsonClass.getDeclaredFields();
            json = jsonClass.newInstance();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    // 设置属性的可访问性
                    field.setAccessible(true);
                    // 获取类型
                    Class<?> type = field.getType();
                    if (type != null) {
                        if (type == int.class) {
                            field.set(json, jsonObj.getInt(field.getName()));
                        } else if (type == String.class) {
                            field.set(json, jsonObj.getString(field.getName()));
                        } else if (type == long.class) {
                            field.set(json, jsonObj.getLong(field.getName()));
                        } else if (type == boolean.class) {
                            field.set(json, jsonObj.get(field.getName()));
                        } else if (type == double.class) {
                            field.set(json, jsonObj.getDouble(field.getName()));
                        } else if (type == JSONObject.class) {
                            field.set(json, jsonObj.getJSONObject(field.getName()));
                        } else if (type == JSONArray.class) {
                            field.set(json, jsonObj.getJSONArray(field.getName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 返回手游宝在sd卡上的缓存目录，形如：/sdcard/Tencent/shouyoubao/
     */
    public static String getSybRootSdcardDir() {
        String root = null;
        File sdcardDiretory = Environment.getExternalStorageDirectory();
        if (sdcardDiretory != null) {
            String sdcardPath = sdcardDiretory.toString();
            root = sdcardPath + "/Tencent/com.tencent.gamebible/";

        } else {
            Log.e(Tools.class.getSimpleName(), "Can't get screen record path!!!");
        }

        return root;
    }

    /**
     * 将数组转换成易读的汉字。 小于10000，直接返回； 小于100 0000，返回几点几万； 大于100 0000，返回几百万。
     */
    public static String number2Text(long num) {
        String res = "0";
        if (num < 1) {
            res = "0";
        } else if (num >= 1 && num < 10000) {
            res = String.valueOf(num);
        } else if (num >= 10000 && num < 1000000) {

            long wan = num / 10000;
            long qian = (num - 10000 * wan) / 1000;

            if (qian > 0) {
                res = wan + "." + qian + "万";
            } else {
                res = wan + "万";
            }
        } else {

            long wan = num / 1000000;
            res = wan + "百万";

        }

        return res;
    }

    public static float getTextWidth(/**px*/float textSize, String text) {
        float width = 0;
        if (null == text || text.length() == 0) {
            return width;
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        float scaleX = paint.getTextScaleX();
        for (int i = 0; i < widths.length; i++) {
            width += widths[i];
        }
        width += scaleX * (text.length() - 1);

        return width;
    }

    /**
     * 格式化schema url
     */
    public static String formatSchemaUrl(String schema, String host, String path, String query) {

        if (TextUtils.isEmpty(schema) || TextUtils.isEmpty(host)) {
            return "";
        }

        String fullSchema;
        if (TextUtils.isEmpty(path)) {
            fullSchema = String.format("%1s://%2s?%3s", schema, host, query);
        } else {
            path = path.replace("/", "");
            fullSchema = String.format("%1s://%2s/%3s?%4s", schema, host, path, query);
        }

        return fullSchema;
    }

    /**
     * 格式化schema url
     */
    public static String formatSchemaUrl(String schema, String host, String query) {
        return formatSchemaUrl(schema, host, null, query);
    }

    /**
     * 超过多少个字符用...代替
     */
    public static String getFormatString(String str, int length) {
        String formatString = null;
        if (str != null && str.length() > length) {
            formatString = str.substring(0, length) + "...";
        } else {
            formatString = str;
        }
        return formatString;
    }

    public static <T> T safeGet(List<T> collection, int index) {
        if (null == collection) {
            return null;
        }
        final int size = collection.size();
        if (index >= size) {
            return null;
        } else {
            return collection.get(index);
        }
    }

    public static Charset getDefaultCharset() {
        return Charset.forName("UTF-8");
    }


    /**
     * 判断是否是Emoji, 这个不准。
     *
     * @param codePoint 比较的单个字符
     */
    public static boolean isEmojiCharacter(int codePoint) {
        boolean notEmoji = (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
        return !notEmoji;
    }

    /**
     * ch 为英文字符，数字，中文，英文，日文时，返回true；否则返回false
     */
    public static boolean isSupportedCharacter(char ch) {

        boolean b = false;

        if (((ch >= 0x20) && (ch <= 0x007e)) //Latin基本
                || ((ch >= 0xA0) && (ch <= 0x00FF))   //Latin扩充
                || ((ch >= 0x0100) && (ch <= 0x024F)) //Latin扩展A、扩展B
                || ((ch >= 0x0300) && (ch <= 0x052F)) //变音符号、Greek和Coptic、Cyrillic基本和Cyrillic补充

                || ((ch >= 0x2000) && (ch <= 0x206f)) //常用标点
                || ((ch >= 0x20A0) && (ch <= 0x20B9)) //货币符号
                || ((ch >= 0x2100) && (ch <= 0x214F)) //字母式符号
                || ((ch >= 0x2190) && (ch <= 0x21Ff)) //箭头
                || ((ch >= 0x2200) && (ch <= 0x22FF)) //数学运算
                || ((ch >= 0x2500) && (ch <= 0x257f)) //制表
                || ((ch >= 0x2580) && (ch <= 0x259f)) //方块元素
                || ((ch >= 0x25A0) && (ch <= 0x25ff)) //几何图形
                || ((ch >= 0x2600) && (ch <= 0x26ff)) //杂项符号
                || ((ch >= 0x2701) && (ch <= 0x27bf)) //印刷符号

                || ((ch >= 0x4e00) && (ch <= 0x9fa5)) //汉字
                || ((ch >= 0x3130) && (ch <= 0x318f)) //韩文
                || ((ch >= 0xac00) && (ch <= 0xd7a3)) //韩文
                || ((ch >= 0xe400) && (ch <= 0x9fa5)) //日文
                ) {
            b = true;
        }


        return b;

    }

    /**
     * 获取系统时间 格式为："yyyy-MM-dd "
     */
    public static String getCurrentDate() {
        return getDate(System.currentTimeMillis());
    }

    /**
     * 获取时间 格式为："yyyy-MM-dd "
     */
    public static String getDate(long datetime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date(datetime));
    }

    /***
     * 播放本地资源的音效
     */
    public static void playPunchSound(Context context, int rawId) {
        MediaPlayer player = null;
        if (context != null) {
            try {
                player = MediaPlayer.create(context, rawId);

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if (mp != null) {
                            mp.release();
                        }
                        Log.d(TAG,"playSound>>OnError");
                        return false;
                    }
                });
                player.start();
            } catch (Exception e) {
                Log.d(TAG,e.getMessage(),e);
            } finally {
                if (player != null) {
                    player.release();
                }
            }
        }
    }

    /**
     * @param colorStr 形容“#ff00ff00”或者“ff00ff00”的ARGB色值，也可以少于八个字符、
     */
    public static int parseColor(String colorStr, int defaultColor) {
        int color = defaultColor;
        if (null != colorStr && colorStr.matches("^#[0-9a-fA-F]{1,6}$")) {
            color = Integer.parseInt(colorStr.substring(1), 16);
            color = 0xff000000 | color;

        } else if (null != colorStr && colorStr.matches("^#[0-9a-fA-F]{7,8}$")) {
            color = Integer.parseInt(colorStr.substring(1), 16);

        } else if (null != colorStr && colorStr.matches("^[0-9a-fA-F]{1,6}$")) {
            color = Integer.parseInt(colorStr, 16);
            color = 0xff000000 | color;

        } else if (null != colorStr && colorStr.matches("^[0-9a-fA-F]{7,8}$")) {
            color = Integer.parseInt(colorStr, 16);

        }
        return color;
    }
}
