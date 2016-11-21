package com.haikuo.spider.down;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.enums.ImgStatus;
import com.haikuo.spider.enums.ImgType;
import com.haikuo.spider.model.DataImg;
import com.haikuo.spider.model.SpiderParams;
import com.haikuo.spider.queue.ImgUrlDeque;
import com.haikuo.spider.storage.DataStorage;
import com.haikuo.spider.utils.RegexUtils;

/**
 * 下载图片
 * @author Administrator
 *
 */
public class DownImg {
	
	private static final Logger logger = LoggerFactory.getLogger(DownImg.class);
	
	public static void downImg(DataImg imgUrl) {
		
		logger.info("开始下载图片地址！{}", imgUrl.getSrcPth());
		
		StringBuffer rootPath = new StringBuffer(SpiderParams.FILEPATH+File.separator);
		if(imgUrl.getImgType() == ImgType.thumb.getType() ) rootPath.append("thumbs");
		rootPath.append(File.separator).append(RegexUtils.getFirstDir(imgUrl.getSrcPth())).append(File.separator).append(RegexUtils.getSecondDir(imgUrl.getSrcPth())).append(File.separator);
		
		String fileName = imgUrl.getSrcPth().substring(imgUrl.getSrcPth().lastIndexOf("/")+1);
		InputStream is = null;
		FileOutputStream out = null;
		try {
			// 创建文件目录
			File files = new File(rootPath.toString());
			// 判断目录是否存在
			if(!files.exists()) {
				files.mkdirs();
			}
			// 获取图片的下载地址
			URL url = new URL(imgUrl.getSrcPth());
			// 连接网络图片地址
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(50000);
			uc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
			// 获取连接的输出流
			is = uc.getInputStream();
			// 创建文件
			String newImgPath = rootPath.append(fileName).toString();
			File file = new File(newImgPath);
			// 创建输出流，写入文件
			out = new FileOutputStream(file);
			int b=0;
			while ((b = is.read()) != -1) {
				out.write(b);
			}
			logger.info("图片下载完成！{}", fileName);
			
			DataStorage.imgDownloadFinish(imgUrl.getId(), ImgStatus.Success.getType(), newImgPath);
			
		} catch (IOException e) {
			logger.error("下载超时:{}", imgUrl);
			ImgUrlDeque.addFirstElement(imgUrl);
			//TODO 更新状态为下载超时 
			
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static void DowImgTask() throws InterruptedException {
//		while(true) {//考虑用多线程实现  生产消费模式
			int page =1, size =10;
			boolean isNext = true;
			int status = ImgStatus.Init.getType();
			BlockingDeque<DataImg> imgUrlQueue = null;
			while (isNext) {//有数据
				imgUrlQueue = DataStorage.listImgUrlByPageSize(0, 1, page, size);
				if(imgUrlQueue==null||imgUrlQueue.isEmpty()){
					isNext = false;
				}else{//可以获取到数据
					while(!ImgUrlDeque.isEmpty()){
						DownImg.downImg(ImgUrlDeque.outElement());
					}
				}
				//page++; //这里做了一个状态更新的操作，这里会造成 每抓取 size条数据，会跳过size 条数据，如：   page 1 10 抓完后   page 2,10 想要抓取的是第 11-20 ，实际抓取的是21-30，中间的10跳过了
			}
			
//		}
	}
	
//	public static void main(String[] args) {
//		for(int i=1; i<34; i++) {
//			String p = String.format("%02d", i);
//			String imgUrl = "http://pic.mmfile.net/2015/12/01x"+p+".jpg";
//			System.out.println(imgUrl);
////			downImg(imgUrl);
//		}
//	}
	
}
