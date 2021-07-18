package com.shenma.common.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class UtilPinyin {
	private static HashMap<String, String> lstPinyin = new HashMap<String, String>();

	public static String getPinyin(String text) {
		StringBuffer sb = new StringBuffer();
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (ch >= 'a' && ch <= 'z') {
				sb.append((ch + "").toUpperCase());
			} else if (ch >= 'A' && ch <= 'Z') {
				sb.append(ch);
			} else if (ch >= '0' && ch <= '9') {
				sb.append(ch);
			} else {
				String pinyin = lstPinyin.get("" + ch);
				if (pinyin != null) {
					sb.append(pinyin.substring(0, 1));
				}
			}
		}
		return sb.toString();
	}

	public static void init(String path) {
		try {
			InputStream in = "".getClass().getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.length() <= 1) {
					return;
				}
				lstPinyin.put(line.substring(0, 1), line.substring(1));
			}
			lstPinyin.put("¿Ç", "K");
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
