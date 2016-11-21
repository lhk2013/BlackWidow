package com.haikuo.spider.utils;

import java.util.UUID;

public class StringUtils {
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 判断字符串是否为null 或者 空字符串
	 */
	public static boolean isEmpty(String args){
		return (args==null||"".equals(args))?true:false;
	}
	
	/**
	 * 判断字符串是否  不为null 或者 不为空字符串
	 */
	public static boolean isNotEmpty(String args){
		return (args!=null&&!"".equals(args))?true:false;
	}
}
