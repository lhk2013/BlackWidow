package com.haikuo.spider.model;

import java.util.Date;

import com.haikuo.spider.enums.DataStatus;
import com.haikuo.spider.enums.DataType;

public class DataUrl {
	
	private Long id;
	
	private Long pid;
	
	private String listDetailUrl;//列表页url
	
	private String detailUrl;//详情页URL
	
	private String url;//图片URL
	
	private Long imgKey;//套图编号
	
	private Integer status;//当前状态   1未完成（初始） 2 完成 3执行失败
	
	private Integer type;//URL类型 1 列表 2 详情 3 图片url
	
	private Date saveTime;
	
	public DataUrl(){
		super();
	}
	/**
	 * 列表
	 * @param listDetailUrl
	 * @param status
	 */
	public DataUrl(String listDetailUrl){
		this.listDetailUrl = listDetailUrl;
		this.status =  DataStatus.Init.getType();
		this.saveTime = new Date();
		this.type = DataType.ListDetailUrl.getType(); 
	}
	/**
	 * 详情
	 * @param DetailUrl
	 * @param status
	 */
	public DataUrl(String listDetailUrl,String detailUrl){
		this.listDetailUrl = listDetailUrl;
		this.detailUrl = detailUrl;
		this.status =  DataStatus.Init.getType();
		this.saveTime = new Date();
		this.type = DataType.DetailUrl.getType(); 
	}
	/**
	 * 图片
	 * @param Url
	 * @param status
	 */
	public DataUrl(String listDetailUrl,String detailUrl,String url,Long pid){
		this.listDetailUrl = listDetailUrl;
		this.detailUrl = detailUrl;
		this.url = url;
		this.pid = pid;
		this.saveTime = new Date();
		this.status = DataStatus.Init.getType();
		this.type = DataType.DetailUrlOthers.getType(); 
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public String getListDetailUrl() {
		return listDetailUrl;
	}
	public void setListDetailUrl(String listDetailUrl) {
		this.listDetailUrl = listDetailUrl;
	}
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getImgKey() {
		return imgKey;
	}
	public void setImgKey(Long imgKey) {
		this.imgKey = imgKey;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}
	
}
