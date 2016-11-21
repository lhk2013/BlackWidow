package com.haikuo.spider.enums;

public enum ImgType {
	
	/**
	 * 当前状态   1 缩略图
	 */
	thumb(1,"缩略图"),
	/**
	 * 当前状态   2 大图
	 */
	large(2,"大图");
	
	private Integer type;
	private String remark;
	
	ImgType(Integer type,String remark){
		this.type =type;
		this.remark = remark;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
