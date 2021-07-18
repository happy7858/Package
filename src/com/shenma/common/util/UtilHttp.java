package com.shenma.common.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class UtilHttp {
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36";

	private static final int TIMEOUT_CONNECT = 5000;// 单位毫秒
	private static final int TIMEOUT_READ = 10000;// 单位毫秒
	private static final int MAX_TRY_TIMES = 3;
	private int nReadTimeOutDefault = TIMEOUT_READ;// 单位毫秒

	public static final int TYPE_GET = 0;
	public static final int TYPE_POST = 1;
	public static final int TYPE_PUT = 2;
	public static final int TYPE_OPTIONS = 3;

	public static final String TYPE_NAME[] = { "GET", "POST", "PUT", "OPTIONS" };
	private int m_type = TYPE_GET;

	public static final int HTTP_CODE_REFUSE = 1007;
	public static final int HTTP_CODE_TIMEOUT = 1008;
	public static final int HTTP_CODE_DISCONNECT = 1009;

	private String m_sRespondData;
	Map<String, List<String>> m_mapRespondHeadProperty;
	public HashMap<String, String> m_mapRequestHeadProperty = new HashMap<String, String>();
	private int m_nRespondCode;

	private ProgressListener m_progressListener;

	public interface ProgressListener {
		// 该函数线程不安全
		public void onProgress(long curr, long total);
	}

	public UtilHttp(int type) {
		super();
		m_type = type;
	}

	public UtilHttp() {
		this(TYPE_GET);
	}

	/**
	 * cookie是身份认证符，自己选择是否使用
	 */
	public void setCookie(String sCookie) {
		m_mapRequestHeadProperty.put("Cookie", sCookie);
	}

	public void setReadTimeOut(int minisecond) {
		nReadTimeOutDefault = minisecond;
	}

	/**
	 * 设置下载进度的监听者
	 */
	public void setProgressListener(ProgressListener listener) {
		m_progressListener = listener;
	}

	public List<String> getRespondProperty(String prop) {
		for (String key : m_mapRespondHeadProperty.keySet()) {
			if (key != null && key.equals(prop)) {
				return m_mapRespondHeadProperty.get(key);
			}
		}
		return null;
	}

	public List<String> getRespondCookie() {
		return getRespondProperty("Set-Cookie");
	}

	public int getRespondCode() {
		return m_nRespondCode;
	}

	public String getRespondData() {
		return m_sRespondData;
	}

	public byte[] sendData(String surl, byte[] data) {
		byte[] result = null;
		for (int i = 0; i <= MAX_TRY_TIMES; i++) {
			result = send(surl, data);
			if (result != null && result.length > 0) {
				break;
			}
			if (m_nRespondCode >= 400) {
				break;
			}
			Log.out("start try send data again " + i + "  :" + surl);
		}
		return result;
	}

	private byte[] send(String surl, byte[] data) {
		boolean bError = false;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(surl);
			connection = (HttpURLConnection) url.openConnection();
			if (m_type != TYPE_GET) {
				connection.setRequestMethod(getTypeName());
			}
			// connection.setRequestProperty("Z-Proxy", "guomai013041");
			// connection.setRequestProperty("Z-Server", "mofa5111");
			if (m_mapRequestHeadProperty.get("User-Agent") == null) {
				connection.setRequestProperty("User-Agent", USER_AGENT);
			}
			Iterator<String> iter = m_mapRequestHeadProperty.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String value = m_mapRequestHeadProperty.get(key);
				connection.setRequestProperty(key, value);
			}

			if (m_type == TYPE_POST && data == null) {
				data = new byte[0]; // 防止没有Content-Length属性，造成411错误
			}
			connection.setConnectTimeout(TIMEOUT_CONNECT);
			connection.setReadTimeout(nReadTimeOutDefault);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("connection", "keep-alive");
			if (data != null) {
				connection.getOutputStream().write(data);// 输入参数
			}

			// String contenttype = connection.getContentType();
			// String charSet = getCharset(contenttype);
			m_mapRespondHeadProperty = connection.getHeaderFields();
			long totalLength = connection.getContentLengthLong();
			long totalLengthRead = 0;
			m_nRespondCode = connection.getResponseCode();
			String cs = connection.getContentEncoding();
			if (cs != null && cs.equals("gzip")) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				decompress(connection.getInputStream(), baos);
				return baos.toByteArray();
			} else {

				InputStream in = connection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024 * 1024];
				int length = 0;
				while ((length = in.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, length);
					totalLengthRead += length;
					if (m_progressListener != null) {
						m_progressListener.onProgress(totalLengthRead, totalLength);
					}
				}
				buf = null;
				in.close();
				baos.close();
				return baos.toByteArray();
			}
		} catch (Exception e) {
			String sError = e.getMessage();

			if (sError.indexOf("refused") > 0 || sError.indexOf("No route to host") != -1) {
				m_nRespondCode = HTTP_CODE_REFUSE;
			} else if (sError.contains("Network is unreachable")) {
				m_nRespondCode = HTTP_CODE_DISCONNECT;
			} else if (sError.contains("Read timed out")) {
				m_nRespondCode = HTTP_CODE_TIMEOUT;
			}
			e.printStackTrace();
			bError = true;
		}

		if (connection != null && (bError || m_nRespondCode >= HttpURLConnection.HTTP_BAD_REQUEST)) {
			try {
				InputStream in = connection.getErrorStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				String cs = connection.getContentEncoding();
				if (cs != null && cs.equals("gzip")) {
					decompress(connection.getErrorStream(), baos);
					return baos.toByteArray();
				} else if (in != null) {
					byte[] buf = new byte[1024 * 1024];
					int length = 0;
					while ((length = in.read(buf, 0, buf.length)) != -1) {
						baos.write(buf, 0, length);
					}
					in.close();
				}
				baos.close();
				return baos.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(surl + ":" + e.getMessage());
			}
		}

		return null;
	}

	/***
	 * 获取HTML内容
	 */
	public static byte[] download(String path, String sCookie) {
		byte[] result = null;
		for (int i = 0; i <= MAX_TRY_TIMES; i++) {
			byte[] data = downloadonce(path, sCookie);
			if (data != null && data.length > 0) {
				result = data;
				break;
			}
			Log.out("start try download again " + i + ": " + path);
		}
		return result;
	}

	private static byte[] downloadonce(String path, String sCookie) {
		try {
			boolean redirect = false;
			HttpURLConnection connection = null;
			do {
				URL url = new URL(path);
				connection = (HttpURLConnection) url.openConnection();
				if (sCookie != null) {
					connection.setRequestProperty("Cookie", sCookie);
				}
				connection.setConnectTimeout(TIMEOUT_CONNECT);
				connection.setReadTimeout(TIMEOUT_READ);
				connection.setRequestProperty("User-Agent", USER_AGENT);

				HostnameVerifier hv = new HostnameVerifier() {
					public boolean verify(String urlHostName, SSLSession session) {
						System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
						return true;
					}
				};
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				redirect = false;
				// 考虑重定向的事情。
				int status = connection.getResponseCode();
				if (status != HttpURLConnection.HTTP_OK) {
					if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER) {
						redirect = true;
						path = connection.getHeaderField("Location");
					}
				}
			} while (redirect);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String cs = connection.getContentEncoding();
			if (cs != null && cs.equals("gzip")) {
				decompress(connection.getInputStream(), baos);
				return baos.toByteArray();
			} else {
				InputStream in = connection.getInputStream();
				byte[] buf = new byte[1024 * 1024];
				int length = 0;
				while ((length = in.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, length);
				}
				in.close();
				baos.close();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	/**
	 * gzip数据解压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	private static void decompress(InputStream is, ByteArrayOutputStream os) throws Exception {
		GZIPInputStream gis = new GZIPInputStream(is);
		int BUFFER = 1024 * 1024;
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = gis.read(data, 0, BUFFER)) != -1) {
			os.write(data, 0, count);
		}

		gis.close();
	}

	public int getType() {
		return m_type;
	}

	public String getTypeName() {
		return TYPE_NAME[m_type];
	}

}
