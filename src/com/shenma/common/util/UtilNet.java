package com.shenma.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.zip.Deflater;

public class UtilNet {

	public static String getLocalMac() {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				// 字节转换为整数
				int temp = mac[i] & 0xff;
				String str = Integer.toHexString(temp);
				if (str.length() == 1) {
					sb.append("0" + str);
				} else {
					sb.append(str);
				}
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "unknown";
	}

	/**
	 * 压缩.
	 * 
	 * @param inputByte
	 *            待压缩的字节数组
	 * @return 压缩后的数据
	 * @throws IOException
	 */
	public static byte[] compress(byte[] inputByte) throws IOException {
		int len = 0;
		Deflater defl = new Deflater();
		defl.setInput(inputByte);
		defl.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] outputByte = new byte[1024];
		try {
			while (!defl.finished()) {
				// 压缩并将压缩后的内容输出到字节输出流bos中
				len = defl.deflate(outputByte);
				bos.write(outputByte, 0, len);
			}
			defl.end();
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}
}
