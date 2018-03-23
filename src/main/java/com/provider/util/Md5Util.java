package com.provider.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Md5Util {
	public static String md5(String str) {
		String s = str;
		if (s == null) {
			return "";
		} else {
			String value = null;
			MessageDigest md5 = null;
			StringBuffer buf = new StringBuffer("");

			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(str.getBytes("utf-8"));
				byte[] b = md5.digest();

				int temp;
				for (int n = 0; n < b.length; n++) {
					temp = b[n];
					if (temp < 0)
						temp += 256;
					if (temp < 16)
						buf.append("0");
					buf.append(Integer.toHexString(temp));
				}
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(Md5Util.class.getName()).log(Level.SEVERE,
						null, ex);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// sun.misc.BASE64Encoder baseEncoder = new
			// sun.misc.BASE64Encoder();
			try {
				// value = baseEncoder.encode(md5.digest(s.getBytes("utf-8")));
				value = buf.toString().toUpperCase();
			} catch (Exception ex) {

			}

			return value;
		}
	}

	public static String enMd5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return byte2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String byte2Hex(byte[] byteArray) {
		String result = "";
		for (int offset = 0; offset < byteArray.length; offset++) {
			String toHexString = Integer.toHexString(byteArray[offset] & 0xFF);
			if (toHexString.length() == 1) {
				result += "0" + toHexString;
			} else {
				result += toHexString;
			}
		}
		return result;
	}
}
