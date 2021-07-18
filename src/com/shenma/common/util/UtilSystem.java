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
		// 获得系统剪切板
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 复制到剪切板上
		StringSelection text1 = new StringSelection(sInfo);
		clipboard.setContents(text1, null);
		return true;
	}

	public static String getClipboard() {
		// 获得系统剪切板
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板中的内容
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// 检查内容是否是文本类型
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

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
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
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
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
	 * 字符串的压缩
	 *
	 * @param str
	 *            待压缩的字符串
	 * @return 返回压缩后的字符串
	 * @throws IOException
	 */
	public static byte[] zip(String str) throws IOException {
		if (null == str || str.length() <= 0) {
			return null;
		}
		// 创建一个新的 byte 数组输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 使用默认缓冲区大小创建新的输出流
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		// 将 b.length 个字节写入此输出流
		byte[] data = str.getBytes("utf-8");
		gzip.write(data);
		gzip.close();
		out.close();
		// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
		return out.toByteArray();
	}

	// 解压文件
	public static boolean unzip(byte[] data, String directory) {
		try {
			File parent = new File(directory);
			if (!parent.exists() && !parent.mkdirs()) {
				Log.err("创建解压目录失败：" + parent.getAbsolutePath());
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
			Log.out("解压成功：" + directory);
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
