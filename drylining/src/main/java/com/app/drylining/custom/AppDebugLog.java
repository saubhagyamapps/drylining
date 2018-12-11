package com.app.drylining.custom;

import android.util.Log;

public class AppDebugLog {
	private static boolean isProductionMode;
	private static boolean isFileLogMode;

	public static void setProductionMode(boolean isProductionMode) {
		AppDebugLog.isProductionMode = isProductionMode;
	}

	public static void setFileLogMode(boolean isFileLogMode) {
		AppDebugLog.isFileLogMode = isFileLogMode;
	}

	public static void println(String message) {

		// if not in production, then print log on console
		if (!isProductionMode) {
			Log.d("CrazyReturns ", " Debug " + message);
		}

		// log in file for testing mode debug
		if (isFileLogMode) {

		}
	}

	public static void printArray(Object[] message) {

		// if not in production, then print log on console
		if (!isProductionMode) {
			for (Object object : message) {
				System.out.println(object);
			}

		}

		// log in file for testing mode debug
		if (isFileLogMode) {

		}
	}

}
