package com.shenma.common.net;

import java.util.ArrayList;
import java.util.List;

public class DownloadResult {
	public static final int WAITING = 0;
	public static final int SENDING = 1;
	public static final int SUCCESS = 2;
	public static final int ERROR_INVALID_PARAMS = 1;
	public static final int ERROR_NETWORK = 2;
	public static final int ERROR_WRITE_FILE = 3;
	public static final int ERROR_TIMEOUT = 4;
	public static final int ERROR_NOFILE = 5;

	public class Task {
		public String url;// 下载地址
		public String save_path;// 本地保存文件

		public byte[] data;// 下载后的数据
		public int progress;// 下载进度

		public int code = SUCCESS;// 错误ID
		public String error;// 错误信息

		public boolean isSuccess() {
			return code == SUCCESS;
		}
	}

	public List<Task> m_tasks = new ArrayList<Task>();

	public boolean isSuccess() {
		return isSuceess(0);
	}

	public boolean isSuceess(int index) {
		if (m_tasks.isEmpty()) {
			return false;
		}
		Task task = m_tasks.get(index);
		if (task != null) {
			return task.isSuccess();
		}
		return false;
	}

	public void addTask(String url, String path) {
		Task task = new Task();
		task.url = url;
		task.save_path = path;
		m_tasks.add(task);
	}

	public void setError(int code, String msg) {
		if (m_tasks.isEmpty()) {
			Task task = new Task();
			m_tasks.add(task);
		}
		Task task = m_tasks.get(0);
		task.code = code;
		task.error = msg;
	}

	public int getProgress() {
		int size = m_tasks.size();
		int curr = 0;
		int taskPro = 0;
		for (int i = 0; i < m_tasks.size(); i++) {
			Task task = m_tasks.get(i);
			if (task.progress >= 100) {
				curr++;
			} else if (task.progress > 0) {
				taskPro = task.progress;
				break;
			}
		}
		return curr * 100 / size + taskPro / size;
	}

	public String getError() {
		for (Task task : m_tasks) {
			if (task.isSuccess() == false) {
				return task.error;
			}
		}
		return "未知错误";
	}

	public int getErrorCode() {
		for (Task task : m_tasks) {
			if (task.isSuccess() == false) {
				return task.code;
			}
		}
		return SUCCESS;
	}
}
