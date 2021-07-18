package com.shenma.common.net;

public class NetResponse {

	private static final int ERROR_TYPE_NORMAL = 1000000;
	public static final int ERROR_CONNECT = ERROR_TYPE_NORMAL + 1;
	public static final int ERROR_UNKNOWN_CMD = ERROR_TYPE_NORMAL + 2;
	public static final int ERROR_UNKNOWN_USERID = ERROR_TYPE_NORMAL + 3;// 未知用户
	public static final int ERROR_SERVER_DECODE = ERROR_TYPE_NORMAL + 4;// 解码出错
	public static final int ERROR_CLIENT_DECODE = ERROR_TYPE_NORMAL + 5;// 数据解密出错
	public static final int ERROR_UPLOAD_IMAGE = ERROR_TYPE_NORMAL + 6;// 上传的文件丢失
	public static final int ERROR_DATA_TOO_LONG = ERROR_TYPE_NORMAL + 7;// 数据过长
	public static final int ERROR_UPLOAD_FILE = ERROR_TYPE_NORMAL + 8;// 找不到本地文件

	private int m_code;
	protected String m_info = "";
	String m_data;
	protected long m_nRequestid;

	public boolean hasError() {
		return !success();
	}

	public boolean success() {
		return m_code == 1;
	};

	public void setResult(int code, String info) {
		m_code = code;
		m_info = info;
	}

	public int getCode() {
		return m_code;
	}

	public String getInfo() {
		return m_info;
	}

	public String getData() {
		return m_data;
	}

	public void setData(String data) {
		m_data = data;
	}

	public void setRequestid(long id) {
		m_nRequestid = id;
	}

	public long getRequestid() {
		return m_nRequestid;
	}
}
