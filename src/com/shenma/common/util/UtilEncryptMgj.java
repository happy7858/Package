package com.shenma.common.util;

public class UtilEncryptMgj {
	private static char SMALL_ARRAY[] = //
			{ '0', '2', '4', '6', '8', 'a', 'c', 'e', 'g', 'i', 'k', 'm', 'o', 'q', 's', 'u', 'w', 'y' };

	public static String encode(long num) {
		num += 28;
		long big = num / 18;
		int small = (int) (num % 18);
		String sBig = Long.toString(big, 36);
		char chSmall = SMALL_ARRAY[small];
		return "1" + sBig + chSmall;
	}

	public static long decode(String str) {
		if (str.length() < 3) {
			return 0;
		}

		String sBig = str.substring(1, str.length() - 1);
		char chSmall = str.charAt(str.length() - 1);

		long big = Long.parseLong(sBig, 36);
		int small = 0;
		for (int i = 0; i < SMALL_ARRAY.length; i++) {
			if (chSmall == SMALL_ARRAY[i]) {
				small = i;
				break;
			}
		}

		return big * 18 + small - 28;
	}
}
