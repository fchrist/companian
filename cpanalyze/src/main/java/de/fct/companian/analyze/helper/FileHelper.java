package de.fct.companian.analyze.helper;

import java.io.File;

public class FileHelper {

	public static String getFileEnding(File subFile) {
		int dotPos = subFile.getName().lastIndexOf('.');

		return subFile.getName().substring(dotPos + 1);
	}
}
