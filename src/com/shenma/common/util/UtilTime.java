package com.shenma.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilTime {
	public static final long MILISECOND_OF_DAY = 24 * 3600 * 1000;

	public static String getDate(long mili) {
		Date date = new Date(mili);
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String text = f.format(date);
		return text;
	}

	public static String getDateYearMonthDay(long mili) {
		Date date = new Date(mili);
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String text = f.format(date);
		return text;
	}

	public static int toDays(long lTimeMili) {
		return (int) ((lTimeMili / 1000 + 8 * 3600) / (24 * 3600));
	}

	private static long m_lServerTimeOffset = 0;

	public static void setServerTime(long time) {
		m_lServerTimeOffset = System.currentTimeMillis() - time;
	}

	// sTime的格式：2018-08-27 13:54:06
	public static void setServerTime(String sTime) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = format.parse(sTime);
			m_lServerTimeOffset = date.getTime() - System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
			m_lServerTimeOffset = 0;
		}
	}

	// sTime的格式：Thu, 06 Jun 2019 00:07:10 GMT
	public static void setServerTimeEnglish(String sTime) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			Date date = format.parse(sTime);
			m_lServerTimeOffset = date.getTime() - System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
			m_lServerTimeOffset = 0;
		}
	}

	public static long currentTimeMillis() {
		return System.currentTimeMillis() + m_lServerTimeOffset;
	}

	public static long getTonight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		long t = cal.getTimeInMillis();
		long now = System.currentTimeMillis();
		if (t > now) {
			t -= 24 * 3600 * 1000;
		}
		return t;
	}
}
