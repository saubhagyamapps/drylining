package com.app.drylining.network;

import com.app.drylining.data.AppConstant;

public interface RequestTaskDelegate {

	void backgroundActivityComp(String response,
			AppConstant.HttpRequestType completedRequestType);

	void timeOut();

	void codeError(int code);

	void percentageDownloadCompleted(int percentage, Object record);
}
