package com.shenma.common.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import com.shenma.common.net.DownloadResult.Task;
import com.shenma.common.util.Log;
import com.shenma.common.util.UtilHttp;
import com.shenma.common.util.UtilString;
import com.shenma.common.util.UtilHttp.ProgressListener;

public class Download implements Runnable {
	private DownloadResult m_result = new DownloadResult();
	private DownloadDelegate m_delegate;

	private long m_lCountTime = 1000 * 60;

	public Download(String url, DownloadDelegate delegate) {
		this(url, null, delegate);
	}

	public Download(List<String> urls, DownloadDelegate delegate) {
		if (urls == null || urls.isEmpty()) {
			throw new IllegalArgumentException("urls list is empty!!");
		}
		for (String url : urls) {
			addTask(url, null);
		}

		m_delegate = delegate;
		new Thread(this).start();
	}

	public Download(List<String> urls, List<String> paths, DownloadDelegate delegate) {
		if (urls == null || urls.isEmpty()) {
			throw new IllegalArgumentException("urls list is empty!!");
		}

		for (String url : urls) {
			addTask(url, null);
		}

		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			m_result.m_tasks.get(i).save_path = path;
			UtilString.writeFile(m_result.m_tasks.get(i).data, path);
		}

		m_delegate = delegate;
		new Thread(this).start();

	}

	public Download(String url, String path, DownloadDelegate delegate) {
		addTask(url, path);
		m_delegate = delegate;

		new Thread(this).start();
	}

	private void addTask(String url, String path) {
		if (path != null) {
			if (UtilString.checkPathValide(path) == false) {
				postError(DownloadResult.ERROR_INVALID_PARAMS, "非法的保存路径");
				return;
			}
		}
		if (UtilString.checkUrlValide(url) == false) {
			postError(DownloadResult.ERROR_INVALID_PARAMS, "非法的下载地址");
			return;
		}

		m_result.addTask(url, path);
	}

	public void setTimeout(int milisecond) {
		m_lCountTime = milisecond;
	}

	public void run() {
		for (final Task task : m_result.m_tasks) {
			UtilHttp http = new UtilHttp();
			http.setProgressListener(new ProgressListener() {

				@Override
				public void onProgress(long curr, long total) {
					task.progress = (int) (curr * 100 / total);
					if (m_delegate != null) {
						int pro = m_result.getProgress();
						m_delegate.onProgress(pro);
					}
				}
			});
			Log.out("[download]start:" + task.url + "[" + task.save_path + "]");
			http.setReadTimeOut((int) m_lCountTime);
			task.data = http.sendData(task.url, null);
			int respondCode = http.getRespondCode();
			if (task.data == null || http.getRespondCode() >= 400 || respondCode == UtilHttp.HTTP_CODE_TIMEOUT
					|| respondCode == UtilHttp.HTTP_CODE_DISCONNECT) {
				int code = http.getRespondCode();
				Log.err("网络繁忙。" + code);
				if (code == 404) {
					task.code = DownloadResult.ERROR_NOFILE;
				} else {
					task.code = DownloadResult.ERROR_NETWORK;
				}

				task.error = "网络繁忙。" + http.getRespondCode();
				continue;
			}

			if (task.save_path != null) {
				try {
					writeToFile(task.save_path, task.data);
				} catch (IOException e) {
					e.printStackTrace();
					task.code = DownloadResult.ERROR_WRITE_FILE;
					task.error = "保存文件失败。";
					Log.err("[download] complete:path=" + task.save_path + "[" + task.url + "]");
					continue;
				}
				Log.out("[download] complete:path=" + task.save_path + "[" + task.url + "]");
			} else {
				Log.out("[download] complete:len=" + task.data.length + ", " + task.url);
			}
		}
		postResult();
	}

	private void postResult() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				m_delegate.onResult(m_result);
			}
		});
	}

	private void postError(final int code, final String msg) {
		m_result.setError(code, msg);
		postResult();

	}

	private void writeToFile(String path, byte[] data) throws IOException {
		File file = new File(path);
		File dir = new File(file.getParent());
		if (dir.exists() == false) {
			dir.mkdir();
		}

		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(data, 0, data.length);
		bos.close();
		fos.close();
	}
}
