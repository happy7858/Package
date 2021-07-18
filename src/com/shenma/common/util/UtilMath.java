package com.shenma.common.util;

public class UtilMath {
	/**
	 * ����С�������λ
	 */
	public static float round(float value, int digital) {
		int temp = (int) Math.round(value * Math.pow(10, digital));
		return (float) (temp * Math.pow(0.1, digital));
	}

	public static int random(int range) {
		return (int) (Math.random() * range);
	}
}
