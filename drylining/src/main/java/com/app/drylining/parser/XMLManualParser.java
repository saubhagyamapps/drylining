package com.app.drylining.parser;

import java.util.ArrayList;

import android.text.Html;

public class XMLManualParser {

	public static boolean isTagPresent(String tagName, String xmlContent) {
		String tagStartName = tagName;
		// String tagEndName = "</" + tagName + ">";
		if (tagName != null && xmlContent != null && xmlContent.length() > tagName.length()) {

			if (xmlContent.contains(tagStartName)) {
				return true;
			}
		}
		return false;
	}

	public static String getTagValue(String tagName, String xmlContent) {
		// AppDebugLog.println("In getTagValue() : ");
		String tagValue = "";
		String tagStartName = "<" + tagName + ">";
		String tagEndName = "</" + tagName + ">";

		if (tagName != null && xmlContent != null && xmlContent.length() > tagName.length()) {
			int tagStartRange = xmlContent.indexOf(tagStartName);
			int tagEndRange = xmlContent.indexOf(tagEndName);

			if (tagStartRange != -1 && tagEndRange != -1) {
				int location = tagStartRange + tagStartName.length();
				tagValue = xmlContent.substring(location, tagEndRange);
			}
		}
		// AppDebugLog.println("TagValue In getTagValue() : " + tagValue);
		return Html.fromHtml(tagValue.trim()).toString();
	}

	public static String getTagWithPropertyValue(String tagName, String xmlContent) {
		String tagValue = "";
		String tagStartName = "<" + tagName;
		String tagEndName = "</" + tagName + ">";

		if (tagName != null && xmlContent != null && xmlContent.length() > tagName.length()) {
			int tagStartRange = xmlContent.indexOf(tagStartName);
			tagStartRange = xmlContent.indexOf(">", tagStartRange);
			int tagEndRange = xmlContent.indexOf(tagEndName);

			if (tagStartRange != -1 && tagEndRange != -1) {
				int location = tagStartRange + 1;
				tagValue = xmlContent.substring(location, tagEndRange);
			}
		}
		return tagValue.trim();
	}

	public static String getTagPropertyValue(String tagName, String property, String xmlContent) {
		String tagValue = "";
		String tagStartName = "<" + tagName;

		if (tagName != null && xmlContent != null && xmlContent.length() > tagName.length()) {
			int tagStartRange = xmlContent.indexOf(tagStartName);
			tagStartRange = xmlContent.indexOf(property, tagStartRange);
			tagStartRange = tagStartRange + property.length() + 2;
			int tagEndRange = xmlContent.indexOf("\"", tagStartRange);
			if (tagEndRange == -1) {
				tagEndRange = xmlContent.indexOf("'", tagStartRange);
			}
			if (tagStartRange != -1 && tagEndRange != -1) {
				tagValue = xmlContent.substring(tagStartRange, tagEndRange);
			}
		}
		return tagValue.trim();
	}

	public static ArrayList<String> getMultipleTagList(String tagName, String xmlContent) {
		// AppDebugLog.println("In getMultipleTagList() : " + tagName + " : " +
		// xmlContent);
		ArrayList<String> tagListArray = new ArrayList<String>();
		// xmlContent = @"Hi<data></data>";

		if (tagName != null && xmlContent != null && xmlContent.length() > tagName.length()) {
			String tagValue = "";
			String tagStartName = "<" + tagName + ">";
			String tagEndName = "</" + tagName + ">";

			// AppDebugLog.println("tagStartName tagEndName In getMultipleTagList() : "
			// + tagStartName + " : "
			// + tagEndName);

			int tagStartRange = xmlContent.indexOf(tagStartName);
			int tagEndRange = xmlContent.indexOf(tagEndName);
			// AppDebugLog.println("tagStartRange tagEndRange In getMultipleTagList() : "
			// + tagStartRange + " : "
			// + tagEndRange);

			while ((tagStartRange != -1) && (tagEndRange != -1)) {
				int location = tagStartRange + tagStartName.length();
				tagValue = xmlContent.substring(location, tagEndRange);
				//AppDebugLog.println("tagValue In getMultipleTagList() : " + tagValue);
				tagListArray.add(tagValue);
				location = tagEndRange + tagEndName.length();

				tagStartRange = xmlContent.indexOf(tagStartName, location);
				tagEndRange = xmlContent.indexOf(tagEndName, location);
			}
		}
		// AppDebugLog.println("tagListArray In getMultipleTagList() : " +
		// tagListArray.size());
		return tagListArray;
	}
}
