package com.haikuo.spider.spider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.down.DownImg;
import com.haikuo.spider.model.SpiderParams;
import com.haikuo.spider.utils.DBConn;


public class ImgDowloadStarter {
	private static final Logger logger = LoggerFactory.getLogger(ImgDowloadStarter.class);
	public static void main(String[] args) throws InterruptedException{

		
		initializeParams();
		
		DownImg.DowImgTask();
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
