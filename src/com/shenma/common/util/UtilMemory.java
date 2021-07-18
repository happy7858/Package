package com.shenma.common.util;


public class UtilMemory {
	public static void gc() {
		long lOldMemory = Runtime.getRuntime().freeMemory();
		System.gc();
		long lCurrMemory = Runtime.getRuntime().freeMemory();
		Log.out("has free memory:" + (lCurrMemory - lOldMemory));
	}
}
