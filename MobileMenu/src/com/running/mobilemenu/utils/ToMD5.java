package com.running.mobilemenu.utils;

import com.huawei.eidc.slee.security.Base64;
import com.huawei.eidc.slee.security.DESTools;
import com.huawei.eidc.slee.security.MD5;


public class ToMD5 {
	private static String KEY = "";

	/*
	 * 加密
	 */
	public static String encrypt(String message) {
		String body = null;
		try {
			DESTools des = DESTools.getInstance(KEY);
			String md5str = MD5.md5(message) + message;
			byte[] b = des.encrypt(md5str.getBytes("UTF8"));
			body = Base64.encode(b);
		} catch (Exception ex) {
			ex.getMessage();
		}
		return body;
	}
	
	/*
	 * 解密
	 */
	public static String decrypt(String message) {
		String body = null;
		try {
			DESTools des = DESTools.getInstance(KEY);
			byte[] b = Base64.decode(message);
			byte[] c = des.decrypt(b);
			String md5body = new String(c, "UTF-8");
			String md5Client = md5body.substring(0, 32);
			body = md5body.substring(32);
		} catch (Exception ex) {
			ex.getMessage();
		}
		return body;
	}
	public static void main(String[] args) {
		String str = encrypt("asdfghjkl");
		System.out.println(decrypt(str));
	}
}
