package com.artifex.mupdf.viewer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

/**
 * 日志工具<br>
 * 需要权限：android.permission.WRITE_EXTERNAL_STORAGE
 * <pre>
 *     author  : Fantasy
 *     version : 1.0, 2018-04-01
 *     since   : 1.0, 2018-04-01
 * </pre>
 */
class Logger {
	/** 存放日志文件的目录 */
	private static String mPath = Environment.getExternalStorageDirectory().getPath() + "/";
	/** 日志打印的标签，也是存放日志的文件夹名 */
	private static String mTag = "MuPDFLog";
	/** 主要日志，是否生成日志txt文件，并存在手机的文件夹下 */
	private static boolean mLogWrite = false;
	/** 额外日志，是否生成日志txt文件，并存在手机的文件夹下 */
	private static boolean mLogWriteOther = false;
	/** 是否打印日志在LogCat */
	private static boolean mSeeLog = false;

	/**
	 * 日志工具初始化
	 * 
	 * @param path 存放日志文件的目录，以"/"结束
	 * @param tag “主要日志”的打印的标签，也是存放“主要日志”的文件夹名
	 * @param logWrite 主要日志，是否生成日志txt文件，并存在手机的文件夹下，文件夹名和打印标签一致
	 * @param logWriteOther 额外日志，是否生成日志txt文件，并存在手机的文件夹下，文件夹名和打印标签一致
	 * @param seeLog 是否打印日志在LogCat
	 */
	public static void init(String path, String tag, boolean logWrite, boolean logWriteOther, 
			boolean seeLog) {
		mPath = path;
		mTag = tag;
		mLogWrite = logWrite;
		mLogWriteOther = logWriteOther;
		mSeeLog = seeLog;
	}
	
	/**
	 * 获取主要日志的打印标签
	 */
	public static String getTag() {
		return mTag;
	}
	
	/**
	 * 相当于Log.d(tag, msg);
	 * 
	 * @param tag 标志
	 * @param msg 内容
	 */
	public static void d(String tag, String msg) {
		if (mSeeLog) {
			Log.d(tag, msg);
		}
	}
	
	/**
	 * 写异常信息日志
	 * 
	 * @param tag 标签，也是存放日志的文件夹名
	 * @param e 异常详细信息
	 * @param where 异常信息来源
	 */
	public static void write(String tag, Exception e, String where) {
		Logger.write(tag, where);
		Logger.write(tag, Logger.getStackElement(e));
	}
	
	/**
	 * 写异常信息日志
	 * 
	 * @param tag 标签，也是存放日志的文件夹名
	 * @param ex 异常详细信息
	 * @param where 异常信息来源
	 */
	public static void write(String tag, Throwable ex, String where) {
		Logger.write(tag, where);
		Logger.write(tag, Logger.getStackElement(ex));
	}
	
	/**
	 * 比较全的错误信息
	 * 
	 * @param ex 错误信息
	 * @return 字符串形式的错误信息
	 */
	private static String getStackElement(Throwable ex) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 写日志文件
	 * 
	 * @param tag 标签，也是存放日志的文件夹名
	 * @param msg 内容
	 */
	public static void write(String tag, String msg) {
		if (mSeeLog) {
			Log.i(tag, msg);
		}
		if (mTag.equals(tag)) {
			if(mLogWrite){
				writeToFile(tag, msg);
			}
		} else { // 其他标签的日志打印
			if (mLogWriteOther) {
				writeToFile(tag, msg);
			}
		}
	}

	/**
	 * 将日志信息写到本地文件中
	 * 
	 * @param tag 标签，也是存放日志的文件夹名
	 * @param msg 内容
	 */
	private static void writeToFile(String tag, String msg) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				String dir = mPath + tag + "/";
				
				Date now = new Date();
				SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
				SimpleDateFormat tempDate2 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
				String dateTime = tempDate1.format(now);
				String fileName = tempDate2.format(now);

				File destDir = new File(dir);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				File file = new File(dir + fileName + ".txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file, true);
				fw.append("\r\n=============" + dateTime + "=====================\r\n");
				fw.append(msg);
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
