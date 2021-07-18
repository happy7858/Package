package com.shenma.common.net;

public interface DownloadDelegate {
	public void onResult(DownloadResult result);
	public void onProgress(int pro);
}
