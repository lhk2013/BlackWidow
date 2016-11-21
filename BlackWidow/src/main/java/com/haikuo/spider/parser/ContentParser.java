package com.haikuo.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.enums.DataStatus;
import com.haikuo.spider.enums.DataType;
import com.haikuo.spider.model.DataImg;
import com.haikuo.spider.model.DataUrl;
import com.haikuo.spider.model.FetchedPage;
import com.haikuo.spider.queue.UrlDeque;
import com.haikuo.spider.storage.DataStorage;
import com.haikuo.spider.utils.StringUtils;

/**
 * 对Fetcher下载的页面内容进行解析，获取目标数据
 * @author Administrator
 */
public class ContentParser {
	
	private static final Logger logger = LoggerFactory.getLogger(ContentParser.class);
	
	private volatile int count=1;
	
	public Object parse(FetchedPage fetchedPage) {
		
				Document doc = Jsoup.parse(fetchedPage.getContent());
		DataUrl fetchedUrl = fetchedPage.getDataUrl();
		Long pid = 0L;
		if(fetchedUrl.getType() == DataType.ListDetailUrl.getType()){//是抓来的List页面 包含很多详情页的链接
			Element pins = doc.getElementById("pins");//列表Ul块
			if(pins!=null){
				Elements indexLiTags = pins.getElementsByTag("li");//详情li块
				if(indexLiTags!=null){
					for (Element indexLiTag : indexLiTags) {
						
						Element indexATag = indexLiTag.getElementsByTag("a").get(0);
						String urlPath = indexATag.attr("href");
						String imgPath = indexATag.getElementsByTag("img").attr("data-original");
						
						//查询当前详情url对应的url是否完成爬取 count说明已经完成  直接跳过
						int count = DataStorage.queryCountByTypeAndStatus(urlPath.trim(), DataType.DetailUrl.getType(), DataStatus.Success.getType());
						if(count>0){
							System.err.println("当前url已经抓取过---"+urlPath.trim());
							continue ;
						}else{//如果没有抓完，则删除 当前detailUrl对应的所有连接
							int existCount  = DataStorage.queryCountByTypeAndStatus(urlPath.trim(),  DataType.DetailUrl.getType(), DataStatus.Init.getType());
							if(existCount>0){
								logger.info("当前url"+urlPath.trim()+" 存在未抓取完成的情况 删了从来");
								DataStorage.deleteByDetailUrl(urlPath.trim());
							}
						}
						

						String indexTitle = indexLiTag.getElementsByTag("span").get(0).getElementsByTag("a").get(0).text();
						String id = StringUtils.uuid();
						DataUrl newDataUrl = new DataUrl(fetchedPage.getDataUrl().getListDetailUrl(),urlPath);
						
						//保存列表页面 所有详情页打开的 url 信息,设置id主键
						pid = DataStorage.saveDataUrl(newDataUrl);
						newDataUrl.setId(pid);
						
						DataImg newDataImg = new DataImg(newDataUrl.getId(),indexTitle,imgPath);
						DataStorage.saveImg(newDataImg);

						
						UrlDeque.addElement(newDataUrl);
						logger.info("Index添加图片地址到列队==妹子id:{}，名字：{}，图片地址：{}", id, indexTitle, imgPath);
						
					}
				}
				
				Element pageDiv = doc.getElementsByClass("nav-links").get(0);
				
				Element nextPage = pageDiv.getElementsByTag("a").last();
				String nextNum = nextPage.text();
				String nextPageUrl = nextPage.attr("href");
				if(!nextNum.equals("下一组»") && !nextNum.equals("下一组&raquo;")) {
					UrlDeque.addElement(new DataUrl(nextPageUrl));
					logger.info("下一页地址=="+nextPageUrl);
				}
				
			}
		}else if(fetchedUrl.getType() == DataType.DetailUrl.getType()||
				fetchedUrl.getType() == DataType.DetailUrlOthers.getType()){
			Element pageDiv = doc.getElementsByClass("pagenavi").get(0);
			// 当前页码
			String pageNum = pageDiv.select("div.pagenavi > span").get(0).text();
			if("…".equals(pageNum)) pageNum = pageDiv.select("div.pagenavi > span").get(1).text();
			// 标题
			Elements mainTitleDiv = doc.getElementsByClass("main-title");
			String title = mainTitleDiv.get(0).text();
			// 类别
			Elements mainMetaDiv = doc.getElementsByClass("main-meta");
			String category = mainMetaDiv.get(0).getElementsByTag("span").get(0).getElementsByTag("a").get(0).text();
			// 图片路径
			Elements mainImgDiv = doc.getElementsByClass("main-image");
			String imgPath = mainImgDiv.get(0).getElementsByTag("img").attr("src");
			
			String id = StringUtils.uuid();
			logger.info("Detail添加图片地址到列队==妹子id:{}，名字：{}，类别：{}，图片地址：{}， pid：{}，index：{}",id, title, category, imgPath, fetchedPage.getDataUrl().getId(), pageNum);

			fetchedUrl = fetchedPage.getDataUrl();
			
			//默认pid是抓取详情页的id 如果不是详情页第一页  then save the record
			pid = fetchedUrl.getId();
			if(fetchedUrl.getType()==DataType.DetailUrlOthers.getType()){
				DataStorage.saveDataUrl(fetchedUrl);
				pid = fetchedUrl.getPid();
				System.err.println("--------------------------------"+fetchedUrl.getPid());
			}
	
			DataImg newDataImg = new DataImg(fetchedUrl.getId(),title,category,imgPath,Integer.valueOf(pageNum));
			DataStorage.saveImg(newDataImg);
			
			Element nextPage = pageDiv.getElementsByTag("a").last();
			String nextNum = nextPage.text();
			String nextPageUrl = nextPage.attr("href");
			
			if(!nextNum.equals("下一组»") && !nextNum.equals("下一组&raquo;")) {
				//加载最前面，让下一个请求直接访问，保证一组图的任务一起完成
				UrlDeque.addFirstElement(new DataUrl(fetchedPage.getDataUrl().getListDetailUrl(),fetchedPage.getDataUrl().getDetailUrl(),nextPageUrl,pid));
				logger.info("下一页地址=="+nextPageUrl);
			}else if(nextNum.equals("下一组»")) {
				//将下载完的 detailUrl 所有记录 置为完成
				DataStorage.updateDataImg(fetchedUrl.getDetailUrl(), DataStatus.Success.getType());
				System.err.println("已下载完 "+ count++ +" 个");
			}
		}
		
		// 如果当前页面包含目标数据
	/*	if(containsTargetData(fetchedPage.getData().getUrl(), doc)){
			// 解析并获取目标数据
			Element pins = doc.getElementById("pins");
			if(pins!=null) {
				Elements indexLiTags = pins.getElementsByTag("li");
				for(Element indexLiTag : indexLiTags) {
					Element indexATag = indexLiTag.getElementsByTag("a").get(0);
					String urlPath = indexATag.attr("href");
					String imgPath = indexATag.getElementsByTag("img").attr("data-original");

					String indexTitle = indexLiTag.getElementsByTag("span").get(0).getElementsByTag("a").get(0).text();
					
					String id = StringUtils.uuid();
					UrlQueue.addElement(new Data(id, urlPath));
					
					DataStorage.indexStore(new ImgModel(id, indexTitle, imgPath, true));
					ImgQueue.addElement(new Data(imgPath, true));
					logger.info("Index添加图片地址到列队==妹子id:{}，名字：{}，图片地址：{}", id, indexTitle, imgPath);
				}
			}else {
				// 获取当前页的主要信息
				
				Element pageDiv = doc.getElementsByClass("pagenavi").get(0);
				// 当前页码
				String pageNum = pageDiv.select("div.pagenavi > span").get(0).text();
				if("…".equals(pageNum)) pageNum = pageDiv.select("div.pagenavi > span").get(1).text();
				// 标题
				Elements mainTitleDiv = doc.getElementsByClass("main-title");
				String title = mainTitleDiv.get(0).text();
				// 类别
				Elements mainMetaDiv = doc.getElementsByClass("main-meta");
				String category = mainMetaDiv.get(0).getElementsByTag("span").get(0).getElementsByTag("a").get(0).text();
				// 图片路径
				Elements mainImgDiv = doc.getElementsByClass("main-image");
				String imgPath = mainImgDiv.get(0).getElementsByTag("img").attr("src");
				
				String id = StringUtils.uuid();
				logger.info("Detail添加图片地址到列队==妹子id:{}，名字：{}，类别：{}，图片地址：{}， pid：{}，index：{}",id, title, category, imgPath, fetchedPage.getData().getId(), pageNum);
				
				img = new ImgModel(id, title, category, imgPath, fetchedPage.getData().getId(), false, pageNum);
				
				ImgQueue.addElement(new Data(imgPath, false));
//				DownImg.downImg(mainImg);
				
				Element nextPage = pageDiv.getElementsByTag("a").last();
				String nextNum = nextPage.text();
				String nextPageUrl = nextPage.attr("href");
				
				if(!nextNum.equals("下一组»") && !nextNum.equals("下一组&raquo;")) {
					UrlQueue.addElement(new Data(fetchedPage.getData().getId(), nextPageUrl));
					logger.info("下一页地址=="+nextPageUrl);
				}else if(nextNum.equals("下一组»")) {
					System.err.println("已下载完 "+ count++ +" 个");
				}
			}
		}*/
		// 将URL放入已爬取队列
//		VisitedUrlQueue.addElement(fetchedPage.getData().getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return "img"; 
	}
	
	public static DataUrl parseNewDataUrl(DataUrl data){
		DataUrl du = new DataUrl();
		du.setPid(data.getPid());
		du.setListDetailUrl(data.getListDetailUrl());
		du.setDetailUrl(data.getDetailUrl());
		du.setUrl(data.getUrl());
		du.setImgKey(data.getImgKey());
		du.setStatus(data.getStatus());
		du.setType(data.getStatus());
		du.setSaveTime(data.getSaveTime());
		return du;
	}
	

}
