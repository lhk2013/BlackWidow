package com.haikuo.spider.model;

import java.util.Date;

import com.haikuo.spider.enums.ImgStatus;
import com.haikuo.spider.enums.ImgType;

public class DataImg {
	private Long id;
	
	private Long urlId;
	
	private Integer imgType; // 1缩略图 2大图
	
	private Long imgKey;//套图编号 编号相同则为一套
	
	private String title;
	
	private String category;//分类 暂时存中文 以后改为英文编码
	
	private String srcPth;//源图片地址 对应url
	
	private String newPath;//现在的存放地址 相对文件夹地址 不加盘符
	
	private Integer seq;//在当前套图中的顺序
	
	private Integer status;
	
	private Date initTime;
	
	private Date downloadTime;
	
	public DataImg() {
		super();
	}
	
	/**
	 * 缩略图
	 * @param urlId
	 * @param title
	 * @param srcPth
	 */
	
	public DataImg(Long urlId,String title,String srcPth) {
		this.urlId = urlId;
		this.imgType = ImgType.thumb.getType();
		this.status = ImgStatus.Init.getType();
		this.initTime = new Date();
		this.title = title;
		this.srcPth = srcPth;
	}
	/**
	 * 大图
	 * @param urlId
	 * @param title
	 * @param category
	 * @param srcPth
	 * @param seq
	 */
	public DataImg(Long urlId,String title,String category,String srcPth,Integer seq) {
		this.urlId = urlId;
		this.imgType = ImgType.large.getType();
		this.status = ImgStatus.Init.getType();
		this.initTime = new Date();
		this.title = title;
		this.category = category;
		this.srcPth = srcPth;
		this.seq = seq;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUrlId() {
		return urlId;
	}

	public void setUrlId(Long urlId) {
		this.urlId = urlId;
	}

	public Integer getImgType() {
		return imgType;
	}

	public void setImgType(Integer imgType) {
		this.imgType = imgType;
	}

	public Long getImgKey() {
		return imgKey;
	}

	public void setImgKey(Long imgKey) {
		this.imgKey = imgKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSrcPth() {
		return srcPth;
	}

	public void setSrcPth(String srcPth) {
		this.srcPth = srcPth;
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getInitTime() {
		return initTime;
	}

	public void setInitTime(Date initTime) {
		this.initTime = initTime;
	}

	public Date getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}
	
	
	
	
}
