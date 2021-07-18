package com.shenma.common.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UtilSystem {
	public static boolean addClipboard(String sInfo) {
		// ���ϵͳ���а�
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// ���Ƶ����а���
		StringSelection text1 = new StringSelection(sInfo);
		clipboard.setContents(text1, null);
		return true;
	}

	public static String getClipboard() {
		// ���ϵͳ���а�
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// ��ȡ���а��е�����
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// ��������Ƿ����ı�����
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					return (String) clipTf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void openUrl(String sUrl) {
		try {
			String sCmd = String.format("rundll32 url.dll,FileProtocolHandler %s", sUrl);
			Runtime.getRuntime().exec(sCmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readData(String path) {
		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = fis.available();
			data = new byte[len];
			fis.read(data, 0, len);
			fis.close();
			baos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	public static int getFileLen(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			int len = fis.available();
			fis.close();
			return len;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	// ɾ���ļ���
	// param folderPath �ļ�����������·��

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}

	public static void listAllFile(String path, List<String> result) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			result.add(path);
			return;
		}
		if (path.endsWith(File.separator) == false) {
			path = path + File.separator;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			String newpath = path + tempList[i];
			listAllFile(newpath, result);
		}
	}

	/**
	 * �ַ�����ѹ��
	 *
	 * @param str
	 *            ��ѹ�����ַ���
	 * @return ����ѹ������ַ���
	 * @throws IOException
	 */
	public static byte[] zip(String str) throws IOException {
		if (null == str || str.length() <= 0) {
			return null;
		}
		// ����һ���µ� byte ���������
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// ʹ��Ĭ�ϻ�������С�����µ������
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		// �� b.length ���ֽ�д��������
		byte[] data = str.getBytes("utf-8");
		gzip.write(data);
		gzip.close();
		out.close();
		// ʹ��ָ���� charsetName��ͨ�������ֽڽ�����������ת��Ϊ�ַ���
		return out.toByteArray();
	}

	// ��ѹ�ļ�
	public static boolean unzip(byte[] data, String directory) {
		try {
			File parent = new File(directory);
			if (!parent.exists() && !parent.mkdirs()) {
				Log.err("������ѹĿ¼ʧ�ܣ�" + parent.getAbsolutePath());
				return false;
			}
			ByteArrayInputStream baos = new ByteArrayInputStream(data);
			ZipInputStream zis = new ZipInputStream(baos);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String name = ze.getName();
				File child = new File(parent, name);
				if (ze.isDirectory()) {
					child.mkdir();
				} else {
					FileOutputStream output = new FileOutputStream(child);
					byte[] buffer = new byte[10240];
					int bytesRead = 0;
					while ((bytesRead = zis.read(buffer)) > 0) {
						output.write(buffer, 0, bytesRead);
					}
					output.flush();
					output.close();
				}
				ze = zis.getNextEntry();
			}
			zis.close();
			Log.out("��ѹ�ɹ���" + directory);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void openDir(String dir) {
		try {
			Runtime.getRuntime().exec("explorer \"" + dir + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeQuietly(InputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static void closeQuietly(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static void closeQuietly(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static void closeQuietly(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException ignore) {
			}
		}
	}
}
