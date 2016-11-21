package com.haikuo.spider.fetcher;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haikuo.spider.model.DataUrl;
import com.haikuo.spider.model.FetchedPage;
import com.haikuo.spider.model.SpiderParams;
import com.haikuo.spider.queue.UrlDeque;

/**
 * 页面的抓取
 * @author Administrator
 */
public class PageFetcher {
	private static final Logger logger = LoggerFactory.getLogger(PageFetcher.class);
	
	private CloseableHttpClient client;
	private HttpUriRequest request;
	
	public void close() {
		if(request!=null) {
			if(request.isAborted()) {
				request.abort();
		    }
		}
	    try {
	    	if(client!=null) {
	    		client.close();
	    	}
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	public FetchedPage getHtmlContentByUrl(DataUrl dataUrl) {
		HttpResponse response;
		int statusCode = 500;
		String content = null;
		String urlString = null;
		
		switch (dataUrl.getType()) {
		case 1:
			urlString = dataUrl.getListDetailUrl();
			break;
		case 2:
			urlString = dataUrl.getDetailUrl();
			break;
		case 3:
			urlString = dataUrl.getUrl();
			break;	
		default:
			break;
		}
		
		logger.info("当前URL"+dataUrl.getListDetailUrl()+"-"+dataUrl.getDetailUrl()+"-"+dataUrl.getUrl());
		
		client = HttpClients.custom().build();
		request = RequestBuilder.get().setUri(urlString)
		  .setHeader(HttpHeaders.TIMEOUT, "10000")
		  .setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0")
		  .build();
		try {
			// 获取Html源码
			response = client.execute(request);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
				content = EntityUtils.toString(entity, SpiderParams.SITECHARSET);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			// 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
			logger.info(">> Put back url: " + urlString);
			
			
			UrlDeque.addFirstElement(dataUrl);
				
		}
		return new FetchedPage(dataUrl, content, statusCode);
	}
	
}
