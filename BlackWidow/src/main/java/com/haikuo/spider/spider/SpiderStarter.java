package com.haikuo.spider.spider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.down.DownImg;
import com.haikuo.spider.enums.DataStatus;
import com.haikuo.spider.model.DataUrl;
import com.haikuo.spider.model.SpiderParams;
import com.haikuo.spider.queue.UrlDeque;
import com.haikuo.spider.utils.DBConn;
import com.haikuo.spider.utils.StringUtils;


public class SpiderStarter {
	private static final Logger logger = LoggerFactory.getLogger(SpiderStarter.class);
	public static void main(String[] args) throws InterruptedException{
		// 初始化配置参数
		initializeParams();
		// 初始化爬取队列
		initializeQueue();
		// 创建worker线程并启动
		/**
		 * 这种写法并不能控制线程的数量，无论i<N N是多少 ，永远只有一个线程thread-0 
		 * 若要使用多个线程需要改造 
		 * TODO
		 */
		for(int i = 0; i < 1; i++){
			new Thread(new SpiderWorker(i)).start();
		}
		
//		TimeUnit.SECONDS.sleep(10);
		//logger.info("开始下载图片任务！");
		//DownImg.DowImgTask();
	}
	/**
	 * 准备初始的爬取链接
	 */
	private static void initializeQueue(){
		// Mzitu
		if(StringUtils.isNotEmpty(SpiderParams.URL)){
			UrlDeque.addElement(new DataUrl(SpiderParams.URL));
		}else {
			String url = "http://www.mzitu.com";
			UrlDeque.addElement(new DataUrl(url));
		}
	}
	
	/**
	 * 初始化配置文件参数
	 */
	public static void initializeParams(){
		InputStream in;
		try {
//			in = new BufferedInputStream(new FileInputStream("spider.properties"));
			in =SpiderStarter.class.getResourceAsStream("spider.properties");
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取数据库连接参数
			DBConn.CONN_URL = properties.getProperty("DB.connUrl");
			DBConn.USERNAME = properties.getProperty("DB.username");
			DBConn.PASSWORD = properties.getProperty("DB.password");
			
			// 从配置文件中读取参数
			SpiderParams.WORKER_NUM = Integer.parseInt(properties.getProperty("spider.threadNum"));
			SpiderParams.DEYLAY_TIME = Integer.parseInt(properties.getProperty("spider.fetchDelay"));
			SpiderParams.FILEPATH = properties.getProperty("spider.filepath");
			SpiderParams.SITECHARSET = properties.getProperty("spider.siteCharset");
			SpiderParams.URL = properties.getProperty("spider.url");

			in.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
