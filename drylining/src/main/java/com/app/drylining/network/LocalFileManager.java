package com.app.drylining.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;
import java.util.Hashtable;

import android.content.Context;

import com.app.drylining.DRYLINING;


public class LocalFileManager {
	private static LocalFileManager fileManager;
	private static String MAP_FILE_NAME = "mapingfile";
	private File localCacheDirectory;
	private Hashtable<String, String> urlFileMap;
	Context currentContext;

	public static LocalFileManager sharedFileManager(Context context) {
		if (fileManager == null) {
			if (context == null) {
				context = DRYLINING.getAppContext();
				// return null;
			}
			fileManager = new LocalFileManager(context);
		}
		return fileManager;
	}

	@SuppressWarnings("unchecked")
	public LocalFileManager(Context context) {
		currentContext = context;
		
		localCacheDirectory = context.getCacheDir();
		if (!localCacheDirectory.exists()) {
			localCacheDirectory.mkdirs();
		}
		try {
			FileInputStream inputStream = currentContext
					.openFileInput(MAP_FILE_NAME);
			ObjectInputStream objectSteam = new ObjectInputStream(inputStream);
			urlFileMap = (Hashtable<String, String>) objectSteam.readObject();

		} catch (FileNotFoundException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		} catch (StreamCorruptedException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		}

		if (urlFileMap == null) {
			urlFileMap = new Hashtable<String, String>();
		}
	}

	public boolean saveContentForURL(String filePath, String content) {
		try {
		
			String localFilePath = "";
			if (urlFileMap.containsKey(filePath)) {
		
				localFilePath = urlFileMap.get(filePath);
			} else {
		
				localFilePath = String.valueOf((new Date()).getTime()) + ".txt";
				urlFileMap.put(filePath, localFilePath);
			}
			FileOutputStream fileOutStream = currentContext.openFileOutput(
					localFilePath, Context.MODE_PRIVATE);
			ObjectOutputStream objectOutStream = new ObjectOutputStream(
					fileOutStream);
			objectOutStream.writeObject(content);
			objectOutStream.flush();
			objectOutStream.close();
			fileOutStream.flush();
			fileOutStream.close();

			this.updateURLMapFile();
		} catch (FileNotFoundException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		} catch (StreamCorruptedException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("Error : " + e.getLocalizedMessage());
		}

		return false;
	}

//	public boolean saveFileForURL(String filePath, HttpEntity content) {
//		try {
//			String localFilePath = "";
//			if (urlFileMap.containsKey(filePath)) {
//				localFilePath = urlFileMap.get(filePath);
//			} else {
//				if (filePath.endsWith("mp3")) {
//					localFilePath = String.valueOf((new Date()).getTime())
//							+ ".mp3";
//				} else {
//					localFilePath = String.valueOf((new Date()).getTime())
//							+ ".pdf";
//				}
//				urlFileMap.put(filePath, localFilePath);
//			}
//			FileOutputStream fileOutStream = currentContext.openFileOutput(
//					localFilePath, Context.MODE_PRIVATE);
//			ObjectOutputStream objectOutStream = new ObjectOutputStream(
//					fileOutStream);
//			int read = 0;
//			byte buf[] = new byte[4096];
//			InputStream responseStream = content.getContent();
//			while ((read = responseStream.read(buf)) > 0) {
//				objectOutStream.write(buf, 0, read);
//			}
//
//			objectOutStream.flush();
//			objectOutStream.close();
//			fileOutStream.flush();
//			fileOutStream.close();
//
//			this.updateURLMapFile();
//
//		} catch (FileNotFoundException e) {
//			System.err.println("Error : " + e.getLocalizedMessage());
//		} catch (StreamCorruptedException e) {
//			System.err.println("Error : " + e.getLocalizedMessage());
//		} catch (IOException e) {
//			System.err.println("Error : " + e.getLocalizedMessage());
//		}
//
//		return false;
//	}

	public void deleteFileForURL(String filePath) {
		if (urlFileMap.containsKey(filePath)) {
			File contentFile = new File(currentContext.getFilesDir() + "/"
					+ urlFileMap.get(filePath));
			if (contentFile.exists()) {
				contentFile.delete();
			}
			urlFileMap.remove(filePath);
			this.updateURLMapFile();
		}
	}

	public String getContentForURL(String filePath) {
		if (urlFileMap.containsKey(filePath)) {
			FileInputStream inputStream;
			try {
				inputStream = currentContext.openFileInput(urlFileMap
						.get(filePath));
				ObjectInputStream objectSteam = new ObjectInputStream(
						inputStream);
				String fileContent = (String) objectSteam.readObject();
				objectSteam.close();
				inputStream.close();
				return fileContent;
			} catch (FileNotFoundException e) {
				System.err.println("Error : " + e.getLocalizedMessage());
			} catch (StreamCorruptedException e) {
				System.err.println("Error : " + e.getLocalizedMessage());
			} catch (IOException e) {
				System.err.println("Error : " + e.getLocalizedMessage());
			} catch (ClassNotFoundException e) {
				System.err.println("Error : " + e.getLocalizedMessage());
			}
		}
		return "";
	}

	public String getAbsolutePathForURL(String filePath, boolean createNew) {
		if (urlFileMap.containsKey(filePath)) {
			return currentContext.getFilesDir() + "/"
					+ urlFileMap.get(filePath);
		} else if (createNew) {
			String localFilePath = "";
			if (filePath.endsWith("mp3")) {
				localFilePath = String.valueOf((new Date()).getTime()) + ".mp3";
			} else {
				localFilePath = String.valueOf((new Date()).getTime()) + ".pdf";
			}
			urlFileMap.put(filePath, localFilePath);
			this.updateURLMapFile();
			localFilePath = currentContext.getFilesDir() + "/" + localFilePath;
			return localFilePath;
		}
		return "";
	}

	private void updateURLMapFile() {
		try {
			FileOutputStream fileOutStream = currentContext.openFileOutput(
					MAP_FILE_NAME, Context.MODE_PRIVATE);

			ObjectOutputStream objectOutStream = new ObjectOutputStream(
					fileOutStream);
			objectOutStream.writeObject(urlFileMap);
			objectOutStream.flush();
			objectOutStream.close();
			fileOutStream.flush();
			fileOutStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	void clearLocalCache() {
		for (String filePath : urlFileMap.values()) {
			File contentFile = new File(filePath);
			if (contentFile.exists()) {
				contentFile.delete();
			}
		}
		urlFileMap.clear();
		File hashFile = new File(MAP_FILE_NAME);
		if (hashFile.exists()) {
			hashFile.delete();
		}
	}
}
