package com.shenma.common.util;

import java.io.IOException;

/**
 * Base64双向加密工具
 * 
 * @author -hcl-[2013-3-14]
 * 
 */
public class SBase64 {
	private static final char[] ENCODE_CHARS = new char[] {
			//
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '-', '+', //
	};

	private static byte[] base64DecodeChars = new byte[256];

	static {
		for (int i = 0; i < base64DecodeChars.length; i++) {
			base64DecodeChars[i] = -1;
		}
		for (int i = 0; i < ENCODE_CHARS.length; i++) {
			char ch = ENCODE_CHARS[i];
			base64DecodeChars[ch] = (byte) i;
		}
	}

	private SBase64() {
		super();
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            明文的字节数组
	 * @return 密文字符串
	 */
	public static String encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(ENCODE_CHARS[b1 >> 2]);
				sb.append(ENCODE_CHARS[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(ENCODE_CHARS[b1 >> 2]);
				sb.append(ENCODE_CHARS[((b1 & 0x03) << 4) | ((b2 & 0xf0) >> 4)]);
				sb.append(ENCODE_CHARS[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(ENCODE_CHARS[b1 >> 2]);
			sb.append(ENCODE_CHARS[((b1 & 0x03) << 4) | ((b2 & 0xf0) >> 4)]);
			sb.append(ENCODE_CHARS[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >> 6)]);
			sb.append(ENCODE_CHARS[b3 & 0x3f]);
		}
		return sb.toString();
	}

	/**
	 * 解密
	 * 
	 * @param str
	 *            密文
	 * @return 明文的字节数组
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decode(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}

		if (str.length() < 4) {
			throw new IOException("长度过短" + str);
		}
		char[] chars = str.toCharArray();
		int len = chars.length;
		if (len % 4 != 0) {
			throw new IOException("长度不为4的倍数" + chars.length);
		}

		// 计算数据长度
		int datalen = len / 4 * 3;
		if (chars[len - 1] == '=') {
			datalen--;
		}
		if (chars[len - 2] == '=') {
			datalen--;
		}
		byte[] data = new byte[datalen];

		int b1, b2, b3, b4;
		int writeindex = 0;
		for (int i = 0; i < len;) {
			/* b1 */
			b1 = base64DecodeChars[chars[i++]];
			if (b1 == -1)
				throw new IOException("非法的字符'" + chars[i - 1] + "'");
			/* b2 */
			b2 = base64DecodeChars[chars[i++]];
			if (b2 == -1)
				throw new IOException("非法的字符'" + chars[i - 1] + "'");
			data[writeindex++] = (byte) ((b1 << 2) | ((b2 & 0x30) >>> 4));
			/* b3 */
			b3 = chars[i++];
			if (b3 == 61)
				return data;
			b3 = base64DecodeChars[b3];
			if (b3 == -1)
				throw new IOException("非法的字符'" + chars[i - 1] + "'");
			data[writeindex++] = (byte) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2));
			/* b4 */
			b4 = chars[i++];
			if (b4 == 61)
				return data;
			b4 = base64DecodeChars[b4];
			if (b4 == -1)
				throw new IOException("非法的字符'" + chars[i - 1] + "'");
			data[writeindex++] = (byte) (((b3 & 0x03) << 6) | b4);
		}
		return data;
	}

}
