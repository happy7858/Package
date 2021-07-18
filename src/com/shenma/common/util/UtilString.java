package com.shenma.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilString {
	public static String encodeUnicode(String str) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {

			// 取出每一个字符
			char c = str.charAt(i);
			unicode.append("\\u");
			// 转换为unicode
			unicode.append(Integer.toHexString(c));
		}

		return unicode.toString();
	}

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public static String regReplace(String str, String regexp, String newexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, newexp);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public final static String MD5(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput); // 使用指定的字节更新摘要
			byte[] md = mdInst.digest(); // 获得密文
			int j = md.length; // 把密文转换成十六进制的字符串形式
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将byte转为16进制
	 * 
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				// 1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	/**
	 * 得到文件的SHA码,用于校验
	 * 
	 * @param file
	 * @return
	 */
	public static String SHA(String filepath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filepath));
			int length = fis.available();
			byte[] buffer = new byte[length];
			fis.read(buffer);
			return SHA(buffer, 0, length);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static String SHA(byte[] data, int start, int len) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(data, start, len);
			byte[] shadata = md.digest();
			String text = byte2Hex(shadata);
			return text;
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String readFile(String path) {
		return readFile(path, null);
	}

	public static String readFile(String path, String charset) {
		StringBuffer result = new StringBuffer();
		if (charset == null) {
			charset = "UTF-8";
		}
		try {
			InputStream is = null;
			if (path.startsWith("/")) {
				is = "".getClass().getResourceAsStream(path);
			} else {
				is = new FileInputStream(path);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
			String line = null;
			int nLineNum = 0;
			while ((line = br.readLine()) != null) {
				if (nLineNum > 0) {
					result.append('\n');
				}
				result.append(line);
				nLineNum++;
			}
			br.close();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public static boolean writeFile(byte[] arr, String path) {
		if (arr == null || arr.length <= 0) {
			return false;
		}
		try {
			File file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(arr);
			fos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean writeFile(String data, String path) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			fos.write(data.getBytes("utf-8"));
			fos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Pattern FilePattern = Pattern.compile("[\t\n#\\\\/:*?\"<>| ]");

	public static String filenameFilter(String str) {
		return str == null ? null : FilePattern.matcher(str).replaceAll("");
	}

	public static boolean checkFileExsist(String url) {
		File f = new File(url);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	private static Pattern pathPattern = Pattern.compile("[a-zA-Z]:*");
	private static Pattern pathPatternName = Pattern.compile("[\t\n:*?\"<>|]");

	public static boolean checkPathValide(String path) {
		Matcher matchPath = pathPattern.matcher(path);
		if (matchPath.find() == false) {
			return false;
		}
		Matcher matchName = pathPatternName.matcher(path.substring(2));
		if (matchName.find()) {
			return false;
		}
		return true;
	}

	public static boolean checkUrlValide(String url) {
		if (url.indexOf(" ") != -1 || url.indexOf("	") != -1 || url.indexOf(".") == -1
				|| url.startsWith("http") == false) {
			return false;
		}

		return true;
	}

	public static boolean checkPhone(String phone) {
		if (phone == null || phone.length() != 11) {
			return false;
		}
		if (phone.charAt(0) != '1') {
			return false;
		}
		for (int i = 1; i < phone.length(); i++) {
			char ch = phone.charAt(i);
			if (ch < '0' || ch > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将十制字的HTML转变成字符串
	 * &#12304;&#26149;&#22799;&#23395;&#26032;&#21697;&#22899;&#35013;&#38889;&
	 */
	public static String decodeOctalHtml(String str) {
		char[] chrs = str.toCharArray();
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < chrs.length; i++) {
			char ch = chrs[i];
			if (ch == '&') {
				int end = str.indexOf(';', i);
				if (end != -1) {
					String value = str.substring(i + 2, end);
					try {

						ch = (char) Integer.parseInt(value);
					} catch (NumberFormatException e) {
						for (int j = i; j <= end; j++) {
							bf.append(chrs[j]);
							continue;
						}
					}
					i = end;
				}
			}
			bf.append(ch);
		}

		return bf.toString();
	}

	/**
	 * 解析XML 5个转义符:<，>，&，”，_;的转义字符分别如下： &lt; &gt;&amp; &quot; &apos;
	 */
	private final static HashMap<String, String> XML_FLAG = new HashMap<>();

	public static String decodeXml(String str) {
		if (XML_FLAG.isEmpty()) {
			XML_FLAG.put("&lt", "<");
			XML_FLAG.put("&gt", ">");
			XML_FLAG.put("&amp", "&");
			XML_FLAG.put("&quot", "\"");
			XML_FLAG.put("&apos", "@");
		}
		char[] chrs = str.toCharArray();
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < chrs.length; i++) {
			char ch = chrs[i];
			if (ch == '&') {
				int end = str.indexOf(';', i);
				if (end != -1) {
					String flag = str.substring(i, end);
					String value = XML_FLAG.get(flag);
					if (value == null) {
						bf.append(flag);
					} else {
						bf.append(value);
					}
					i = end;
					continue;
				}
			}
			bf.append(ch);
		}

		return bf.toString();
	}

	public static String reduceByByteNumber(String text, int nMaxLen) {
		int nLen = text.getBytes().length;
		if (nLen <= nMaxLen) {
			return text;
		}
		char[] chars = text.toCharArray();
		for (int i = chars.length - 1; i > 0; i--) {
			String ch = chars[i] + "";
			nLen -= ch.getBytes().length;
			if (nLen <= nMaxLen) {
				String ret = text.substring(0, i);
				// System.out.println(ret.getBytes().length);
				return ret;
			}
		}
		return text;
	}

	public static float parseFloat(String text) {
		if (text == null) {
			return 0;
		}

		float num = 0;
		try {
			num = Float.parseFloat(text);
		} catch (Exception e) {
		}
		return num;
	}

	public static boolean hasPriceInfo(String title) {
		int start = 0;
		while ((start = title.indexOf("元", start)) >= 0) {
			if (start == 0) {
				start++;
				continue;
			}
			char ch = title.charAt(start - 1);
			if (ch >= '0' && ch <= '9') {
				return true;
			}
			start++;
		}
		return false;
	}

	public static boolean checkAccount(String account) {
		return account.matches("[0-9A-Za-z_-]+");
	}

	public static boolean checkNumber(String number) {
		return number.matches("[0-9]+");
	}

}
