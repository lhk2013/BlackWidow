package com.haikuo.spider.queue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.haikuo.spider.model.DataUrl;

/**
 * 所有抓取页面 page的url
 * @author Kevin
 * 2016年2月16日17:36:31
 */
public class UrlDeque {
	
	private static BlockingDeque<DataUrl> urlQueue = new LinkedBlockingDeque<>(); 
	
	/**
	 * 在最后面添加（等效于 put）
	 */
	public static void addElement(DataUrl url) {
		try {
			urlQueue.putLast(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取最前面(等效于take)
	 */
	public static DataUrl outElement() {
		try {
			return urlQueue.takeFirst();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 添加放在最后面（相反于 put）
	 */
	public static void addFirstElement(DataUrl url){
		try {
			urlQueue.putFirst(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取最后一个(相反于take)
	 */
	public static DataUrl ouLasttElement() {
		try {
			return urlQueue.takeLast();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public static int size() {
		return urlQueue.size();
	}
	public static boolean isEmpty() {
		return urlQueue.isEmpty();
	}
	public static boolean isContains(DataUrl url) {
		return urlQueue.contains(url);
	}
}
