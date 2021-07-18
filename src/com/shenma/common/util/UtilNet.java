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
				// �ֽ�ת��Ϊ����
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
	 * ѹ��.
	 * 
	 * @param inputByte
	 *            ��ѹ�����ֽ�����
	 * @return ѹ���������
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
				// ѹ������ѹ���������������ֽ������bos��
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
