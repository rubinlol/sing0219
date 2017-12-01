package com.tencent.appframework;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 显示Feeds的日期
 * @author fortune
 */
public class DateUtil {

	static final String TAG = DateUtil.class.getSimpleName();

    public static final long ONE_MINUTE = 60 * 1000;
	public static final long ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	
	private static final String RES_EXACTLY = "刚刚"; // ComponentContext.getContext().getResources().getString(R.string.date_exactly);
	private static final String RES_MINS_AGO = "分钟前" ; //ComponentContext.getContext().getResources().getString(R.string.date_mins_ago);
	private static final String RES_HOURES_AGO = "小时前" ; //ComponentContext.getContext().getResources().getString(R.string.date_hours_ago);
	private static final String RES_DAYS_AGO = "天前" ; //ComponentContext.getContext().getResources().getString(R.string.date_yesterday);


	/**
	 * <pre>
	 * 规则：
	 * 1.一个小时以内的，以当前时间，减去真实发表时间=37分钟前，45分钟前，51分钟前
	 * 2.超过1小时
	 * 小于24小时的，以当前时间，减去真实发表时间=2小时前，5小时前，16小时前
	 * 3.超过24小时，小于48小时的，统一显示=昨天
	 * 4超过48小时的，直接显示发表的日期=8月18日
	 * </pre>
	 *
	 * @param timeMillis
	 */
	public static String getFeedDisplay(long timeMillis) {
		return getFeedDisplay(System.currentTimeMillis(), timeMillis);
	}

	public static String getFeedDisplay(long currentTimeMillis, long timeMillis) {
		return getFeedDisplay(currentTimeMillis, timeMillis, false);
	}

	/**
	 * <pre>
	 * 比较timeMillis相对currentTimeMillis的时间差，
	 * 规则： 
	 * 1.一个小时以内的，以当前时间，减去真实发表时间=37分钟前，45分钟前，51分钟前
	 * 2.超过1小时
	 * 小于24小时的，以当前时间，减去真实发表时间=2小时前，5小时前，16小时前 
	 * 3.超过24小时，小于7*24小时的，统一显示=昨天
	 * 4超过7*24小时的，直接显示发表的日期=8月18日
	 * </pre>
	 * 
	 * @param timeMillis
	 */
	public static String getFeedDisplay(long currentTimeMillis, long timeMillis, boolean withTime) {
		String formatStr = "MM月dd日" + (withTime ? "  HH:mm" : "");
		SimpleDateFormat sDateFormat = new SimpleDateFormat(formatStr, Locale.CHINA);

		long correctTime = currentTimeMillis;
		long timemillisInterval = correctTime - timeMillis;
		
		if (timemillisInterval < 0) {
			return sDateFormat.format(new Date(timeMillis));
		} else if (timemillisInterval < ONE_MINUTE) { //1分钟以内
			return RES_EXACTLY;
		} else if (timemillisInterval < ONE_HOUR) {
			return (timemillisInterval / 60*1000) + RES_MINS_AGO;
		} else if (timemillisInterval < ONE_DAY) {
			return (timemillisInterval / 60*60*1000) + RES_HOURES_AGO;
		} else {
			Calendar dayBeforeSevenday= Calendar.getInstance();
			dayBeforeSevenday.add(Calendar.DAY_OF_YEAR, -7);
			dayBeforeSevenday.set(Calendar.HOUR_OF_DAY,0);
			dayBeforeSevenday.set(Calendar.MINUTE, 0);
			dayBeforeSevenday.set(Calendar.SECOND, 0);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeMillis); // feed时间
			if (calendar.before(dayBeforeSevenday)) {// 7前天之前的Feed时间
				return sDateFormat.format(new Date(timeMillis));
			} else {
				formatStr = (timemillisInterval / ONE_DAY) + RES_DAYS_AGO;
				sDateFormat = new SimpleDateFormat(formatStr, Locale.CHINA);
				return sDateFormat.format(new Date(timeMillis));
			}
		}

	}
}
