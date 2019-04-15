package com.acewill.call;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class CombineSoundController {
	public static final int FEMALE = 0;
	public static final int MALE = 1;
	public static final int QUICK = 0;
	public static final int SLOW = 1;
	
	public static File getSound(Context context, String fetchid,
			String soundPath,int sex,int speed) throws IOException {
		String sourcePath = "sound";
		if(sex == FEMALE){
			sourcePath+="/female";
		}else{
			sourcePath+="/male";
		}
		if(speed == QUICK){
			sourcePath+="/1";
		}else{
			sourcePath+="/0";
		}

		// .list("sound/female");

		String targetFolderPath = soundPath;
		File targetFolder = new File(targetFolderPath);
		if (!targetFolder.exists())
			targetFolder.mkdirs();

		String targetFileName = System.currentTimeMillis() + ".mp3";
		File targetFile = new File(targetFolder + File.separator
				+ targetFileName);
		OutputStream os = new FileOutputStream(targetFile);

		// File prefixFile = new File(sourcePath + File.separator +
		// "prefix.mp3");
		// File tailfixFile = new File(sourcePath + File.separator +
		// "tailfix.mp3");
		InputStream preis = context.getAssets().open(
				sourcePath + File.separator + "prefix.mp3");
		// File prefixFile = new File();
		int pre = 0;
		int lenPre = 0;
		byte[] preb = new byte[128];
		// InputStream preis = new FileInputStream(prefixFile);
		while ((lenPre = preis.read(preb)) != -1) {
			pre++;
			if (pre == 1) {
				continue;
			}
			os.write(preb, 0, lenPre);
		}
		preis.close();

		// 组合
		char[] cc = fetchid.toCharArray();
		for (char c : cc) {
			char upper = toUpper(c);
			int index = 0;
			int len = 0;
			byte[] b1 = new byte[128];
			// InputStream is = new FileInputStream(file);
			InputStream is = context.getAssets().open(
					sourcePath + File.separator + upper + ".mp3");
			while ((len = is.read(b1)) != -1) {
				index++;
				if (index == 1) {
					continue;
				}
				os.write(b1, 0, len);
			}
			is.close();

		}

		int tail = 0;
		int lenTail = 0;
		byte[] tailb = new byte[128];
		InputStream tailis = context.getAssets().open(
				sourcePath + File.separator + "tailfix.mp3");
		// InputStream tailis = new FileInputStream(tailfixFile);
		while ((lenTail = tailis.read(tailb)) != -1) {
			tail++;
			if (tail == 1) {
				continue;
			}
			os.write(tailb, 0, lenTail);
		}
		tailis.close();

		os.flush();
		os.close();
		return targetFile;
	}
	
	private static char toUpper(char c){
		if (c >= 'a' && c <= 'z') {
			c -= 32;
			return c;
		}
		return c;
	}
}
