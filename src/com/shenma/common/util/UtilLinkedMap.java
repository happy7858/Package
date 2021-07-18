package com.shenma.common.util;

import java.util.LinkedHashMap;

public class UtilLinkedMap {
	public static String getKeyByIndex(LinkedHashMap<String, String> map, int index) {
		if (map == null || index < 0) {
			return null;
		}
		int currIndex = 0;
		for (String key : map.keySet()) {
			if (currIndex == index) {
				return key;
			}
			currIndex++;
		}
		return null;
	}

	public static String getValueByIndex(LinkedHashMap<String, String> map, int index) {
		if (map == null || index < 0) {
			return null;
		}
		int currIndex = 0;
		for (String value : map.values()) {
			if (currIndex == index) {
				return value;
			}
			currIndex++;
		}
		return null;
	}

	public static int indexOfValue(LinkedHashMap<String, String> map, String flag) {
		int currIndex = 0;
		for (String value : map.values()) {
			if (value.equals(flag)) {
				return currIndex;
			}
			currIndex++;
		}
		return -1;
	}

	public static int indexOfKey(LinkedHashMap<String, String> map, String flag) {
		int currIndex = 0;
		for (String key : map.keySet()) {
			if (key.equals(flag)) {
				return currIndex;
			}
			currIndex++;
		}
		return -1;
	}
	
}
