package com.haikuo.spider.model;

public class FetchedPage {

	private DataUrl dataUrl;
	private String content;
	private int statusCode;
	
	public FetchedPage() {
		super();
	}
	
	public FetchedPage(DataUrl dataUrl, String content, int statusCode) {
		super();
		this.dataUrl = dataUrl;
		this.content = content;
		this.statusCode = statusCode;
	}
	
	public DataUrl getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(DataUrl dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
